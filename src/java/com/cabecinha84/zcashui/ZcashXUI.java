package com.cabecinha84.zcashui;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import com.vaklinov.zcashui.LanguageUtil;
import com.vaklinov.zcashui.Log;
import com.vaklinov.zcashui.OSUtil;

public class ZcashXUI {

	public static final String BUTTON_PROPERTY_COLOR = "button.background.color";
	public static final String BUTTON_SELECT_PROPERTY_COLOR = "button.select.background.color";
	public static final String CHECKBOX_PROPERTY_COLOR = "checkbox.background.color";
	public static final String CHECKBOX_SELECT_PROPERTY_COLOR = "checkbox.select.background.color";
	public static final String COLORCHOOSER_PROPERTY_COLOR = "colorchooser.background.color";
	public static final String COMBOBOX_PROPERTY_COLOR = "combobox.background.color";
	public static final String CONTAINER_PROPERTY_COLOR = "container.background.color";
	public static final String DIALOG_PROPERTY_COLOR = "dialog.background.color";
	public static final String FILECHOOSER_PROPERTY_COLOR = "filechooser.background.color";
	public static final String FRAME_PROPERTY_COLOR = "frame.background.color";
	public static final String LIST_PROPERTY_COLOR = "list.background.color";
	public static final String MENU_PROPERTY_COLOR = "menu.background.color";
	public static final String MENU_SELECTION_PROPERTY_COLOR = "menu.selection.background.color";
	public static final String MENUBAR_PROPERTY_COLOR = "menubar.background.color";
	public static final String MENUITEM_PROPERTY_COLOR = "menuitem.background.color";
	public static final String MENUITEM_SELECTION_PROPERTY_COLOR = "menuitem.selection.background.color";
	public static final String PANEL_PROPERTY_COLOR = "panel.background.color";
	public static final String PASSWORDFIELD_PROPERTY_COLOR = "passwordfield.background.color";
	public static final String POPUPMENU_PROPERTY_COLOR = "popupmenu.background.color";
	public static final String PRESENTATIONPANEL_PROPERTY_COLOR = "presentationpanel.background.color";
	public static final String PRESENTATIONPANEL_BORDER_PROPERTY_COLOR = "presentationpanel.border.color";
	public static final String PROGRESSBAR_PROPERTY_COLOR = "progressbar.background.color";
	public static final String PROGRESSBAR_FOREGROUND_PROPERTY_COLOR = "progressbar.foregroundcolor";
	public static final String RADIOBUTTON_PROPERTY_COLOR = "radiobutton.background.color";
	public static final String SCROLLBAR_PROPERTY_COLOR = "scrollbar.background.color";
	public static final String SCROLLBAR_THUMB_PROPERTY_COLOR = "scrollbar.thumb.background.color";
	public static final String SCROLLBAR_FOREGROUND_PROPERTY_COLOR = "scrollbar.foreground.color";
	public static final String SCROLLPANE_PROPERTY_COLOR = "scrollpane.background.color";
	public static final String STARTUP_PROPERTY_COLOR = "startup.background.color";
	public static final String SPLITPANE_PROPERTY_COLOR = "splitpane.background.color";
	public static final String TABBEDPANE_PROPERTY_COLOR = "tabbedpane.background.color";
	public static final String TABBEDPANE_UNSELECTED_PROPERTY_COLOR = "tabbedPane.unselected.background.color";
	public static final String TABLE_PROPERTY_COLOR = "table.background.color";
	public static final String TABLE_HEADER_PROPERTY_COLOR = "table.header.background.color";
	public static final String TEXT_PROPERTY_COLOR = "text.color";
	public static final String TEXTAREA_PROPERTY_COLOR = "textarea.background.color";
	public static final String TEXTFIELD_PROPERTY_COLOR = "textfield.background.color";
	public static final String TEXTPANE_PROPERTY_COLOR = "textpane.background.color";
	public static final String TOOLTIP_PROPERTY_COLOR = "tooltip.background.color";
	public static final String VIEWPORT_PROPERTY_COLOR = "viewport.background.color";
	public static final String MESSAGE_SENT_PROPERTY_COLOR = "message.sent.color";
	public static final String MESSAGE_RECEIVED_PROPERTY_COLOR = "message.received.color";

	public static final String CURRENCY = "currency";

	public static final String DEFAULT_COLOR = "#ffffff";
	public static final String DEFAULT_COLOR_BLACK = "#000000";
	public static final String DEFAULT_COLOR_RED = "#ff0000";
	public static final String DEFAULT_COLOR_BLUE = "#0000ff";
	public static final String DEFAULT_CURRENCY = "USD";
	
	public static Color button;
	public static Color buttonSelect;
	public static Color checkbox;
	public static Color checkboxSelect;
	public static Color colorChooser;
	public static Color combobox;
	public static Color container;
	public static Color dialog;
	public static Color filechooser;
	public static Color frame;
	public static Color list;
	public static Color menu;
	public static Color menuSelection;
	public static Color menubar;
	public static Color menuitem;
	public static Color menuitemSelection;
	public static Color panel;
	public static Color passwordfield;
	public static Color popupmenu;
	public static Color presentationpanel;
	public static Color presentationpanelBorder;
	public static Color progressbar;
	public static Color progressbarForeground;
	public static Color radiobutton;
	public static Color scrollbar;
	public static Color scrollbarForeground;
	public static Color scrollbarThumb;
	public static Color scrollpane;
	public static Color splitpane;
	public static Color startup;
	public static Color tabbedpane;
	public static Color tabbedpaneUnselected;
	public static Color table;
	public static Color tableHeader;
	public static Color text;
	public static Color textarea;
	public static Color textfield;
	public static Color textpane;
	public static Color tooltip;
	public static Color viewport;
	public static Color messageSent;
	public static Color messageReceived;
	public static String currency;

	private static LanguageUtil langUtil = LanguageUtil.instance();
	
	public ZcashXUI() {
		Log.info("Loading ZECmate");
		loadZcashXUIFile();
		javax.swing.UIManager.put("ScrollBar.background", ZcashXUI.scrollbar);
    	javax.swing.UIManager.put("ScrollPane.background", ZcashXUI.scrollpane);
    	javax.swing.UIManager.put("SplitPane.background", ZcashXUI.splitpane);
    	javax.swing.UIManager.put("TabbedPane.unselectedTabBackground", ZcashXUI.tabbedpaneUnselected);
		javax.swing.UIManager.put("TabbedPane.focus", ZcashXUI.tabbedpane);
    	javax.swing.UIManager.put("Viewport.background", ZcashXUI.viewport);
    	javax.swing.UIManager.put("ToolTip.background", ZcashXUI.tooltip);
    	
    	javax.swing.UIManager.put("Menu.selectionBackground", ZcashXUI.menuSelection);
    	javax.swing.UIManager.put("MenuItem.selectionBackground", ZcashXUI.menuitemSelection);
    	javax.swing.UIManager.put("MenuBar.selectionBackground", ZcashXUI.menuitemSelection);
    	javax.swing.UIManager.put("Button.select", ZcashXUI.buttonSelect);
    	javax.swing.UIManager.put("CheckBox.select", ZcashXUI.checkboxSelect);
    	javax.swing.UIManager.put("ScrollBar.thumb", ZcashXUI.scrollbarThumb);
		javax.swing.UIManager.put("ScrollBar.foreground", ZcashXUI.scrollbarForeground);
		javax.swing.UIManager.put("ScrollBar.background", ZcashXUI.scrollbar);
		javax.swing.UIManager.put("ColorChooser.background", ZcashXUI.colorChooser);
		javax.swing.UIManager.put("ColorChooser.swatchesDefaultRecentColor", ZcashXUI.colorChooser);
		javax.swing.UIManager.put("Focus.color", ZcashXUI.viewport);
		javax.swing.UIManager.put("Slider.focus", ZcashXUI.viewport);
		javax.swing.UIManager.put("Button.focus", ZcashXUI.viewport);
		javax.swing.UIManager.put("OptionPane.background",ZcashXUI.viewport);
		javax.swing.UIManager.put("OptionPane.foreground",ZcashXUI.text);
		javax.swing.UIManager.put("OptionPane.messageForeground",ZcashXUI.text);
		javax.swing.UIManager.put("Button.background",ZcashXUI.button);
		javax.swing.UIManager.put("Button.foreground",ZcashXUI.text);
		javax.swing.UIManager.put("Panel.background",ZcashXUI.panel);
		javax.swing.UIManager.put("Panel.foreground",ZcashXUI.text);
		javax.swing.UIManager.put("OptionPane.okButtonText", langUtil.getString("button.option.ok"));
		

		Log.info("Finished loading ZECmate");
	}

	private void loadZcashXUIFile() {
		try {
			
			String settingsDir = OSUtil.getSettingsDirectory();
			File zcashConf = new File(settingsDir + File.separator + "zcash_ui.properties");
			if (!zcashConf.exists())
			{
				Log.warning("Could not find file: {0} , will create a new one from default!", zcashConf.getAbsolutePath());
				copy(getClass().getResourceAsStream("/ui/zcash_ui.properties"),zcashConf.getAbsolutePath());
				zcashConf = new File(settingsDir + File.separator + "zcash_ui.properties");
				
			} 
			
			Log.info("File zcash_ui.properties found");
			Properties confProps = new Properties();
			FileInputStream fis = null;
			try
			{
				Log.info("Lets parse all the ui settings");
				fis = new FileInputStream(zcashConf);
				confProps.load(fis);
				button = Color.decode(confProps.getProperty(BUTTON_PROPERTY_COLOR)!= null? confProps.getProperty(BUTTON_PROPERTY_COLOR).trim():DEFAULT_COLOR);
				buttonSelect = Color.decode(confProps.getProperty(BUTTON_SELECT_PROPERTY_COLOR)!= null? confProps.getProperty(BUTTON_SELECT_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				checkbox = Color.decode(confProps.getProperty(CHECKBOX_PROPERTY_COLOR)!= null? confProps.getProperty(CHECKBOX_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				checkboxSelect = Color.decode(confProps.getProperty(CHECKBOX_SELECT_PROPERTY_COLOR)!= null? confProps.getProperty(CHECKBOX_SELECT_PROPERTY_COLOR).trim():DEFAULT_COLOR);
				colorChooser = Color.decode(confProps.getProperty(COLORCHOOSER_PROPERTY_COLOR)!= null? confProps.getProperty(COLORCHOOSER_PROPERTY_COLOR).trim():DEFAULT_COLOR);
				combobox = Color.decode(confProps.getProperty(COMBOBOX_PROPERTY_COLOR)!= null? confProps.getProperty(COMBOBOX_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				container = Color.decode(confProps.getProperty(CONTAINER_PROPERTY_COLOR)!= null? confProps.getProperty(CONTAINER_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				dialog = Color.decode(confProps.getProperty(DIALOG_PROPERTY_COLOR)!= null? confProps.getProperty(DIALOG_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				filechooser = Color.decode(confProps.getProperty(FILECHOOSER_PROPERTY_COLOR)!= null? confProps.getProperty(FILECHOOSER_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				frame = Color.decode(confProps.getProperty(FRAME_PROPERTY_COLOR)!= null? confProps.getProperty(FRAME_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				list = Color.decode(confProps.getProperty(LIST_PROPERTY_COLOR)!= null? confProps.getProperty(LIST_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				menu = Color.decode(confProps.getProperty(MENU_PROPERTY_COLOR)!= null? confProps.getProperty(MENU_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				menuSelection = Color.decode(confProps.getProperty(MENU_SELECTION_PROPERTY_COLOR)!= null? confProps.getProperty(MENU_SELECTION_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				menubar = Color.decode(confProps.getProperty(MENUBAR_PROPERTY_COLOR)!= null? confProps.getProperty(MENUBAR_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				menuitem = Color.decode(confProps.getProperty(MENUITEM_PROPERTY_COLOR)!= null? confProps.getProperty(MENUITEM_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				menuitemSelection = Color.decode(confProps.getProperty(MENUITEM_SELECTION_PROPERTY_COLOR)!= null? confProps.getProperty(MENUITEM_SELECTION_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				panel = Color.decode(confProps.getProperty(PANEL_PROPERTY_COLOR)!= null? confProps.getProperty(PANEL_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				passwordfield = Color.decode(confProps.getProperty(PASSWORDFIELD_PROPERTY_COLOR)!= null? confProps.getProperty(PASSWORDFIELD_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				popupmenu = Color.decode(confProps.getProperty(POPUPMENU_PROPERTY_COLOR)!= null? confProps.getProperty(POPUPMENU_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				presentationpanel = Color.decode(confProps.getProperty(PRESENTATIONPANEL_PROPERTY_COLOR)!= null? confProps.getProperty(PRESENTATIONPANEL_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				presentationpanelBorder = Color.decode(confProps.getProperty(PRESENTATIONPANEL_BORDER_PROPERTY_COLOR)!= null? confProps.getProperty(PRESENTATIONPANEL_BORDER_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				progressbar = Color.decode(confProps.getProperty(PROGRESSBAR_PROPERTY_COLOR)!= null? confProps.getProperty(PROGRESSBAR_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				progressbarForeground = Color.decode(confProps.getProperty(PROGRESSBAR_FOREGROUND_PROPERTY_COLOR)!= null? confProps.getProperty(PROGRESSBAR_FOREGROUND_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				radiobutton = Color.decode(confProps.getProperty(RADIOBUTTON_PROPERTY_COLOR)!= null? confProps.getProperty(RADIOBUTTON_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				scrollbar = Color.decode(confProps.getProperty(SCROLLBAR_PROPERTY_COLOR)!= null? confProps.getProperty(SCROLLBAR_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				scrollbarForeground = Color.decode(confProps.getProperty(SCROLLBAR_FOREGROUND_PROPERTY_COLOR)!= null? confProps.getProperty(SCROLLBAR_FOREGROUND_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				scrollbarThumb = Color.decode(confProps.getProperty(SCROLLBAR_THUMB_PROPERTY_COLOR)!= null? confProps.getProperty(SCROLLBAR_THUMB_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				scrollpane = Color.decode(confProps.getProperty(SCROLLPANE_PROPERTY_COLOR)!= null? confProps.getProperty(SCROLLPANE_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				splitpane = Color.decode(confProps.getProperty(SPLITPANE_PROPERTY_COLOR)!= null? confProps.getProperty(SPLITPANE_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				startup = Color.decode(confProps.getProperty(STARTUP_PROPERTY_COLOR)!= null? confProps.getProperty(STARTUP_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				tabbedpane = Color.decode(confProps.getProperty(TABBEDPANE_PROPERTY_COLOR)!= null? confProps.getProperty(TABBEDPANE_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				tabbedpaneUnselected = Color.decode(confProps.getProperty(TABBEDPANE_UNSELECTED_PROPERTY_COLOR)!= null? confProps.getProperty(TABBEDPANE_UNSELECTED_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				table = Color.decode(confProps.getProperty(TABLE_PROPERTY_COLOR)!= null? confProps.getProperty(TABLE_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				tableHeader = Color.decode(confProps.getProperty(TABLE_HEADER_PROPERTY_COLOR)!= null? confProps.getProperty(TABLE_HEADER_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				textarea = Color.decode(confProps.getProperty(TEXTAREA_PROPERTY_COLOR)!= null? confProps.getProperty(TEXTAREA_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				text = Color.decode(confProps.getProperty(TEXT_PROPERTY_COLOR)!= null? confProps.getProperty(TEXT_PROPERTY_COLOR).trim():DEFAULT_COLOR_BLACK); 
				textfield = Color.decode(confProps.getProperty(TEXTFIELD_PROPERTY_COLOR)!= null? confProps.getProperty(TEXTFIELD_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				textpane = Color.decode(confProps.getProperty(TEXTPANE_PROPERTY_COLOR)!= null? confProps.getProperty(TEXTPANE_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				tooltip = Color.decode(confProps.getProperty(TOOLTIP_PROPERTY_COLOR)!= null? confProps.getProperty(TOOLTIP_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				viewport = Color.decode(confProps.getProperty(VIEWPORT_PROPERTY_COLOR)!= null? confProps.getProperty(VIEWPORT_PROPERTY_COLOR).trim():DEFAULT_COLOR); 
				messageSent = Color.decode(confProps.getProperty(MESSAGE_SENT_PROPERTY_COLOR)!= null? confProps.getProperty(MESSAGE_SENT_PROPERTY_COLOR).trim():DEFAULT_COLOR_BLUE); 
				messageReceived = Color.decode(confProps.getProperty(MESSAGE_RECEIVED_PROPERTY_COLOR)!= null? confProps.getProperty(MESSAGE_RECEIVED_PROPERTY_COLOR).trim():DEFAULT_COLOR_RED); 
				currency = confProps.getProperty(CURRENCY)!= null? confProps.getProperty(CURRENCY).toUpperCase().trim():DEFAULT_CURRENCY; 
				
			} finally
			{
				if (fis != null)
				{
					fis.close();
				}
			}
		}
		catch(Exception e) {
			Log.warning("Error obtaining properties from zcash_ui.properties file due to: {0} {1}",
					e.getClass().getName(), e.getMessage());
		}	
	}
	
	private static void copy(InputStream source , String destination) {

		Log.info("Copying ->" + source + "\n\tto ->" + destination);

        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
        	Log.warning("Error to copy zcash_ui.properties from resources due to: {0} {1}",
					ex.getClass().getName(), ex.getMessage());
        }


    }

}
