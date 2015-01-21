/**
 * 
 */
package br.com.cams7.arduino.util;

/**
 * @author cams7
 *
 */
public final class Binary {

	// Retorna o bit pelo indice
	public static byte getValueBit(long value, byte index) {
		// Value = .. 0 0 0 0 0 0 0 0 0 0
		// Index = .. 9 8 7 6 5 4 3 2 1 0

		// Comeca a contagem do 'bit menos significativo' para o 'bit mais
		// significativo', ou seja a contagem e feita da direita para esquerda.

		value = (value & (0x0000000000000001 << index)) >> index;

		byte bitValue = (byte) value;
		if (bitValue < 0)
			bitValue = (byte) (bitValue * (-1));

		return bitValue;
	}

	public static byte getLastBitByte(int value) {
		return getValueBit(value, (byte) 7);
	}

}
