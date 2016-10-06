/*
 * PWAL -Waste Bin Data Simulator
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.pwal.wastebinsimulator;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.api.devices.polling.PWALPollingTask;
import it.ismb.pertlab.pwal.wastebinsimulator.data.WasteBinSensorData;

import java.util.Set;
import java.util.Vector;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

/**
 * The polling task handling actual polling for simulated waste bin sensors. It
 * takes care of querying the network-level end point and to deliver updates to
 * the subscribing devices, with the required timing properties.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class WasteBinSimulatorPollingTask extends PWALPollingTask<WasteBinSimulatorManager,WasteBinSensorData> implements Runnable
{
	/**
	 * Creates a new polling task managed by the given DeviceManager and using
	 * the given logger to printout working information.
	 * 
	 * @param manager
	 *            The {@link WasteBinSimulatorManager} controlling this polling
	 *            task.
	 * @param logger
	 *            The {@link Logger} instance used to log relevant information
	 *            generated by this task.
	 */
	public WasteBinSimulatorPollingTask(PollingDevicesManager<WasteBinSensorData> manager, Logger logger)
	{
		super(manager, logger);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		// info
		this.logger.debug("Polling " + this.manager.getActiveSubscriptionsSize() + "subscription...");
		
		// if there is at least one subscription
		if (this.manager.getActiveSubscriptionsSize() > 0)
		{
			// get the available updates
			Vector<WasteBinSensorData> updates = ((WasteBinSimulatorManager)this.manager).getNetworkLayer().getLatestUpdates();
			
			// the updates counter, used to avoid sleeping in the last cycle
			int nUpdates = updates.size();
			
			// the time per cycle
			// too optimistic, shall probably be reduced by using a kind of
			// safety factor
			int cycleTime = (this.manager.getPollingTimeMillis() * 8) / (10 * updates.size());
			
			// iterate over the available updates
			for (int i=0; i<updates.size(); i++)
			{
				WasteBinSensorData cUpdate  = updates.elementAt(i);
				
				long time = System.currentTimeMillis();
				
				// get all active subscriptions for the give lUID
				// debug
				this.logger.debug("lUID: " + cUpdate.getlUID());
				
				// dispatch the new measure if a subscription is registered for
				// the given lUID
				Set<DataUpdateSubscription<WasteBinSensorData>> subscriptionBucket = this.manager
						.getSubscriptions(cUpdate.getlUID());
				
				// if there are subscriptions for the given device
				if (subscriptionBucket != null)
				{
					// iterate over device subscriptions
					for (DataUpdateSubscription<WasteBinSensorData> subscription : subscriptionBucket)
					{
						// if the current subscription is not null
						if (subscription != null)
						{
							// check how much time passed since last update
							long currentTime = System.currentTimeMillis();
							
							// if needed generate a new update event (low-level)
							if (currentTime - subscription.getTimestamp() >= (subscription.getDeliveryTimeMillis()))
							{
								// update the device
								DateTime updateAt = DateTime.now(DateTimeZone.UTC);
								String expiresAt = updateAt.plusMillis(subscription.getDeliveryTimeMillis()).toString();
								((Device) subscription.getSubscriber()).setUpdatedAt(updateAt.toString());
								((Device) subscription.getSubscriber()).setExpiresAt(expiresAt);
								subscription.getSubscriber().handleUpdate(cUpdate);
								
								// log
								this.logger.debug("Updating device: {} type: {}.",
										((Device) subscription.getSubscriber()).getPwalId(),
										((Device) subscription.getSubscriber()).getType());
							}
						}
					}
					
					// update done
					nUpdates--;
					
					// execute only if updates are still pending
					if (nUpdates > 0)
					{
						try
						{
							
							// compute the remaining time to sleep
							time = System.currentTimeMillis() - time;
							long sleepTime = cycleTime - time;
							this.logger.info("Should sleep for: " + sleepTime);
							
							// sleep for the computed sleep time or continue
							// politely if the computed time is less than 1ms
							if (sleepTime > 1)
								Thread.sleep(sleepTime);
							else
								Thread.yield();
							
						}
						catch (InterruptedException e)
						{
							this.logger.warn("Interrupted");
						}
					}
				}
			}
		}
		
	}
	
}