/**
 * 
 */
package br.com.cams7.arduino.util;

import java.util.Properties;

import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

/**
 * @author cams7
 *
 */
public class ArduinoStatus {
	// Numero maximo da porta e 63
	public static final byte PIN_MAX = 0x3F;
	// Valor maximo da porta e 1023
	public static final short PIN_VALUE_MAX = 0x03FF;

	private static byte[] pinsDigital;
	private static byte[] pinsDigitalPWM;
	private static byte[] pinsAnalog;

	static {
		try {
			Properties pins = AppUtil.getPropertiesFile(ArduinoStatus.class,
					"pins.properties");
			final String SEPARATE = ",";

			pinsDigital = getPins(pins.getProperty("PIN_DIGITAL").trim()
					.split(SEPARATE));
			pinsDigitalPWM = getPins(pins.getProperty("PIN_DIGITAL_PWM").trim()
					.split(SEPARATE));
			pinsAnalog = getPins(pins.getProperty("PIN_ANALOG").trim()
					.split(SEPARATE));
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	private Transmitter transmitter;
	private Status status;
	private PinType pinType;

	private byte pin;
	private short pinValue;

	public ArduinoStatus() {
		super();

		setTransmitter(Transmitter.PC);
	}

	public ArduinoStatus(PinType pinType, byte pin, short pinValue,
			Status status) {
		this();

		setPinType(pinType);
		setPin(pin);
		setPinValue(pinValue);

		setStatus(status);
	}

	private static byte[] getPins(final String[] values) {
		byte[] pins = new byte[values.length];
		for (byte i = 0; i < values.length; i++)
			pins[i] = Byte.parseByte(values[i].trim());
		return pins;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[transmitter = " + getTransmitter()
				+ ", status = " + getStatus() + ", pinType = " + getPinType()
				+ ", pin = " + getPin() + ", pinValue = " + getPinValue() + "]";
	}

	public Transmitter getTransmitter() {
		return transmitter;
	}

	public void setTransmitter(Transmitter transmitter) {
		this.transmitter = transmitter;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public PinType getPinType() {
		return pinType;
	}

	public void setPinType(PinType pinType) {
		this.pinType = pinType;
	}

	public byte getPin() {
		return pin;
	}

	public void setPin(byte pin) {
		this.pin = pin;
	}

	public short getPinValue() {
		return pinValue;
	}

	public void setPinValue(short pinValue) {
		this.pinValue = pinValue;
	}

	public enum Transmitter {
		ARDUINO, // Mensagem enviada do Arduino
		PC; // Mensagem enviada do PC
	}

	public enum Status {
		SEND, // Mensagem de envio que nao exige uma resposta
		SEND_RESPONSE, // Mensagem de envio que exige uma resposta
		RESPONSE, // Mensagem de resposta
		RESPONSE_RESPONSE;// Mensagem de resposta que exige outra resposta
	}

	public enum PinType {
		DIGITAL, // Porta Digital
		ANALOG; // Porta Analogica
	}

	public static byte[] getPinsDigital() {
		return pinsDigital;
	}

	public static byte[] getPinsDigitalPWM() {
		return pinsDigitalPWM;
	}

	public static byte[] getPinsAnalog() {
		return pinsAnalog;
	}

}
