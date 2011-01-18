package net.frontlinesms.plugins.sync;


import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.resources.UserHomeFilePropertySet;

public class SyncPluginProperties extends UserHomeFilePropertySet {
	private static final String PROP_LAST_ID = "sync.last.id";
	private static final String PROP_SYNC_URL = "sync.url";
	private static final String PROP_REQUEST_METHOD = "sync.request.method";
	private static final String PROP_STARTUP_MODE = "sync.startup.mode";
	private static SyncPluginProperties instance;

//> CONSTRUCTORs	
	private SyncPluginProperties() {
		super("plugin.sync");
	}
	
//> SETTERS AND GETTERS
	/** Gets the last message id to be synchronised */
	public long getLastSyncedId() {
		String val = super.getProperty(PROP_LAST_ID);
		return val == null ? 0 : Long.parseLong(val);
	}

	/** Sets the last message id to be synchronised */
	public void setLastSyncedId(FrontlineMessage message) {
		// TODO get this id properly
		long id = SyncUtils.getId(message);
		
		super.setProperty(PROP_LAST_ID, Long.toString(id));
	}

	/** Gets the URL to which this plugin is synchronising message */
	public String getSynchronisationURL() {
		return super.getProperty(PROP_SYNC_URL);
	}
	
	/** Sets the synchronisation URL in the properties file for this plugin */
	public void setSynchronisationURL(String url) {
		super.setProperty(PROP_SYNC_URL, url);
	}
	
	/** Gets the request method for the synchronisation URL */
	public String getRequestMethod() {
		String method =  super.getProperty(PROP_REQUEST_METHOD);
		return method == null? "GET":method;
	}
	
	/** Sets the request method for the synchronisation URL */
	public void setRequestMethod(String requestMethod) {
		super.setProperty(PROP_REQUEST_METHOD, requestMethod);
	}
	
	public boolean isAutomaticStartup() {
		String startupMode = super.getProperty(PROP_STARTUP_MODE);
		
		return startupMode == null ? false : Boolean.parseBoolean(startupMode);
	}
	
	public void setStartupMode(boolean mode) {
		super.setProperty(PROP_STARTUP_MODE, Boolean.toString(mode));
	}
	
//> STATIC FACTORIES
	/**
	 * Lazy getter for {@link #instance}
	 * @return The singleton instance of this class
	 */
	public static synchronized SyncPluginProperties getInstance() {
		if(instance == null) {
			instance = new SyncPluginProperties();
		}
		return instance;
	}

}