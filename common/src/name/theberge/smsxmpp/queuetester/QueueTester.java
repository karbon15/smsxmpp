package name.theberge.smsxmpp.queuetester;

import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.RabbitQueueManager;
import name.theberge.smsxmpp.common.SMSMessage;

public class QueueTester {
	
	
	public static void main(String args[])	{
		QueueManager qm = new RabbitQueueManager();
		SMSMessage s = new SMSMessage();
		s.setFrom("15143161134");
		s.setTo("15145536024");
		s.setMessage("Ainsî va ça vie qui va!");
		qm.enqueue(s, "tosip");
	}
}
