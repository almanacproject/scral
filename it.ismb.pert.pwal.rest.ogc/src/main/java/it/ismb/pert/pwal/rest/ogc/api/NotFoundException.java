package it.ismb.pert.pwal.rest.ogc.api;

public class NotFoundException extends ApiException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;
	public NotFoundException (int code, String msg) {
		super(code, msg);
		this.setCode(code);
	}
	/**
	 * @return the code
	 */
	public int getCode()
	{
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code)
	{
		this.code = code;
	}
}
