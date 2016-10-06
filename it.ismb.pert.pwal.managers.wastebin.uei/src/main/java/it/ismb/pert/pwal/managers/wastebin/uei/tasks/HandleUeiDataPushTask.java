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
package it.ismb.pert.pwal.managers.wastebin.uei.tasks;

import it.ismb.pert.pwal.managers.wastebin.uei.UeiManager;
import it.ismb.pert.pwal.managers.wastebin.uei.device.data.UeiData;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class HandleUeiDataPushTask implements Runnable
{
	
	// the posted UeiData
	private UeiData data[];
	
	// the originating manager
	private UeiManager manager;
	
	/**
	 * Class constructor, builds an asynch task for handling bulk data posted to the manager.
	 * @param data The Bulk data received through post.
	 * @param manager The manager handling the data, used for filetring based on Uei validity.
	 */
	public HandleUeiDataPushTask(UeiData data[], UeiManager manager)
	{
		// store the needed data
		this.data = data;
		
		//store a reference to the originating manager
		this.manager = manager;
	}

	public void run()
	{
		//iterate over received data
		for(int i=0; i<data.length; i++)
		{
			//handle a single data point
			
			//check if valid
			if(manager.isValid(data[i].getId()))
			{
				// UEI is valid, inject the data in the platform will be 2 observation
				this.manager.updateDevice(data[i]);
				
			}
		}
		
	}
	
}
