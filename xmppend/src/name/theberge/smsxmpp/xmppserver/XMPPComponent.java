package name.theberge.smsxmpp.xmppserver;

import name.theberge.smsxmpp.common.QueueListener;
import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.RabbitQueueManager;
import name.theberge.smsxmpp.common.SMSMessage;

import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
 
public class XMPPComponent extends AbstractComponent implements QueueListener {
 
    private JID myAddress = null;
    
    QueueManager qm = new RabbitQueueManager();
 
    @Override
    public String getDescription() {
        return "A component that will respond with a friendly "
                + "'hello' to every message it receives.";
    }
 
    @Override
    public String getDomain() {
        return "sms2.theberge.name";
    }
 
    @Override
    public String getName() {
        return "hello";
    }
    
    public void preComponentStart()	{
    	qm.setConsumingQueue("toxmpp");
    	qm.subscribe(this);
    }
 
    @Override
    protected void handleMessage(Message received) {
        // construct the response
        Message response = new Message();
        response.setFrom(myAddress);
        response.setTo(received.getFrom());
        response.setBody("Hello!");
 
        // send the response using AbstractComponent#send(Packet)
        send(response);
    }
    
    

	@Override
	public void notify(SMSMessage s) {
		System.out.println(s);
		//Here be handling of adding/sending message via xmpp
		
	}
}