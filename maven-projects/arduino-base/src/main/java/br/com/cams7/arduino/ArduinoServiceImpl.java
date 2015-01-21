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

import br.com.cams7.arduino.util.Protocol;
import br.com.cams7.arduino.util.Arduino;
import br.com.cams7.arduino.util.Binary;

public abstract class ArduinoServiceImpl implements ArduinoService, Runnable,
		SerialPortEventListener {

	private OutputStream output;
	private InputStream input;

	private String serialPort;
	private int serialBaudRate;
	private long serialThreadTime;

	private int dadoEnviado = -1;
	private int ultimoDadoRecebido = -1;

	protected final byte EVENTO_NAO_INFORMADO = 0;

	private byte eventoAtual = EVENTO_NAO_INFORMADO;

	private List<Integer> serialData;

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
	protected void serialWrite(int dado) throws ArduinoException {
		if (output == null)
			throw new ArduinoException("O 'OutputStream' nao foi inicializado");

		try {
			output.write(dado);// escreve o valor na porta serial para ser
								// enviado
			dadoEnviado = dado;
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
			int dadoRecebido = 0x00000000;

			try {
				while (input.available() > 0) {
					dadoRecebido = input.read();
					receiveByteBySerial(dadoRecebido);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println();
			// System.out.println(new String(readBuffer));
			comparaDadoEnviadoRecebido(dadoRecebido);

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

	private void comparaDadoEnviadoRecebido(int dadoRecebido) {
		if (dadoRecebido == -1)
			recebimentoError();
		else if (dadoEnviado != -1) {
			if (dadoEnviado == dadoRecebido)
				envioOK();
			else
				envioError();

			dadoEnviado = -1;
		} else if (ultimoDadoRecebido != dadoRecebido)
			setDadoRecebido(dadoRecebido);

		ultimoDadoRecebido = dadoRecebido;
	}

	protected abstract void recebimentoError();

	protected abstract void envioOK();

	protected abstract void envioError();

	protected abstract void setDadoRecebido(int dadoRecebido);

	protected byte getEventoAtual() {
		return eventoAtual;
	}

	protected void setEventoAtual(byte eventoAtual) {
		this.eventoAtual = eventoAtual;
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

	public void receiveByteBySerial(int data) {
		final byte TOTAL_BYTES = 4;
		byte lastBit = Binary.getLastBitByte(data);

		if (0x01 == lastBit) {
			serialData = new ArrayList<Integer>();
			serialData.add(data);
		} else if (serialData != null) {
			serialData.add(data);

			if (serialData.size() == TOTAL_BYTES) {
				Integer[] values = serialData.toArray(new Integer[TOTAL_BYTES]);
				try {
					Arduino arduino = Protocol.receive(values);
					System.out.println(arduino);
				} catch (ArduinoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				serialData = null;
			}
		}
	}

}
