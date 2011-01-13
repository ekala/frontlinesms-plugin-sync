package net.frontlinesms.plugins.sync;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.sync.ui.SyncPluginThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import IntelliSoftware.Common.HTTPConnection;

@PluginControllerProperties(name = "Basic Plugin",
		iconPath = "/icons/basicplugin_logo_small.png",
		springConfigLocation=PluginControllerProperties.NO_VALUE,
		hibernateConfigPath=PluginControllerProperties.NO_VALUE,
		i18nKey="plugins.sync.name")
public class SyncPluginController extends BasePluginController  implements EventObserver {

	private ApplicationContext appCon;
	private FrontlineSMS frontlineController;
	private QueueProcessor queueProcessor;
	
	protected Object initThinletTab(UiGeneratorController uiController) {
		return new SyncPluginThinletTabController(this, uiController, appCon).getTab();
	}

	public void deinit() {
		// TODO shutdown message processor and discard
		this.queueProcessor.stopProcessing();
	}

	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
		this.appCon = applicationContext;
		this.frontlineController = frontlineController;
		
		this.queueProcessor = new QueueProcessor();
		
		// TODO Queue unsychronised messages
		
		this.queueProcessor.start();
	}

	public void notify(FrontlineEventNotification e) {
		if(e instanceof EntitySavedNotification) {
			Object databaseEntity = ((EntitySavedNotification) e).getDatabaseEntity();
			if(databaseEntity instanceof FrontlineMessage) {
				// Get the frontline message instance
				FrontlineMessage m = (FrontlineMessage) databaseEntity;
				
				// Only trap for received messages
				if (m.getType() == FrontlineMessage.Type.RECEIVED)
					this.queueProcessor.queue(m);
			}
		}
	}
	
}

// Queue processing thread
class QueueProcessor extends Thread {
	private static final long MAX_SLEEP_TIME = 1000;
	private static final long INITIAL_SLEEP_TIME = 10;
	private LinkedList<FrontlineMessage> queue = new LinkedList<FrontlineMessage>();
	private boolean keepProcessing;
	private long sleepTime = INITIAL_SLEEP_TIME;
	
	@Override
	public void run() {
		// loop this until shutdown
		this.keepProcessing = true;
		while(this.keepProcessing) {
		
			// Get messages off the queue
			FrontlineMessage message = poll();
			
			if(message != null) {
				// Attempt to sync message and if it fails, put it back at the head of the queue
				boolean status = syncMessage(message);
				if (!status) {
					addToHead(message);
				}
				sleepTime = INITIAL_SLEEP_TIME;
			} else {
				sleepTime = Math.min(sleepTime << 1, MAX_SLEEP_TIME);
			}
			try {
				Thread.sleep(sleepTime);
			} catch(InterruptedException ex) {
				// ignore this...!
			}
		}
	}

	public void stopProcessing() {
		this.keepProcessing = false;
	}

	private boolean syncMessage(FrontlineMessage message) {
		Map<String, String> paramMap = createParamMap(message);
		return doPost(getUrl(), paramMap);
	}

	private boolean doPost(String url, Map<String, String> paramMap) {
		String data = buildRequestString(paramMap);
		
		HttpURLConnection conn = null;
		OutputStreamWriter out = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.flush();
			
			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (Exception e) {
			return false;
		} finally {
			try { out.close(); } catch(Exception ex) { /* ignore */ }
			try { conn.disconnect(); } catch(Exception ex) { /* ignore */ }
		}
	}

	private static String buildRequestString(Map<String, String> paramMap) {
		StringBuilder bob = new StringBuilder();
		for(Entry<String, String> e : paramMap.entrySet()) {
			bob.append('&');
			assert(!e.getKey().contains("="));
			bob.append(e.getKey());
			bob.append('=');
			bob.append(FrontlineUtils.urlEncode(e.getValue()));
		}
		return bob.length() > 0 ? bob.substring(1) : "";
	}

	private String getUrl() {
		return "http://localhost/whatever";
	}

	private Map<String, String> createParamMap(FrontlineMessage message) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("sender", message.getSenderMsisdn());
		params.put("text", message.getTextContent());
		return params;
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
