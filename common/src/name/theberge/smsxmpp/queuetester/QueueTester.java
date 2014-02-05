package name.theberge.smsxmpp.queuetester;

import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.RabbitQueueManager;
import name.theberge.smsxmpp.common.SMSMessage;

public class QueueTester {
	
	
	public static void main(String args[])	{
		QueueManager qm = new RabbitQueueManager();
		SMSMessage s = new SMSMessage();
		s.setFrom("15143161134");
		s.setTo("18194522486");
		s.setMessage("Mow?");
		qm.enqueue(s, "tosip");
	}
}
