/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.arduino.ArduinoException;
import br.com.cams7.arduino.ArduinoServiceImpl;
import br.com.cams7.arduino.util.ArduinoStatus;
import br.com.cams7.arduino.util.ArduinoStatus.Status;
import br.com.cams7.sisbarc.aal.LedStatus;
import br.com.cams7.sisbarc.aal.LedType;

/**
 * @author cesar
 *
 */
public class AppArduinoService extends ArduinoServiceImpl implements
		AppArduinoServiceMBean {

	public AppArduinoService(String serialPort, int baudRate, long threadTime)
			throws ArduinoException {
		super(serialPort, baudRate, threadTime);
		System.out.println("Novo Servico");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.arduino.ArduinoServiceImpl#receiveDataBySerial(br.com.cams7
	 * .arduino.util.ArduinoStatus)
	 */
	protected void receiveDataBySerial(ArduinoStatus arduino) {
		System.out.println("Data: " + arduino);
	}

	// @Override
	// public void mudaStatusLED(LedType ledType, LedStatus ledStatus) {
	private void mudaStatusLED(int ledType, int ledStatus) {

		Byte pin = null;
		switch (ledType) {
		case 0:// AMARELA:
			pin = 0x0B;// Pin 11
			break;
		case 1:// VERDE:
			pin = 0x0A;// Pin 10
			break;
		case 2:// VERMELHA:
			pin = 0x09;// Pin 09
			break;
		default:
			break;
		}

		Boolean pinValue = null;
		switch (ledStatus) {
		case 1:// ACESA:
			pinValue = true;
			break;
		case 0:// APAGADA:
			pinValue = false;
			break;
		default:
			break;
		}

		if (pin != null && pinValue != null)
			try {
				sendPinDigital(pin, pinValue, Status.SEND_RESPONSE);
				// sendPinPWM(pin, (short) (pinValue ? 255 :
				// 0),Status.RESPONSE_RESPONSE);
				// sendPinAnalog(pin, (short) (pinValue ? 1023 : 0),
				// Status.RESPONSE);

			} catch (ArduinoException e) {
				e.printStackTrace();
			}

	}

	@Override
	public void ledAmarelaAcesa() {
		mudaStatusLED(0, 1);
	}

	@Override
	public void ledAmarelaAPagada() {
		mudaStatusLED(0, 0);
	}

	@Override
	public void ledVerdeAcesa() {
		mudaStatusLED(1, 1);
	}

	@Override
	public void ledVerdeAPagada() {
		mudaStatusLED(1, 0);
	}

	@Override
	public void ledVermelhaAcesa() {
		mudaStatusLED(2, 1);
	}

	@Override
	public void ledVermelhaAPagada() {
		mudaStatusLED(2, 0);
	}

}
