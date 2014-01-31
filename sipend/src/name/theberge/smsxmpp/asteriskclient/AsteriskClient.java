package name.theberge.smsxmpp.asteriskclient;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.RabbitQueueManager;



public class AsteriskClient {
	public static void main(String[] argv) throws IOException {
		
		//TODO: Make path configurable
		Path fromxmpp = Paths.get("/var/spool/asterisk/sms");
		//Path toxmpp = Paths.get("/var/spool/asterisk/outgoing");
		
		System.out.println("Init qm");
		QueueManager qm = new RabbitQueueManager();
		
		System.out.println("Init fr");
		new AsteriskFileReciever(fromxmpp, qm);
		System.out.println("Init qr");
		new AsteriskQueueReciever(qm);
		
		new Thread(qm).start();
	}
}