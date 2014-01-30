package name.theberge.smsxmpp.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitQueueManager implements QueueManager {

	Connection connection;
	Channel channel;
	String consumingQueue = "tosip";
	String host = "192.168.1.26";

	private ArrayList<QueueListener> listeners = new ArrayList<QueueListener>();

	public RabbitQueueManager() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);

		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare("toxmpp", false, false, false, null);
			channel.queueDeclare("tosip", false, false, false, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void finalize() {
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enqueue(SMSMessage m, String channelName) {
		try {
			channel.basicPublish("", channelName, null, m.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// FROM:
	// http://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
	@Override
	public void run() {
		QueueingConsumer consumer = new QueueingConsumer(channel);
		QueueingConsumer.Delivery delivery = null;
		Object o = null;
		ByteArrayInputStream bis;
		ObjectInput in;
		try {

			channel.basicConsume(this.consumingQueue, true, consumer);

			while (true) {
				delivery = consumer.nextDelivery();

				bis = new ByteArrayInputStream(delivery.getBody());
				in = new ObjectInputStream(bis);
				o = in.readObject();

				bis.close();
				in.close();

				SMSMessage message = (SMSMessage) o;

				for (QueueListener l : listeners) {
					l.notify(message);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void subscribe(QueueListener l) {
		listeners.add(l);
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getConsumingQueue() {
		return consumingQueue;
	}

	public void setConsumingQueue(String consumingQueue) {
		this.consumingQueue = consumingQueue;
	}

}
