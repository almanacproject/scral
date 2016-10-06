/**
 * 
 */
package it.ismb.pert.pwal.rest.ogc.api;

import it.ismb.pertlab.pwal.api.internal.Pwal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author bonino
 *
 */
public abstract class PwalRestApi
{
	//the class logger
	protected Logger logger;
	
	@Autowired
	@Qualifier("PWAL")
	protected Pwal pwal;
	
	/**
	 * 
	 */
	public PwalRestApi()
	{
		// TODO Auto-generated constructor stub
		this.logger = LoggerFactory.getLogger(PwalRestApi.class);
	}
	/**
	 * @return the pwal
	 */
	public Pwal getPwal()
	{
		return pwal;
	}
	/**
	 * @param pwal the pwal to set
	 */
	public void setPwal(Pwal pwal)
	{
		this.pwal = pwal;
	}	
}
