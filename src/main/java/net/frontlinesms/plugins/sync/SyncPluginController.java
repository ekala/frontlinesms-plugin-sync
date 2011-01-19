package net.frontlinesms.plugins.sync;

import java.util.List;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.PluginSettingsController;
import net.frontlinesms.plugins.sync.ui.SyncPluginThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

@PluginControllerProperties(name = "SMS Sync Plugin",
		iconPath = "/icons/basicplugin_logo_small.png",
		springConfigLocation=PluginControllerProperties.NO_VALUE,
		hibernateConfigPath=PluginControllerProperties.NO_VALUE,
		i18nKey="plugins.sync.name")
public class SyncPluginController extends BasePluginController implements EventObserver {

	private ApplicationContext appCon;
	private FrontlineSMS frontlineController;
	private QueueProcessor queueProcessor;
	private SyncMessageDao syncMessageDao;
	private EventBus eventBus;
	private SyncPluginThinletTabController tabController;
	private boolean autoStartup;
	private String syncURL;
	
	QueueProcessor getQueueProcessor() {
		return queueProcessor;
	}
	void setQueueProcessor(QueueProcessor queueProcessor) {
		this.queueProcessor = queueProcessor;
	}
	
	SyncMessageDao getSyncMessageDao() {
		return syncMessageDao;
	}
	
	void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	protected Object initThinletTab(UiGeneratorController uiController) {
		this.tabController = new SyncPluginThinletTabController(this, uiController, appCon);
		
		// Set the synchronisation URL on the UI
		this.tabController.setSynchronisationURL(this.syncURL);
		
		// Set the start up mode on the UI
		this.tabController.setStartupMode(this.autoStartup);
		
		return this.tabController.getTab();
	}

	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
		this.appCon = applicationContext;
		this.frontlineController = frontlineController;
		
		// Create the queue processor
		createQueueProcessor();
		
		setEventBus(frontlineController.getEventBus());
		this.eventBus.registerObserver(this);
		
	}
	
	private void createQueueProcessor() {
		QueueProcessor queueProcessor = new QueueProcessor();
		// TODO configure syncher depending on settings
		
		SyncPluginProperties syncProperties  = SyncPluginProperties.getInstance();
		
		// Get the start up mode
		autoStartup = syncProperties.isAutomaticStartup();
		
		// Get the sync URL
		syncURL = syncProperties.getSynchronisationURL();
		
		// Set the synchronisation URL on the UI
		
		// Instantiate the message syncher
		MessageSyncher syncher = new MessageSyncher(syncURL, syncProperties.getParamsMap());
		syncher.setRequestMethod(syncProperties.getRequestMethod());
		
		queueProcessor.setMessageSyncher(syncher);
		setQueueProcessor(queueProcessor);
		
		// Queue unsynchronised messages
		this.syncMessageDao = new SyncMessageDao(frontlineController.getMessageDao());
		queueUnsynchronizedMessages();
		
		// Automatically startup the queue processor only if the startup mode = TRUE
		if (autoStartup) {
			this.queueProcessor.start();
		}
		
	}

	public void deinit() {
		// Shutdown message processor and discard
		this.eventBus.unregisterObserver(this);
		setQueueProcessorStatus(false);
	}

	private void queueUnsynchronizedMessages() {
		// TODO Auto-generated method stub
		long lastId = SyncPluginProperties.getInstance().getLastSyncedId();
		
		List<FrontlineMessage> messages = this.syncMessageDao.getUnsynchronisedMessages(lastId);
		
		this.queueProcessor.queue(messages);
	}

	public void notify(FrontlineEventNotification e) {
		if(e instanceof EntitySavedNotification) {
			Object databaseEntity = ((EntitySavedNotification) e).getDatabaseEntity();
			if(databaseEntity instanceof FrontlineMessage) {
				// Get the frontline message instance
				FrontlineMessage m = (FrontlineMessage) databaseEntity;
				
				// Only trap for received messages when the queue processor is running
				boolean isAlive = this.queueProcessor != null && this.queueProcessor.isAlive();
				if (m.getType() == FrontlineMessage.Type.RECEIVED && isAlive) {
					this.queueProcessor.queue(m);
				}
			}
		}
	}
	
	/**
	 * Sets the state of the queue processor. If <code>false</code> the queue processor
	 * is paused else its resumed
	 * 
	 * @param state
	 */
	public synchronized void setQueueProcessorStatus(boolean state) {
		if (state) {
			// Re-create the queue processor
			createQueueProcessor();
		} else {
			// Stop the queue processor
			this.queueProcessor.stopProcessing();
			
			// Destroy the current queue processor reference
			this.queueProcessor = null;
		}
	}
	
	@Override
	public PluginSettingsController getSettingsController(UiGeneratorController ui) {
		return new SyncPluginSettingsController(ui);
	}
}
