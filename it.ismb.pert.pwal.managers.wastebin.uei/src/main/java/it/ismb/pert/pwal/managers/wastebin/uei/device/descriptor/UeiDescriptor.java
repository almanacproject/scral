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
package it.ismb.pert.pwal.managers.wastebin.uei.device.descriptor;

import it.ismb.pertlab.smartcity.api.Garbage;
import it.ismb.pertlab.smartcity.api.GeoPoint;

/**
 * A class containing the relevant information about an Underground Ecological
 * Island, as those in ALMANAC. Instances of this class can either be generated
 * automatically or by configuration. The latter is the default for ALMANAC
 * Pilot UEI.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class UeiDescriptor
{
	// the UEI id
	// this maps to the so-called stazione field in the given csv file
	private String id;
	
	// the UEI address
	private String streetAddress;
	
	// the UEI waste type
	private Garbage garbageType;
	
	// the UEI Location
	private GeoPoint location;
	
	/**
	 * Empty class constructor respecting / supporting the bean instantiation
	 * pattern
	 */
	public UeiDescriptor()
	{
		// Intentionally left empty
	}
	
	/**
	 * Full class constructor, builds a new UEI descriptor given the needed
	 * parameters, i.e., where the bin is and what type of garbage it collects.
	 * For the sake of simplicity no reverse geocoding is performed here and both
	 * address and latitude, longitude shall be provided.
	 * 
	 * @param id The UEI id
	 * @param streetAddress The street address of the UEI
	 * @param garbageType The type of garbage collected by the UEI
	 * @param location The latitude and longitude of the UEI
	 */
	public UeiDescriptor(String id, String streetAddress, Garbage garbageType, GeoPoint location)
	{
		this.id = id;
		this.streetAddress = streetAddress;
		this.garbageType = garbageType;
		this.location = location;
	}

	/**
	 * Provides back the id of the UEI represented by this descriptor
	 * @return the id
	 */
	public String getId()
	{
		//the UEI id
		return id;
	}

	/**
	 * Sets the id of the UEI represented by this descriptor
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the address of the UEI represented by this descriptor
	 * @return the streetAddress
	 */
	public String getStreetAddress()
	{
		return streetAddress;
	}

	/**
	 * Sets the address of the UEI represented by this descriptor
	 * @param streetAddress the streetAddress to set
	 */
	public void setStreetAddress(String streetAddress)
	{
		this.streetAddress = streetAddress;
	}

	/**
	 * Gets the type of garbage collected by this UEI
	 * @return the garbageType
	 */
	public Garbage getGarbageType()
	{
		return garbageType;
	}

	/**
	 * Sets the type of garbage collected by this UEI
	 * @param garbageType the garbageType to set
	 */
	public void setGarbageType(Garbage garbageType)
	{
		this.garbageType = garbageType;
	}
	
	/**
	 * Sets the type of garbage collected by this UEI
	 * @param garbageType the garbageType to set
	 */
	public void setGarbageType(String garbageType)
	{
		this.garbageType = Garbage.valueOf(garbageType);
	}

	/**
	 * Gets the location of the UEI represented by this UEI
	 * @return the location, as a {@link GeoPoint}
	 */
	public GeoPoint getLocation()
	{
		return location;
	}

	/**
	 * Sets the location of the UEI represented by this descriptor, as a {@link GeoPoint} instance.
	 * @param location the location of the UEI as a {@link GeoPoint} instance.
	 */
	public void setLocation(GeoPoint location)
	{
		this.location = location;
	}
	
	/**
	 * Sets the location of the UEI represented by this descriptor, as a {latitude, longitude} couple
	 * @param lat The latitude of the UEI
	 * @param lon The longitude of the UEI
	 */
	public void setLocation(double lat, double lon)
	{
		this.location = new GeoPoint(lat, lon);
	}
}
