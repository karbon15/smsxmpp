package name.theberge.smsxmpp.common;

public interface QueueManager extends Runnable {
	
	public void enqueue(SMSMessage m);

	public void subscribe(QueueListener l);

	public void run();

	public void setConsumingQueue(String consumingQueue);

	public void setProducingQueue(String producingQueue);

	public void setQueueHost(String queueHost);

	public void setQueueUser(String queueUser);

	public void setQueuePassword(String queuePassword);

}