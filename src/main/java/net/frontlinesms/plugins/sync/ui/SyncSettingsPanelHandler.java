package net.frontlinesms.plugins.sync.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.frontlinesms.events.AppPropertiesEventNotification;
import net.frontlinesms.messaging.MessageFormatter;
import net.frontlinesms.plugins.sync.SyncPluginProperties;
import net.frontlinesms.settings.BaseSectionHandler;
import net.frontlinesms.settings.FrontlineValidationMessage;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.settings.SettingsChangedEventNotification;
import net.frontlinesms.ui.settings.UiSettingsSectionHandler;

public class SyncSettingsPanelHandler extends BaseSectionHandler implements ThinletUiEventHandler, UiSettingsSectionHandler {
//> UI FILES
	private static final String XML_LAYOUT_FILE = "/ui/plugins/sync/pnSettings.xml";
	private static final String XML_LAYOUT_FILE_PARAM_NEW = "/ui/plugins/sync/dgNewParam.xml";
	
//> COMPONENT NAMES
	private static final String COMPONENT_TF_URL = "tfUrl";
	private static final String COMPONENT_TB_PARAMS = "tbParams";
	private static final String COMPONENT_CHK_STARTUP_MODE = "chkStartupMode";
	private static final String COMPONENT_TF_PARAM_KEY = "tfParamKey";
	private static final String COMPONENT_TF_PARAM_VALUE = "tfParamValue";
	
//> PROPERTIES
	private final SyncPluginProperties syncProperties;
	/** Parameter definition dialog */
	private Object paramDialog;
	/** Name of the plugin */
	private String pluginName;
	/** Path for the plugin's icon */
	private String pluginIcon;

//> CONSTRUCTOR
	/**
	 * Initialises the UI for specifying the settings for the Sync plugin
	 * 
	 * @param ui
	 * @param pluginName
	 * @param pluginIcon
	 */
	public SyncSettingsPanelHandler(UiGeneratorController ui, String pluginName, String pluginIcon) {
		super(ui);
		this.pluginName = pluginName;
		this.pluginIcon = pluginIcon;
		
		syncProperties = SyncPluginProperties.getInstance();
	}
	
	@Override
	protected void init() {
		this.panel = uiController.loadComponentFromFile(XML_LAYOUT_FILE, this);
		
		this.setUrlTextfield(syncProperties.getSynchronisationURL());
		this.initParamsTable(syncProperties.getParamsMap());
		setStartupModeCheckboxStatus(syncProperties.isAutomaticStartup());
	}

	private void setStartupModeCheckboxStatus(boolean status) {
		uiController.setSelected(find(COMPONENT_CHK_STARTUP_MODE), status);
	}

	/** Saves the synchronisation settings */
	public void save() {
		syncProperties.setSynchronisationURL(getUrl());
		syncProperties.setParamsMap(getParamsMap());
		syncProperties.saveToDisk();
		
		// Notify observers of the change in auto start setting and synchronisation URL
		this.eventBus.notifyObservers(new AppPropertiesEventNotification(SyncPluginProperties.class, SyncPluginProperties.PROP_SYNC_URL));
		this.eventBus.notifyObservers(new AppPropertiesEventNotification(SyncPluginProperties.class, SyncPluginProperties.PROP_AUTO_START));
	}

	
	/** Populates the parameters table with the currently saved set of parameters */
	private void initParamsTable(Map<String, String> paramsMap) {
		Object table = find(COMPONENT_TB_PARAMS);
		for(Entry<String, String> e : paramsMap.entrySet()) {
			uiController.add(table, getParamRow(e.getKey(), e.getValue()));
		}
	}
	
	/** Gets UI table row from the parameters table */
	private Object getParamRow(String key, String value) {
		Object row = uiController.createTableRow();
		uiController.add(row, uiController.createTableCell(key));
		uiController.add(row, uiController.createTableCell(value));
		return row;
	}

	/** Generates a {@link Map} of the parameters in the parameters table on the UI */
	private Map<String, String> getParamsMap() {
		Map<String, String> params = new HashMap<String, String>();
		Object paramsTable = find(COMPONENT_TB_PARAMS);
		for(Object row: uiController.getItems(paramsTable)) {
			String key = uiController.getText(uiController.getItem(row, 0));
			String value = uiController.getText(uiController.getItem(row, 1));
			params.put(key, value);
		}
		return params ;
	}

	/** Gets the text in the synchronisation URL text field */
	private String getUrl() {
		return uiController.getText(find(COMPONENT_TF_URL));
	}

	/** Sets the text for the synchronsation URL text field*/
	//TODO Validate the URL
	private void setUrlTextfield(String synchronisationURL) {
		uiController.setText(find(COMPONENT_TF_URL), synchronisationURL);
	}

	public List<FrontlineValidationMessage> validateFields() {
		// TODO check URL is valid
		// TODO check the key-value pairs are valid
		return null;
	}

	/** Gets the title for this section */
	public String getTitle() {
		return this.pluginName;
	}
	
	/** Creates a node for the settings section */
	public Object getSectionNode() {
		return createSectionNode(getTitle(), this, this.pluginIcon);
	}
	
	/** Displays the parameter definition dialog */
	public void addParam() {
		uiController.add(getNewParamDialog());
		hasChanged(COMPONENT_TB_PARAMS);
	}
	
	/** Adds the key,value pair contained in @param row */
	public void addParam(Object row) {
		uiController.add(find(COMPONENT_TB_PARAMS), row);
	}
	
	/** Adds a key-value pair to the parameters table */
	public void addParam(String key, String value) {
		uiController.add(find(COMPONENT_TB_PARAMS), getParamRow(key, value));
		
		// Remove any object currently attached to the dialog
		uiController.setAttachedObject(paramDialog, null);
		
		removeDialog();
	}
	
	/** Loads the parameter defintion dialog and returns a reference to the same */
	private Object getNewParamDialog() {
		paramDialog = uiController.loadComponentFromFile(XML_LAYOUT_FILE_PARAM_NEW, this);
		return paramDialog;
	}

	/** Deletes a parameter item from the parameters list */
	public void removeParam() {
		uiController.remove(uiController.getSelectedItem(find(COMPONENT_TB_PARAMS)));
	}
	
	/** Removes the dialog from the UI */
	public void removeDialog() {
		Object row = uiController.getAttachedObject(paramDialog);
		
		if (row != null) {
			addParam(row);
		}
		
		uiController.remove(paramDialog);
		paramDialog = null;
		hasChanged(COMPONENT_TB_PARAMS);
	}
	
	public void hasChanged(String componentName) {
		// TODO check for unchanges
		SettingsChangedEventNotification notification = new SettingsChangedEventNotification("plugin.sync." + componentName, false);
		this.uiController.getFrontlineController().getEventBus().notifyObservers(notification);
	}
	
	/** Sets start up mode of the SMS Sync plugin */
	public void setStartupMode(Object button) {
		boolean mode = uiController.isSelected(button);
		syncProperties.setStartupMode(mode);
		
		// Fire the change listener
		hasChanged(COMPONENT_CHK_STARTUP_MODE);
	}
	
	
//> EVENT HELPERS FOR THE PARAMETERS DIALOG	
	/** 
	 * Adds the sender's name parameter to the URL specified in the sync URL textfield 
	 * 
	 * @param textField 
	 */
	public void addSenderNameParameter(Object textField) {
		updateParamValueTextField(textField, MessageFormatter.MARKER_SENDER_NAME);
	}
	

	/** 
	 * Adds the sender's number parameter to the URL contained in sync URL textfield 
	 * 
	 * @param textField 
	 */
	public void addSenderNumberParameter(Object textField) {
		updateParamValueTextField(textField, MessageFormatter.MARKER_SENDER_NUMBER);
	}
	
	/** 
	 * Adds the message content parameter to the URL contained in the sync URL field 
	 * 
	 * @param textField 
	 */
	public void addMessageContentParameter(Object textField) {
		updateParamValueTextField(textField, MessageFormatter.MARKER_MESSAGE_CONTENT);
	}
	
	/** Updates the value of the parameter text field */
	private void updateParamValueTextField(Object textField, String markerMessageContent) {
		String value = uiController.getText(textField);
		uiController.setText(textField, value + markerMessageContent);
		
		uiController.repaint(this.panel);
	}
	
	/** Shows the edit dialog for the parameter item in @param row */
	public void showParameterItem(Object table) {
		Object row = uiController.getSelectedItem(table);
		
		String key = uiController.getText(uiController.getItem(row, 0));
		String value = uiController.getText(uiController.getItem(row, 1));
		
		// Generate the dialog
		getNewParamDialog();
		
		// Set the text fields for the dialog
		uiController.setText(uiController.find(paramDialog, COMPONENT_TF_PARAM_KEY), key);
		uiController.setText(uiController.find(paramDialog, COMPONENT_TF_PARAM_VALUE), value);
		
		// Attach the row object to the dialog & remove it from the table to prevent duplicate entries
		uiController.setAttachedObject(paramDialog, row);
		uiController.remove(row);		
		
		// Show the dialog
		uiController.add(paramDialog);
		
	}
}