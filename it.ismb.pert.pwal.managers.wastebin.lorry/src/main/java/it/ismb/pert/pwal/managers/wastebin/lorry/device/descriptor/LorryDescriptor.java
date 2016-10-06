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
package it.ismb.pert.pwal.managers.wastebin.lorry.device.descriptor;

import it.ismb.pertlab.smartcity.api.GeoPoint;

/**
 * A class representing the initial device configuration defined in the Pwal
 * configuration file
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class LorryDescriptor
{
	// the network-level id
	private String id;
	
	// the initial lorry Location
	private GeoPoint location;
	
	/**
	 * Empty constructor, implements the bean instantiation pattern
	 */
	public LorryDescriptor()
	{
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Creates lorry descriptor hosting the given lorry identifier and the given
	 * initial location of the lorry.
	 * 
	 * @param id
	 *            The lorry unique identifier
	 * @param location
	 *            The initial location of the lorry
	 */
	public LorryDescriptor(String id, GeoPoint location)
	{
		this.id = id;
		this.location = location;
	}
	
	/**
	 * Provides back the lorry unique identifier
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Sets the lorry unique identifier
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
	/**
	 * Provides back the initial location of the lorry described byt the {@link LorryDescriptor} instance.
	 * @return the location
	 */
	public GeoPoint getLocation()
	{
		return location;
	}
	
	/**
	 * Sets the initial location of the lorry described byt the {@link LorryDescriptor} instance.
	 * @param location
	 *            the location to set
	 */
	public void setLocation(GeoPoint location)
	{
		this.location = location;
	}
	
}
