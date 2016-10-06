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
package it.ismb.pert.pwal.managers.wastebin.uei.rest;

import it.ismb.pert.pwal.managers.wastebin.uei.UeiManager;
import it.ismb.pert.pwal.managers.wastebin.uei.device.data.UeiData;
import it.ismb.pert.pwal.managers.wastebin.uei.tasks.HandleUeiDataPushTask;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
@RestController
@RequestMapping(value = "/internal/manager/uei")
public class UeiRestAPI
{
	@Autowired
	@Qualifier("PWAL")
	private Pwal pwal;
	
	// the executor service for handling requests asynchronously
	private ExecutorService ayncExecutor;
	
	/**
	 * 
	 */
	public UeiRestAPI()
	{
		
		// build the async executor for handling post data
		this.ayncExecutor = Executors.newCachedThreadPool();
	}
	
	/**
	 * Handle POST of an array of UEI data points, each possibly referring to a
	 * different UEI (not completely REST)
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<Void> ueiDataPost(@RequestBody UeiData[] data)
	{
		// the default response
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// check if the manager is running and can accept posts
		UeiManager theUeiManager = this.getUeiManager();
		
		if ((theUeiManager != null) && (theUeiManager.isRunning()))
		{
			// create the asynchronous executor task
			HandleUeiDataPushTask task = new HandleUeiDataPushTask(data, theUeiManager);
			
			// exec asynchronously
			this.ayncExecutor.submit(task);
			
			// lazy response, even data referred to missing UEI generates an 404
			// OK
			// (safer as it does not provide means to understand whether a UEI
			// is known or not)
			response = new ResponseEntity<Void>(HttpStatus.OK);
		}
		
		return response;
	}
	
	/**
	 * @return the pwal
	 */
	public Pwal getPwal()
	{
		return pwal;
	}
	
	/**
	 * @param pwal
	 *            the pwal to set
	 */
	public void setPwal(Pwal pwal)
	{
		this.pwal = pwal;
	}
	
	private UeiManager getUeiManager()
	{
		UeiManager manager = null;
		
		if (this.pwal != null)
		{
			Collection<DevicesManager> managers = this.pwal.getDevicesManagerList();
			
			// search for the UeiManager
			for (DevicesManager cManager : managers)
			{
				if (cManager instanceof UeiManager)
				{
					// store the manager
					manager = (UeiManager) cManager;
				}
			}
		}
		
		return manager;
	}
}
