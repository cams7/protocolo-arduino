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
#include <Binary.h>
#include <ArduinoStatus.h>
#include <Thread.h>
#include <ThreadController.h>

#define BAUD_RATE 9600

#define LED_PISCA_TIME  1000 // 1 segundo
//#define LED_TIME        500  // 1/2 segundo

#define PIN_LED_PISCA   13   //Pino 13
#define PIN_LED_AMARELA 11   //Pino 11 PWM

using namespace SISBARC;

//Declared weak in Arduino.h to allow user redefinitions.
int atexit(void (*func)()) {
	return 0;
}

// Weak empty variant initialization function.
// May be redefined by variant files.
void initVariant() __attribute__((weak));

void receiveDataBySerial(uint8_t data);
void receiveDataBySerial(ArduinoStatus* arduino);

void acendeApagaLEDPisca(void);

Thread* ledPiscaThread = new Thread();
//Thread* ledThread = new Thread();

ThreadController* controll = new ThreadController();

uint8_t* serialDataReceive;
uint8_t serialDataReceiveIndex = 0x00;

int8_t statusLEDPisca = (int8_t) HIGH;

int main(void) {
	init();

	initVariant();

#if defined(USBCON)
	USBDevice.attach();
#endif

	setup();

	for (;;) {
		loop();
		//if (serialEventRun)
		if (Serial.available() > 0) //verifica se existe comunicação com a porta serial
			serialEventRun();
	}

	return 0;
}

void initVariant() {
}

void setup(void) {
	Serial.begin(BAUD_RATE); //frequência da porta serial - USART

	pinMode(PIN_LED_PISCA, OUTPUT);
	pinMode(PIN_LED_AMARELA, OUTPUT);

	ledPiscaThread->onRun(acendeApagaLEDPisca);
	ledPiscaThread->setInterval(LED_PISCA_TIME);

	//ledThread->onRun(acendeApagaLED);
	//ledThread->setInterval(LED_TIME);

	controll->add(ledPiscaThread);
	//controll->add(ledThread);
}

void loop(void) {
	controll->run();
}

void serialEventRun(void) {
	uint8_t data = (uint8_t) Serial.read(); //lê os dados da porta serial - Maximo 64 bytes
	receiveDataBySerial(data);
}

void receiveDataBySerial(uint8_t data) {
	uint8_t lastBit = data & 0x80;

	if (lastBit) {
		serialDataReceive = (uint8_t*) malloc(
				ArduinoProtocol::TOTAL_BYTES_PROTOCOL);
		*(serialDataReceive) = data;

		serialDataReceiveIndex = 0x01;
	} else if (serialDataReceiveIndex > 0x00 && serialDataReceive != NULL) {
		*(serialDataReceive + serialDataReceiveIndex) = data;

		if (serialDataReceiveIndex
				== (ArduinoProtocol::TOTAL_BYTES_PROTOCOL - 1)) {
			ArduinoStatus* arduino = ArduinoProtocol::receive(
					serialDataReceive);
			if (arduino != NULL) {
				receiveDataBySerial(arduino);
				free(arduino);
			}
			free(serialDataReceive);
		} else
			serialDataReceiveIndex++;
	} else {
	}

}

void receiveDataBySerial(ArduinoStatus* arduino) {
	if (!(ArduinoStatus::PC == arduino->getTransmitterValue()
			&& ArduinoStatus::SEND_RESPONSE == arduino->getStatusValue()
			&& ArduinoStatus::DIGITAL == arduino->getPinType()))
		return;

	if (PIN_LED_AMARELA == arduino->getPin())
		digitalWrite(PIN_LED_AMARELA, (uint8_t) arduino->getPinValue());

	uint8_t* serialDataSend;
	serialDataSend = ArduinoProtocol::send(arduino->getPinType(),
			arduino->getPin(), arduino->getPinValue(), ArduinoStatus::RESPONSE);

	if (serialDataSend != NULL) {
		Serial.write(serialDataSend, ArduinoProtocol::TOTAL_BYTES_PROTOCOL);
		free(serialDataSend);
	}

}

void acendeApagaLEDPisca(void) {
	switch (statusLEDPisca) {
	case HIGH:
		statusLEDPisca = (int8_t) LOW;
		break;
	case LOW:
		statusLEDPisca = (int8_t) HIGH;
		break;
	default:
		break;
	}

	digitalWrite(PIN_LED_PISCA, statusLEDPisca);

	uint8_t* serialDataSend;
	serialDataSend = ArduinoProtocol::send(ArduinoStatus::DIGITAL,
	PIN_LED_PISCA, statusLEDPisca, ArduinoStatus::SEND);

	if (serialDataSend != NULL) {
		Serial.write(serialDataSend, ArduinoProtocol::TOTAL_BYTES_PROTOCOL);
		free(serialDataSend);
	}

}

