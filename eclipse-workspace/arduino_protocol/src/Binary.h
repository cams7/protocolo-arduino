/*
 * Binary.h
 *
 *  Created on: 13/01/2015
 *      Author: cams7
 */

#ifndef BINARY_H_
#define BINARY_H_

#include <Arduino.h>

namespace SISBARC {

class Binary {
private:
	static uint8_t *intToBytes(uint32_t value, uint8_t totalBytes);
	static uint32_t bytesToInt(uint8_t const values[], uint8_t totalBytes);

	static unsigned char *intToStringBytes(uint8_t totalBits, uint32_t value);

public:
	static const uint8_t MAX_1BYTE;
	static const uint16_t MAX_2BYTE;
	static const uint32_t MAX_3BYTE;
	static const uint32_t MAX_4BYTE;

	static uint8_t *intTo4Bytes(uint32_t value);
	static uint8_t *intTo3Bytes(uint32_t value);
	static uint8_t *intTo2Bytes(uint16_t value);

	static uint32_t bytesToInt32(uint8_t const values[]);

	static unsigned char *intToString4Bytes(uint32_t value);
	static unsigned char *intToString3Bytes(uint32_t value);
	static unsigned char *intToString2Bytes(uint16_t value);
	static unsigned char *intToString1Byte(uint8_t value);

	static uint8_t getValueBit(uint32_t value, uint8_t index);
};

} /* namespace SISBARC */

#endif /* BINARY_H_ */
