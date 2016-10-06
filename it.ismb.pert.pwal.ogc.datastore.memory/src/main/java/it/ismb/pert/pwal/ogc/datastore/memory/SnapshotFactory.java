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

import java.util.Hashtable;

import it.ismb.pert.pwal.ogc.datastore.memory.type.SnapshotType;

/**
 * Snapshot Factory class, allows generating snapshots for several entities with
 * a singleton pattern. This is mainly used for memory efficiency purposes,
 * having a single snapshot shared among many classes.
 * 
 * @author bonino
 *
 */
public class SnapshotFactory
{
	// the set of currently active snapshots
	private Hashtable<String, InMemorySnapshot> snapshots;
	
	// the singleton factory
	private static SnapshotFactory theFactory;
	
	private SnapshotFactory()
	{
		// create the snapshot set
		this.snapshots = new Hashtable<String, InMemorySnapshot>();
	}
	
	/**
	 * Retrieves a singleton instance of {@link SnapshotFactory} shared among
	 * all callers. It is guaranteed that subsequent calls to this method will
	 * always return the same instance of factory.
	 * 
	 * @return The {@link SnapshotFactory} instance
	 */
	public static SnapshotFactory getInstance()
	{
		// create the singleton factory if not existing
		if (SnapshotFactory.theFactory == null)
			SnapshotFactory.theFactory = new SnapshotFactory();
		
		// re-use the singleton
		return SnapshotFactory.theFactory;
	}
	
	/**
	 * Returns a singleton InMemorySnapshot of the given type
	 * 
	 * @param type
	 *            The {@link SnapshotType} of the required snapshot
	 * @param fqdn
	 *            The fqdn associated to the snapshot, if any
	 * @return The singleton snapshot instance, if supported or null otherwise.
	 */
	public InMemorySnapshot getSnapshot(SnapshotType type, String fqdn)
	{
		// switch over types
		switch (type)
		{
			case OBSERVATION:
			{
				// build the snapshot if it does not exists
				if (!this.snapshots.containsKey(type.name()))
				{
					if (fqdn != null)
					{
						this.snapshots.put(type.name(), new ObservationSnapshot(fqdn));
					}
					else
					{
						this.snapshots.put(type.name(), new ObservationSnapshot());
					}
				}
				break;
			}
			case THING:
			{
				// build the snapshot if it does not exists
				if (!this.snapshots.containsKey(type.name()))
				{
					if (fqdn != null)
					{
						this.snapshots.put(type.name(), new ThingSnapshot(fqdn));
					}
					else
					{
						this.snapshots.put(type.name(), new ThingSnapshot());
					}
				}
				break;
			}
			default:
			{
				// not yet supported
			}
		}
		
		// return the snapshot singleton if exists, or null
		return this.snapshots.get(type.toString());
	}
	
}
