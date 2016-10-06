package it.ismb.pert.pwal.rest.ogc.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.ObservedProperty;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/ogc/ObservedProperties", produces = { APPLICATION_JSON_VALUE })
public class ObservedPropertiesApi extends PwalRestApi
{
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<List<ObservedProperty>> observedPropertiesGet(
			@RequestParam(value = "$Orderby", required = false) String orderby,
			@RequestParam(value = "$Top", required = false) Integer top,
			@RequestParam(value = "$Skip", required = false) Integer skip,
			@RequestParam(value = "$Filter", required = false) Integer filter) throws NotFoundException
	{
		// do some magic!
		return new ResponseEntity<List<ObservedProperty>>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Void> observedPropertiesPost(ObservedProperty observedProperty) throws NotFoundException
	{
		// do some magic!
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	
}
