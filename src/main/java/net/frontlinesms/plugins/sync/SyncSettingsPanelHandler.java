package net.frontlinesms.plugins.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.frontlinesms.messaging.MessageFormatter;
import net.frontlinesms.settings.FrontlineValidationMessage;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.settings.SettingsChangedEventNotification;
import net.frontlinesms.ui.settings.UiSettingsSectionHandler;

public class SyncSettingsPanelHandler implements ThinletUiEventHandler, UiSettingsSectionHandler {
	/** UI definition file for the settings panel*/
	private static final String XML_LAYOUT_FILE = "/ui/plugins/sync/pnSettings.xml";
	/** Dialog for adding parameters */
	private static final String XML_LAYOUT_FILE_PARAM_NEW = "/ui/plugins/sync/dgNewParam.xml";
	/** Name of the synchronisation URL */
	private static final String COMPONENT_TF_URL = "tfUrl";
	/** Parameters table */
	private static final String COMPONENT_TB_PARAMS = "tbParams";
	/** Startup mode checkbox */
	private static final String COMPONENT_CHK_STARTUP_MODE = "chkStartupMode";
	private static final String COMPONENT_TF_PARAM_KEY = "tfParamKey";
	private static final String COMPONENT_TF_PARAM_VALUE = "tfParamValue";

	private final UiGeneratorController ui;
	private Object panel;
	private final SyncPluginProperties syncProperties;
	private Object paramDialog;

	/** 
	 * Constructor 
	 * @param ui {@link UiGeneratorController} instance
	 */
	public SyncSettingsPanelHandler(UiGeneratorController ui) {
		this.ui = ui;
		syncProperties = SyncPluginProperties.getInstance();
	}

	/** Returns a reference to the parameter settings panel */
	public Object getPanel() {
		this.panel = ui.loadComponentFromFile(XML_LAYOUT_FILE, this);
		
		this.setUrlTextfield(syncProperties.getSynchronisationURL());
		this.initParamsTable(syncProperties.getParamsMap());
		setStartupModeCheckboxStatus(syncProperties.isAutomaticStartup());
		return panel;
	}

	private void setStartupModeCheckboxStatus(boolean status) {
		ui.setSelected(find(COMPONENT_CHK_STARTUP_MODE), status);
		
	}

	public void save() {
		syncProperties.setSynchronisationURL(getUrl());
		syncProperties.setParamsMap(getParamsMap());
		syncProperties.saveToDisk();
	}


	private void initParamsTable(Map<String, String> paramsMap) {
		Object table = find(COMPONENT_TB_PARAMS);
		for(Entry<String, String> e : paramsMap.entrySet()) {
			ui.add(table, getParamRow(e.getKey(), e.getValue()));
		}
	}
	private Object getParamRow(String key, String value) {
		Object row = ui.createTableRow();
		ui.add(row, ui.createTableCell(key));
		ui.add(row, ui.createTableCell(value));
		return row;
	}

	private Map<String, String> getParamsMap() {
		Map<String, String> params = new HashMap<String, String>();
		Object paramsTable = find(COMPONENT_TB_PARAMS);
		for(Object row: ui.getItems(paramsTable)) {
			String key = ui.getText(ui.getItem(row, 0));
			String value = ui.getText(ui.getItem(row, 1));
			params.put(key, value);
		}
		return params ;
	}

	private String getUrl() {
		return ui.getText(find(COMPONENT_TF_URL));
	}

	private void setUrlTextfield(String synchronisationURL) {
		ui.setText(find(COMPONENT_TF_URL), synchronisationURL);
	}

	private Object find(String componentName) {
		return ui.find(this.panel, componentName);
	}

	public List<FrontlineValidationMessage> validateFields() {
		// TODO check URL is valid
		// TODO check the key-value pairs are valid
		return null;
	}

	public String getTitle() {
		return "What is this";
	}
	
	/** Displays the parameter definition dialog */
	public void addParam() {
		ui.add(getNewParamDialog());
		hasChanged(COMPONENT_TB_PARAMS);
	}
	
	/** Adds the key,value pair contained in @param row */
	public void addParam(Object row) {
		ui.add(find(COMPONENT_TB_PARAMS), row);
	}
	
	/** Adds a key-value pair to the parameters table */
	public void addParam(String key, String value) {
		ui.add(find(COMPONENT_TB_PARAMS), getParamRow(key, value));
		
		// Remove any object currently attached to the dialog
		ui.setAttachedObject(paramDialog, null);
		
		removeDialog();
	}
	
	/** Loads the parameter defintion dialog and returns a reference to the same */
	private Object getNewParamDialog() {
		paramDialog = ui.loadComponentFromFile(XML_LAYOUT_FILE_PARAM_NEW, this);
		return paramDialog;
	}

	/** Deletes a parameter item from the parameters list */
	public void removeParam() {
		ui.remove(ui.getSelectedItem(find(COMPONENT_TB_PARAMS)));
	}
	
	/** Removes the dialog from the UI */
	public void removeDialog() {
		Object row = ui.getAttachedObject(paramDialog);
		
		if (row != null) {
			addParam(row);
		}
		
		ui.remove(paramDialog);
		paramDialog = null;
		hasChanged(COMPONENT_TB_PARAMS);
	}
	
	public void hasChanged(String componentName) {
		// TODO check for unchanges
		SettingsChangedEventNotification notification = new SettingsChangedEventNotification("plugin.sync." + componentName, false);
		this.ui.getFrontlineController().getEventBus().notifyObservers(notification);
	}
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
		String value = ui.getText(textField);
		ui.setText(textField, value + markerMessageContent);
		
		ui.repaint(this.panel);
	}
	
	/** Sets start up mode of the SMS Sync plugin */
	public void setStartupMode(Object button) {
		boolean mode = ui.isSelected(button);
		syncProperties.setStartupMode(mode);
		
		// Fire the change listener
		hasChanged(COMPONENT_CHK_STARTUP_MODE);
	}
	
	/** Shows the edit dialog for the parameter item in @param row */
	public void showParameterItem(Object table) {
		Object row = ui.getSelectedItem(table);
		
		String key = ui.getText(ui.getItem(row, 0));
		String value = ui.getText(ui.getItem(row, 1));
		
		// Generate the dialog
		getNewParamDialog();
		
		// Set the text fields for the dialog
		ui.setText(ui.find(paramDialog, COMPONENT_TF_PARAM_KEY), key);
		ui.setText(ui.find(paramDialog, COMPONENT_TF_PARAM_VALUE), value);
		
		// Attach the row object to the dialog & remove it from the table to prevent duplicate entries
		ui.setAttachedObject(paramDialog, row);
		ui.remove(row);		
		
		// Show the dialog
		ui.add(paramDialog);
		
	}
}