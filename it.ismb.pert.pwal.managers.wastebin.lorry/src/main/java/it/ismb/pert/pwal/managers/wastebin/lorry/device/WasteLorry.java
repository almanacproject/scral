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
package it.ismb.pert.pwal.managers.wastebin.lorry.device;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import it.ismb.pert.pwal.managers.wastebin.lorry.device.data.LorryObservationData;
import it.ismb.pert.pwal.managers.wastebin.lorry.device.descriptor.LorryDescriptor;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.WasteCollectionLorry;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.smartcity.api.GeoPoint;

/**
 * A device roughly representing the waste collection lorry employed in the
 * ALMANAC pilot, for what concerns sensor data and position.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WasteLorry implements WasteCollectionLorry
{
	// the descriptor associated to this UEI
	private LorryDescriptor descriptor;
	
	// the pwalId
	private String pwalId;
	
	// the network type
	private String networkType;
	
	// the last update time as a String UTC timestamp
	// really WEIRD!! thanks PWAL architects for ignoring anything regarding sw
	// design!
	private String updatedAt;
	
	// data expiration - never expires for UEIs :-D
	// really WEIRD!! thanks PWAL architects for ignoring anything regarding sw
	// design!
	private String expiresAt;
	
	// the only "wise one"
	private Date lastUpdate;
	
	// the event publisher used to send events in the inner event bus
	private PWALEventPublisher eventPublisher;
	
	// the lorry data
	private LorryObservationData currentData;
	
	// the lorry location
	private Location location;
	
	public WasteLorry(LorryDescriptor descriptor)
	{
		// store the descriptor
		this.descriptor = descriptor;
		
		// initialize the network type
		this.networkType = DeviceNetworkType.WASTELORRY;
		
		// build the event publisher
		this.eventPublisher = new PWALEventPublisher();
		
		// initial location
		GeoPoint location = this.descriptor.getLocation();
		
		// if the uei has a location
		if (location != null)
		{
			// convert location representations
			this.location = new Location();
			
			this.location.setLat(location.getLatitude());
			this.location.setLon(location.getLongitude());
		}
	}
	
	/**
	 * Updates the status of this lorry with information contained in the given
	 * {@link LorryObservationData} received through REST
	 * 
	 * @param lorryData
	 */
	public void updateStatus(LorryObservationData lorryData)
	{
		// store the lorry data
		this.currentData = lorryData;
		
		// store the latest location of the lorry
		// this.location = new Location();
		this.location.setLon(this.currentData.getLon());
		this.location.setLat(this.currentData.getLat());
		
		// update the UEI status timestamp
		this.lastUpdate = lorryData.getTime();
		
		// ignore user data
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.updatedAt = df.format(this.lastUpdate);
		
	}
	
	/**
	 * Provides back the {@link LorryDescriptor} instance used to represent
	 * initial lorry configuration available in the pwal configuration file
	 * 
	 * @return the descriptor
	 */
	public LorryDescriptor getDescriptor()
	{
		return descriptor;
	}
	
	/**
	 * Sets the {@link LorryDescriptor} instance used to represent initial lorry
	 * configuration available in the pwal configuration file
	 * 
	 * @param descriptor
	 *            the descriptor to set
	 */
	public void setDescriptor(LorryDescriptor descriptor)
	{
		this.descriptor = descriptor;
	}
	
	/**
	 * Gets the Pwal-level id associated to this {@link WasteLorry} instance
	 * 
	 * @return the pwalId
	 */
	public String getPwalId()
	{
		return pwalId;
	}
	
	/**
	 * Sets the Pwal-level id associated to this {@link WasteLorry} instance
	 * 
	 * @param pwalId
	 *            the pwalId to set
	 */
	public void setPwalId(String pwalId)
	{
		if ((pwalId != null) && (!pwalId.isEmpty()))
		{
			// store the pwal id
			this.pwalId = pwalId;
			
			// generate the corresponding device topic
			this.eventPublisher.setTopics(new String[] {
					PWALTopicsUtility.createNewDataFromDeviceTopic(DeviceNetworkType.WASTELORRY, this.getPwalId()) });
		}
	}
	
	/**
	 * Gets the network identifier (type) associated to this {@link WasteLorry}
	 * instance.
	 * 
	 * @return the networkType
	 */
	public String getNetworkType()
	{
		return networkType;
	}
	
	/**
	 * Sets the network identifier (type) associated to this {@link WasteLorry}
	 * instance.
	 * 
	 * @param networkType
	 *            the networkType to set
	 */
	public void setNetworkType(String networkType)
	{
		this.networkType = networkType;
	}
	
	/**
	 * Provides back the time at which data referred to this waste collection
	 * lorry was updated, as a ISO8601 time string
	 * 
	 * @return the updatedAt
	 */
	public String getUpdatedAt()
	{
		return updatedAt;
	}
	
	/**
	 * Sets the time at which data referred to this waste collection lorry was
	 * updated, as a ISO8601 time string
	 * 
	 * @param updatedAt
	 *            the updatedAt to set
	 */
	public void setUpdatedAt(String updatedAt)
	{
		// WARNING!!! There is absolutely no warranty that this awful supposed
		// time stamp would not contain any WTF string!! Thanks again to PWAL
		// architects.
		if ((updatedAt != null) && (!updatedAt.isEmpty()))
		{
			this.updatedAt = updatedAt;
		}
	}
	
	/**
	 * Provides back the time at which currently stored data should be
	 * considered "expired", i.e., too old. Time is provided back as ISO8601
	 * string.
	 * 
	 * @return the expiresAt
	 */
	public String getExpiresAt()
	{
		return expiresAt;
	}
	
	/**
	 * Sets the time at which currently stored data should be considered
	 * "expired", i.e., too old. Time shall be provided as ISO8601 string.
	 * 
	 * @param expiresAt
	 *            the expiresAt to set
	 */
	public void setExpiresAt(String expiresAt)
	{
		// WARNING!!! There is absolutely no warranty that this awful supposed
		// time stamp would not contain any WTF string!! Thanks again to PWAL
		// architects.
		if ((expiresAt != null) && (!expiresAt.isEmpty()))
		{
			this.expiresAt = expiresAt;
		}
	}
	
	/**
	 * Get the time at which data stored in this {@link WasteLorry} instance was
	 * last updated
	 * 
	 * @return the lastUpdate, as a {@link Date} instance.
	 */
	public Date getLastUpdate()
	{
		return lastUpdate;
	}
	
	/**
	 * Sets the time at which data stored in this {@link WasteLorry} instance
	 * was last updated
	 * 
	 * @param lastUpdate
	 *            the lastUpdate to set as a {@link Date} instance.
	 */
	public void setLastUpdate(Date lastUpdate)
	{
		this.lastUpdate = lastUpdate;
	}
	
	/**
	 * Gets the event publisher exploited by this {@link WasteLorry} instance to
	 * publish data over the inner event bus TODO: check why devices shall
	 * depend on this, perhaps some other "interesting" design choice...
	 * 
	 * @return the eventPublisher
	 */
	public PWALEventPublisher getEventPublisher()
	{
		return eventPublisher;
	}
	
	/**
	 * Sets the event publisher exploited by this {@link WasteLorry} instance to
	 * publish data over the inner event bus TODO: check why devices shall
	 * depend on this, perhaps some other "interesting" design choice...
	 * 
	 * @param eventPublisher
	 *            the eventPublisher to set
	 */
	public void setEventPublisher(PWALEventPublisher eventPublisher)
	{
		this.eventPublisher = eventPublisher;
	}
	
	/*****************
	 * 
	 * Methods inherited from device
	 * 
	 *****************/
	
	/**
	 * Gets the network-level id of the device
	 */
	public String getId()
	{
		return this.descriptor.getId();
	}
	
	/**
	 * Sets the network-level id of device. Since for {@link WasteLorry} the
	 * network level id cannot be set by the SCRAL, this method does not perform
	 * any action.
	 */
	public void setId(String id)
	{
		// Lorry IDs cannot be changed
		// do nothing
	}
	
	/**
	 * Returns the type of device ({@ WasteCollectionLorry}) implemented by this
	 * {@link WasteLorry} instance
	 */
	public String getType()
	{
		// TODO Auto-generated method stub
		return "WasteCollectionLorry";
	}
	
	/**
	 * Returns the last known location of the lorry
	 */
	public Location getLocation()
	{
		return this.location;
	}
	
	/**
	 * Sets the last known location of the lorry
	 */
	public void setLocation(Location location)
	{
		// store the location
		if (location != null)
		{
			this.location = location;
		}
	}
	
	/**
	 * Completely useless as this {@link WasteLorry} handles complex data, each
	 * with its own unit of measure. Moreover handling units separately from
	 * relative values is a completely odd design choice inherited from the
	 * original Pwal design that should be fixed.
	 */
	public Unit getUnit()
	{
		// One unit for more than one sensed value, the WISDOM rules!!!
		// Hurray for PWAL architects!!! May god multiply your wise fingers!
		
		// describes the unit of measure according to the pWAL inner format.
		Unit u = new Unit();
		u.setSymbol("");
		u.setType("none");
		u.setValue("unknown");
		
		return u;
	}
	
	/**
	 * does nothing as the handled data is far more complex than what can be
	 * handled by this simple device model
	 */
	public void setUnit(Unit unit)
	{
		// TODO Auto-generated method stub
		// does nothing as the handled data is far more complex than what can be
		// handled by this simple device model
	}
	
	/**
	 * Gets the last data available about the waste collection lorry represented
	 * by this instance, represented as complex object (
	 * {@link LorryObservationData})
	 */
	public LorryObservationData getLorryData()
	{
		return this.currentData;
	}
	
	/***********************************************
	 * TYPE PATCH
	 * 
	 * TODO: move this in a more suitable place and tackle the problem generally
	 * if possible
	 * 
	 ***********************************************/
	public String getDeviceOntClass()
	{
		return "http://almanac-project.eu/ontologies/smartcity.owl#" + this.getType();
	}
	
}
