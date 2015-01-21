/**
 * 
 */
package br.com.cams7.util;

/**
 * @author cams7
 *
 */
public class AppException extends Exception {

	private static final long serialVersionUID = 1L;

	public AppException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AppException(String msg) {
		super(msg);
	}

	public AppException(Throwable cause) {
		super(cause);
	}

}
