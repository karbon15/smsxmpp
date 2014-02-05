package name.theberge.smsxmpp.asteriskclient;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.RabbitQueueManager;

public class AsteriskClient {
	public static void main(String[] argv) throws IOException {
		Path fromxmpp = Paths.get(AsteriskClientPropertiesReader.getProperties().getProperty("smsxmpp.inputfolder"));
		
		System.out.println("Initialized queue manager");
		QueueManager qm = new RabbitQueueManager();
		qm.setConsumingQueue(AsteriskClientPropertiesReader.getProperties().getProperty("smsxmpp.inboundq"));
		qm.setProducingQueue(AsteriskClientPropertiesReader.getProperties().getProperty("smsxmpp.outboundq"));
		qm.setQueueHost(AsteriskClientPropertiesReader.getProperties().getProperty("smsxmpp.rabbitmq.host"));
		qm.setQueueUser(AsteriskClientPropertiesReader.getProperties().getProperty("smsxmpp.rabbitmq.username"));
		qm.setQueuePassword(AsteriskClientPropertiesReader.getProperties().getProperty("smsxmpp.rabbitmq.password"));
		
		System.out.println("Initialized File Reciever");
		new AsteriskFileReciever(fromxmpp, qm);
		System.out.println("Initialized Queue Reciever");
		new AsteriskQueueReciever(qm);
		
		new Thread(qm).start();
	}
}
