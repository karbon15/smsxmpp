package name.theberge.smsxmpp.asteriskclient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AsteriskClientPropertiesReader {

	private static Properties properties = null;

	public static Properties getProperties() {
		if (properties == null) {
			readProperties();
		}

		return properties;
	}

	private static void readProperties() {
		InputStream input = null;

		try {
			input = new FileInputStream("asteriskclient.properties");

			properties = new Properties();
			properties.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
