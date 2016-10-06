package it.ismb.pert.pwal.rest.ogc.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import it.ismb.pert.pwal.ogc.datastore.memory.ObservationSnapshot;
import it.ismb.pert.pwal.ogc.datastore.memory.SnapshotFactory;
import it.ismb.pert.pwal.ogc.datastore.memory.ThingSnapshot;
import it.ismb.pert.pwal.ogc.datastore.memory.type.SnapshotType;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Datastream;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/ogc", produces = { APPLICATION_JSON_VALUE })
public class DatastreamsApi extends PwalRestApi
{
	// the observation snapshot, only uses it assuming that initialization
	// happens somewhere else...
	private ObservationSnapshot observationSnapshot;
	
	// the thing snapshot, only uses it assuming that initialization
	// happens somewhere else...
	private ThingSnapshot thingSnapshot;
	
	@PostConstruct
	public void initCommon()
	{
		// get a snapshot factory instance
		SnapshotFactory factory = SnapshotFactory.getInstance();
		
		// get the observation snapshot
		this.observationSnapshot = (ObservationSnapshot) factory.getSnapshot(SnapshotType.OBSERVATION,
				this.pwal.getFqdn());
		
		// get the thing snapshot
		this.thingSnapshot = (ThingSnapshot) factory.getSnapshot(SnapshotType.THING, this.pwal.getFqdn());
	}
	
	@RequestMapping(value = "Datastreams", method = RequestMethod.GET)
	public ResponseEntity<List<Datastream>> datastreamsGet(
			@RequestParam(value = "$Orderby", required = false) String orderBy,
			@RequestParam(value = "$Top", required = false) Integer top,
			@RequestParam(value = "$Skip", required = false) Integer skip,
			@RequestParam(value = "$Filter", required = false) Integer filter
	
	) throws NotFoundException
	{
		// do some magic!
		// default response
		ResponseEntity<List<Datastream>> entity = new ResponseEntity<List<Datastream>>(HttpStatus.NOT_IMPLEMENTED);
		
		if ((orderBy != null) || (filter != null))
		{
			// not supported exception
			entity = new ResponseEntity<List<Datastream>>(HttpStatus.NOT_IMPLEMENTED);
			
		}
		else
		{
			if ((this.thingSnapshot != null) && (this.thingSnapshot.isConnected()))
			{
				//-- harvest datastreams --
				ArrayList<Datastream> datastreams = new ArrayList<>();
				
				// list all available things
				List<Thing> things = thingSnapshot.listThings();
				
				//iterate over things and extract data streams
				for(Thing thing : things)
				{
					datastreams.addAll(thing.getDatastreams());
				}
				
				//build the entity for the response
				entity = new ResponseEntity<List<Datastream>>(datastreams, HttpStatus.OK);
				
			}
		}
		return entity;
	}
	
	@RequestMapping(value = "Datastreams",
			
			method = RequestMethod.POST)
	public ResponseEntity<Void> datastreamsPost(Datastream datastream) throws NotFoundException
	{
		// do some magic!
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}
	
	@RequestMapping(value = "Datastreams({DatastreamId})/Observations", method = RequestMethod.GET)
	public ResponseEntity<List<Observation>> getDatastreamObservation(
			@PathVariable(value = "DatastreamId") String datastreamId)
	{
		ResponseEntity<List<Observation>> entity = new ResponseEntity<List<Observation>>(HttpStatus.NOT_FOUND);
		
		// check if there is one observation snapshot available and reachable
		if ((this.observationSnapshot != null) && (observationSnapshot.isConnected()))
		{
			// get the latest observation from the stream (no history is
			// supported)
			Observation obs = this.observationSnapshot.getLastObservation(datastreamId);
			
			if (obs != null)
			{
				// build the list of observations to return
				ArrayList<Observation> observationList = new ArrayList<>();
				observationList.add(obs);
				
				// create the return entity
				entity = new ResponseEntity<List<Observation>>(observationList, HttpStatus.OK);
			}
		}
		
		return entity;
	}
}
