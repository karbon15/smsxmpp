/* Très très inspiré d'un example de Oracle
 * http://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
 */

package name.theberge.smsxmpp.asteriskclient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.StandardWatchEventKinds;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.theberge.smsxmpp.common.FileSystemListener;
import name.theberge.smsxmpp.common.FileSystemMonitor;
import name.theberge.smsxmpp.common.QueueManager;

public class AsteriskFileReciever implements FileSystemListener	{
	
	private Path dir;
	private FileSystemMonitor monitor;
	private QueueManager queueManager;
	
	public AsteriskFileReciever(Path dir, QueueManager manager) throws IOException {
		this.dir = dir;
		this.queueManager = manager;
		monitor = new FileSystemMonitor(dir);
		monitor.subscribe(this);
		
		new Thread(monitor).start();
	}
	
	@SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

	@Override
	public void notify(WatchEvent e) {
		try {
			processEvent(e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void processEvent(WatchEvent e) throws IOException	{
        WatchEvent<Path> ev = cast(e);
        Path name = ev.context();
        Path child = dir.resolve(name);
        
        if(e.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))	{        	        	
        	byte[] encoded = Files.readAllBytes(child);
        	String fileContents = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
        	//TODO: Singleton Factory?
        	queueManager.enqueue(new AsteriskSMSMessageFactory().createMessage(fileContents), "toxmpp");
        	Files.delete(child);
        } 
	}
}
