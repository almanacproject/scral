package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import java.util.HashMap;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FillLevelSensorWSN extends BaseSerialDevice implements FillLevel
{
	// static location to be externalized as configuration parameter
	public static final double LAT = 45.0647802;
	public static final double LON = 7.6585141;
	public static final double DISTANCE_FULL = 20.0;
	public static final double DISTANCE_EMPTY = 100.0;
	
	// configuration keys
	public static final String DISTANCE_FULL_KEY = "full_distance";
	public static final String DISTANCE_EMPTY_KEY = "empty_distance";
	public static final String LAT_KEY = "latitude";
	public static final String LON_KEY = "longitude";
	
	// the class this.logger
	private Logger logger;
	
	// the sensor id
	private String id;
	
	// the sensor pwalId
	private String pwalId;
	
	// a reference to the serial manager handling this sensor
	private SerialManager manager;
	
	// the current level in percentage
	private Double level;
	
	// the current depth in centimeters
	private Integer depthCm;
	
	// the bin empty distance
	private double distanceEmpty;
	
	// the bin full distance
	private double distanceFull;
	
	// the last time at which the values were updated
	private String updatedAt;
	
	// the time at which the values are expected to be updated
	private String expiresAt;
	
	// a reference to the inner eventing system
	private PWALEventPublisher eventPublisher;
	
	// the sensor location
	private Location location;
	
	public FillLevelSensorWSN(SerialManager manager)
	{
		// initialize the logger
		this.logger = LoggerFactory.getLogger(FillLevelSensorWSN.class);
		
		// initialize the level and depth
		this.level = 0.0;
		this.depthCm = (int) Math.ceil(FillLevelSensorWSN.DISTANCE_EMPTY);
		
		// initialize the empty and full distances based on default values
		this.distanceEmpty = FillLevelSensorWSN.DISTANCE_EMPTY;
		this.distanceFull = FillLevelSensorWSN.DISTANCE_FULL;
		
		// store a reference to the manager
		this.manager = manager;
		
		// create a new publisher for the inner eventing system
		this.eventPublisher = new PWALEventPublisher();
		
		// prepare the bin location
		this.location = new Location();
		
		// Set location to default
		this.location.setLat(FillLevelSensorWSN.LAT);
		this.location.setLon(FillLevelSensorWSN.LON);
	}
	
	public FillLevelSensorWSN(SerialManager manager, Properties configuration)
	{
		// initialize the logger
		this.logger = LoggerFactory.getLogger(FillLevelSensorWSN.class);
		
		// initialize the level and depth
		this.level = 0.0;
		this.depthCm = (int) Math
				.ceil(((configuration.getProperty(FillLevelSensorWSN.DISTANCE_EMPTY_KEY) != null) ? Double
						.parseDouble(configuration.getProperty(FillLevelSensorWSN.DISTANCE_EMPTY_KEY))
						: FillLevelSensorWSN.DISTANCE_EMPTY));
		
		// initialize the empty and full distances based on received values, if
		// present
		this.distanceEmpty = (configuration.getProperty(FillLevelSensorWSN.DISTANCE_EMPTY_KEY) != null) ? Double
				.parseDouble(configuration.getProperty(FillLevelSensorWSN.DISTANCE_EMPTY_KEY))
				: FillLevelSensorWSN.DISTANCE_EMPTY;
		this.distanceFull = (configuration.getProperty(FillLevelSensorWSN.DISTANCE_FULL_KEY) != null) ? Double
				.parseDouble(configuration.getProperty(FillLevelSensorWSN.DISTANCE_FULL_KEY))
				: FillLevelSensorWSN.DISTANCE_FULL;
		
		// store a reference to the manager
		this.manager = manager;
		
		// create a new publisher for the inner eventing system
		this.eventPublisher = new PWALEventPublisher();
		
		// prepare the bin location
		this.location = new Location();
		
		// Set location to default
		this.location.setLat((configuration.getProperty(FillLevelSensorWSN.LAT_KEY) != null) ? Double
				.parseDouble(configuration.getProperty(FillLevelSensorWSN.LAT_KEY)) : FillLevelSensorWSN.LAT);
		this.location.setLon((configuration.getProperty(FillLevelSensorWSN.LON_KEY) != null) ? Double
				.parseDouble(configuration.getProperty(FillLevelSensorWSN.LON_KEY)) : FillLevelSensorWSN.LON);
	}
	
	@Override
	public void messageReceived(String payload)
	{
		// remove the first character from the payload, this is due to serial
		// protocol micro issue
		payload = payload.trim();
		
		// debug
		this.logger.debug("Received message: " + payload);
		try
		{
			this.depthCm = Integer.parseInt(payload);
			this.level = ((this.distanceEmpty - this.depthCm) * 100.0) / (this.distanceEmpty - this.distanceFull);
			
			
			// saturation on both ends
			if (this.level > 100.0)
				this.level = 100.0;
			else if (this.level < 0.0)
				this.level = 0.0;
			
			DateTime updated = new DateTime(DateTime.now(), DateTimeZone.UTC);
			this.updatedAt = updated.toString();
			this.expiresAt = updated.plusSeconds(1).toString();
			HashMap<String, Object> valuesUpdated = new HashMap<>();
			valuesUpdated.put("getDepth", this.depthCm);
			valuesUpdated.put("getLevel", this.level);
			PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(this.updatedAt, this.getPwalId(),
					this.expiresAt, valuesUpdated, this);
			this.eventPublisher.publish(event);
			this.logger.debug("Payload  parsed: " + this.depthCm + " cm");
		}
		catch (NumberFormatException e)
		{
			this.logger.info("Received non valid data", e);
		}
	}
	
	@Override
	public String getPwalId()
	{
		return this.pwalId;
	}
	
	@Override
	public void setPwalId(String pwalId)
	{
		this.pwalId = pwalId;
		this.eventPublisher.setTopics(new String[] { PWALTopicsUtility.createNewDataFromDeviceTopic(
				this.getNetworkType(), this.getPwalId()) });
	}
	
	@Override
	public String getId()
	{
		return this.id;
	}
	
	@Override
	public void setId(String id)
	{
		this.id = id;
	}
	
	@Override
	public String getType()
	{
		return DeviceType.FILL_LEVEL_SENSOR;
	}
	
	@Override
	public String getNetworkType()
	{
		return manager.getNetworkType();
	}
	
	@Override
	public Integer getDepth()
	{
		return this.depthCm;
	}
	
	@Override
	public Double getLevel()
	{
		return this.level;
	}
	
	@Override
	public String getUpdatedAt()
	{
		return this.updatedAt;
	}
	
	@Override
	public void setUpdatedAt(String updatedAt)
	{
		this.updatedAt = updatedAt;
	}
	
	@Override
	public Location getLocation()
	{
		return this.location;
	}
	
	@Override
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	@Override
	public Unit getUnit()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setUnit(Unit unit)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getExpiresAt()
	{
		return this.expiresAt;
	}
	
	@Override
	public void setExpiresAt(String expiresAt)
	{
		this.expiresAt = expiresAt;
	}
	
}
