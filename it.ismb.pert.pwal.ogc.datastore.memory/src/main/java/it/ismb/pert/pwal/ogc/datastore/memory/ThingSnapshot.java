/*
 * In-Memory OGC Datastore
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
package it.ismb.pert.pwal.ogc.datastore.memory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mycila.event.Event;
import com.mycila.event.Subscriber;

import it.ismb.pertlab.pwal.api.events.base.PWALBaseEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALDeviceRemovedEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.event.format.ogc.sensorthings.api.OGCSensorThingsAPIPayloadFactory;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;

/**
 * A memory snapshot of Things currently "active" in the PWAL, the list is kept
 * up-to-data by listening to device creation / deletion events dispatched
 * through the PWAL inner event bus.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class ThingSnapshot implements Subscriber<PWALBaseEvent>, InMemorySnapshot
{
	// the Things snapshot
	private Hashtable<String, Thing> thingsSnapshot;
	
	// the OGC Payload factory
	private OGCSensorThingsAPIPayloadFactory payloadFactory;
	
	// the platform fully qualified domain name
	private String fqdn;
	
	// the connected flag
	private boolean connected;
	
	public ThingSnapshot()
	{
		// create the things snapshot
		this.thingsSnapshot = new Hashtable<String, Thing>();
		
		// the OGC payload factory
		this.payloadFactory = OGCSensorThingsAPIPayloadFactory.getInstance();
		
		// empty fqdn
		this.fqdn = "";
	}
	
	public ThingSnapshot(String platformFQDN)
	{
		// create the things snapshot
		this.thingsSnapshot = new Hashtable<String, Thing>();
		
		// the OGC payload factory
		this.payloadFactory = OGCSensorThingsAPIPayloadFactory.getInstance();
		
		// empty fqdn
		this.fqdn = platformFQDN;
	}
	
	/**
	 * Listens to events dispatched on the PWAL inner event bus
	 */
	public void onEvent(Event<PWALBaseEvent> event) throws Exception
	{
		if (!(event.getSource() instanceof PWALNewDataAvailableEvent))
		{
			// add the corresponding thing
			Thing thingToAdd = this.payloadFactory.getSensorMetadataPayload(event.getSource(), this.fqdn);
			
			// discriminate actions depending on event types
			if (event.getSource() instanceof PWALNewDeviceAddedEvent)
			{
				// add the thing to the snapshot
				this.thingsSnapshot.put(thingToAdd.getId(), thingToAdd);
			}
			else if (event.getSource() instanceof PWALDeviceRemovedEvent)
			{
				// remove the thing from the snapshot
				this.thingsSnapshot.remove(thingToAdd).getId();
			}
		}
		
	}
	
	/**
	 * Get the full thing description of the OGC Thing having the given ID
	 * 
	 * @param thingId
	 *            The Thing id
	 * @return the full Thing description, in OGC.
	 */
	public Thing getThing(String thingId)
	{
		// prepare the thing to return
		Thing thing = null;
		
		// chekc if the given id exists, is so set the corresponding thing as
		// return value
		if (this.thingsSnapshot.containsKey(thingId))
			thing = this.thingsSnapshot.get(thingId);
		
		return thing;
	}
	
	/**
	 * Returns the list of Things currently available in the in-memory things
	 * snapshot
	 * 
	 * @return The list of currently available Things
	 */
	public List<Thing> listThings()
	{
		// prepare the list to return
		ArrayList<Thing> things = new ArrayList<Thing>();
		
		// load the current snapshot in the list
		things.addAll(this.thingsSnapshot.values());
		
		// return the Thing list
		return things;
	}
	
	/**
	 * Provides the size of the current Thing snapshot, 0 if the snapshot is
	 * empty or not intialized
	 * 
	 * @return The snapshot size as an integer.
	 */
	public int size()
	{
		// Default empty
		int size = 0;
		
		// if the snapshot exists, get the snapshot size
		if (this.thingsSnapshot != null)
			size = this.thingsSnapshot.size();
		
		// return the size
		return size;
	}
	
	/**
	 * Sets the snapshot as a whole, replacing any existing entry
	 * 
	 * @param thingsSnapshot
	 *            the thingsSnapshot to set
	 */
	public void setThingsSnapshot(Hashtable<String, Thing> thingsSnapshot)
	{
		// set the snapshot replacing any existing entry
		this.thingsSnapshot = thingsSnapshot;
	}
	
	/**
	 * Implements the InMemorySnapshot connected check, returns true if the
	 * snapshot is connected, false otherwise
	 */
	public boolean isConnected()
	{
		return this.connected;
	}
	
	/**
	 * Sets the connection status for this snapshot
	 * 
	 * @param connected
	 *            the connected to set
	 */
	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}
	
}
