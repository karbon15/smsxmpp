package name.theberge.smsxmpp.asteriskclient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import name.theberge.smsxmpp.common.SMSMessage;
import name.theberge.smsxmpp.common.SMSMessageFactory;

public class AsteriskSMSMessageFactory implements SMSMessageFactory {

	public SMSMessage createMessage(Object o) {
		String input = (String) o;
		
		Pattern p = Pattern.compile("^Channel: ([^|]+)|Extension: ([^|]+)|Priority: ([0-9]+)|Context: ([^|]+)|SetVar: sms_host=([^|]+)|SetVar: sms_to=([0-9]+)|SetVar: sms_from=([0-9]+)|SetVar: sms_body=([^|]+)$");
		Matcher m = p.matcher(input);
		
		SMSMessage sms = new SMSMessage();
		
		//TODO: Remove magic numbers
		int i = 1;
		while(m.find())	{
		    if(i == 6){
		    	sms.setTo(m.group(i));
		    }	
		    else if(i == 7)	{
		    	sms.setFrom(m.group(i));
		    }
		    else if(i == 8)	{
		    	sms.setMessage(new String(DatatypeConverter.parseBase64Binary(m.group(i))));
		    }
	        i++;
		}

		return sms;
	}

}
