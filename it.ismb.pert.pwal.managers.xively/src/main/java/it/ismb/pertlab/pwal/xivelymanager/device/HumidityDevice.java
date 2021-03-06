package it.ismb.pertlab.pwal.xivelymanager.device;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xively.client.model.Datastream;

import it.ismb.pertlab.pwal.api.devices.model.HumiditySensor;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.xivelymanager.XivelyManager;
import it.ismb.pertlab.pwal.xivelymanager.utils.Utils;

/**
 * Class used to drive an humidity sensor via xively
 *
 */
public class HumidityDevice implements HumiditySensor, DataUpdateSubscriber<Datastream> {

	private static final Logger LOG = LoggerFactory.getLogger(HumidityDevice.class);
	
	private String pwalId;
	private String id;
	private String updatedAt;
	private Location location;
	private Unit unit;
	private String expiresAt;
	private final String type=DeviceType.HUMIDITY_SENSOR;
	private Double humidity;

    private PWALEventPublisher eventPublisher;
	
	/**
	 * Constructor of the temperature sensor controlled using xively
	 * 
	 * @param feedID
	 *           id of the feed containing the datastream 
	 * 
	 * @param streamID
	 *           id of the datastream of the sensor
	 */
	public HumidityDevice(Integer feedID, String streamID) {
		// req = XivelyService.instance().datastream(feedID);
		id = feedID+ XivelyManager.ID_SEPARATOR +streamID;
		this.humidity = 0.0;
		this.eventPublisher = new PWALEventPublisher();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return this.type;
	}
	
	@Override
	public String getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public Unit getUnit() {
		return unit;
	}


	@Override
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	@Override
	public Double getHumidity() {
		//Datastream stream = req.get(streamId);
/*		if(stream.getValue()!=null) {
			if(stream.getUpdatedAt()!=null) {
				this.setUpdatedAt(stream.getUpdatedAt());
			}
			if(stream.getUnit()!=null) {
				this.setUnit(Utils.convertUnit(stream.getUnit()));
			}
			LOG.info("Humidity value for "+stream.getId()+
					" "+stream.getValue()+
					((stream.getUnit()!=null) ? " "+stream.getUnit().getSymbol() : "") +
					" updated at: "+stream.getUpdatedAt());
			return Double.valueOf(stream.getValue());
		} else {
			LOG.error("Current value for "+stream.getId()+" not available");
			return 0.0;
		}*/
		return this.humidity;
	}	
	
	@Override
	public String getPwalId() {
		return pwalId;
	}


	@Override
	public void setPwalId(String pwalId) {
		this.pwalId=pwalId;
		this.eventPublisher.setTopics(new String[]
		        { PWALTopicsUtility.createNewDataFromDeviceTopic(DeviceNetworkType.XIVELY,
		                this.getPwalId()) });
	}


	@Override
	public String getNetworkType() {
		return DeviceNetworkType.XIVELY;
	}	
	@Override
	public String getExpiresAt() {
		return this.expiresAt;
	}

	@Override
	public void setExpiresAt(String expiresAt) {
		this.expiresAt = expiresAt;
	}

	@Override
	public void handleUpdate(Datastream stream) {
		if(stream.getValue()!=null) {
			if(stream.getUnit()!=null) {
				this.setUnit(Utils.convertUnit(stream.getUnit()));
			}
			LOG.info("Humidity value for "+stream.getId()+
					" "+stream.getValue()+
					((stream.getUnit()!=null) ? " "+stream.getUnit().getSymbol() : "") +
					" updated at: "+stream.getUpdatedAt());
			if(stream.getValue() != null && !stream.getValue().isEmpty())
			    this.humidity = Double.valueOf(stream.getValue());
	                HashMap<String, Object> valuesMap = new HashMap<>();
	                valuesMap.put("getHumidity", this.getHumidity());
	                PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(
	                        this.updatedAt, this.getPwalId(), this.getExpiresAt(),
	                        valuesMap, this);
	                LOG.debug(
	                        "Device {} is publishing a new data available event on topic: {}",
	                        this.getPwalId(), this.eventPublisher.getTopics());
	                this.eventPublisher.publish(event);
	                LOG.debug("Json parsed: {}", stream.getValue());
	                return;
		} else {
			LOG.error("Current value for "+stream.getId()+" not available");
			this.humidity = 0.0;
		}
	}

	@Override
	public String getNetworkLevelId() {
		return this.id;
	}
}
