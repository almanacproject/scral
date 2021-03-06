package it.ismb.pertlab.pwal.api.internal;

import it.ismb.pertlab.pwal.api.devices.events.DeviceLogger;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;

import java.util.ArrayList;
import java.util.Collection;

public interface Pwal {

	/**
	 * Provides a subclass of Device (see the device taxonomy) for a given pwal ID
	 * 
	 * @param id - the pwal id of the device
	 * @return a subclass of Device (see the device taxonomy)
	 */
	public Device getDevice(String id);
	/**
	 * Provides a collection containing all device configured.
	 * 
	 * @return a list of subclasses of Device (see the device taxonomy) 
	 */
	public Collection<Device> getDevicesList();
	
	/**
	 * Provides Log of devices attached or removed in the form of an ArrayList
	 * @return ArrayList
	 */
	public ArrayList<DeviceLogger> getDeviceLogList();
	
	/**
	 * Provides devices of a given type (e.g. pwal:Temperature)
	 * @param type is the device type, according to the types available into the APIs
	 * @return
	 */
	public Collection<Device> getDevicesByType(String type);
	
	/**
	 * Provides a collection containing all the devices managers
	 * @return
	 */
	public Collection<DevicesManager> getDevicesManagerList();
	
	/**
	 * Provide a method to start a device manager
	 * @param deviceManagerName is the name of the device manager
	 * @return true if the device manager actually starts, false instead
	 */
	public Boolean startDeviceManager(String deviceManagerName);
	
	/**
	 * Provide a method to stop a device manager
	 * @param deviceManagerName is the name of the device manager
	 * @return true if the device manager actually stop, false instead
	 */
	public Boolean stopDeviceManager(String deviceManagerName);
	
	/**
	 * Add a listener for PWAL devices events
	 * @param listener
	 */
	public void addPwalDeviceListener(PWALDeviceListener listener);
	
	/**
	 * Remove a listener for PWAL devices events
	 * @param listener
	 */
	public void removePwalDeviceListener(PWALDeviceListener listener);
	
	/**
	 * Get the pwal FQDN if defined
	 */
	public String getFqdn();
	
	/**
	 * Start managers after initialization
	 * @param autostartOnly
	 */
	void startManagers(boolean autostartOnly);
}
