/**
 * 
 */
package br.com.cams7.arduino.util;

import java.util.List;

/**
 * @author cams7
 *
 */
public final class Bytes {

	public static byte[] toArray(List<Byte> values) {
		byte[] bytes = new byte[values.size()];
		for (byte i = 0x00; i < values.size(); i++)
			bytes[i] = values.get(i);

		return bytes;
	}
}
