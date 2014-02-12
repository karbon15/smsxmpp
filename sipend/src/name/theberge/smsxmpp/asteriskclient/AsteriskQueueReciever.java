package name.theberge.smsxmpp.asteriskclient;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.mlu.encoding.gsm7bitencoding.Gsm7BitEncoderDecoder;

import name.theberge.smsxmpp.common.QueueListener;
import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.SMSMessage;

public class AsteriskQueueReciever implements QueueListener {

	private QueueManager queueManager;

	public AsteriskQueueReciever(QueueManager qm) {
		this.queueManager = qm;
		this.queueManager.setConsumingQueue(AsteriskClientPropertiesReader
					.getProperties().getProperty("smsxmpp.inboundq"));
		this.queueManager.subscribe(this);
		System.out.println("Subscribe to queue");
	}

	@Override
	public void notify(SMSMessage s) {
		System.out.println(s.getMessage());

		PrintWriter writer;
		try {

			//Gsm7BitEncoderDecoder encoder = new Gsm7BitEncoderDecoder();
			//String encoded = encoder.encode(s.getMessage());

			// TODO: Find a charset
			//Splitting messages by line break.  Asterisk doesn't support line breaks.
			//Sanitizing inspired from http://stackoverflow.com/questions/1963908/how-to-convert-accented-letters-to-regular-char-in-java
			String[] messages = s.getMessage().split("\n");
			
			for(int i = 0; i < messages.length; i++)	{
				writer = new PrintWriter(AsteriskClientPropertiesReader
						.getProperties().getProperty("smsxmpp.outputfolder")
						+ UUID.randomUUID() + ".sms", "UTF-8");
				writer.println("Channel: Local/s@smsxmpp-send");
				writer.println("Extension: s");
				writer.println("Priority: 1");
				writer.println("Context: smsxmpp-send");
				writer.println("SetVar: sms_host=" + AsteriskClientPropertiesReader.getProperties().getProperty("smsxmpp.smshost"));
				writer.println("SetVar: sms_from=" + s.getFrom());
				writer.println("SetVar: sms_to=" + s.getTo());
				writer.println("SetVar: sms_body=" + Normalizer.normalize(messages[i], Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
				writer.println("");
				writer.close();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
