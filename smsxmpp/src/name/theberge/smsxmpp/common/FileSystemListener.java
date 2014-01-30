package name.theberge.smsxmpp.common;

import java.nio.file.WatchEvent;

public interface FileSystemListener {
	public void notify(WatchEvent e);
}
