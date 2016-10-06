/*
 * PWAL - UEI Manager
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
package it.ismb.pert.pwal.managers.wastebin.uei;

import it.ismb.pert.pwal.managers.wastebin.uei.device.AbstractUei;
import it.ismb.pert.pwal.managers.wastebin.uei.device.GlassCanWasteUei;
import it.ismb.pert.pwal.managers.wastebin.uei.device.NotRecyclableWasteUei;
import it.ismb.pert.pwal.managers.wastebin.uei.device.OrganicWasteUei;
import it.ismb.pert.pwal.managers.wastebin.uei.device.PlasticWasteUei;
import it.ismb.pert.pwal.managers.wastebin.uei.device.data.UeiData;
import it.ismb.pert.pwal.managers.wastebin.uei.device.descriptor.UeiDescriptor;
import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class UeiManager extends DevicesManager
{
	// a map of currently valid UEIs, with the Uei id as key
	private Map<String, UeiDescriptor> validUeis;
	
	// the running flag to mask operations when the manager is stopped
	// TODO: check if actually needed
	private boolean running;
	
	// the class logger
	private Logger logger;
	
	/**
	 * The class constructor, creates a new manager instance handling the given
	 * set of UEIs. Any UEI not matching with the given set (which might in
	 * future be synchronized with UEIs registered in any external catalog
	 * service) posting data to this manager will be ignored. The same for UEIs
	 * not adopting the needed security tokens in case the service is secured
	 * with keycloak.
	 * 
	 * @param ueis
	 *            The list of "enabled" UEIs
	 */
	public UeiManager(Map<String, UeiDescriptor> ueis)
	{
		// call the super class constructor
		super();
		
		this.initCommon(ueis, false);
	}
	
	/**
	 * The class constructor, creates a new manager instance handling the given
	 * set of UEIs. Any UEI not matching with the given set (which might in
	 * future be synchronized with UEIs registered in any external catalog
	 * service) posting data to this manager will be ignored. The same for UEIs
	 * not adopting the needed security tokens in case the service is secured
	 * with keycloak.
	 * 
	 * @param ueis
	 *            The list of "enabled" UEIs
	 * @param autostart
	 *            true if the manager shall start automatically, false otherwise
	 */
	public UeiManager(Map<String, UeiDescriptor> ueis, boolean autostart)
	{
		// call the super class constructor
		super();
		
		this.initCommon(ueis, autostart);
	}
	
	private void initCommon(Map<String, UeiDescriptor> ueis, boolean autostart)
	{
		// build the logger
		this.logger = LoggerFactory.getLogger(UeiManager.class);
		
		// store the UEIs if any
		if (ueis != null)
		{
			this.validUeis = ueis;
		}
		else
		{
			this.validUeis = new HashMap<String, UeiDescriptor>();
		}
		
		// set the manager at not running
		this.running = false;
		
		this.autostart = autostart;
	}
	
	/**
	 * Weird life cycle handling forced by the awful PWAL design.
	 */
	public void run()
	{
		// set the runningflag at true
		this.running = true;
		
		// WARNING this cycle is almost useless, it must implemented only to let
		// the weird PWAL implementation of Device Managers to work.
		while (!t.isInterrupted())
		{
			try
			{
				// create the UEI devices corresponding to the descriptors
				for (UeiDescriptor desc : this.validUeis.values())
				{
					if (!this.devicesDiscovered.containsKey(desc.getId()))
					{
						switch (desc.getGarbageType())
						{
							case DryRubbish:
							{
								// create the device array list
								ArrayList<Device> ueiDevices = new ArrayList<Device>();
								
								// add the right UEI
								NotRecyclableWasteUei uei = new NotRecyclableWasteUei(desc);
								ueiDevices.add(uei);
								
								// store in the set of discovered devices
								this.devicesDiscovered.put(desc.getId(), ueiDevices);
								this.notifyDeviceListeners(uei);
								
								break;
							}
							case GlassOrAluminumRubbish:
							{
								// create the device array list
								ArrayList<Device> ueiDevices = new ArrayList<Device>();
								
								// add the right UEI
								GlassCanWasteUei uei = new GlassCanWasteUei(desc);
								ueiDevices.add(uei);
								
								// store in the set of discovered devices
								this.devicesDiscovered.put(desc.getId(), ueiDevices);
								this.notifyDeviceListeners(uei);
								
								break;
							}
							case PlasticRubbish:
							{
								// create the device array list
								ArrayList<Device> ueiDevices = new ArrayList<Device>();
								
								// add the right UEI
								PlasticWasteUei uei = new PlasticWasteUei(desc);
								ueiDevices.add(uei);
								
								// store in the set of discovered devices
								this.devicesDiscovered.put(desc.getId(), ueiDevices);
								this.notifyDeviceListeners(uei);
								
								break;
							}
							case OrganicRubbish:
							{
								// create the device array list
								ArrayList<Device> ueiDevices = new ArrayList<Device>();
								
								// add the right UEI
								OrganicWasteUei uei = new OrganicWasteUei(desc);
								ueiDevices.add(uei);
								
								// store in the set of discovered devices
								this.devicesDiscovered.put(desc.getId(), ueiDevices);
								this.notifyDeviceListeners(uei);
								
								break;
							}
							
							default:
							{
								break;
							}
						}
					}
				}
				// TODO: check from the catalogue if there are any new UEI to
				// handle
				
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				log.error("Exception: ", e);
				t.interrupt();
			}
		}
		
		// set the running flag at false
		this.running = false;
	}
	
	private void notifyDeviceListeners(AbstractUei uei)
	{
		for (DeviceListener l : deviceListener)
		{
			l.notifyDeviceAdded(uei);
		}
	}
	
	@Override
	public String getNetworkType()
	{
		// return the kind of network implemented by this manager
		return DeviceNetworkType.UEI;
	}
	
	/**
	 * Checks if the given UEI id corresponds to one of the vlid UEIs
	 * 
	 * @param id
	 *            The UEI id to check.
	 * @return true if it corresponds to a valid UEI, false otherwise.
	 */
	public synchronized boolean isValid(String id)
	{
		return this.validUeis.containsKey(id);
	}
	
	/**
	 * @return the running
	 */
	public boolean isRunning()
	{
		return running;
	}
	
	public void updateDevice(UeiData ueiData)
	{
		// get the device
		// horrible, but only one uei per id is allowed.
		AbstractUei uei = (AbstractUei) this.devicesDiscovered.get(ueiData.getId()).get(0);
		
		// update
		uei.updateStatus(ueiData);
		
		// push over MQTT
		HashMap<String, Object> valuesMap = new HashMap<String, Object>();
		valuesMap.put("getTemperature", uei.getTemperature());
		valuesMap.put("getLevel", uei.getLevel());
		valuesMap.put("getBattery", uei.getBattery());
		PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(uei.getUpdatedAt(), uei.getPwalId(),
				uei.getExpiresAt(), valuesMap, uei);
		logger.info("Device {} is publishing a new data available event on topic: {}", uei.getPwalId(),
				uei.getEventPublisher().getTopics());
		uei.getEventPublisher().publish(event);
	}
	
}
