/**
 * 
 */
package br.com.cams7.arduino.util;

/**
 * @author cams7
 *
 */
public class Arduino {
	public static final byte PIN_MAX = 0x3F; // Numero maximo da porta e 63
	public static final short PIN_VALUE_MAX = 0x03FF; // Valor maximo da porta e
														// 1023

	private Transmitter transmitter;
	private Status status;
	private PinType pinType;

	private int pin;
	private int pinValue;

	public Arduino() {
		super();
	}

	@Override
	public String toString() {
		return "[transmitter = " + getTransmitter() + ", status = "
				+ getStatus() + ", pinType = " + getPinType() + ", pin = "
				+ getPin() + ", pinValue = " + getPinValue() + "]";
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

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public int getPinValue() {
		return pinValue;
	}

	public void setPinValue(int pinValue) {
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

}
