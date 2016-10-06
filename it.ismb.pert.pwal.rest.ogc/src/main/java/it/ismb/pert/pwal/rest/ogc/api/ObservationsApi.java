package it.ismb.pert.pwal.rest.ogc.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycila.event.Topic;

import it.ismb.pert.pwal.ogc.datastore.memory.ObservationSnapshot;
import it.ismb.pert.pwal.ogc.datastore.memory.SnapshotFactory;
import it.ismb.pert.pwal.ogc.datastore.memory.type.SnapshotType;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.PWALEventDispatcher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;

@Controller
@RequestMapping(value = "/ogc/Observations", produces = { APPLICATION_JSON_VALUE })
public class ObservationsApi extends PwalRestApi
{
	// the observation snapshot
	private ObservationSnapshot observationSnapshot;
	
	/**
	 * 
	 */
	public ObservationsApi()
	{
		// do nothing
	}
	
	@PostConstruct
	public void initCommon()
	{	
		// create and connect the observation snapshot
		SnapshotFactory factory = SnapshotFactory.getInstance();
		this.observationSnapshot = (ObservationSnapshot)factory.getSnapshot(SnapshotType.OBSERVATION,this.pwal.getFqdn());
		
		// register the snapshot as listener to observations
		PWALEventDispatcher.getInstance().getDispatcher().subscribe(
				Topic.match(PWALTopicsUtility.createNewDataFromDeviceTopic("**", "**")),
				PWALNewDataAvailableEvent.class, this.observationSnapshot);
		
		//set the observation snapshot status at connected
		this.observationSnapshot.setConnected(true);
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<List<Observation>> observationsGet(
			@RequestParam(value = "$Orderby", required = false) String orderby,
			@RequestParam(value = "$Top", required = false) Integer top,
			@RequestParam(value = "$Skip", required = false) Integer skip,
			@RequestParam(value = "$Filter", required = false) Integer filter
	
	) throws NotFoundException
	{
		// default response
		ResponseEntity<List<Observation>> entity = new ResponseEntity<List<Observation>>(HttpStatus.NOT_IMPLEMENTED);
		
		// check if the snapshot is not null and get the list of last
		// observations
		if (this.observationSnapshot != null)
			entity = new ResponseEntity<List<Observation>>(this.observationSnapshot.listObservations(), HttpStatus.OK);
		
		// return the entity
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Void> observationsPost(Observation observation) throws NotFoundException
	{
		// do some magic!
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
}
