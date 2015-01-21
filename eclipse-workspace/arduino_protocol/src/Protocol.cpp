/*
 * ArduinoProtocol.cpp
 *
 *  Created on: 14/01/2015
 *      Author: cams7
 */

#include "Protocol.h"

namespace SISBARC {

const transmitter Protocol::ARDUINO = 0x00;
const transmitter Protocol::PC = 0x01;

const status Protocol::SEND = 0x00;
const status Protocol::SEND_RESPONSE = 0x01;
const status Protocol::RESPONSE = 0x02;
const status Protocol::RESPONSE_RESPONSE = 0x03;

const pin_type Protocol::DIGITAL = 0x00;
const pin_type Protocol::ANALOG = 0x01;

const uint8_t Protocol::PIN_MAX = 0x3F; //63
const uint16_t Protocol::PIN_VALUE_MAX = 0x3FF; //1023

const uint32_t Protocol::EMPTY_BITS = 0x00000000;

const uint8_t Protocol::TOTAL_BITS_PROTOCOL = 0x20; //Total de BITs do protocolo - 32 bits

const uint8_t Protocol::TOTAL_BITS_INDEX = 0x04; //Total de BITs reservado para o INDICE - 4 bits
const uint8_t Protocol::TOTAL_BITS_CHECKSUM = 0x08; //Total de BITs reservado para o CRC - 8 bits
const uint8_t Protocol::TOTAL_BITS_DATA = TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX
		- TOTAL_BITS_CHECKSUM; //Total de BITs reservado para os DADOs - 20 bits

const uint8_t Protocol::TOTAL_BITS_PIN = 0x06; //Total de BITs reservado para o PINO - 6 bits
const uint8_t Protocol::TOTAL_BITS_PIN_VALUE = 0x0A; //Total de BITs reservado para o VALOR do pino - 10 bits

//cria o protocolo
uint32_t Protocol::encode(uint8_t pin, pin_type pinType, uint16_t pinValue,
		status statusValue) {

	//Protocolo Arduino
	//0000 0 00 0 000000 0000000000 00000000

	//0000 _ Ordem bytes                                    4bits

	//0/1 _ ARDUINO/PC                                      1bit
	//0-3 _ SEND/SEND_RESPONSE/RESPONSE/RESPONSE_RESPONSE   2bit
	//0/1 _ DIGITAL/ANALOG                                  1bit

	//0-63  _ PIN                                           6bits
	//0-123 _ VALOR PIN                                    10bits
	//0-255 _ CRC                                           8bits

	//Os pinos estao entre 0-63
	if (pin > PIN_MAX)
		return EMPTY_BITS;

	//O tipos de pino sao 0/1 = DIGITAL/ANALOG
	if (pinType > 0x01)
		return EMPTY_BITS;

	//Os valores do pino estao entre 0-1023
	if (pinValue > PIN_VALUE_MAX)
		return EMPTY_BITS;

	//Os status sao 0/1/2/3 = SEND/ SEND_RESPONSE/ RESPONSE/ RESPONSE_RESPONSE
	if (statusValue > 0x01)
		return EMPTY_BITS;

	//protocol = 0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0 0 0 0 0 0 0 0 0 0
	//   index = 31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 9 8 7 6 5 4 3 2 1 0

	uint32_t protocol = EMPTY_BITS;

	uint32_t aux = ARDUINO;
	aux <<= (TOTAL_BITS_DATA - 1);
	protocol |= (aux & 0x00080000); //00000000 00001000 00000000 00000000

	aux = statusValue;
	aux <<= (TOTAL_BITS_DATA - 3);
	protocol |= (aux & 0x00060000); //00000000 00000110 00000000 00000000

	aux = pinType;
	aux <<= (TOTAL_BITS_DATA - 4);
	protocol |= (aux & 0x00010000); //00000000 00000001 00000000 00000000

	aux = pin;
	aux <<= (TOTAL_BITS_DATA - 4 - TOTAL_BITS_PIN);
	protocol |= (aux & 0x0000FC00); //00000000 00000000 11111100 00000000

	aux = pinValue;
	protocol |= (aux & 0x000003FF); //00000000 00000000 00000011 11111111

	crc checksum = Checksum::getCrc3Bytes(protocol);

	protocol <<= TOTAL_BITS_CHECKSUM;

	aux = checksum;
	protocol |= (aux & 0x000000FF); //00000000 00000000 00000000 11111111

	const uint8_t TOTAL_BYTES = 0x04;

	uint8_t *pointer;
	pointer = (uint8_t*) malloc(TOTAL_BYTES);

	if (pointer == NULL)
		return EMPTY_BITS;

	for (uint8_t i = 0x00; i < TOTAL_BYTES; i++) { //00001111 11111111 11111111 11111111 -> 11111111 01111111 01111111 01111111
		aux = protocol;
		aux <<= (TOTAL_BITS_INDEX + (i * 7));
		aux >>= (TOTAL_BITS_PROTOCOL - 7);
		if (i == 0x00) {
			aux |= 0x00000080;
			aux = aux & 0x000000FF; //00000000 00000000 00000000 11111111
		} else
			aux = aux & 0x0000007F; //00000000 00000000 00000000 01111111

		*(pointer + i) = (uint8_t) aux;
	}

	protocol = Binary::bytesToInt32(pointer);

	free(pointer);

	return protocol;
}

uint8_t *Protocol::send(uint8_t pin, pin_type pinType, uint16_t pinValue,
		status statusValue) {
	uint32_t protocol = encode(pin, pinType, pinValue, statusValue);

	if (protocol == EMPTY_BITS)
		return NULL;

	return Binary::intTo4Bytes(protocol);
}

//Decodifica o protocolo
uint16_t *Protocol::decode(uint8_t const message[]) {
	const uint8_t TOTAL_BYTES = 0x04;

	uint32_t aux;
	uint32_t protocol = EMPTY_BITS;

	for (uint8_t i = 0; i < TOTAL_BYTES; i++) {
		aux = message[i];
		aux <<= ((TOTAL_BYTES - i - 1) * 7);

		uint32_t mask = 0x0000007F << ((TOTAL_BYTES - i - 1) * 7);
		protocol |= (aux & mask);
	}

	crc checksumProtocol = protocol & 0x000000FF; // 0000_0000_000000_0000000000_11111111

	aux = protocol >> TOTAL_BITS_CHECKSUM;

	crc checksum = Checksum::getCrc3Bytes(aux);

	//Verifica se os dados do protocolo nao foram corrompidos
	if (checksumProtocol != checksum)
		return NULL;

	uint16_t *pointer;
	const uint8_t TOTAL_VALUES = 0x05;

	pointer = (uint16_t*) malloc(TOTAL_VALUES);

	if (pointer == NULL)
		return NULL;

	//0/1 _ ARDUINO/PC      1bit
	aux = (protocol & 0x08000000)
			>> (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 1); // 0000_1000_000000_0000000000_00000000
	*(pointer) = aux;

	//0-3 _ SEND/SEND_RESPONSE/RESPONSE/RESPONSE_RESPONSE   2bit
	aux = (protocol & 0x06000000)
			>> (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 3); // 0000_0110_000000_0000000000_00000000
	*(pointer + 1) = aux;

	//0/1 _ DIGITAL/ANALOG  1bit
	aux = (protocol & 0x01000000)
			>> (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 4); // 0000_0001_000000_0000000000_00000000
	*(pointer + 2) = aux;

	//0-63  _ PIN           6bits
	aux = (protocol & 0x00FC0000)
			>> (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 4 - TOTAL_BITS_PIN); // 0000_0000_111111_0000000000_00000000
	*(pointer + 3) = aux;

	//0-123 _ VALOR PIN    10bits
	aux = (protocol & 0x0003FF00)
			>> (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 4 - TOTAL_BITS_PIN
					- TOTAL_BITS_PIN_VALUE); // 0000_0000_000000_1111111111_00000000
	*(pointer + 4) = aux;

	return pointer;
}

//Recebe protocolo
uint16_t *Protocol::receive(uint8_t const message[]) {
	return decode(message);
}

} /* namespace SISBARC */
