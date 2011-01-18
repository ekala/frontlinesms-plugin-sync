package net.frontlinesms.plugins.sync.ui;

import net.frontlinesms.plugins.BasePluginThinletTabController;
import net.frontlinesms.plugins.sync.SyncPluginController;
import net.frontlinesms.plugins.sync.SyncUtils;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class SyncPluginThinletTabController extends BasePluginThinletTabController<SyncPluginController> implements ThinletUiEventHandler{

	private ApplicationContext appCon;
	
	private static final String TAB_XML_FILE = "/ui/plugins/sync/syncPluginTab.xml";
	private static final String COMPONENT_CHK_STARTUP_MODE = "chkStartAutomatically";
	private static final String COMPONENT_FLD_SYNCHRONISATION_URL = "txtSynchronisationURL";
	private Object tabComponent;
	
	public SyncPluginThinletTabController(SyncPluginController pluginController, UiGeneratorController uiController, ApplicationContext appCon) {
		super(pluginController, uiController);
		this.appCon = appCon;
		this.tabComponent = uiController.loadComponentFromFile(TAB_XML_FILE, this);
		super.setTabComponent(tabComponent);
	}
	
	public Object getTab(){
		return this.tabComponent;
	}
		
	/** Sets the selected value of the the start up mode check box */
	public void setStartupMode(boolean mode) {
		Object chkStartupMode = this.ui.find(this.tabComponent, COMPONENT_CHK_STARTUP_MODE);
		ui.setSelected(chkStartupMode, mode);
		ui.repaint(chkStartupMode);
	}
	
	/** Adds the sender's name parameter to the URL contained in the field specified in @param textField */
	public void addSenderNameParameter(Object textField) {
		updateSynchronisationURL(textField, SyncUtils.PARAM_SENDER_NAME);
	}
	

	/** Adds the sender's number parameter to the URL contained in the field in @param textField */
	public void addSenderNumberParameter(Object textField) {
		updateSynchronisationURL(textField, SyncUtils.PARAM_SENDER_NUMBER);
	}
	
	/** Adds the message content parameter to the URL contained in the field in @param textField */
	public void addMessageContentParameter(Object textField) {
		updateSynchronisationURL(textField, SyncUtils.PARAM_MESSAGE_CONTENT);
	}
	
	/** Update the synchronisation URL display */
	private void updateSynchronisationURL(Object textField, String parameterName) {
		String updatedURL =  this.ui.getText(textField) + parameterName;
		
		this.ui.setText(textField, updatedURL);
		this.ui.repaint(textField);
	}

	/** Sets the text for the synchronisation URL field @param syncURL Synchronisation URL */
	public void setSynchronisationURL(String syncURL) {
		// Get the text field
		Object txtField = this.ui.find(this.tabComponent, COMPONENT_FLD_SYNCHRONISATION_URL);
		
		// Set the new URL text
		this.ui.setText(txtField, syncURL);
		
		// Repaint the component
		this.ui.repaint(txtField);
	}
	
	/** Gets the synchronisation URL value from the text field in the UI*/ 
	public String getSynchronisationURL() {
		String url = ui.getText(ui.find(this.tabComponent, COMPONENT_FLD_SYNCHRONISATION_URL));
		return (url.length() == 0 || url == null)?"" : url;
	}
	
	public boolean getStartupMode() {
		return ui.isSelected(ui.find(this.tabComponent, COMPONENT_CHK_STARTUP_MODE));
	}
	
	

}
