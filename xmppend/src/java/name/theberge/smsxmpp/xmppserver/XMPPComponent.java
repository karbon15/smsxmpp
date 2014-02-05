package name.theberge.smsxmpp.xmppserver;

import java.sql.SQLException;

import name.theberge.smsxmpp.common.QueueListener;
import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.SMSMessage;

import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.Log;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;
import org.xmpp.packet.Presence;

public class XMPPComponent extends AbstractComponent implements QueueListener {

	AuthorizationManager authManager = AuthorizationManager.getInstance();

	QueueManager qm;

	public QueueManager getQueueManager() {
		return qm;
	}

	public void setQueueManager(QueueManager qm) {
		this.qm = qm;
	}

	@Override
	public String getDescription() {
		return "Bidirectional message handler for SMSXMPP";
	}

	@Override
	public String getDomain() {
		return JiveGlobals.getProperty("xmpp.domain");
	}

	@Override
	public String getName() {
		return JiveGlobals.getProperty("smsxmpp.subdomain");
	}

	public void preComponentStart() {
		// Initialize some variables upon first start
		if (JiveGlobals.getProperty("smsxmpp.subdomain").isEmpty()) {
			JiveGlobals.setProperty("smsxmpp.subdomain", "sms");
		}
		if (JiveGlobals.getProperty("smsxmpp.inboundq").isEmpty()) {
			JiveGlobals.setProperty("smsxmpp.inboundq", "toxmpp");
		}
		if (JiveGlobals.getProperty("smsxmpp.outboundq").isEmpty()) {
			JiveGlobals.setProperty("smsxmpp.outboundq", "tosip");
		}
	}

	@Override
	protected void handleMessage(Message received) {
		User u = null;
		try {
			u = authManager.getUserByJID(received.getFrom().toBareJID());
		} catch (SQLException e) {
			Log.debug(e);
			e.printStackTrace();
		}

		// If user is authorized
		if (u != null) {
			if (received.getType() == Type.chat	|| received.getType() == Type.normal) {
				if (!received.getBody().isEmpty()) {
					SMSMessage m = new SMSMessage();

					// Get destination number by removing domain part
					m.setTo(received.getTo().toString()
							.replace("@" + this.getJID(), ""));
					// Get the origin number from the mapping
					m.setFrom(u.getNumber());
					m.setMessage(received.getBody());

					qm.enqueue(m, JiveGlobals.getProperty("smsxmpp.outboundq"));
					Log.debug("Enqueued message " + received.getBody() + " for SIP Delivery");
				}
			}
		}
	}

	@Override
	protected void handlePresence(Presence presence) {
		// TODO: Mabye validate the origin is in our mapping list
		// If we are done subscribing, get presence status from our new friend
		if (presence.getType() == Presence.Type.subscribed) {
			Log.debug("Recieved a subscription confirmation");
			Presence response = new Presence(Presence.Type.probe);
			response.setTo(presence.getFrom());
			response.setFrom(presence.getTo());
			send(response);
			// If we recieve a subscription query, accept it and then subscribe
		} else if (presence.getType() == Presence.Type.subscribe) {
			Log.debug("Recieved a subscription query");
			Presence original = presence;
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
			// If we get presence probe, just say we're chatty.
		} else if (presence.getType() == Presence.Type.probe) {
			Log.debug("Recieved a probe");
			Presence original = presence;
			Presence response = new Presence();
			response.setTo(original.getFrom());
			response.setFrom(original.getTo());
			response.setShow(Presence.Show.chat);
			send(response);
		}
	}

	@Override
	public void notify(SMSMessage s) {
		User u = null;

		try {
			u = authManager.getUserByNumber(s.getTo());
		} catch (SQLException e) {
			Log.debug(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If user is authorized
		if (u != null) {
			// TODO: Valider que la destination a bien un TO valide
			Message m = new Message();
			Presence p = new Presence();

			// Subscribe anyway
			p.setFrom(s.getFrom() + "@" + this.getJID());
			p.setTo(u.getJid());
			p.setType(Presence.Type.subscribe);
			send(p);

			// Send message
			m.setTo(u.getJid());
			m.setFrom(s.getFrom() + "@" + this.getJID());
			m.setBody(s.getMessage());
			send(m);

			Log.debug("Dequed message " + s.getMessage() + " for XMPP Delivery");
		}
	}
}