package name.theberge.smsxmpp.common;


public interface QueueManager extends Runnable {
	public void enqueue(SMSMessage m, String channelName);
	public void subscribe(QueueListener l);
	public void run();
}