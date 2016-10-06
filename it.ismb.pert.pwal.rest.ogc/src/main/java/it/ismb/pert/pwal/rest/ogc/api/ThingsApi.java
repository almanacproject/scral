/*
 * PWAL - OGC SensorThings API endpoint
 * 
 * Copyright (c) 2015 Dario Bonino
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
package it.ismb.pert.pwal.rest.ogc.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycila.event.Topic;

import it.ismb.pert.pwal.ogc.datastore.memory.SnapshotFactory;
import it.ismb.pert.pwal.ogc.datastore.memory.ThingSnapshot;
import it.ismb.pert.pwal.ogc.datastore.memory.type.SnapshotType;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import it.ismb.pertlab.pwal.api.events.base.PWALDeviceRemovedEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.PWALEventDispatcher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;

/**
 * The REST end point for things. It implements a minimal subset of the OGC
 * SensorThings API specification with sparse support to filters.
 * 
 * TODO: check compliance and coverage, definition of the OGC API shall probably
 * be improved.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
@Controller
@RequestMapping(value = "/ogc/Things", produces = { APPLICATION_JSON_VALUE })
public class ThingsApi extends PwalRestApi
{
	
	// the Things snapshot
	private ThingSnapshot snapshot;
	
	/**
	 * Empty constructor to fulfill the bean instantiation pattern
	 */
	public ThingsApi()
	{
		// schedule initialization
	}
	
	@PostConstruct
	public void initCommon()
	{
		// build the thing snapshot "feeding the api"
		SnapshotFactory factory = SnapshotFactory.getInstance();
		this.snapshot = (ThingSnapshot)factory.getSnapshot(SnapshotType.THING, this.pwal.getFqdn());
	
		// register the snapshot to add and remove device events
		PWALEventDispatcher.getInstance().getDispatcher().subscribe(
				Topic.match(PWALTopicsUtility.createNewDeviceAddedTopic("**")), PWALNewDeviceAddedEvent.class,
				this.snapshot);
		PWALEventDispatcher.getInstance().getDispatcher().subscribe(
				Topic.match(PWALTopicsUtility.createDeviceRemovedTopic("**")), PWALDeviceRemovedEvent.class,
				this.snapshot);
		
		//set the thing snapshot at connected
		this.snapshot.setConnected(true);
	}
	
	/**
	 * Implements the "Things" resource of the OGC Sensor Things api. It only
	 * supports a very limited subset of available filters and operators due to
	 * restrictions of the Pwal back-end.
	 * 
	 * @param $Orderby
	 *            Specifies an ordering field, not yet implemented
	 * @param $Top
	 *            Specifies a number of devices to be returned. WARNING!!! there
	 *            is no guarantee that the order is preserved among calls due to
	 *            underlying data structure used in the pwal. Results from $Top
	 *            and $Skip might be different in different time instants.
	 * @param $Skip
	 *            Specifies how many devices shall be skipped. WARNING!!! there
	 *            is no guarantee that the order is preserved among calls due to
	 *            underlying data structure used in the pwal. Results from $Top
	 *            and $Skip might be different in different time instants.
	 * @param $Filter
	 *            Filters out things depending on some field value - not yet
	 *            implemented
	 * @return the found Things in JSON notation.
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<List<Thing>> thingsGet(@RequestParam(value = "$Orderby", required = false) String orderBy,
			@RequestParam(value = "$Top", required = false) Integer top,
			@RequestParam(value = "$Skip", required = false) Integer skip,
			@RequestParam(value = "$Filter", required = false) Integer filter)
	{
		// default response
		ResponseEntity<List<Thing>> entity = new ResponseEntity<List<Thing>>(HttpStatus.NOT_FOUND);
		
		// no param is supported
		if ((orderBy == null) && (top == null) && (skip == null) && (filter == null))
		{
			if (this.snapshot != null)
			{		
				// get all things in the snapshot
				List<Thing> allThings = this.snapshot.listThings();
				
				// check not null
				if (allThings != null)
					entity = new ResponseEntity<List<Thing>>(allThings, HttpStatus.OK);
			}
			
		}
		// do some magic!
		return entity;
	}
	
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public ResponseEntity<Long> thingsCount()
	{
		// default response
		ResponseEntity<Long> entity = new ResponseEntity<Long>(HttpStatus.NOT_FOUND);
		
		// if the snapshot is not null, return the snapshot size
		if (this.snapshot != null)
			entity = new ResponseEntity<Long>(new Long(this.snapshot.size()), HttpStatus.OK);
		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<String> thingsPost(Thing thing) throws NotFoundException
	{
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
	
	/**
	 * Provide full details about a Pwal device, given the corresponding OGC
	 * Sensor Things API Thing id
	 * 
	 * @param thingId
	 *            The id of the Thing corresponding to the Pwal device
	 * @return The device details
	 */
	@RequestMapping(value = "{thingId}", method = RequestMethod.GET)
	public ResponseEntity<Thing> thingsGetById(@PathVariable String thingId)
	{
		// default response
		ResponseEntity<Thing> entity = new ResponseEntity<Thing>(HttpStatus.NOT_FOUND);
		
		// check if the parameter exists (probably useless)
		if (thingId != null)
		{
			// if the snapshot exists
			if (this.snapshot != null)
			{
				// get the thing
				Thing thing = this.snapshot.getThing(thingId);
				
				// check if the thing exists
				if (thing != null)
					entity = new ResponseEntity<Thing>(thing, HttpStatus.OK);
			}
		}
		return entity;
	}
}
