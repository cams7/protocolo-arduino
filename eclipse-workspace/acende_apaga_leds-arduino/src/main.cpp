/*
 main.cpp - Main loop for Arduino sketches
 Copyright (c) 2005-2013 Arduino Team.  All right reserved.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

#include <Arduino.h>
#include <ArduinoProtocol.h>
#include <ArduinoStatus.h>
#include <Thread.h>
#include <ThreadController.h>

#define BAUD_RATE 9600
#define LED_TIME  500 // 1 segundo
#define PIN_LED   13   //Pino 13

using namespace SISBARC;

//Declared weak in Arduino.h to allow user redefinitions.
int atexit(void (*func)()) {
	return 0;
}

// Weak empty variant initialization function.
// May be redefined by variant files.
void initVariant() __attribute__((weak));

int8_t statusLED = (int8_t) HIGH;

uint8_t pin = 0x00;
uint16_t pinValue = 0x0000;
uint8_t statusValue = 0x00;

Thread* ledThread = new Thread();
ThreadController* controll = new ThreadController();

void acendeApagaLED(void);

int main(void) {
	init();

	initVariant();

#if defined(USBCON)
	USBDevice.attach();
#endif

	setup();

	for (;;) {
		loop();
		if (serialEventRun)
			serialEventRun();
	}

	return 0;
}

void initVariant() {
}

void setup(void) {
	// initialize digital pin 13 as an output.
	pinMode(PIN_LED, OUTPUT);

	ledThread->onRun(acendeApagaLED);
	ledThread->setInterval(LED_TIME);

	controll->add(ledThread);

	Serial.begin(BAUD_RATE); //frequência da porta serial - USART
}

void loop(void) {
	controll->run();
	//acendeApagaLED();
	//delay(LED_TIME);
}

void serialEventRun(void) {
}

void acendeApagaLED(void) {
	switch (statusLED) {
	case HIGH:
		statusLED = (int8_t) LOW;
		break;
	case LOW:
		statusLED = (int8_t) HIGH;
		break;
	default:
		break;
	}

	digitalWrite(PIN_LED, statusLED);

	if (pin <= ArduinoStatus::PIN_MAX)
		pin++;
	else
		pin = 0x00;

	if (pinValue <= ArduinoStatus::PIN_VALUE_MAX)
		pinValue++;
	else
		pinValue = 0x0000;

	if (pin % 3 == 0)
		statusValue = ArduinoStatus::SEND;
	else if (pin % 5 == 0)
		statusValue = ArduinoStatus::SEND_RESPONSE;
	else if (pin % 10 == 0)
		statusValue = ArduinoStatus::RESPONSE_RESPONSE;
	else
		statusValue = ArduinoStatus::RESPONSE;

	uint8_t* protocol = ArduinoProtocol::send(
			pin % 2 == 0 ? ArduinoStatus::DIGITAL : ArduinoStatus::ANALOG, pin,
			pinValue, statusValue);

	if (protocol != NULL) {
		Serial.write(protocol, ArduinoProtocol::TOTAL_BYTES_PROTOCOL);
		free(protocol);
	}

}
