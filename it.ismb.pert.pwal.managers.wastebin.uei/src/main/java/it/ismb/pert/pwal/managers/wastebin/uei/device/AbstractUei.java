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
package it.ismb.pert.pwal.managers.wastebin.uei.device;

import it.ismb.pert.pwal.managers.wastebin.uei.device.data.UeiData;
import it.ismb.pert.pwal.managers.wastebin.uei.device.descriptor.UeiDescriptor;
import it.ismb.pertlab.pwal.api.devices.model.BatteryLevelSensor;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.WasteBin;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;
import it.ismb.pertlab.smartcity.api.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public abstract class AbstractUei implements WasteBin, BatteryLevelSensor
{
	// the descriptor associated to this UEI
	private UeiDescriptor descriptor;
	
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
	
	// the current temperature
	private DecimalMeasure<Temperature> temperature;
	
	// the current level
	private DecimalMeasure<Dimensionless> level;
	
	// the current battery
	private DecimalMeasure<ElectricPotential> battery;
	
	// the event publisher used to send events in the inner event bus
	public PWALEventPublisher eventPublisher;
	
	/**
	 * The abstract implementation of a UEI
	 */
	public AbstractUei(UeiDescriptor descriptor)
	{
		// store the descriptor
		this.descriptor = descriptor;
		
		// initialize the network type
		this.networkType = DeviceNetworkType.UEI;
		
		// build the event publisher
		this.eventPublisher = new PWALEventPublisher();
	}
	
	/**
	 * Handles updates
	 * 
	 * @param data
	 *            The UeiData object representing the status update for this UEI
	 */
	public void updateStatus(UeiData data)
	{
		// update the UEI level
		this.level = data.getLevelAsMeasure();
		
		// update the UEI battery
		this.battery = data.getBatteryAsMeasure();
		
		// update the UEI status timestamp
		this.lastUpdate = data.getTimestamp();
		
		// ignore user data
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.updatedAt = df.format(this.lastUpdate);
	}
	
	/**
	 * @return the descriptor
	 */
	@JsonIgnore
	public UeiDescriptor getDescriptor()
	{
		return descriptor;
	}
	
	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate()
	{
		return lastUpdate;
	}
	
	/**
	 * @return the battery
	 */
	@JsonIgnore
	@SemanticModel(value = "http://almanac-project.eu/ontologies/smartcity.owl#BatteryLevelState", name = "class")
	public DecimalMeasure<ElectricPotential> getBatteryAsMeasure()
	{
		if(this.battery==null)
			return DecimalMeasure.valueOf("0.0 "+SI.VOLT.toString());
		return battery;
	}
	
	@Override
	@SemanticModel(value = "http://almanac-project.eu/ontologies/smartcity.owl#BatteryLevelState", name = "class")
	public Double getBattery()
	{
		//initially not valid
		Double battery = Double.NaN;
		
		//if not null convert from measure
		if(this.battery!=null)
			battery =new Double(this.battery.doubleValue(SI.VOLT));
		
		//return the current battery level
		return battery;
	}
	
	
	
	/**
	 * @return the eventPublisher
	 */
	@JsonIgnore
	public PWALEventPublisher getEventPublisher()
	{
		return eventPublisher;
	}
	
	/***********************************************************
	 * WasteBin interface implementation
	 **********************************************************/
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.model.Thermometer#getTemperature()
	 */
	@SemanticModel(name = "class", value = "http://almanac-project.eu/ontologies/smartcity.owl#TemperatureState")
	public Double getTemperature()
	{
		//initially not valid
		Double t = Double.NaN;
		
		//if not null convert from measure
		if (this.temperature != null)
			t = this.temperature.doubleValue(SI.CELSIUS);
		
		//return the temperature
		return t;
	}
	
	@JsonIgnore
	@SemanticModel(name = "class", value = "http://almanac-project.eu/ontologies/smartcity.owl#TemperatureState")
	public DecimalMeasure<Temperature> getTemperatureAsMeasure()
	{
		if(this.temperature==null)
			return DecimalMeasure.valueOf("-273.15 "+SI.CELSIUS.toString());
		return this.temperature;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getPwalId()
	 */
	public String getPwalId()
	{
		return this.pwalId;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setPwalId(java.lang
	 * .String)
	 */
	public void setPwalId(String pwalId)
	{
		if ((pwalId != null) && (!pwalId.isEmpty()))
		{
			//store the pwal id
			this.pwalId = pwalId;
			
			// generate the corresponding device topic
			this.eventPublisher.setTopics(new String[] { PWALTopicsUtility.createNewDataFromDeviceTopic(
					DeviceNetworkType.UEI, this.getPwalId()) });
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getId()
	 */
	public String getId()
	{
		return this.descriptor.getId();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setId(java.lang.String
	 * )
	 */
	public void setId(String id)
	{
		// Uei IDs cannot be changed
		// do nothing
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getNetworkType()
	 */
	public String getNetworkType()
	{
		// TODO Auto-generated method stub
		return this.networkType;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getUpdatedAt()
	 */
	public String getUpdatedAt()
	{
		return this.updatedAt;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setUpdatedAt(java.
	 * lang.String)
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getExpiresAt()
	 */
	public String getExpiresAt()
	{
		return this.expiresAt;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setExpiresAt(java.
	 * lang.String)
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getLocation()
	 */
	public Location getLocation()
	{
		// the location of the UEI in the PWAL-specific notation
		// Thanks again to the architects WISDOM
		Location ueiLocation = null;
		
		// get the actual location of the uei
		GeoPoint location = this.descriptor.getLocation();
		
		// if the uei has a location
		if (location != null)
		{
			// convert location representations
			ueiLocation = new Location();
			
			ueiLocation.setLat(location.getLatitude());
			ueiLocation.setLon(location.getLongitude());
		}
		
		// return the found location
		return ueiLocation;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setLocation(it.ismb
	 * .pertlab.pwal.api.devices.model.Location)
	 */
	public void setLocation(Location location)
	{
		// cannot be set independently from the descriptor
		// do nothing
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getUnit()
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setUnit(it.ismb.pertlab
	 * .pwal.api.devices.model.Unit)
	 */
	public void setUnit(Unit unit)
	{
		// Nobody could set something useless!!!
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.model.FillLevel#getDepth()
	 */
	public Integer getDepth()
	{
		// not depth supported
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.model.FillLevel#getLevel()
	 */
	@SemanticModel(value = "http://almanac-project.eu/ontologies/smartcity.owl#FillLevelState", name = "class")
	public Double getLevel()
	{
		Double level = Double.NaN;
		
		if(this.level!=null)
			level = this.level.doubleValue(Dimensionless.UNIT);

		return level;
	}
	
	@JsonIgnore
	@SemanticModel(value = "http://almanac-project.eu/ontologies/smartcity.owl#FillLevelState", name = "class")
	public DecimalMeasure<Dimensionless> getLevelAsMeasure()
	{
		if(this.level == null)
			return DecimalMeasure.valueOf("0.0");
		return this.level;
	}
	
	/***********************************************
	 * BIN TYPE PATCH
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
