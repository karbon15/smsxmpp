package name.theberge.smsxmpp.xmppserver;

import name.theberge.smsxmpp.common.QueueManager;
import name.theberge.smsxmpp.common.RabbitQueueManager;

import org.jivesoftware.openfire.component.InternalComponentManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.xmpp.component.ComponentException;

import java.io.File;

/**
 * A sample plugin for Openfire.
 */
public class SMSXMPPPlugin implements Plugin {

	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		InternalComponentManager componentManager = InternalComponentManager.getInstance();
		XMPPComponent component = new XMPPComponent();

		QueueManager qm = new RabbitQueueManager();
		qm.setConsumingQueue("toxmpp");
		qm.subscribe(component);

		component.setQueueManager(qm);
		
		new Thread(qm).start();
		
		try {
			componentManager.addComponent("a", component);
		} catch (ComponentException e) {
			e.printStackTrace();
		}
	}

	public void destroyPlugin() {
		// Your code goes here
	}
}
