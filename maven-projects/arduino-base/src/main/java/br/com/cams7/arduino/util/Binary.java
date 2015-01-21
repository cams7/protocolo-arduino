/**
 * 
 */
package br.com.cams7.arduino.util;

/**
 * @author cams7
 *
 */
public final class Binary {
	public static final short MAX_1BYTE = 0xFF; // 255
	public static final int MAX_2BYTES = 0xFFFF; // 65535
	public static final int MAX_3BYTES = 0xFFFFFF; // 16777215
	public static final int MAX_4BYTES = 0xFFFFFFFF; // 4294967295

	// Retorna o bit pelo indice
	public static byte getValueBit(int value, byte index) {
		if (index < 0 && index > 31)
			return -1;
		// Value = .. 0 0 0 0 0 0 0 0 0 0
		// Index = .. 9 8 7 6 5 4 3 2 1 0

		// Comeca a contagem do 'bit menos significativo' para o 'bit mais
		// significativo', ou seja a contagem e feita da direita para esquerda.

		value = (value & (0x00000001 << index)) >> index;

		byte bitValue = (byte) value;
		if (bitValue < 0)
			bitValue = (byte) (bitValue * (-1));

		return bitValue;
	}

	public static byte getLastBitByte(int value) {
		return getValueBit(value, (byte) 7);
	}

	// Converte um numero inteiro em array de bytes
	private static byte[] intToBytes(int value, byte totalBytes) {
		if (totalBytes < 0 && totalBytes > 8)
			return null;

		byte[] array = new byte[totalBytes];

		final byte BITS = 8;
		final byte BITS_DESLOCADOS = (byte) ((totalBytes - 1) * BITS);

		for (byte i = 0; i < totalBytes; i++) {
			int aux = (value << (i * BITS));
			aux >>= BITS_DESLOCADOS;
			array[i] = (byte) aux;
		}

		return array;
	}

	// Converte um 'int' em array de bytes
	public static byte[] intTo4Bytes(int value) {
		return intToBytes(value, (byte) 4);
	}

	// Converte um 'int' em array de bytes
	public static byte[] intTo3Bytes(int value) {
		// Valor maximo 3 bytes
		if (value > MAX_3BYTES)
			return null;

		return intToBytes(value, (byte) 3);
	}

	// Converte um 'unsigned int 16' em array de bytes
	public static byte[] intTo2Bytes(int value) {
		return intToBytes(value, (byte) 2);
	}

	// Converte um 'int' em uma string de bytes
	public static String intToString4Bytes(int value) {
		StringBuffer buffer = new StringBuffer();

		byte[] bytes = intTo4Bytes(value);

		for (byte byteValue : bytes) {
			for (byte i = 7; i >= 0; i--)
				buffer.append(getValueBit(byteValue, i));
			buffer.append(" ");
		}
		return buffer.toString();
	}

}
