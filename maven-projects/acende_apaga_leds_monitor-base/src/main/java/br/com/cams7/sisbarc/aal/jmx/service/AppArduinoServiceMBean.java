/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.arduino.ArduinoService;

/**
 * @author cesar
 *
 */
public interface AppArduinoServiceMBean extends ArduinoService {
	// public void mudaStatusLED(LedType ledType, LedStatus ledStatus);
	// public void mudaStatusLED(int ledType, int ledStatus);

	public void ledAmarelaAcesa();

	public void ledAmarelaAPagada();

	public void ledVerdeAcesa();

	public void ledVerdeAPagada();

	public void ledVermelhaAcesa();

	public void ledVermelhaAPagada();
}
