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

/**
 * Interface to identify if a memory snapshot si connected to the inner PWAL event bus or not.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public interface InMemorySnapshot
{
	/**
	 * Checks if the in-memory snapshot is connected to the inner event bus or not
	 * @return true if the snapshot is connected, false otherwise.
	 */
	public boolean isConnected();
}
