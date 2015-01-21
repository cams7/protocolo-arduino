/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.arduino.ArduinoServiceImpl;
import br.com.cams7.arduino.ArduinoException;
import br.com.cams7.sisbarc.aal.jmx.service.AppArduinoServiceMBean;

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
	 * @see br.com.cams7.arduino.Arduino#envioOK()
	 */
	protected void envioOK() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.arduino.Arduino#envioError()
	 */
	protected void envioError() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.arduino.Arduino#setDadoRecebido(int)
	 */
	protected void setDadoRecebido(int dadoRecebido) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.arduino.Arduino#recebimentoError()
	 */
	protected void recebimentoError() {
		System.out
				.println("O prototipo no Proteus nao esta rodando ou a porta '"
						+ getSerialPort() + "' esta fechada");

	}

}
