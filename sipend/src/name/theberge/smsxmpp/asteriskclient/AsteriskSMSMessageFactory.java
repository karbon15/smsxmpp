package name.theberge.smsxmpp.asteriskclient;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.mlu.encoding.gsm7bitencoding.Gsm7BitEncoderDecoder;

import name.theberge.smsxmpp.common.SMSMessage;
import name.theberge.smsxmpp.common.SMSMessageFactory;

public class AsteriskSMSMessageFactory implements SMSMessageFactory {

	public SMSMessage createMessage(Object o) {
		String input = (String) o;

		Pattern p = Pattern
				.compile("^Channel: ([^|]+)\\s+Extension: ([^|]+)\\s+Priority: ([0-9]+)\\s+Context: ([^|]+)\\s+SetVar: sms_host=([^|]+)\\s+SetVar: sms_to=([0-9]+)\\s+SetVar: sms_from=([0-9]+)\\s+SetVar: sms_body=([^|]+)$");
		Matcher m = p.matcher(input);

		SMSMessage sms = new SMSMessage();

		// TODO: Remove magic numbers
		if (m.find()) {
			sms.setTo(m.group(6));
			sms.setFrom(m.group(7));

			// String s = new String(bytes, "US-ASCII");
			// Gsm7BitEncoderDecoder encoder = new Gsm7BitEncoderDecoder();
			// sms.setMessage(encoder.decode(m.group(i)));
			try {
				sms.setMessage(new String(m.group(8).getBytes(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sms;
	}

}
