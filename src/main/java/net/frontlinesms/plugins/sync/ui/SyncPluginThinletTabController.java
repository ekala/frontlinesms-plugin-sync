package net.frontlinesms.plugins.sync.ui;

import net.frontlinesms.plugins.BasePluginThinletTabController;
import net.frontlinesms.plugins.sync.SyncPluginController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class SyncPluginThinletTabController extends BasePluginThinletTabController<SyncPluginController> implements ThinletUiEventHandler{

	private ApplicationContext appCon;
	
	private static final String TAB_XML_FILE = "/ui/plugins/sync/syncPluginTab.xml";
	
	public SyncPluginThinletTabController(SyncPluginController pluginController, UiGeneratorController uiController, ApplicationContext appCon) {
		super(pluginController, uiController);
		this.appCon = appCon;
		super.setTabComponent(uiController.loadComponentFromFile(TAB_XML_FILE, this));
	}
	
	public Object getTab(){
		return super.getTabComponent();
	}

}
