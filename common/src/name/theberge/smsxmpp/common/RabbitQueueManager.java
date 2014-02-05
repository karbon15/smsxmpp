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
	String consumingQueue, producingQueue, queueHost = "", queueUser = "", queuePassword = "";

	private ArrayList<QueueListener> listeners = new ArrayList<QueueListener>();

	public void finalize() {
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enqueue(SMSMessage m) {
		try {
			channel.basicPublish("", this.producingQueue, null, m.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// FROM:
	// http://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
	@Override
	public void run() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(queueHost);
		if(!queueUser.isEmpty())	{
			factory.setUsername(queueUser);
			if(!queuePassword.isEmpty()){
				factory.setPassword(queuePassword);
			}
		}

		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(this.consumingQueue, false, false, false, null);
			channel.queueDeclare(this.producingQueue, false, false, false, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	public String getConsumingQueue() {
		return consumingQueue;
	}

	public void setConsumingQueue(String consumingQueue) {
		this.consumingQueue = consumingQueue;
	}

	public String getProducingQueue() {
		return producingQueue;
	}

	public void setProducingQueue(String producingQueue) {
		this.producingQueue = producingQueue;
	}

	public String getQueueHost() {
		return queueHost;
	}

	public void setQueueHost(String queueHost) {
		this.queueHost = queueHost;
	}

	public String getQueueUser() {
		return queueUser;
	}

	public void setQueueUser(String queueUser) {
		if(queueUser != null)	{
			this.queueUser = queueUser;
		}
	}

	public String getQueuePassword() {
		return queuePassword;
	}

	public void setQueuePassword(String queuePassword) {
		if(queuePassword != null)	{
			this.queuePassword = queuePassword;
		}
	}

}
