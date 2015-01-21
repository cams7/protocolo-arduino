/**
 * 
 */
package br.com.cams7.arduino;

/**
 * @author cams7
 *
 */
public interface ArduinoService {
	/**
	 * Porta Serial
	 * 
	 * @return
	 */
	public String getSerialPort();

	/**
	 * Baud rate
	 * 
	 * @return
	 */
	public int getSerialBaudRate();

	/**
	 * Serial thread time
	 * 
	 * @return
	 */
	public long getSerialThreadTime();
}
