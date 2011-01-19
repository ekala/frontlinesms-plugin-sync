package net.frontlinesms.plugins.sync;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.domain.FrontlineMessage;

public class QueueProcessor extends Thread {
	private static final long MAX_SLEEP_TIME = 1000;
	private static final long INITIAL_SLEEP_TIME = 10;
	private LinkedList<FrontlineMessage> queue = new LinkedList<FrontlineMessage>();
	private boolean keepProcessing;
	private long sleepTime = INITIAL_SLEEP_TIME;
	private MessageSyncher messageSyncher;
	private static SyncPluginProperties pluginProperties =  SyncPluginProperties.getInstance();
	private static final Logger LOG =   FrontlineUtils.getLogger(QueueProcessor.class);
	private SyncPluginController controller;

	public QueueProcessor() {
		
	}
	
	public QueueProcessor(SyncPluginController controller) {
		this.controller = controller;
	}
	
	public void setMessageSyncher(MessageSyncher messageSyncher) {
		this.messageSyncher = messageSyncher;
	}
	
	@Override
	public void run() {
		assert(this.messageSyncher != null);
		
		// loop this until shutdown
		this.keepProcessing = true;
		while(this.keepProcessing) {
		
			// Get messages off the queue
			processMessage(poll());
			
			try {
				Thread.sleep(sleepTime);
			} catch(InterruptedException ex) {
				LOG.debug("Queue processor got interrupted", ex);
			}
		}
	}

	/**
	 * This should only be called from within <code>this</code>, and from unit tests.
	 * @param poll
	 */
	void processMessage(FrontlineMessage message) {
		if(message != null) {
			// Attempt to sync message and if it fails, put it back at the head of the queue
			boolean success = messageSyncher.syncMessage(message);
			LOG.debug("Synchronisation status: " + Boolean.toString(success));
			if (success) {
				// TODO save last processed message number
				pluginProperties.setLastSyncedId(message);
				sleepTime = INITIAL_SLEEP_TIME;
			} else {
				addToHead(message);
				doubleSleepTime();
			}
			
			if (this.controller != null) {
				String requestURL = messageSyncher.getHttpRequestURL(message);
				this.controller.updateSynchronisationLog(requestURL, success);
			}
			
		} else {
			doubleSleepTime();
		}
	}

	private void doubleSleepTime() {
		LOG.debug("Doubling the sleep time");
		sleepTime = Math.min(sleepTime << 1, MAX_SLEEP_TIME);
	}

	public synchronized void queue(List<FrontlineMessage> messages) {
		this.queue.addAll(messages);
	}

	public synchronized void stopProcessing() {
		this.keepProcessing = false;
		// Flush the queue
		this.queue.clear();
		
		// Nullify the queue
		this.queue = null;
	}
	
	synchronized void queue(FrontlineMessage m) {
		queue.add(m);
	}
	
	private synchronized void addToHead(FrontlineMessage m) {
		queue.add(0, m);
	}
	
	private synchronized FrontlineMessage poll() {
		return queue.poll();
	}

}