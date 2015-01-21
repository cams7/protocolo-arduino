/*
 * Arduino.cpp
 *
 *  Created on: 21/01/2015
 *      Author: cams7
 */

#include "ArduinoStatus.h"

namespace SISBARC {

const transmitter ArduinoStatus::ARDUINO = 0x00;
const transmitter ArduinoStatus::PC = 0x01;

const status ArduinoStatus::SEND = 0x00;
const status ArduinoStatus::SEND_RESPONSE = 0x01;
const status ArduinoStatus::RESPONSE = 0x02;
const status ArduinoStatus::RESPONSE_RESPONSE = 0x03;

const pin_type ArduinoStatus::DIGITAL = 0x00;
const pin_type ArduinoStatus::ANALOG = 0x01;

const uint8_t ArduinoStatus::PIN_MAX = 0x3F; //63
const uint16_t ArduinoStatus::PIN_VALUE_MAX = 0x3FF; //1023

ArduinoStatus::ArduinoStatus() {
	setTransmitterValue(ARDUINO);

	setPinType(0x00);
	setPin(0x00);
	setPinValue(0x0000);

	setStatusValue(0x00);
}

ArduinoStatus::ArduinoStatus(pin_type pinType, uint8_t pin, uint16_t pinValue,
		status statusValue) {
	setTransmitterValue(ARDUINO);

	setPinType(pinType);
	setPin(pin);
	setPinValue(pinValue);

	setStatusValue(statusValue);
}

ArduinoStatus::~ArduinoStatus() {
	// TODO Auto-generated destructor stub
}

transmitter ArduinoStatus::getTransmitterValue(void) {
	return transmitterValue;
}

void ArduinoStatus::setTransmitterValue(transmitter transmitterValue) {
	this->transmitterValue = transmitterValue;
}

status ArduinoStatus::getStatusValue(void) {
	return statusValue;
}

void ArduinoStatus::setStatusValue(status statusValue) {
	this->statusValue = statusValue;
}

pin_type ArduinoStatus::getPinType(void) {
	return pinType;
}

void ArduinoStatus::setPinType(pin_type pinType) {
	this->pinType = pinType;
}

uint8_t ArduinoStatus::getPin(void) {
	return pin;
}

void ArduinoStatus::setPin(uint8_t pin) {
	this->pin = pin;
}

uint16_t ArduinoStatus::getPinValue(void) {
	return pinValue;
}

void ArduinoStatus::setPinValue(uint16_t pinValue) {
	this->pinValue = pinValue;
}

} /* namespace SISBARC */
