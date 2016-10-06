/*
 * PWAL - Lorry Data Manager
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
package it.ismb.pert.pwal.managers.wastebin.lorry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.ismb.pert.pwal.managers.wastebin.lorry.device.WasteLorry;
import it.ismb.pert.pwal.managers.wastebin.lorry.device.data.LorryObservationData;
import it.ismb.pert.pwal.managers.wastebin.lorry.device.descriptor.LorryDescriptor;
import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;

/**
 * A Device manager handling raw data collected by the Waste collection lorry
 * connected to the ALMANAC pilot. This is a very rough and dirt implementation
 * with several shortcuts on device/data representation. TODO: improve lorry
 * representation to generate more meaningful data streams.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class LorryManager extends DevicesManager
{
	// a map of currently valid Lorries, with the Lorry id as key
	private Map<String, LorryDescriptor> validLorries;
	
	// the running flag to mask operations when the manager is stopped
	// TODO: check if actually needed
	private boolean running;
	
	// the class logger
	private Logger logger;
	
	/**
	 * The class constructor, creates a new manager instance handling the given
	 * set of Lorries. Any Lorry not matching with the given set (which might in
	 * future be synchronized with Lorries registered in any external catalog
	 * service) posting data to this manager will be ignored. The same for
	 * Lorries not adopting the needed security tokens in case the service is
	 * secured with keycloak.
	 * 
	 * @param lorries
	 *            The list of "enabled" Lorries
	 */
	public LorryManager(Map<String, LorryDescriptor> lorries)
	{
		// call the super class constructor
		super();
		
		this.initCommon(lorries, false);
	}
	
	/**
	 * The class constructor, creates a new manager instance handling the given
	 * set of Lorries. Any Lorry not matching with the given set (which might in
	 * future be synchronized with Lorries registered in any external catalog
	 * service) posting data to this manager will be ignored. The same for
	 * Lorries not adopting the needed security tokens in case the service is
	 * secured with keycloak.
	 * 
	 * @param lorries
	 *            The list of "enabled" Lorries
	 * @param autostart
	 *            true if the manager shall start automatically, false otherwise
	 */
	public LorryManager(Map<String, LorryDescriptor> lorries, boolean autostart)
	{
		// call the super class constructor
		super();
		
		this.initCommon(lorries, autostart);
	}
	
	/**
	 * Performs the common initialization tasks such as creating needed instance
	 * variables, etc.
	 * 
	 * @param lorries
	 * @param autostart
	 */
	private void initCommon(Map<String, LorryDescriptor> lorries, boolean autostart)
	{
		// build the logger
		this.logger = LoggerFactory.getLogger(LorryManager.class);
		
		// store the Lorries if any
		if (lorries != null)
		{
			this.validLorries = lorries;
		}
		else
		{
			this.validLorries = new HashMap<String, LorryDescriptor>();
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
		// set the running flag at true
		this.running = true;
		
		// WARNING this cycle is almost useless, it must implemented only to let
		// the weird PWAL implementation of Device Managers to work.
		while (!t.isInterrupted())
		{
			try
			{
				// create the Lorry devices corresponding to the descriptors
				for (LorryDescriptor desc : this.validLorries.values())
				{
					if (!this.devicesDiscovered.containsKey(desc.getId()))
					{
						// create the device array list
						ArrayList<Device> lorryDevices = new ArrayList<Device>();
						
						// create the Lorry device
						WasteLorry lorry = new WasteLorry(desc);
						
						// add the lorry
						lorryDevices.add(lorry);
						
						// store in the set of discovered devices
						this.devicesDiscovered.put(desc.getId(), lorryDevices);
						this.notifyDeviceListeners(lorry);
					}
				}
				// TODO: check from the catalogue if there are any new Lorry to
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
	
	/**
	 * Notify listeners tha a new WasteLorry device has been added.
	 * 
	 * @param lorry
	 */
	private void notifyDeviceListeners(WasteLorry lorry)
	{
		for (DeviceListener l : deviceListener)
		{
			l.notifyDeviceAdded(lorry);
		}
	}
	
	@Override
	public String getNetworkType()
	{
		// return the kind of network implemented by this manager
		return DeviceNetworkType.WASTELORRY;
	}
	
	/**
	 * Checks if the given Lorry id corresponds to one of the valid Lorries
	 * 
	 * @param id
	 *            The Lorry id to check.
	 * @return true if it corresponds to a valid Lorry, false otherwise.
	 */
	public synchronized boolean isValid(String id)
	{
		return this.validLorries.containsKey(id);
	}
	
	/**
	 * Provides back the current status of the manager, which can either be
	 * running (true) or not (false)
	 * 
	 * @return the running status, as a boolean.
	 */
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Update the status of a single Waste Lorry instance with the data
	 * contained in the given {@link LorryObservationData} object.
	 * 
	 * @param lorryData
	 *            the lorry observation data to be used for updating the current
	 *            lorry status
	 */
	public void updateDevice(LorryObservationData lorryData)
	{
		// get the device
		// horrible, but only one Lorry per id is allowed.
		WasteLorry lorry = (WasteLorry) this.devicesDiscovered.get(lorryData.getDeviceId()).get(0);
		
		// update
		lorry.updateStatus(lorryData);
		
		// push over MQTT
		HashMap<String, Object> valuesMap = new HashMap<String, Object>();
		
		valuesMap.put("getLorryData", lorry.getLorryData());
		PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(lorry.getUpdatedAt(), lorry.getPwalId(),
				lorry.getExpiresAt(), valuesMap, lorry);
		logger.info("Device {} is publishing a new data available event on topic: {}", lorry.getPwalId(),
				lorry.getEventPublisher().getTopics());
		lorry.getEventPublisher().publish(event);
		
		// push lorry metadata update (TODO: do this properly)
		this.notifyDeviceListeners(lorry);
	}
}
