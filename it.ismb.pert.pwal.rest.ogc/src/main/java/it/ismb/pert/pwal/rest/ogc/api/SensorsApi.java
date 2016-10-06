package it.ismb.pert.pwal.rest.ogc.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Sensor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/ogc/Sensors", produces = { APPLICATION_JSON_VALUE })
public class SensorsApi extends PwalRestApi
{
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<List<Sensor>> sensorsGet(@RequestParam(value = "$Orderby", required = false) String $Orderby
	
	, @RequestParam(value = "$Top", required = false) Integer $Top,
			@RequestParam(value = "$Skip", required = false) Integer $Skip,
			@RequestParam(value = "$Filter", required = false) Integer $Filter
	
	) throws NotFoundException
	{
		// do some magic!
		return new ResponseEntity<List<Sensor>>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Void> sensorsPost(Sensor sensor) throws NotFoundException
	{
		// do some magic!
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
}
