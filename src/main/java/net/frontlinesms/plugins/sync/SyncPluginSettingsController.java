/**
 * 
 */
package net.frontlinesms.plugins.sync;


import net.frontlinesms.plugins.PluginSettingsController;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.settings.UiSettingsSectionHandler;

/**
 * @author ekala
 *
 */
public class SyncPluginSettingsController implements PluginSettingsController {
	private final UiGeneratorController ui;
	private final SyncSettingsPanelHandler handler;

	public SyncPluginSettingsController(UiGeneratorController ui) {
		this.ui = ui;
		handler = new SyncSettingsPanelHandler(ui);
	}

	/** @see net.frontlinesms.plugins.PluginSettingsController#addSubSettingsNodes(java.lang.Object) */
	public void addSubSettingsNodes(Object rootSettingsNode) {

	}

	/**
	 * @see net.frontlinesms.plugins.PluginSettingsController#getHandlerForSection(java.lang.String)
	 */
	public UiSettingsSectionHandler getHandlerForSection(String section) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.frontlinesms.plugins.PluginSettingsController#getRootPanelHandler()
	 */
	public UiSettingsSectionHandler getRootPanelHandler() {
		return handler;
	}

	/**
	 * @see net.frontlinesms.plugins.PluginSettingsController#getTitle()
	 */
	public String getTitle() {
		return "SMS Sync";
	}

	/**
	 * @see net.frontlinesms.plugins.PluginSettingsController#getRootNode()
	 */
	public Object getRootNode() {
		return ui.createNode("SMS Sync", this.handler);
	}
}