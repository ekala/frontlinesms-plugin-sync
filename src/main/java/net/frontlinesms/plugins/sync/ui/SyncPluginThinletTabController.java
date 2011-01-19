package net.frontlinesms.plugins.sync.ui;

import net.frontlinesms.plugins.BasePluginThinletTabController;
import net.frontlinesms.plugins.sync.SyncPluginController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class SyncPluginThinletTabController extends BasePluginThinletTabController<SyncPluginController> implements ThinletUiEventHandler{

	private ApplicationContext appCon;
	
	/** UI files*/
	private static final String TAB_XML_FILE = "/ui/plugins/sync/syncPluginTab.xml";
	
	/** UI Component names */
	private static final String COMPONENT_CHK_STARTUP_MODE = "chkStartAutomatically";
	private static final String COMPONENT_FLD_SYNCHRONISATION_URL = "txtSynchronisationURL";
	private static final String COMPONENT_BTN_START_SYNC = "btnStartSynchronisation";
	private static final String COMPONENT_BTN_STOP_SYNC = "btnStopSynchronisation";
	private static final String COMPONENT_TB_SYNCHRONISATION_LOG = "tblSychronisationLog";
	
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
		
	/** 
	 * Sets the selected value of the the start up mode check box 
	 */
	public void setStartupMode(boolean mode) {
		Object chkStartupMode = this.ui.find(this.tabComponent, COMPONENT_CHK_STARTUP_MODE);
		
		// Get references to the buttons
		Object btnStart = ui.find(this.tabComponent, COMPONENT_BTN_START_SYNC);
		Object btnStop = ui.find(tabComponent, COMPONENT_BTN_STOP_SYNC);
		
		// Disable/Enable the start & stop buttons depending on the start up mode
		if (mode) {
			ui.setEnabled(btnStart, false);
			ui.setEnabled(btnStop, true);
			ui.setEnabled(ui.find(tabComponent, COMPONENT_FLD_SYNCHRONISATION_URL), false);
		} else {
			ui.setEnabled(btnStop, false);
		}
		
		ui.setSelected(chkStartupMode, mode);
		ui.repaint(this.tabComponent);
	}
	
	/** 
	 * Sets the text for the synchronisation URL field 
	 * 
	 * @param syncURL Synchronisation URL 
	 */
	public void setSynchronisationURL(String syncURL) {
		// Get the text field
		Object txtField = this.ui.find(this.tabComponent, COMPONENT_FLD_SYNCHRONISATION_URL);
		
		// Set the new URL text
		this.ui.setText(txtField, syncURL);
		
		// Repaint the component
		this.ui.repaint(txtField);
	}
	
	/** 
	 * Gets the synchronisation URL value from the text field in the UI
	 */ 
	public String getSynchronisationURL() {
		String url = ui.getText(ui.find(this.tabComponent, COMPONENT_FLD_SYNCHRONISATION_URL));
		return (url.length() == 0 || url == null)?"" : url;
	}
	
	/**
	 * Gets the start up mode currently specified in the UI 
	 * @return
	 */
	public boolean getStartupMode() {
		return ui.isSelected(ui.find(this.tabComponent, COMPONENT_CHK_STARTUP_MODE));
	}
	
	/** Event helper method for pausing/stopping the synchronisation thread */
	public void stopSynchronisation(Object startButton, Object stopButton) {
		ui.setEnabled(ui.find(tabComponent, COMPONENT_FLD_SYNCHRONISATION_URL), true);
		modifySynchronisationState(stopButton, startButton, false);
	}
	
	/** Event helper method for starting the synchronisation thread */
	public void startSynchronisation(Object startButton, Object stopButton) {
		ui.setEnabled(ui.find(tabComponent, COMPONENT_FLD_SYNCHRONISATION_URL), false);
		modifySynchronisationState(stopButton, startButton, true);
	}
	
	private void modifySynchronisationState(Object stopButton, Object startButton, boolean state) {
		ui.setEnabled(stopButton, state);
		ui.setEnabled(startButton, !state);
		
		// Send stop signal to queue processor
		SyncPluginController pluginController = (SyncPluginController)getPluginController();
		pluginController.setQueueProcessorStatus(state);
		
		// Repain the UI
		ui.repaint(this.tabComponent);
	}
	
	/** Updates the synchronisation log */
	public void updateSynchronisationLog(String message) {
		Object table = ui.find(this.tabComponent, COMPONENT_TB_SYNCHRONISATION_LOG);
		Object row = ui.createTableRow();
		
		ui.add(row, ui.createTableCell(message));
		ui.add(table, row);
		ui.repaint(table);
	}
	
	/** Removes all the items from the sychronisation log */
	public void clearSynchronisationLog(Object table) {
		ui.removeAll(table);
		
		ui.repaint();
	}

}
