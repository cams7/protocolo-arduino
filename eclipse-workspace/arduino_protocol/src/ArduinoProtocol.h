/*
 * ArduinoProtocol.h
 *
 *  Created on: 14/01/2015
 *      Author: cams7
 */

#ifndef ARDUINOPROTOCOL_H_
#define ARDUINOPROTOCOL_H_

#include "ArduinoStatus.h"
#include "Binary.h"
#include "Checksum.h"

namespace SISBARC {

class ArduinoProtocol {
private:
	static const uint32_t EMPTY_BITS;

	static const uint8_t TOTAL_BITS_PROTOCOL;

	static const uint8_t TOTAL_BITS_INDEX;
	static const uint8_t TOTAL_BITS_CHECKSUM;
	static const uint8_t TOTAL_BITS_DATA;

	static const uint8_t TOTAL_BITS_PIN;
	static const uint8_t TOTAL_BITS_PIN_VALUE;

	static uint32_t encode(ArduinoStatus* arduino);

	static ArduinoStatus *decode(uint8_t const message[]);

public:
	static const uint8_t TOTAL_BYTES_PROTOCOL;

	static uint8_t *send(pin_type pinType, uint8_t pin, uint16_t pinValue,
			status statusValue);

	static ArduinoStatus *receive(uint8_t const message[]);
};

} /* namespace SISBARC */

#endif /* ARDUINOPROTOCOL_H_ */
