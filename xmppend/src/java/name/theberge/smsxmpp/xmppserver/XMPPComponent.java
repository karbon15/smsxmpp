package name.theberge.smsxmpp.xmppserver;

import name.theberge.smsxmpp.common.QueueListener;
import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.SMSMessage;

import org.jivesoftware.util.Log;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;
import org.xmpp.packet.Presence;
 
public class XMPPComponent extends AbstractComponent implements QueueListener {
   
    QueueManager qm;
 
    public QueueManager getQueueManager() {
		return qm;
	}

	public void setQueueManager(QueueManager qm) {
		this.qm = qm;
	}

	@Override
    public String getDescription() {
        return "TODO: Description";
    }
 
    @Override
    public String getDomain() {
        return "sms.theberge.name";
    }
 
    @Override
    public String getName() {
        return "a";
    }
    
    public void preComponentStart()	{
    }
 
    @Override
    protected void handleMessage(Message received) {
    	if(received.getType() == Type.chat || received.getType() == Type.normal){
    		if(!received.getBody().isEmpty()){
		    	Log.debug("GOT MESSAGE " + received.getBody());
		    	SMSMessage m = new SMSMessage();
		    	m.setTo(received.getTo().toString().replace("@a.sms.theberge.name", ""));
		    	//TODO: Validate FROM, get # mapping
		    	m.setFrom("15143161134");
		    	m.setMessage(received.getBody());
		    	qm.enqueue(m, "tosip");
		    	Log.debug("Queued" + received.getBody());
    		}
    	}
    }
    
    @Override
    protected void handlePresence(Presence presence){
    	Log.debug("PRESENCE");
    	if (presence.getType() ==  Presence.Type.subscribed){
    		Log.debug("subscribed");
            System.out.println("The component has received Subscribtion request.");
            Presence response = new Presence(Presence.Type.probe);
            response.setTo(presence.getFrom());
            response.setFrom(presence.getTo());
            send(response);
        }
    	else if (presence.getType() ==  Presence.Type.subscribe){
    		Log.debug("subscribe");
            Presence original =  presence;
            Presence response = new Presence();
            response.setTo(original.getFrom());
            response.setFrom(original.getTo());
            response.setShow(Presence.Show.chat);
            response.setType(Presence.Type.subscribed);
            send(response);
            
            response = new Presence();
            response.setTo(original.getFrom());
            response.setFrom(original.getTo());
            response.setType(Presence.Type.subscribe);
            send(response);
        }	else if(presence.getType() == Presence.Type.probe){
        	Log.debug("probe");
             Presence original =  presence;
             Presence response = new Presence();
             response.setTo(original.getFrom());
             response.setFrom(original.getTo());
             response.setShow(Presence.Show.chat);
             send(response);
        }
    }  

	@Override
	public void notify(SMSMessage s) {
		//TODO: Valider que la destination a bien un TO valide
		Message m = new Message();
		Presence p = new Presence();
		p.setFrom(s.getFrom() +"@a.sms.theberge.name");
		p.setTo("pier-luc@theberge.name");
		p.setType(Presence.Type.subscribe);
		send(p);
		
		m.setTo("pier-luc@theberge.name");
		m.setFrom(s.getFrom() +"@a.sms.theberge.name");
		m.setBody(s.getMessage());
		send(m);
		Log.debug(s.getMessage());	
	}
}