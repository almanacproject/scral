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
package it.ismb.pert.pwal.managers.wastebin.uei.device.data;

import java.util.Date;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class UeiData
{
	/**
	 * <pre>
	 * {
	 * 		"timestamp": "2016-02-07T20:52:48+00:00",
	 * 		"type":"",
	 * 		"fillLevel": "89 %",
	 * 		"batteryLevel": "11.6 V",
	 * 		"id":"RSU 1"
	 * 		"address":"Via Conte Verde",
	 * 		"wasteType":"RSU",
	 * 		"transactionType":"2",
	 * 		"user":{
	 * 			"name":"Pippo",
	 * 			"surname":"Pluto",
	 * 			"title":"Ph.D",
	 * 			"tagId":"ldfwkuhgwdkjhr",
	 * 			"tagDescription":"hwdfjhdjhdfjhgv wdfjhbfw wfejhefw wefhbfrw"
	 * 		}
	 * }
	 * </pre>
	 */
	
	// the datapoint time stamp
	@JsonPropertyDescription("The timestamp of waste disposal in the format dd.MM.YYYY hh:mm:ss")
	@JsonProperty(value = "timestamp")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Date timestamp;
	
	// the user type?? TODO: check what "tipo" means in the UEI data
	@JsonPropertyDescription("Currently unkown/unused")
	@JsonProperty(value = "type")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String type;
	
	// the fill level in percentage
	@JsonPropertyDescription("The fill level in percentage")
	@JsonProperty(value = "fillLevel")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private DecimalMeasure<Dimensionless> level;
	
	// the battery level in V
	@JsonPropertyDescription("The battery level in voltage")
	@JsonProperty(value = "batteryLevel")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private DecimalMeasure<ElectricPotential> battery;
	
	// the UEI id
	@JsonPropertyDescription("The UEI unique identifier")
	@JsonProperty(value = "id")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String id;
	
	// the UEI address
	@JsonPropertyDescription("The UEI street address")
	@JsonProperty(value = "address")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String address;
	
	// the UEI garbage type
	@JsonPropertyDescription("The UEI waste type")
	@JsonProperty(value = "wasteType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String wasteType;
	
	// transaction type TODO: check what it means
	@JsonPropertyDescription("The type of transaction (currently unused/unknown)")
	@JsonProperty(value = "transactionType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private int transactionType;
	
	@JsonPropertyDescription("The user disposing the waste, if available")
	@JsonProperty(value = "user")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private PersonData userData;
	
	/**
	 * 
	 */
	public UeiData()
	{
		// TODO Auto-generated constructor stub
	}
	
	

	/**
	 * @param timestamp
	 * @param type
	 * @param level
	 * @param battery
	 * @param id
	 * @param address
	 * @param wasteType
	 * @param transactionType
	 * @param userData
	 */
	public UeiData(Date timestamp, String type, DecimalMeasure<Dimensionless> level,
			DecimalMeasure<ElectricPotential> battery, String id, String address, String wasteType,
			int transactionType, PersonData userData)
	{
		this.timestamp = timestamp;
		this.type = type;
		this.level = level;
		this.battery = battery;
		this.id = id;
		this.address = address;
		this.wasteType = wasteType;
		this.transactionType = transactionType;
		this.userData = userData;
	}



	/**
	 * Gets the timestamp at which this UEI data has been generated
	 * @return the timestamp as a {@link Date} instance.
	 */
	@JsonPropertyDescription("The timestamp of waste disposal in the format dd.MM.YYYY hh:mm:ss")
	@JsonProperty(value = "timestamp")
	public Date getTimestamp()
	{
		return timestamp;
	}

	/**
	 * Sets the timestamp at which this UEI data has been generated
	 * @param timestamp the timestamp as a {@link Date} instance.
	 */
	@JsonPropertyDescription("The timestamp of waste disposal in the format dd.MM.YYYY hh:mm:ss")
	@JsonProperty(value = "timestamp")
	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * Gets the type of this UEI data. Currently there is no notion about the actual meaning of such parameter which seldom appears.
	 * @return the type The type of UEI data as {@link String}
	 */
	@JsonPropertyDescription("Currently unkown/unused")
	@JsonProperty(value = "type")
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type of this UEI data. Currently there is no notion about the actual meaning of such parameter which seldom appears.
	 * @param type the type to set as {@link String}
	 */
	@JsonPropertyDescription("Currently unkown/unused")
	@JsonProperty(value = "type")
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Gets the fill level of the UEI in percentage
	 * @return The level as a {@link Dimensionless} {@link DecimalMeasure}.
	 */
	public DecimalMeasure<Dimensionless> getLevelAsMeasure()
	{
		return level;
	}
	
	/**
	 * Gets the fill level of the UEI in percentage
	 * @return The level as a {@link String} including the value and the unit of measure
	 */
	@JsonPropertyDescription("The fill level in percentage")
	@JsonProperty(value = "fillLevel")
	public String getLevel()
	{
		return level.toString();
	}

	/**
	 * Sets the fill level of the UEI in percentage
	 * @param level The level of the UEI as a {@link Dimensionless}{@link DecimalMeasure}.
	 */
	public void setLevelAsMeasure(DecimalMeasure<Dimensionless> level)
	{
		this.level = level;
	}
	
	/**
	 * Sets the fill level of the UEI in percentage
	 * @param level The level of the UEI as a {@link String} including the value and the unit of measure
	 */
	@JsonPropertyDescription("The fill level in percentage")
	@JsonProperty(value = "fillLevel")
	public void setLevel(String level)
	{
		//TODO add alias for %
		this.level = DecimalMeasure.valueOf(level);
	}

	/**
	 * Gets the battery voltage as a {@link DecimalMeasure}<{@link ElectricPotential}>.
	 * @return the battery The battery voltage.
	 */
	public DecimalMeasure<ElectricPotential> getBatteryAsMeasure()
	{
		return battery;
	}
	
	/**
	 * Gets the battery voltage as a {@link String} including the value and the unit of measure (V).
	 * @return The battery voltage
	 */
	@JsonPropertyDescription("The battery level in voltage")
	@JsonProperty(value = "batteryLevel")
	public String getBattery()
	{
		return this.battery.toString();
	}

	/**
	 * Sets the battery voltage as a {@link DecimalMeasure}<{@link ElectricPotential}>.
	 * @param battery the battery voltage
	 */
	public void setBatteryAsMeasure(DecimalMeasure<ElectricPotential> battery)
	{
		this.battery = battery;
	}
	
	/**
	 * Sets the battery voltage as a {@link String} including the value and the unit of measure (V).
	 * @param battery The battery voltage
	 */
	@JsonPropertyDescription("The battery level in voltage")
	@JsonProperty(value = "batteryLevel")
	public void setBattery(String battery)
	{
		this.battery = DecimalMeasure.valueOf(battery);
	}

	/**
	 * Gets the unique id of the UEI sending this data
	 * @return The id The unique id of the UEI as a {@link String}
	 */
	@JsonPropertyDescription("The UEI unique identifier")
	@JsonProperty(value = "id")
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the id of the UEI tom which this data is associated
	 * @param id The unique id of the UEI associated to this data
	 */
	@JsonPropertyDescription("The UEI unique identifier")
	@JsonProperty(value = "id")
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the surface mail address of the UEI sending this data
	 * @return the address The surface mail address of the UEI sending this data, as a {@link String}
	 */
	@JsonPropertyDescription("The UEI street address")
	@JsonProperty(value = "address")
	public String getAddress()
	{
		return address;
	}

	/**
	 * Sets the surface mail address of the UEI 
	 * @param address The surface mail address of the UEI associated to this data
	 */
	@JsonPropertyDescription("The UEI street address")
	@JsonProperty(value = "address")
	public void setAddress(String address)
	{
		this.address = address;
	}

	/**
	 * Gets the type of waste to which the this data refers, i.e., the type of waste collected by the UEI sending this data.
	 * @return The waste type as a {@link String}
	 */
	@JsonPropertyDescription("The UEI waste type")
	@JsonProperty(value = "wasteType")
	public String getWasteType()
	{
		return wasteType;
	}

	/**
	 * Sets the type of waste to which the this data refers, i.e., the type of waste collected by the UEI sending this data.
	 * @param wasteType The waste type as a {@link String}
	 */
	@JsonPropertyDescription("The UEI waste type")
	@JsonProperty(value = "wasteType")
	public void setWasteType(String wasteType)
	{
		this.wasteType = wasteType;
	}

	/**
	 * Gets the type of transaction to which this data corresponds
	 * @return The type of transaction as {@link String}
	 */
	@JsonPropertyDescription("The type of transaction (currently unused/unknown)")
	@JsonProperty(value = "transactionType")
	public int getTransactionType()
	{
		return transactionType;
	}

	/**
	 * Sets the type of transaction to which this data corresponds
	 * @param transactionType The type of transaction as {@link String}
	 */
	@JsonPropertyDescription("The type of transaction (currently unused/unknown)")
	@JsonProperty(value = "transactionType")
	public void setTransactionType(int transactionType)
	{
		this.transactionType = transactionType;
	}

	/**
	 * Gets data about the user performing the operation whose outcome is reported in this UEI data object.
	 * @return the userData The data available about the user performing the waste disposal described by this UEI data object.
	 */
	@JsonPropertyDescription("The user disposing the waste, if available")
	@JsonProperty(value = "user")
	public PersonData getUserData()
	{
		return userData;
	}

	/**
	 * Sets data about the user performing the operation whose outcome is reported in this UEI data object.
	 * @param userData The data available about the user performing the waste disposal described by this UEI data object.
	 */
	@JsonPropertyDescription("The user disposing the waste, if available")
	@JsonProperty(value = "user")
	public void setUserData(PersonData userData)
	{
		this.userData = userData;
	}
	
	
	
}
