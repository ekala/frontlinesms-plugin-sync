package net.frontlinesms.plugins.sync;

import java.util.LinkedList;

import net.frontlinesms.FrontlineSMS;
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
				FrontlineMessage m = (FrontlineMessage) databaseEntity;
				this.queueProcessor.queue(m);
			}
		}
	}
	
}

// Queue processing thread
class QueueProcessor extends Thread {
	private LinkedList<FrontlineMessage> queue = new LinkedList<FrontlineMessage>();
	
	@Override
	public void run() {
		// Get messages off the queue
		FrontlineMessage message = poll();
		
		// Attempt to sync message and if it fails, put it back at the head of the queue
		boolean status = syncMessage(message);
		if (!status) {
			addToHead(message);
		}
	}

	private boolean syncMessage(FrontlineMessage message) {
		// TODO Auto-generated method stub
		return false;
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
