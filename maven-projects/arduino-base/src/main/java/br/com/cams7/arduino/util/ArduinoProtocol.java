package br.com.cams7.arduino.util;

import br.com.cams7.arduino.ArduinoException;
import br.com.cams7.arduino.util.ArduinoStatus.PinType;
import br.com.cams7.arduino.util.ArduinoStatus.Status;
import br.com.cams7.arduino.util.ArduinoStatus.Transmitter;

public final class ArduinoProtocol {

	private static final int EMPTY_BITS = 0x00000000;

	// Total de BITs do protocolo - 32 bits
	private static final byte TOTAL_BITS_PROTOCOL = 0x20;
	// Total de BITs reservado para o INDICE - 4 bits
	private static final byte TOTAL_BITS_INDEX = 0x04;
	// Total de BITs reservado para o CRC - 8 bits
	private static final byte TOTAL_BITS_CHECKSUM = 0x08;

	// Total de BITs reservado para os DADOs - 20 bits
	private static final byte TOTAL_BITS_DATA = TOTAL_BITS_PROTOCOL
			- TOTAL_BITS_INDEX - TOTAL_BITS_CHECKSUM;

	// Total de BITs reservado para o PINO - 6 bits
	private static final byte TOTAL_BITS_PIN = 0x06;

	// Total de BITs reservado para o VALOR do pino - 10 bits
	private static final byte TOTAL_BITS_PIN_VALUE = 0x0A;

	// Total de BYTEs do protocolo - 4 bytes
	public static final byte TOTAL_BYTES_PROTOCOL = 0x04;

	// cria o protocolo
	private static int encode(ArduinoStatus arduino) {

		// Protocolo Arduino
		// 0000 0 00 0 000000 0000000000 00000000

		// 0000 _ Ordem bytes 4bits

		// 0/1 _ ARDUINO/PC 1bit
		// 0-3 _ SEND/SEND_RESPONSE/RESPONSE/RESPONSE_RESPONSE 2bit
		// 0/1 _ DIGITAL/ANALOG 1bit

		// 0-63 _ PIN 6bits
		// 0-123 _ VALOR PIN 10bits
		// 0-255 _ CRC 8bits

		if (arduino == null)
			return EMPTY_BITS;

		// Os status sao 0/1/2/3 = SEND/ SEND_RESPONSE/ RESPONSE/
		// RESPONSE_RESPONSE
		if (arduino.getStatus() == null)
			return EMPTY_BITS;

		// O tipos de pino sao 0/1 = DIGITAL/ANALOG
		if (arduino.getPinType() == null)
			return EMPTY_BITS;

		// Os pinos estao entre 0-63
		if (arduino.getPin() > ArduinoStatus.PIN_MAX)
			return EMPTY_BITS;

		// Os valores do pino estao entre 0-1023
		if (arduino.getPinValue() > ArduinoStatus.PIN_VALUE_MAX)
			return EMPTY_BITS;

		// protocol = 0 0 0 0 0 0 0 0 0 0
		// index = 9 8 7 6 5 4 3 2 1 0

		int protocol = EMPTY_BITS;

		int transmitterValue = Transmitter.PC.ordinal();
		transmitterValue <<= (TOTAL_BITS_DATA - 1);
		protocol |= (transmitterValue & 0x00080000); // 000000000000_1000_000000_0000000000

		int statusValue = arduino.getStatus().ordinal();
		statusValue <<= (TOTAL_BITS_DATA - 3);
		protocol |= (statusValue & 0x00060000); // 000000000000_0110_000000_0000000000

		int pinType = arduino.getPinType().ordinal();
		pinType <<= (TOTAL_BITS_DATA - 4);
		protocol |= (pinType & 0x00010000); // 000000000000_0001_000000_0000000000

		int pin = arduino.getPin();
		pin <<= (TOTAL_BITS_DATA - 4 - TOTAL_BITS_PIN);
		protocol |= (pin & 0x0000FC00); // 000000000000_0000_111111_0000000000

		int pinValue = arduino.getPinValue();
		protocol |= (pinValue & 0x000003FF); // 000000000000_0000_000000_1111111111

		byte checksum = Checksum.getCrc3Bytes(protocol);

		protocol <<= TOTAL_BITS_CHECKSUM;

		protocol |= (checksum & 0x000000FF); // 0000_00000000000000000000_11111111

		byte[] bytes = new byte[TOTAL_BYTES_PROTOCOL];

		for (byte i = 0x00; i < TOTAL_BYTES_PROTOCOL; i++) { // 0000_1111_111111_1111111111_11111111->11111111_01111111_01111111_01111111
			int aux = protocol;
			aux <<= (TOTAL_BITS_INDEX + (i * 7));
			aux >>= (TOTAL_BITS_PROTOCOL - 7);
			if (i == 0x00) {
				aux |= 0x00000080;
				aux = aux & 0x000000FF; // 00000000 00000000 00000000 11111111
			} else
				aux = aux & 0x0000007F; // 00000000 00000000 00000000 01111111

			bytes[i] = (byte) aux;
		}

		protocol = Binary.bytesToInt32(bytes);

		return protocol;
	}

	public static byte[] send(PinType pinType, byte pin, short pinValue,
			Status statusValue) {
		ArduinoStatus arduino = new ArduinoStatus(pinType, pin, pinValue,
				statusValue);
		int protocol = encode(arduino);

		if (protocol == EMPTY_BITS)
			return null;

		return Binary.intTo4Bytes(protocol);
	}

	private static ArduinoStatus decode(byte[] values) throws ArduinoException {
		int protocol = 0x00000000;
		final int TOTAL_BYTES = values.length;

		for (byte i = 0; i < TOTAL_BYTES; i++) {
			int byteValue = values[i];
			byteValue <<= ((TOTAL_BYTES - i - 1) * 7);

			int mask = 0x0000007F << ((TOTAL_BYTES - i - 1) * 7);
			protocol |= (byteValue & mask);
		}

		byte checksumProtocol = (byte) (protocol & 0x000000FF);// 0000_0000_000000_0000000000_11111111

		int message = (protocol & 0x0FFFFF00) >> TOTAL_BITS_CHECKSUM;// 0000_1111_111111_1111111111_00000000

		byte checksum = Checksum.getCrc3Bytes(message);

		if (checksumProtocol != checksum)
			throw new ArduinoException("CRC invalido");

		byte transmitterValue = (byte) ((protocol & 0x08000000) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 1)); // 0000_1000_000000_0000000000_00000000
		byte statusValue = (byte) ((protocol & 0x06000000) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 3)); // 0000_0110_000000_0000000000_00000000
		byte pinTypeValue = (byte) ((protocol & 0x01000000) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 4)); // 0000_0001_000000_0000000000_00000000

		byte pin = (byte) ((protocol & 0x00FC0000) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 4 - TOTAL_BITS_PIN));// 0000_0000_111111_0000000000_00000000
		short pinValue = (short) ((protocol & 0x0003FF00) >> (TOTAL_BITS_PROTOCOL
				- TOTAL_BITS_INDEX - 4 - TOTAL_BITS_PIN - TOTAL_BITS_PIN_VALUE));// 0000_0000_000000_1111111111_00000000

		Transmitter transmitter = getTransmitter(transmitterValue);
		Status status = getStatus(statusValue);
		PinType pinType = getPinType(pinTypeValue);

		ArduinoStatus arduino = new ArduinoStatus();
		arduino.setTransmitter(transmitter);
		arduino.setStatus(status);
		arduino.setPinType(pinType);
		arduino.setPin(pin);
		arduino.setPinValue(pinValue);

		return arduino;
	}

	public static ArduinoStatus receive(byte[] values) throws ArduinoException {
		return decode(values);
	}

	private static Transmitter getTransmitter(byte transmitterValue) {
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

	private static Status getStatus(byte statusValue) {
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

	private static PinType getPinType(byte pinTypeValue) {
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

	public static void main(String[] args) {
		for (byte pin = 0x00; pin <= ArduinoStatus.PIN_MAX; pin++) {
			for (short pinValue = 0x0000; pinValue <= ArduinoStatus.PIN_VALUE_MAX; pinValue++) {
				byte[] protocol = send(PinType.DIGITAL, pin, pinValue,
						Status.SEND);

				try {
					ArduinoStatus arduino = receive(protocol);

					if (!(Transmitter.PC == arduino.getTransmitter()
							&& PinType.DIGITAL == arduino.getPinType()
							&& pin == arduino.getPin()
							&& pinValue == arduino.getPinValue() && Status.SEND == arduino
								.getStatus())) {
						System.out.println(arduino);
					}
				} catch (ArduinoException e) {
					System.err.println(e);

				}
			}
		}
	}

}
