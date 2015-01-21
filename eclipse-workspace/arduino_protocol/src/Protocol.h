/*
 * ArduinoProtocol.h
 *
 *  Created on: 14/01/2015
 *      Author: cams7
 */

#ifndef PROTOCOL_H_
#define PROTOCOL_H_

#include "Binary.h"
#include "Checksum.h"

namespace SISBARC {

typedef uint8_t transmitter;

typedef uint8_t status;

typedef uint8_t pin_type;

class Protocol {
private:
	static const uint32_t EMPTY_BITS;

	static const uint8_t TOTAL_BITS_PROTOCOL;

	static const uint8_t TOTAL_BITS_INDEX;
	static const uint8_t TOTAL_BITS_CHECKSUM;
	static const uint8_t TOTAL_BITS_DATA;

	static const uint8_t TOTAL_BITS_PIN;
	static const uint8_t TOTAL_BITS_PIN_VALUE;

	static uint32_t encode(uint8_t pin, pin_type pinType, uint16_t pinValue,
			status statusValue);

	static uint16_t *decode(uint8_t const message[]);

public:
	static const transmitter ARDUINO; //Mensagem enviada do Arduino
	static const transmitter PC; //Mensagem enviada do PC

	static const status SEND; //Mensagem de envio que nao exige uma resposta
	static const status SEND_RESPONSE; //Mensagem de envio que exige uma resposta
	static const status RESPONSE; //Mensagem de resposta
	static const status RESPONSE_RESPONSE; //Mensagem de resposta que exige outra resposta

	static const pin_type DIGITAL; //Porta Digital
	static const pin_type ANALOG; //Porta Analogica

	static const uint8_t PIN_MAX;        //Numero maximo da porta e 63
	static const uint16_t PIN_VALUE_MAX; //Valor maximo da porta e 1023

	static uint8_t *send(uint8_t pin, pin_type pinType, uint16_t pinValue,
			status statusValue);

	static uint16_t *receive(uint8_t const message[]);
};

} /* namespace SISBARC */

#endif /* PROTOCOL_H_ */
