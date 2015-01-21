/**
 * 
 */
package br.com.cams7.arduino.util;

/**
 * @author cams7
 *
 */
public final class Checksum {

	private static final short POLYNOMIAL = 0xD8; /* 11011 followed by 0's */

	private static final byte WIDTH = 0x08;
	private static final short TOPBIT = (0x01 << (WIDTH - 1));

	// Gera CRC para 1 byte
	private static byte crcNaive(final byte message) {
		int remainder;

		/*
		 * Initially, the dividend is the remainder.
		 */
		remainder = message;

		/*
		 * For each bit position in the message....
		 */
		for (byte i = 8; i > 0; --i) {
			/*
			 * If the uppermost bit is a 1...
			 */
			if (Binary.getLastBitByte(remainder & 0x00000080) == 1) {
				/*
				 * XOR the previous remainder with the divisor.
				 */
				remainder ^= POLYNOMIAL;
			}

			/*
			 * Shift the next bit of the message into the remainder.
			 */
			remainder = (remainder << 1);
		}

		/*
		 * Return only the relevant bits of the remainder as CRC.
		 */
		return (byte) (remainder >> 4);

	} /* crcNaive() */

	// Gera CRC para um array de bytes
	private static byte crcSlow(final byte message[], byte totalBytes) {
		int remainder = 0x00000000;

		/*
		 * Perform modulo-2 division, a byte at a time.
		 */
		for (byte i = 0; i < totalBytes; ++i) {
			/*
			 * Bring the next byte into the remainder.
			 */
			remainder ^= (message[i] << (WIDTH - 8));

			/*
			 * Perform modulo-2 division, a bit at a time.
			 */
			for (byte bit = 8; bit > 0; --bit) {
				/*
				 * Try to divide the current data bit.
				 */
				if (Binary.getValueBit(remainder & TOPBIT, (byte) (WIDTH - 1)) == 1) {
					remainder = (remainder << 1) ^ POLYNOMIAL;
				} else {
					remainder = (remainder << 1);
				}
			}
		}

		/*
		 * The final remainder is the CRC result.
		 */
		return (byte) (remainder);

	} /* crcSlow() */

	// Gera CRC para 1,2,3 e 4 bytes
	private static byte getCrcAll(final int message, byte totalBytes) {
		if (totalBytes < 1 || totalBytes > 4)
			return 0;

		if (totalBytes > 1) {
			switch (totalBytes) {
			case 2:
				return crcSlow(Binary.intTo2Bytes(message), totalBytes);
			case 3:
				return crcSlow(Binary.intTo3Bytes(message), totalBytes);
			case 4:
				return crcSlow(Binary.intTo4Bytes(message), totalBytes);
			default:
				break;
			}
		}
		return crcNaive((byte) message);
	}

	// Gera CRC para 4 bytes
	public static byte getCrc4Bytes(final int message) {
		return getCrcAll(message, (byte) 4);
	}

	// Gera CRC para 3 bytes
	public static byte getCrc3Bytes(final int message) {
		return getCrcAll(message, (byte) 3);
	}

	// Gera CRC para 2 bytes
	public static byte getCrc2Bytes(final int message) {
		return getCrcAll(message, (byte) 2);
	}

	// Gera CRC para 1 byte
	public static byte getCrc1Byte(final int message) {
		return getCrcAll(message, (byte) 1);
	}

}
