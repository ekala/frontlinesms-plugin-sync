/**
 * 
 */
package net.frontlinesms.plugins.sync;


import net.frontlinesms.plugins.PluginSettingsController;
import net.frontlinesms.plugins.sync.ui.SyncSettingsPanelHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.settings.UiSettingsSectionHandler;

/**
 * @author ekala
 *
 */
public class SyncPluginSettingsController implements PluginSettingsController {
	private static final String I18N_PLUGIN_SYNC_NAME = "plugin.sync.name";
	private final SyncSettingsPanelHandler handler;
	private String pluginIcon;

	public SyncPluginSettingsController(UiGeneratorController ui, String pluginIcon) {
		this.pluginIcon = pluginIcon;
		handler = new SyncSettingsPanelHandler(ui, this.getTitle(), this.pluginIcon);
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
		return InternationalisationUtils.getI18nString(I18N_PLUGIN_SYNC_NAME);
	}

	/**
	 * @see net.frontlinesms.plugins.PluginSettingsController#getRootNode()
	 */
	public Object getRootNode() {
		return this.handler.getSectionNode();
	}
}