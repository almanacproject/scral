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
package it.ismb.pert.pwal.managers.wastebin.lorry.device.data;

import java.util.Date;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Mass;
import javax.measure.unit.SI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class LorryObservationData
{
	/**
	 * <pre>
	 * {
	 * 	"weight": "0 kg", 
	 * 	"tagId": "44714", 
	 * 	"time": "2016-03-01 09:57:26", 
	 * 	"lon": "7.69052", 
	 * 	"pointId": "4134556", 
	 * 	"totalWeight": "0 kg", 
	 * 	"cycleNumber": "None", 
	 * 	"deviceId": "AMT-NE419", 
	 * 	"operationType": "D", 
	 * 	"wasteType": "1", 
	 * 	"lat": "45.11881", 
	 * 	"side": "DX"
	 * }
	 * </pre>
	 */
	
	// the weight of the waste collected by the lorry in the current operation
	private DecimalMeasure<Mass> weight;
	
	// the id of the lorry tag (always the same)
	@JsonPropertyDescription("The id of the lorry tag, always the same for the same lorry")
	@JsonProperty(value = "tagId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String tagId;
	
	// the datapoint time stamp
	@JsonPropertyDescription("The timestamp of waste collection in the format dd.MM.YYYY hh:mm:ss")
	@JsonProperty(value = "time")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Date time;
	
	// the lorry latitude
	@JsonPropertyDescription("The lorry position latitude")
	@JsonProperty(value = "lat")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private double lat;
	
	// the lorry longitude
	@JsonPropertyDescription("The lorry position latitude")
	@JsonProperty(value = "lon")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private double lon;
	
	// the lorry datapoint id
	@JsonPropertyDescription("The lorry datapoint id")
	@JsonProperty(value = "pointId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String pointId;
	
	// the incremental weight of the waste collected by the lorry in the current
	// track
	private DecimalMeasure<Mass> totalWeight;
	
	// the number of charge/discharge cycle of the lorry
	@JsonPropertyDescription("The number of charge/discharge cycle corresponding to this observation data")
	@JsonProperty(value = "cycleNumber")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private int cycleNumber;
	
	// the lorry device id, always the same
	@JsonPropertyDescription("The lorry device id, always the same for the same lorry")
	@JsonProperty(value = "deviceId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String deviceId;
	
	// the lorry operation type, can either be charge (C) or discharge (D)
	@JsonPropertyDescription("The type of operations performed by the lorry, either charge or discharge")
	@JsonProperty(value = "operationType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private LorryOperationType operationType;
	
	// the lorry waste type
	@JsonPropertyDescription("The waste type handled by the lorry")
	@JsonProperty(value = "wasteType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private LorryWasteType wasteType;
	
	// the side on which the operation occurs, either SX or DX
	@JsonPropertyDescription("The side on which charge/discharge occurs")
	@JsonProperty(value = "side")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String side;
	
	/**
	 * 
	 */
	public LorryObservationData()
	{
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Returns the weight of the collected (or discharged) waste as a double
	 * number. The provide value is in kilograms. Adoption of pure numeric
	 * values for measures is discouraged, please refer to the
	 * {@link getWeightAsMeasure} method, instead.
	 * 
	 * @return the weight
	 */
	@JsonProperty(value = "weight")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public double getWeight()
	{
		return weight.doubleValue(SI.KILOGRAM);
	}
	
	/**
	 * Returns the weight of the collected (or discharged) waste as a
	 * {@link DecimalMeasure} instance hosting both the weight value and unit of
	 * measure. The weight as a {@link DecimalMeasure}<{@link Mass}>
	 * 
	 * @return The weight of collected / discharged waste.
	 */
	@JsonIgnore
	public DecimalMeasure<Mass> getWeightAsMeasure()
	{
		return weight;
	}
	
	/**
	 * Sets the weight of the collected (or discharged) waste as a
	 * {@link DecimalMeasure} instance hosting both the weight value and unit of
	 * measure.
	 * 
	 * @param weight
	 *            the weight to set
	 */
	public void setWeightAsMeasure(DecimalMeasure<Mass> weight)
	{
		this.weight = weight;
	}
	
	/**
	 * Sets the weight of the collected (or discharged) waste as a
	 * {@link String} instance hosting both the weight value and unit of
	 * measure.
	 * 
	 * @param weight
	 *            the weight to set
	 */
	@JsonProperty(value = "weight")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setWeight(String weight)
	{
		// try to parse the weight
		this.weight = DecimalMeasure.valueOf(weight);
	}
	
	/**
	 * Gets the tagId associated to the lorry collecting the waste in the
	 * ALMANAC pilot.
	 * 
	 * @return the tagId
	 */
	@JsonProperty(value = "tagId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public String getTagId()
	{
		return tagId;
	}
	
	/**
	 * Sets the tagId associated to the lorry collecting the waste in the
	 * ALMANAC pilot.
	 * 
	 * @param tagId
	 *            the tagId to set
	 */
	@JsonProperty(value = "tagId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setTagId(String tagId)
	{
		this.tagId = tagId;
	}
	
	/**
	 * Gets the time at which this UEI data has been generated
	 * 
	 * @return the time as a {@link Date} instance.
	 */
	@JsonProperty(value = "time")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public Date getTime()
	{
		return time;
	}
	
	/**
	 * Sets the time at which this UEI data has been generated
	 * 
	 * @param time
	 *            the time to set
	 */
	@JsonProperty(value = "time")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setTime(Date time)
	{
		this.time = time;
	}
	
	/**
	 * Gets the latitude of the geographic point at which the lorry was located
	 * when collecting the waste described by this observation
	 * 
	 * @return the latitude as a double
	 */
	@JsonProperty(value = "lat")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public double getLat()
	{
		return lat;
	}
	
	/**
	 * Sets the latitude of the geographic point at which the lorry was located
	 * when collecting the waste described by this observation
	 * 
	 * @param lat
	 *            the latitude to set, as double.
	 */
	@JsonProperty(value = "lat")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setLat(double lat)
	{
		this.lat = lat;
	}
	
	/**
	 * Gets the longitude of the geographic point at which the lorry was located
	 * when collecting the waste described by this observation
	 * 
	 * @return the longitude as double
	 */
	@JsonProperty(value = "lon")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public double getLon()
	{
		return lon;
	}
	
	/**
	 * Sets the longitude of the geographic point at which the lorry was located
	 * when collecting the waste described by this observation
	 * 
	 * @param longitude
	 *            the longitude to set
	 */
	@JsonProperty(value = "lon")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setLon(double lon)
	{
		this.lon = lon;
	}
	
	/**
	 * Returns the unique id (typically an incremental number, but we do not
	 * have any guarantee that it will always remain an integer) of the data
	 * point represented by this {@link LorryObservationData} instance
	 * 
	 * @return the pointId as a String.
	 */
	@JsonProperty(value = "pointId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public String getPointId()
	{
		return pointId;
	}
	
	/**
	 * Sets the unique id (typically an incremental number, but we do not have
	 * any guarantee that it will always remain an integer) of the data point
	 * represented by this {@link LorryObservationData} instance
	 * 
	 * @param pointId
	 *            the pointId to set
	 */
	@JsonProperty(value = "pointId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setPointId(String pointId)
	{
		this.pointId = pointId;
	}
	
	/**
	 * Provides the total weight loaded by the truck from the collection start
	 * up to this collection point. Although this should be provided by the
	 * truck itself, actually it is computed by the ALMANAC data dump script for
	 * the lorry involved in the Turin pilot.
	 * 
	 * @return the totalWeight as a Measure
	 */
	@JsonIgnore
	public DecimalMeasure<Mass> getTotalWeightAsMeasure()
	{
		return totalWeight;
	}
	
	/**
	 * Provides the total weight (in kg) loaded by the truck from the collection
	 * start up to this collection point. Although this should be provided by
	 * the truck itself, actually it is computed by the ALMANAC data dump script
	 * for the lorry involved in the Turin pilot.
	 * 
	 * @return
	 */
	@JsonProperty(value = "totalWeight")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public double getTotalWeight()
	{
		return this.totalWeight.doubleValue(SI.KILOGRAM);
	}
	
	/**
	 * Sets the total weight (in kg) loaded by the truck from the collection
	 * start up to this collection point.
	 * 
	 * @param totalWeight
	 *            the totalWeight to set, as a string containing the value and
	 *            the unit of measure, e.g., "1074.32 kg"
	 */
	@JsonProperty(value = "totalWeight")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setTotalWeight(String totalWeight)
	{
		this.totalWeight = DecimalMeasure.valueOf(totalWeight);
	}
	
	/**
	 * Provides the number of operating cycles, i.e. operations done by the
	 * lorry from the collection start, up to this collection point. Can be used
	 * to detect collection tracks.
	 * 
	 * @return the cycleNumber
	 */
	@JsonProperty(value = "cycleNumber")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public int getCycleNumber()
	{
		return cycleNumber;
	}
	
	/**
	 * Sets the number of operating cycles, i.e. operations done by the lorry
	 * from the collection start, up to this collection point. Can be used to
	 * detect collection tracks.
	 * 
	 * @param cycleNumber
	 *            the cycleNumber to set
	 */
	@JsonProperty(value = "cycleNumber")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setCycleNumber(int cycleNumber)
	{
		this.cycleNumber = cycleNumber;
	}
	
	/**
	 * Gets the id associated to the device capturing data on the lorry, this is
	 * typically fixed for a single lorry.
	 * 
	 * @return the deviceId
	 */
	@JsonProperty(value = "deviceId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public String getDeviceId()
	{
		return deviceId;
	}
	
	/**
	 * Sets the id associated to the device capturing data on the lorry, this is
	 * typically fixed for a single lorry.
	 * 
	 * @param deviceId
	 *            the deviceId to set
	 */
	@JsonProperty(value = "deviceId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}
	
	/**
	 * Gets the type of operation performed by the lorry, either charge or discharge.
	 * @return the operationType
	 */
	@JsonProperty(value = "operationType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public LorryOperationType getOperationType()
	{
		return operationType;
	}
	
	/**
	 * Sets the type of operation performed by the lorry, either charge or discharge.
	 * @param operationType
	 *            the operationType to set
	 */
	public void setOperationType(LorryOperationType operationType)
	{
		this.operationType = operationType;
	}
	
	/**
	 * the type of operation performed by the lorry, either charge or discharge.
	 * @param operationType
	 *            the operationType to set, as a String
	 */
	@JsonPropertyDescription("The type of operations performed by the lorry, either charge or discharge")
	@JsonProperty(value = "operationType")
	public void setOperationType(String operationType)
	{
		// parse the value
		if (operationType.equalsIgnoreCase("c"))
			this.operationType = LorryOperationType.CHARGE;
		else if (operationType.equalsIgnoreCase("d"))
			this.operationType = LorryOperationType.DISCHARGE;
	}
	
	/**
	 * Get the type of waste handled by the lorry.
	 * @return the wasteType
	 */
	@JsonProperty(value = "wasteType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public LorryWasteType getWasteType()
	{
		return wasteType;
	}
	
	/**
	 * Sets the type of waste handled by the lorry.
	 * @param wasteType
	 *            the wasteType to set
	 */
	public void setWasteType(LorryWasteType wasteType)
	{
		this.wasteType = wasteType;
	}
	
	/**
	 * Sets the type of waste handled by the lorry.
	 * @param type
	 */
	@JsonProperty(value = "wasteType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setWasteType(int type) 
	{
		LorryWasteType.fromIntType(type);
	}
	
	/**
	 * Gets the side of the lorry on which operation occurred, DX or SX.
	 * @return the side
	 */
	@JsonProperty(value = "side")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public String getSide()
	{
		return side;
	}
	
	/**
	 * Sets the side of the lorry on which operation occurred, DX or SX.
	 * @param side
	 *            the side to set
	 */
	@JsonProperty(value = "side")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public void setSide(String side)
	{
		this.side = side;
	}
	
}
