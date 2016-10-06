/**
 * 
 */
package it.ismb.pertlab.pwal.manager.serial;

import java.util.Properties;

/**
 * @author bonino
 *
 */
public class SerialDeviceDescriptor
{
	// the device class
	private Class<?> clazz;
	
	// the parameters to be passed to the class constructor 
	private Properties clazzParameters;
	
	// the serial bit rate
	private int baudrate;
	
	// the number of databits to parse
	private int databits;
	
	// the number of stopbits used in the connection
	private int stopbits;
	
	// the kind of parity check to be used
	private int parity;
	
	// the serial port to which the device is connected
	private String port;
	
	/**
	 * 
	 */
	public SerialDeviceDescriptor()
	{
		//empty constructor to fulfill the bean instantiation pattern
	}

	/**
	 * @return the clazz
	 */
	public Class<?> getClazz()
	{
		return clazz;
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(Class<?> clazz)
	{
		this.clazz = clazz;
	}
	
	public void setClazz(String clazz) throws ClassNotFoundException
	{
		if((clazz!=null)&&(!clazz.isEmpty()))
				this.clazz =  Class.forName(clazz);
	}

	/**
	 * @return the clazzParameters
	 */
	public Properties getClazzParameters()
	{
		return clazzParameters;
	}

	/**
	 * @param clazzParameters the clazzParameters to set
	 */
	public void setClazzParameters(Properties clazzParameters)
	{
		this.clazzParameters = clazzParameters;
	}

	/**
	 * @return the baudrate
	 */
	public int getBaudrate()
	{
		return baudrate;
	}

	/**
	 * @param baudrate the baudrate to set
	 */
	public void setBaudrate(int baudrate)
	{
		this.baudrate = baudrate;
	}

	/**
	 * @return the databits
	 */
	public int getDatabits()
	{
		return databits;
	}

	/**
	 * @param databits the databits to set
	 */
	public void setDatabits(int databits)
	{
		this.databits = databits;
	}

	/**
	 * @return the stopbits
	 */
	public int getStopbits()
	{
		return stopbits;
	}

	/**
	 * @param stopbits the stopbits to set
	 */
	public void setStopbits(int stopbits)
	{
		this.stopbits = stopbits;
	}

	/**
	 * @return the parity
	 */
	public int getParity()
	{
		return parity;
	}

	/**
	 * @param parity the parity to set
	 */
	public void setParity(int parity)
	{
		this.parity = parity;
	}

	/**
	 * @return the port
	 */
	public String getPort()
	{
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port)
	{
		this.port = port;
	}
	
	
	
}
