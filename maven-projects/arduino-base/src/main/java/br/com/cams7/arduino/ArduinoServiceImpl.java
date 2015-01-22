package br.com.cams7.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import br.com.cams7.arduino.util.ArduinoProtocol;
import br.com.cams7.arduino.util.ArduinoStatus;
import br.com.cams7.arduino.util.Binary;
import br.com.cams7.arduino.util.Bytes;
import br.com.cams7.arduino.util.ArduinoStatus.Status;

public abstract class ArduinoServiceImpl implements ArduinoService, Runnable,
		SerialPortEventListener {

	private OutputStream output;
	private InputStream input;

	private String serialPort;
	private int serialBaudRate;
	private long serialThreadTime;

	private List<Byte> serialData;

	/**
	 * Construtor da classe Arduino
	 * 
	 * @param serialPort
	 *            - Porta COM que sera utilizada para enviar os dados para o
	 *            Arduino
	 * @param bauldRate
	 *            - Taxa de transferencia da porta serial geralmente e 9600
	 */
	protected ArduinoServiceImpl(String serialPort, int serialBaudRate,
			long serialThreadTime) throws ArduinoException {
		super();

		this.serialPort = serialPort;
		this.serialBaudRate = serialBaudRate;
		this.serialThreadTime = serialThreadTime;

		init();
	}

	/**
	 * Metodo que verifica se a comunicacao com a porta serial esta OK
	 */
	private void init() throws ArduinoException {
		// close();

		// Define uma variavel portId do tipo CommPortIdentifier para
		// realizar a comunicacao serial
		CommPortIdentifier portId = null;
		try {
			// Tenta verificar se a porta COM informada existe
			portId = CommPortIdentifier.getPortIdentifier(serialPort);
		} catch (NoSuchPortException e) {
			// Caso a porta COM nao exista sera exibido um erro
			throw new ArduinoException("Porta '" + serialPort
					+ "' nao encontrada", e.getCause());
		}

		try {
			// Abre a porta COM
			SerialPort port = (SerialPort) portId.open("Comunicacao serial",
					serialBaudRate);

			output = port.getOutputStream();
			input = port.getInputStream();

			port.addEventListener(this);

			port.notifyOnDataAvailable(true);

			port.setSerialPortParams(serialBaudRate, // taxa de transferencia da
					// porta serial
					SerialPort.DATABITS_8, // taxa de 10 bits 8 (envio)
					SerialPort.STOPBITS_1, // taxa de 10 bits 1 (recebimento)
					SerialPort.PARITY_NONE); // receber e enviar dados

			Thread readThread = new Thread(this);
			readThread.start();
		} catch (PortInUseException | IOException | TooManyListenersException
				| UnsupportedCommOperationException e) {
			throw new ArduinoException("Erro na comunicacao serial",
					e.getCause());
		} finally {
			close();
		}

	}

	/**
	 * M�todo que fecha a comunica��o com a porta serial
	 */
	private void close() throws ArduinoException {
		ArduinoException exception = new ArduinoException(
				"Nao foi possivel fechar a porta '" + serialPort + "'");
		try {
			if (input != null)
				input.close();
		} catch (IOException e) {
			exception.addSuppressed(e);
		}

		try {
			if (output != null)
				output.close();
		} catch (IOException e) {
			exception.addSuppressed(e);
		}

		if (exception.getSuppressed().length > 0)
			throw exception;
	}

	/**
	 * @param opcao
	 *            - Valor a ser enviado pela porta serial
	 */
	private void serialWrite(byte[] data) throws ArduinoException {
		if (output == null)
			throw new ArduinoException("O 'OutputStream' nao foi inicializado");

		try {
			// escreve o valor na porta serial para ser enviado
			output.write(data);
		} catch (IOException e) {
			throw new ArduinoException("Nao foi possivel enviar o dado",
					e.getCause());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
	 */
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			try {
				while (input.available() > 0)
					receiveDataBySerial((byte) input.read());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Thread.sleep(serialThreadTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void receiveDataBySerial(byte data) {

		byte lastBit = Binary.getLastBitByte(data);

		if (0x01 == lastBit) {
			serialData = new ArrayList<Byte>();
			serialData.add(data);
		} else if (0x00 == lastBit && serialData != null) {
			serialData.add(data);

			if (serialData.size() == ArduinoProtocol.TOTAL_BYTES_PROTOCOL) {
				byte[] values = Bytes.toArray(serialData);
				try {
					ArduinoStatus arduino = ArduinoProtocol.receive(values);
					receiveDataBySerial(arduino);
				} catch (ArduinoException e) {
					e.printStackTrace();
				}

				serialData = null;
			}
		} else {
			System.err.println("O dado '" + Integer.toBinaryString(data)
					+ "' foi corrompido");
		}
	}

	protected abstract void receiveDataBySerial(ArduinoStatus arduino);

	protected void sendPinDigital(byte pin, boolean pinValue, Status statusValue)
			throws ArduinoException {
		byte[] data = ArduinoProtocol
				.sendPinDigital(pin, pinValue, statusValue);
		serialWrite(data);
	}

	protected void sendPinPWM(byte pin, short pinValue, Status statusValue)
			throws ArduinoException {
		byte[] data = ArduinoProtocol.sendPinPWM(pin, pinValue, statusValue);
		serialWrite(data);
	}

	protected void sendPinAnalog(byte pin, short pinValue, Status statusValue)
			throws ArduinoException {
		byte[] data = ArduinoProtocol.sendPinAnalog(pin, pinValue, statusValue);
		serialWrite(data);
	}

	public String getSerialPort() {
		return serialPort;
	}

	public int getSerialBaudRate() {
		return serialBaudRate;
	}

	public long getSerialThreadTime() {
		return serialThreadTime;
	}

}
