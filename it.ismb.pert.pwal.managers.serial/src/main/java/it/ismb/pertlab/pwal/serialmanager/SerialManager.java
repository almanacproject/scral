package it.ismb.pertlab.pwal.serialmanager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.manager.serial.SerialDeviceDescriptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Each sensor using this manager must send a message
 * 
 */
public class SerialManager extends DevicesManager implements SerialPortEventListener
{
	
	// Map< port string, port object>
	private Map<String, SerialPort> ports = new HashMap<>();
	// Map< port string, queue>
	private Map<String, ArrayBlockingQueue<Byte>> queue = new HashMap<>();
	// Map< port string, queue thread>
	private Map<String, MessageQueue> queueThreads = new HashMap<>();
	// Map< port, lista di id >
	private Map<String, List<String>> idsPort;
	// Map< device id, object class name>
	private Map<String, SerialDeviceDescriptor> configuredDevices;
	
	/***
	 * This constructor create the SerialManager
	 * 
	 * @param props
	 *            a map with device ids (id_flowSensor...) as key and the
	 *            complete class name as value
	 * @param serialPorts
	 *            a comma separated string containing all the serial ports paths
	 *            (COM7,COM8...)
	 */
	public SerialManager(Map<String, SerialDeviceDescriptor> devices)
	{
		this.initCommon(devices, false);
	}
	
	public SerialManager(Map<String, SerialDeviceDescriptor> devices, boolean autostart)
	{
		this.initCommon(devices, autostart);
	}
	
	private void initCommon(Map<String, SerialDeviceDescriptor> devices, boolean autostart)
	{
		// store the configured devices
		this.configuredDevices = devices;
		
		// iterate over devices and prepare serial connections
		for (String deviceDescriptorId : devices.keySet())
		{
			// get the current device descriptor
			SerialDeviceDescriptor deviceDescriptor = devices.get(deviceDescriptorId);
			
			// set up the serial connection for this device
			try
			{
				this.setUpEventQueue(deviceDescriptor);
				this.addManagedSerialPort(deviceDescriptor);
			}
			catch (SerialPortException e)
			{
				// log the error
				DevicesManager.log.error("Error while binding the serial port" + deviceDescriptor.getPort(), e);
			}
			
		}
		
		this.autostart = autostart;
	}
	
	private void setUpEventQueue(SerialDeviceDescriptor deviceDescriptor)
	{
		// prepare the queue to be used by the serial manager to handle messages
		// coming from the serial port
		ArrayBlockingQueue<Byte> portQueue = new ArrayBlockingQueue<Byte>(2048);
		
		// store the queue
		this.queue.put(deviceDescriptor.getPort(), portQueue);
		
		// create a proper queue handling thread for the port
		MessageQueue portQueueThread = new MessageQueue(portQueue, this);
		
		// store the thread reference
		this.queueThreads.put(deviceDescriptor.getPort(), portQueueThread);
		
		// start the message handling thread
		portQueueThread.start();
	}
	
	private void addManagedSerialPort(SerialDeviceDescriptor deviceDescriptor) throws SerialPortException
	{
		// create the serial port stub
		SerialPort serialPort = new SerialPort(deviceDescriptor.getPort());
		
		// store the stub in the "OLD" way
		this.ports.put(deviceDescriptor.getPort(), serialPort);
		
		// initialize the serial port
		
		// open the port
		serialPort.openPort();
		
		// set the port parameters for the specific device
		serialPort.setParams(deviceDescriptor.getBaudrate(), deviceDescriptor.getDatabits(),
				deviceDescriptor.getStopbits(), deviceDescriptor.getParity());
		
		// add this class as serial event listener for the port
		serialPort.addEventListener(this);
		
	}
	
	@Override
	public void run()
	{
		discoverDevice();
		synchronized (this)
		{
			try
			{
				this.wait();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void discoverDevice()
	{
		for (String deviceId : configuredDevices.keySet())
		{
			try
			{
				// the current device descriptor
				SerialDeviceDescriptor deviceDescriptor = this.configuredDevices.get(deviceId);
				
				// get the device class
				Class<?> deviceClazz = deviceDescriptor.getClazz();
				
				// declare the class constructor
				Constructor<?> constructor = null;
				
				// declare the device manager instance
				Device device = null;
				
				// handle old-type drivers
				if ((deviceDescriptor.getClazzParameters() != null)
						&& (!deviceDescriptor.getClazzParameters().isEmpty()))
				{
					constructor = deviceClazz.getConstructor(SerialManager.class, Properties.class);
					device = (Device) constructor.newInstance(this, deviceDescriptor.getClazzParameters());
				}
				else
				{
					constructor = deviceClazz.getConstructor(SerialManager.class);
					device = (Device) constructor.newInstance(this);
				}
				
				// set the device id
				device.setId(deviceId);
				
				// PWAL crap stuff
				if (!this.devicesDiscovered.containsKey(deviceId))
				{
					List<Device> ld = new ArrayList<>();
					this.devicesDiscovered.put(deviceId, ld);
				}
				devicesDiscovered.get(deviceId).add(device);
				for (DeviceListener l : deviceListener)
				{
					try
					{
						Thread.sleep(2000);
					}
					catch (InterruptedException e)
					{
						log.error("Exception: ", e);
					}
					l.notifyDeviceAdded(device);
				}
				log.debug("Device discovered: id=" + deviceId);
			}
			catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e)
			{
				log.error("SerialManager:", e);
			}
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent arg0)
	{
		
		if (arg0.isRXCHAR())
		{// If data is available
			try
			{
				String justread = ports.get(arg0.getPortName()).readString();
				if (justread == null)
				{
					return;
				}
				byte[] bl = justread.getBytes();
				for (int i = 0; i < bl.length; i++)
				{
					// log.debug("put message in the queue: "+(char)bl[i]);
					queueThreads.get(arg0.getPortName()).put(bl[i]);
				}
			}
			catch (SerialPortException ex)
			{
				System.out.println(ex);
			}
		}
		else if (arg0.isCTS())
		{// If CTS line has changed state
			if (arg0.getEventValue() == 1)
			{// If line is ON
				System.out.println("CTS - ON");
			}
			else
			{
				System.out.println("CTS - OFF");
			}
		}
		else if (arg0.isDSR())
		{// /If DSR line has changed state
			if (arg0.getEventValue() == 1)
			{// If line is ON
				System.out.println("DSR - ON");
			}
			else
			{
				System.out.println("DSR - OFF");
			}
		}
	}
	
	public void dispatchMessage(String cs)
	{
		try
		{
			log.info("going to dispatch message: " + cs);
			String[] data = cs.split(" ");
			if (data.length < 2)
			{
				return;
			}
			// modificato perchè struttura dati di devicesDiscovered è diventata
			// Map<String,List<Device>>
			BaseSerialDevice d = (BaseSerialDevice) devicesDiscovered.get(data[0]).get(0);
			if (d == null)
			{
				return;
			}
			String payload = "";
			for (int i = 1; i < data.length; i++)
				payload += data[i];
			d.messageReceived(payload);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendCommand(String deviceId, String string)
	{
		try
		{
			// get all the port
			for (String port : idsPort.keySet())
			{
				List<String> ids = idsPort.get(port);
				for (String id : ids)
				{
					if (id.equals(deviceId))
					{
						ports.get(port).writeString(string);
						break;
					}
				}
			}
			
		}
		catch (SerialPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String getNetworkType()
	{
		return DeviceNetworkType.SERIAL;
	}
	
}
