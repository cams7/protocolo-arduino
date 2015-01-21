package br.com.cams7.arduino.util;

import br.com.cams7.arduino.ArduinoException;
import br.com.cams7.arduino.util.Arduino.PinType;
import br.com.cams7.arduino.util.Arduino.Status;
import br.com.cams7.arduino.util.Arduino.Transmitter;

public final class Protocol {

	// Total de BITs do protocolo - 32 bits
	private static final byte TOTAL_BITS_PROTOCOL = 0x20;
	// Total de BITs reservado para o INDICE - 4 bits
	private static final byte TOTAL_BITS_INDEX = 0x04;
	// Total de BITs reservado para o CRC - 8 bits
	// private static final byte TOTAL_BITS_CHECKSUM = 0x08;

	// Total de BITs reservado para os DADOs - 20 bits
	// private static final byte TOTAL_BITS_DATA = TOTAL_BITS_PROTOCOL -
	// TOTAL_BITS_INDEX - TOTAL_BITS_CHECKSUM;

	// Total de BITs reservado para o PINO - 6 bits
	private static final byte TOTAL_BITS_PIN = 0x06;
	// Total de BITs reservado
	private static final byte TOTAL_BITS_PIN_VALUE = 0x0A;

	// para o VALOR do pino - 10
	// bits

	private static Arduino decode(Integer[] values) throws ArduinoException {
		int protocol = 0x00000000;
		final int TOTAL_BYTES = values.length;

		for (byte i = 0; i < TOTAL_BYTES; i++) {
			int byteValue = values[i];
			byteValue <<= ((TOTAL_BYTES - i - 1) * 7);

			int mask = 0x0000007F << ((TOTAL_BYTES - i - 1) * 7);
			protocol |= (byteValue & mask);
		}

		int checksumProtocol = protocol & 0x000000FF;// 0000_0000_000000_0000000000_11111111

		int checksum = checksumProtocol;

		if (checksumProtocol != checksum)
			throw new ArduinoException("CRC invalido");

		int transmitterValue = (protocol & 0x08000000) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 1); // 0000_1000_000000_0000000000_00000000
		int statusValue = (protocol & 0x06000000) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 3); // 0000_0110_000000_0000000000_00000000
		int pinTypeValue = (protocol & 0x01000000) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 4); // 0000_0001_000000_0000000000_00000000

		int pin = (protocol & 0x00FC0000) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 4 - TOTAL_BITS_PIN);// 0000_0000_111111_0000000000_00000000
		int pinValue = (protocol & 0x0003FF00) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 4 - TOTAL_BITS_PIN - TOTAL_BITS_PIN_VALUE);// 0000_0000_000000_1111111111_00000000

		Transmitter transmitter = getTransmitter(transmitterValue);
		Status status = getStatus(statusValue);
		PinType pinType = getPinType(pinTypeValue);

		Arduino arduino = new Arduino();
		arduino.setTransmitter(transmitter);
		arduino.setStatus(status);
		arduino.setPinType(pinType);
		arduino.setPin(pin);
		arduino.setPinValue(pinValue);

		return arduino;
	}

	private static Transmitter getTransmitter(int transmitterValue) {
		Transmitter transmitter = null;
		switch (transmitterValue) {
		case 0:
			transmitter = Transmitter.ARDUINO;
			break;
		case 1:
			transmitter = Transmitter.PC;
			break;
		default:
			break;
		}
		return transmitter;
	}

	private static Status getStatus(int statusValue) {
		Status status = null;
		switch (statusValue) {
		case 0:
			status = Status.SEND;
			break;
		case 1:
			status = Status.SEND_RESPONSE;
			break;
		case 2:
			status = Status.RESPONSE;
			break;
		case 3:
			status = Status.RESPONSE_RESPONSE;
			break;
		default:
			break;
		}
		return status;
	}

	private static PinType getPinType(int pinTypeValue) {
		PinType pinType = null;
		switch (pinTypeValue) {
		case 0:
			pinType = PinType.DIGITAL;
			break;
		case 1:
			pinType = PinType.ANALOG;
			break;
		default:
			break;
		}
		return pinType;
	}

	public static Arduino receive(Integer[] values) throws ArduinoException {
		return decode(values);
	}

}
