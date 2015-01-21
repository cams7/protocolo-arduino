/**
 * 
 */
package br.com.cams7.arduino;

/**
 * @author cesar
 *
 */
public class ArduinoException extends Exception {

	private static final long serialVersionUID = 1L;

	public ArduinoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ArduinoException(String msg) {
		super(msg);
	}

	public ArduinoException(Throwable cause) {
		super(cause);
	}

}
