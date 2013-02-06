package org.jboss.portlet.util;

/**
 * Indicates some exception thrown when calling netflix or parse results from netflix.
 * 
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class WrapperNetflixException extends Exception 
{
	private static final long serialVersionUID = 145435L;

	public WrapperNetflixException() 
	{
		super();
	}
	
	public WrapperNetflixException(String message) 
	{
		super(message);
	}
	
	public WrapperNetflixException(Throwable cause) 
	{
		super(cause);
	}	
	
	public WrapperNetflixException(String message, Throwable casue) 
	{
		super(message, casue);
	}	

}
