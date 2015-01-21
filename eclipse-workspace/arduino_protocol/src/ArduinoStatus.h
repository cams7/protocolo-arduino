/*
 * Arduino.h
 *
 *  Created on: 21/01/2015
 *      Author: cams7
 */

#ifndef ARDUINOSTATUS_H_
#define ARDUINOSTATUS_H_

#include <Arduino.h>

namespace SISBARC {

typedef uint8_t transmitter;

typedef uint8_t status;

typedef uint8_t pin_type;

class ArduinoStatus {
private:
	transmitter transmitterValue;
	status statusValue;
	pin_type pinType;

	uint8_t pin;
	uint16_t pinValue;
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

	ArduinoStatus();
	ArduinoStatus(pin_type pinType, uint8_t pin, uint16_t pinValue,
			status statusValue);

	virtual ~ArduinoStatus();

	transmitter getTransmitterValue(void);
	void setTransmitterValue(transmitter transmitterValue);

	status getStatusValue(void);
	void setStatusValue(status statusValue);

	pin_type getPinType(void);
	void setPinType(pin_type pinType);

	uint8_t getPin(void);
	void setPin(uint8_t pin);

	uint16_t getPinValue(void);
	void setPinValue(uint16_t pinValue);
};

} /* namespace SISBARC */

#endif /* ARDUINOSTATUS_H_ */
