/**
 * 
 */
package br.com.cams7.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

/**
 * @author cams7
 *
 */
public final class AppUtil {
	public static URL getURLFile(Class<?> classType, String fileName) {
		URL fileURL = classType.getClassLoader().getResource(fileName);
		return fileURL;
	}

	public static Properties getPropertiesFile(Class<?> classType,
			String fileName) throws AppException {
		URL fileURL = getURLFile(classType, fileName);

		InputStream in = null;
		try {
			in = fileURL.openStream();
			Reader reader = new InputStreamReader(in, "UTF-8");

			Properties properties = new Properties();

			try {
				properties.load(reader);
			} finally {
				reader.close();
			}

			return properties;
		} catch (IOException e) {
			throw new AppException(e.getCause());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
