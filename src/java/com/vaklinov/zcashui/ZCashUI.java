/************************************************************************************************
 *   ____________ _   _  _____          _      _____ _    _ _______          __   _ _      _   
 *  |___  /  ____| \ | |/ ____|        | |    / ____| |  | |_   _\ \        / /  | | |    | |  
 *     / /| |__  |  \| | |     __ _ ___| |__ | |  __| |  | | | |  \ \  /\  / /_ _| | | ___| |_ 
 *    / / |  __| | . ` | |    / _` / __| '_ \| | |_ | |  | | | |   \ \/  \/ / _` | | |/ _ \ __|
 *   / /__| |____| |\  | |___| (_| \__ \ | | | |__| | |__| |_| |_   \  /\  / (_| | | |  __/ |_ 
 *  /_____|______|_| \_|\_____\__,_|___/_| |_|\_____|\____/|_____|   \/  \/ \__,_|_|_|\___|\__|
 *                                       
 * Copyright (c) 2016-2018 The ZEN Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 **********************************************************************************/
package com.vaklinov.zcashui;


import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cabecinha84.zcashui.AppLock;
import com.cabecinha84.zcashui.ZcashJFrame;
import com.cabecinha84.zcashui.ZcashJMenu;
import com.cabecinha84.zcashui.ZcashJMenuBar;
import com.cabecinha84.zcashui.ZcashJMenuItem;
import com.cabecinha84.zcashui.ZcashJTabbedPane;
import com.cabecinha84.zcashui.ZcashYUIEditDialog;
import com.cabecinha84.zcashui.ZcashXUI;

import com.vaklinov.zcashui.OSUtil.OS_TYPE;
import com.vaklinov.zcashui.ZCashClientCaller.NetworkAndBlockchainInfo;
import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;
import com.vaklinov.zcashui.ZCashInstallationObserver.DAEMON_STATUS;
import com.vaklinov.zcashui.ZCashInstallationObserver.DaemonInfo;
import com.vaklinov.zcashui.ZCashInstallationObserver.InstallationDetectionException;
import com.vaklinov.zcashui.msg.MessagingPanel;


/**
 * Main Zcash Window.
 */
public class ZCashUI
    extends ZcashJFrame
{
    private static final long THREAD_WAIT_1_SECOND = 1000;
	private static final long THREAD_WAIT_5_SECONDS = 5000;
    private ZCashInstallationObserver installationObserver;
    private ZCashClientCaller         clientCaller;
    private StatusUpdateErrorReporter errorReporter;

    private WalletOperations walletOps;

    private ZcashJMenuItem menuItemExit;
    private ZcashJMenuItem menuItemAbout;
    private ZcashJMenuItem menuItemZcashXUI;
    private ZcashJMenuItem menuItemEncrypt;
    private ZcashJMenuItem menuItemBackup;
    private ZcashJMenuItem menuItemExportKeys;
    private ZcashJMenuItem menuItemImportKeys;
    private ZcashJMenuItem menuItemShowPrivateKey;
    private ZcashJMenuItem menuItemImportOnePrivateKey;
    private ZcashJMenuItem menuItemOwnIdentity;
    private ZcashJMenuItem menuItemExportOwnIdentity;
    private ZcashJMenuItem menuItemImportContactIdentity;
    private ZcashJMenuItem menuItemAddMessagingGroup;
    private ZcashJMenuItem menuItemRemoveContactIdentity;
    private ZcashJMenuItem menuItemMessagingOptions;
    private ZcashJMenuItem menuItemShareFileViaIPFS;
    private ZcashJMenuItem menuItemExportToArizen;

    private DashboardPanel   dashboard;
    private TransactionsDetailPanel transactionDetailsPanel;
    private AddressesPanel   addresses;
    private SendCashPanel    sendPanel;
    private AddressBookPanel addressBookPanel;
    private MessagingPanel   messagingPanel;
    private LanguageUtil langUtil;

    private static File walletLock;
    private static FileChannel channel;
    private static FileLock lock;

    ZcashJTabbedPane tabs;

    public ZCashUI(StartupProgressDialog progressDialog)
        throws IOException, InterruptedException, WalletCallException
    {

        langUtil = LanguageUtil.instance();

        this.setTitle(langUtil.getString("main.frame.title"));

        if (progressDialog != null)
        {
        	progressDialog.setProgressText(langUtil.getString("main.frame.progressbar"));
        }
        
        ClassLoader cl = this.getClass().getClassLoader();

        this.setIconImage(new ImageIcon(cl.getResource("images/ZECmate-logo.png")).getImage());

        Container contentPane = this.getContentPane();
        contentPane.setBackground(ZcashXUI.container);

        errorReporter = new StatusUpdateErrorReporter(this);
        installationObserver = new ZCashInstallationObserver(OSUtil.getProgramDirectory());
        clientCaller = new ZCashClientCaller(OSUtil.getProgramDirectory());
        
        if (installationObserver.isOnTestNet())
        {
        	this.setTitle(this.getTitle() + langUtil.getString("main.frame.title.testnet"));
        }

        // Build content
        tabs = new ZcashJTabbedPane();
        Font oldTabFont = tabs.getFont();
        Font newTabFont  = new Font(oldTabFont.getName(), Font.BOLD | Font.ITALIC, oldTabFont.getSize() * 57 / 50);
        tabs.setFont(newTabFont);
        BackupTracker backupTracker = new BackupTracker(this);
		LabelStorage labelStorage = new LabelStorage();
        tabs.addTab(langUtil.getString("main.frame.tab.overview.title"),
        		    new ImageIcon(cl.getResource("images/overview.png")),
        		    dashboard = new DashboardPanel(this, installationObserver, clientCaller, 
        		    		                       errorReporter, backupTracker, labelStorage));
        tabs.addTab(langUtil.getString("main.frame.tab.transactions.title"),
    		        new ImageIcon(cl.getResource("images/transactions.png")),
    		        transactionDetailsPanel = new TransactionsDetailPanel(
    		        	this, tabs, installationObserver, clientCaller, 
    		    	    errorReporter, dashboard.getTransactionGatheringThread(), labelStorage));
        this.dashboard.setDetailsPanelForSelection(this.transactionDetailsPanel);
        tabs.addTab(langUtil.getString("main.frame.tab.own.address.title"),
        		    new ImageIcon(cl.getResource("images/own-addresses.png")),
        		    addresses = new AddressesPanel(this, clientCaller, errorReporter, labelStorage, installationObserver));
        tabs.addTab(langUtil.getString("main.frame.tab.send.cash.title"),
        		    new ImageIcon(cl.getResource("images/send.png")),
        		    sendPanel = new SendCashPanel(clientCaller, errorReporter, installationObserver, backupTracker));
        tabs.addTab(langUtil.getString("main.frame.tab.address.book.title"),
    		        new ImageIcon(cl.getResource("images/address-book.png")),
    		        addressBookPanel = new AddressBookPanel(sendPanel, tabs, labelStorage));
        tabs.addTab(langUtil.getString("main.frame.tab.messaging.title"),
		            new ImageIcon(cl.getResource("images/messaging.png")),
		            messagingPanel = new MessagingPanel(this, sendPanel, tabs, clientCaller, errorReporter, labelStorage));
        contentPane.add(tabs);

        this.walletOps = new WalletOperations(
            	this, tabs, dashboard, addresses, sendPanel, 
            	installationObserver, clientCaller, errorReporter, backupTracker);        

        // Build menu
        ZcashJMenuBar mb = new ZcashJMenuBar();
        ZcashJMenu file = new ZcashJMenu(langUtil.getString("menu.label.main"));
        file.setMnemonic(KeyEvent.VK_M);
        int accelaratorKeyMask = Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask();
        file.add(menuItemZcashXUI = new ZcashJMenuItem(langUtil.getString("menu.label.zcashui"), KeyEvent.VK_U));
        menuItemZcashXUI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, accelaratorKeyMask));
        file.add(menuItemAbout = new ZcashJMenuItem(langUtil.getString("menu.label.about"), KeyEvent.VK_T));
        menuItemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, accelaratorKeyMask));
        file.addSeparator();
        file.add(menuItemExit = new ZcashJMenuItem(langUtil.getString("menu.label.quit"), KeyEvent.VK_Q));
        menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, accelaratorKeyMask));
        mb.add(file);

        ZcashJMenu wallet = new ZcashJMenu(langUtil.getString("menu.label.wallet"));
        wallet.setMnemonic(KeyEvent.VK_W);
        wallet.add(menuItemBackup = new ZcashJMenuItem(langUtil.getString("menu.label.backup"), KeyEvent.VK_B));
        menuItemBackup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, accelaratorKeyMask));
        // Encryption menu item is hidden since encryption is not possible
        //wallet.add(menuItemEncrypt = new ZcashJMenuItem(langUtil.getString("menu.label.encrypt"), KeyEvent.VK_E));
        //menuItemEncrypt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, accelaratorKeyMask));
        wallet.add(menuItemExportKeys = new ZcashJMenuItem(langUtil.getString("menu.label.export.private.keys"), KeyEvent.VK_K));
        menuItemExportKeys.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, accelaratorKeyMask));
        wallet.add(menuItemImportKeys = new ZcashJMenuItem(langUtil.getString("menu.label.import.private.keys"), KeyEvent.VK_I));
        menuItemImportKeys.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, accelaratorKeyMask));
        wallet.add(menuItemShowPrivateKey = new ZcashJMenuItem(langUtil.getString("menu.label.show.private.key"), KeyEvent.VK_P));
        menuItemShowPrivateKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, accelaratorKeyMask));
        wallet.add(menuItemImportOnePrivateKey = new ZcashJMenuItem(langUtil.getString("menu.label.import.one.private.key"), KeyEvent.VK_N));
        menuItemImportOnePrivateKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, accelaratorKeyMask));
        //wallet.add(menuItemExportToArizen = new ZcashJMenuItem(langUtil.getString("menu.label.export.to.arizen"), KeyEvent.VK_A));
        //menuItemExportToArizen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, accelaratorKeyMask));
        mb.add(wallet);

        ZcashJMenu messaging = new ZcashJMenu(langUtil.getString("menu.label.messaging"));
        messaging.setMnemonic(KeyEvent.VK_S);
        messaging.add(menuItemOwnIdentity = new ZcashJMenuItem(langUtil.getString("menu.label.own.identity"), KeyEvent.VK_D));
        menuItemOwnIdentity.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, accelaratorKeyMask));        
        messaging.add(menuItemExportOwnIdentity = new ZcashJMenuItem(langUtil.getString("menu.label.export.own.identity"), KeyEvent.VK_L));
        menuItemExportOwnIdentity.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, accelaratorKeyMask));        
        messaging.add(menuItemAddMessagingGroup = new ZcashJMenuItem(langUtil.getString("menu.label.add.messaging.group"), KeyEvent.VK_G));
        menuItemAddMessagingGroup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, accelaratorKeyMask));
        messaging.add(menuItemImportContactIdentity = new ZcashJMenuItem(langUtil.getString("menu.label.import.contact.identity"), KeyEvent.VK_Y));
        menuItemImportContactIdentity.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, accelaratorKeyMask));
        messaging.add(menuItemRemoveContactIdentity = new ZcashJMenuItem(langUtil.getString("menu.label.remove.contact"), KeyEvent.VK_R));
        menuItemRemoveContactIdentity.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, accelaratorKeyMask));
        messaging.add(menuItemMessagingOptions = new ZcashJMenuItem(langUtil.getString("menu.label.options"), KeyEvent.VK_O));
        menuItemMessagingOptions.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, accelaratorKeyMask));
        
        ZcashJMenu shareFileVia = new ZcashJMenu(langUtil.getString("menu.label.share.file"));
        shareFileVia.setMnemonic(KeyEvent.VK_V);
        // TODO: uncomment this for IPFS integration
        //messaging.add(shareFileVia);
        shareFileVia.add(menuItemShareFileViaIPFS = new ZcashJMenuItem(langUtil.getString("menu.label.ipfs"), KeyEvent.VK_F));
        menuItemShareFileViaIPFS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, accelaratorKeyMask));
        
        mb.add(messaging);

        ActionListener languageSelectionAction = new ActionListener(  ) {
            public void actionPerformed(ActionEvent e) {
                try {
                    Log.info("Action ["+e.getActionCommand(  )+"] performed");
                    LanguageMenuItem item = (LanguageMenuItem) e.getSource();
                    langUtil.updatePreferredLanguage(item.getLocale());
                    JOptionPane.showMessageDialog(
                            ZCashUI.this.getRootPane().getParent(),
                            langUtil.getString("dialog.message.language.prefs.update"),
                            langUtil.getString("dialog.message.language.prefs.update.title"),
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) { ex.printStackTrace(  ); }
            }
        };
        ZcashJMenu languageMenu = new ZcashJMenu(langUtil.getString("menu.label.language"));
        LanguageMenuItem italian = new
                LanguageMenuItem(langUtil.getString("menu.label.language.italian"),
                new ImageIcon(cl.getResource("images/italian.png")), Locale.ITALY);
        italian.setHorizontalTextPosition(ZcashJMenuItem.RIGHT);

        italian.addActionListener(languageSelectionAction);


        LanguageMenuItem english = new
                LanguageMenuItem(langUtil.getString("menu.label.language.english"),
                new ImageIcon(cl.getResource("images/uk.png")), Locale.US);
        english.setHorizontalTextPosition(ZcashJMenuItem.RIGHT);

        english.addActionListener(languageSelectionAction);

        ButtonGroup group = new ButtonGroup(  );
        group.add(italian);
        group.add(english);

        languageMenu.add(italian);
        languageMenu.add(english);

        mb.add(languageMenu);

        this.setJMenuBar(mb);

        // Add listeners etc.
        menuItemExit.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.exitProgram();
                }
            }
        );

        menuItemAbout.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                	try
                	{
                		AboutDialog ad = new AboutDialog(ZCashUI.this);
                		ad.setVisible(true);
                	} catch (UnsupportedEncodingException uee)
                	{
                		Log.error("Unexpected error: ", uee);
                		ZCashUI.this.errorReporter.reportError(uee);
                	}
                }
            }
        );

        menuItemZcashXUI.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                    	try
                    	{
                    		ZcashYUIEditDialog ad = new ZcashYUIEditDialog(ZCashUI.this);
                            ad.setVisible(true);
                    	} catch (UnsupportedEncodingException uee)
                    	{
                    		Log.error("Unexpected error: ", uee);
                    		ZCashUI.this.errorReporter.reportError(uee);
                        }
                    }
                }
            );

        menuItemBackup.addActionListener(   
        	new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.walletOps.backupWallet();
                }
            }
        );
        
        /** Encrypt menu item is not initialized
        menuItemEncrypt.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.walletOps.encryptWallet();
                }
            }
        );
        */

        menuItemExportKeys.addActionListener(   
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.walletOps.exportWalletPrivateKeys();
                }
            }
       );
        
       menuItemImportKeys.addActionListener(   
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.walletOps.importWalletPrivateKeys();
                }
            }
       );
       
       menuItemShowPrivateKey.addActionListener(   
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.walletOps.showPrivateKey();
                }
            }
       );
       
       menuItemImportOnePrivateKey.addActionListener(   
           new ActionListener()
           {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                   ZCashUI.this.walletOps.importSinglePrivateKey();
               }
           }
       );
       
       menuItemOwnIdentity.addActionListener(   
               new ActionListener()
               {
                   @Override
                   public void actionPerformed(ActionEvent e)
                   {
            			ZCashUI.this.messagingPanel.openOwnIdentityDialog();
                   }
               }
        );
       
       menuItemExportOwnIdentity.addActionListener(   
               new ActionListener()
               {
                   @Override
                   public void actionPerformed(ActionEvent e)
                   {
            			ZCashUI.this.messagingPanel.exportOwnIdentity();
                   }
               }
        );

       menuItemImportContactIdentity.addActionListener(   
               new ActionListener()
               {
                   @Override
                   public void actionPerformed(ActionEvent e)
                   {
            			ZCashUI.this.messagingPanel.importContactIdentity();
                   }
               }
        );
              
       menuItemAddMessagingGroup.addActionListener(   
               new ActionListener()
               {
                   @Override
                   public void actionPerformed(ActionEvent e)
                   {
            			ZCashUI.this.messagingPanel.addMessagingGroup();
                   }
               }
        );
       
       menuItemRemoveContactIdentity.addActionListener(   
               new ActionListener()
               {
                   @Override
                   public void actionPerformed(ActionEvent e)
                   {
            			ZCashUI.this.messagingPanel.removeSelectedContact();
                   }
               }
        );
       
       menuItemMessagingOptions.addActionListener(   
               new ActionListener()
               {
                   @Override
                   public void actionPerformed(ActionEvent e)
                   {
            			ZCashUI.this.messagingPanel.openOptionsDialog();
                   }
               }
       );
       
       menuItemShareFileViaIPFS.addActionListener(   
               new ActionListener()
               {
                   @Override
                   public void actionPerformed(ActionEvent e)
                   {
            			ZCashUI.this.messagingPanel.shareFileViaIPFS();
                   }
               }
       );
        /*
        menuItemExportToArizen.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        ZCashUI.this.walletOps.exportToArizenWallet();
                    }
                }
        );
        */
        // Close operation
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                ZCashUI.this.exitProgram();
            }
        });

        // Show initial message
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    String userDir = OSUtil.getSettingsDirectory();
                    File warningFlagFile = new File(userDir + File.separator + "initialInfoShown_1.2.1.flag");
                    if (warningFlagFile.exists())
                    {
                        return;
                    };
                    
                    Object[] options = 
                   	{ 
                   		langUtil.getString("main.frame.disclaimer.button.agree"),
                   		langUtil.getString("main.frame.disclaimer.button.disagree")
                   	};
                    
                    int option = JOptionPane.showOptionDialog(
                    		ZCashUI.this.getRootPane().getParent(), 
                            langUtil.getString("main.frame.disclaimer.text"),
                            langUtil.getString("main.frame.disclaimer.title"),
                            JOptionPane.DEFAULT_OPTION, 
            			    JOptionPane.INFORMATION_MESSAGE,
            			    null, 
            			    options, 
            			    options[0]);

                    if (option == 0)
                    {
                        warningFlagFile.createNewFile();
                    } else
                    {
                    	ZCashUI.this.exitProgram();
                    }

                } catch (IOException ioe)
                {
                    /* TODO: report exceptions to the user */
                	Log.error("Unexpected error: ", ioe);
                }
            }
        });
        
        // Finally dispose of the progress dialog
        if (progressDialog != null)
        {
        	progressDialog.doDispose();
        }
        
        // Notify the messaging TAB that it is being selected - every time
        tabs.addChangeListener(
            new ChangeListener() 
            {	
    			@Override
    			public void stateChanged(ChangeEvent e) 
    			{
    				ZcashJTabbedPane tabs = (ZcashJTabbedPane)e.getSource();
    				if (tabs.getSelectedIndex() == 5)
    				{
    					ZCashUI.this.messagingPanel.tabSelected();
    				}
    			}
    		}
        );
  
        
        this.validate();
		this.repaint();
		
		
		this.pack();
		Dimension currentSize = this.getSize();

		new ZcashXUI();
		OS_TYPE os = OSUtil.getOSType();
		int width = 1040;
		if (os == OS_TYPE.MAC_OS)
		{
			width += 100; // Needs to be wider on macOS
		}
		
		this.setSize(new Dimension(width, currentSize.height));
        this.validate();
		this.repaint();
    }

    public void exitProgram()
    {
    	Log.info("Exiting ...");

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        this.dashboard.stopThreadsAndTimers();
        this.transactionDetailsPanel.stopThreadsAndTimers();
        this.addresses.stopThreadsAndTimers();
        this.sendPanel.stopThreadsAndTimers();
        this.messagingPanel.stopThreadsAndTimers();
        
        ZCashUI.this.setVisible(false);
        ZCashUI.this.dispose();

        System.exit(0);
    }

    public static void main(String argv[])
        throws IOException
    {
        ZCashUI ui = null;
        StartupProgressDialog startupBar = null;
    	ZCashClientCaller initialClientCaller = null;
        ZCashInstallationObserver initialInstallationObserver = null;
    	DaemonInfo zcashdInfo = null;
        try
        {
            new ZcashXUI();
        	OS_TYPE os = OSUtil.getOSType();
        	
        	if ((os == OS_TYPE.WINDOWS) || (os == OS_TYPE.MAC_OS))
        	{
        		possiblyCreateZCashConfigFile();
        	}

        	LanguageUtil langUtil = LanguageUtil.instance();
        	
        	Log.info("Starting ZECmate Swing Wallet ...");
        	Log.info("OS: " + System.getProperty("os.name") + " = " + os);
        	Log.info("Current directory: " + new File(".").getCanonicalPath());
        	Log.info("Class path: " + System.getProperty("java.class.path"));
        	Log.info("Environment PATH: " + System.getenv("PATH"));

            // Look and feel settings - a custom OS-look and feel is set for Windows
        	if (os == OS_TYPE.WINDOWS) {
				// Custom Windows L&F and font settings
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");

				// This font looks good but on Windows 7 it misses some chars like the stars...
				// FontUIResource font = new FontUIResource("Lucida Sans Unicode", Font.PLAIN,
				// 11);
				// UIManager.put("Table.font", font);
			} else if (os == OS_TYPE.MAC_OS) {
				// The MacOS L&F is active by default - the property sets the menu bar Mac style
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name",
						LanguageUtil.instance().getString("apple.menu.about.name"));
			} else {
				for (LookAndFeelInfo lf : UIManager.getInstalledLookAndFeels()) {
					Log.info("Available look and feel: " + lf.getName() + " " + lf.getClassName());
					if (lf.getName().equals("Nimbus")) {
						Log.info("Setting look and feel: {0}", lf.getClassName());
						UIManager.setLookAndFeel(lf.getClassName());
						break;
					}
					;
				}
			}

            
            // If zcashd is currently not running, do a startup of the daemon as a child process
            // It may be started but not ready - then also show dialog
            initialInstallationObserver = 
            	new ZCashInstallationObserver(OSUtil.getProgramDirectory());
            zcashdInfo = initialInstallationObserver.getDaemonInfo();
            initialInstallationObserver = null;
            
            initialClientCaller = new ZCashClientCaller(OSUtil.getProgramDirectory());
            boolean daemonStartInProgress = false;
            try
            {
            	if (zcashdInfo.status == DAEMON_STATUS.RUNNING)
            	{
            		NetworkAndBlockchainInfo info = initialClientCaller.getNetworkAndBlockchainInfo();
            		// If more than 20 minutes behind in the blockchain - startup in progress
            		if ((System.currentTimeMillis() - info.lastBlockDate.getTime()) > (20 * 60 * 1000))
            		{
            			Log.info("Current blockchain synchronization date is "  + 
            		                       new Date(info.lastBlockDate.getTime()));
            			daemonStartInProgress = true;
            		}
            	}
            } catch (WalletCallException wce)
            {
                if ((wce.getMessage().indexOf("{\"code\":-28") != -1) || // Started but not ready
                	(wce.getMessage().indexOf("error code: -28") != -1))
                {
                	Log.info("zcashd is currently starting...");
                	daemonStartInProgress = true;
                }
            }
            if (false == AppLock.lock()) {
                throw new Exception(LanguageUtil.instance().getString("duplicate.instante.detected"));
            }
            installShutdownHook();

            if ((zcashdInfo.status != DAEMON_STATUS.RUNNING) || (daemonStartInProgress))
            {
            	Log.info(
            		"zcashd is not running at the moment or has not started/synchronized 100% - showing splash...");
	            startupBar = new StartupProgressDialog(initialClientCaller);
	            startupBar.setVisible(true);
	            startupBar.waitForStartup();
            }
            initialClientCaller = null;
            
            // Main GUI is created here
            ui = new ZCashUI(startupBar);
            ui.setVisible(true);

        } catch (InstallationDetectionException ide)
        {
        	Log.error("Unexpected error: ", ide);
            JOptionPane.showMessageDialog(
                null,
                LanguageUtil.instance().getString("main.frame.option.pane.installation.error.text",
                        OSUtil.getProgramDirectory(),
                        ide.getMessage() ),
                LanguageUtil.instance().getString("main.frame.option.pane.installation.error.title"),
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (WalletCallException wce)
        {
        	Log.error("Unexpected error: ", wce);

            if ((wce.getMessage().indexOf("{\"code\":-28,\"message\"") != -1) ||
            	(wce.getMessage().indexOf("error code: -28") != -1))
            {
                JOptionPane.showMessageDialog(
                        null,
                        LanguageUtil.instance().getString("main.frame.option.pane.wallet.communication.error.text"),
                        LanguageUtil.instance().getString("main.frame.option.pane.wallet.communication.error.title"),
                        JOptionPane.ERROR_MESSAGE);
            } else
            {
                JOptionPane.showMessageDialog(
                    null,
                        LanguageUtil.instance().getString("main.frame.option.pane.wallet.communication.error.2.text", wce.getMessage()),
                        LanguageUtil.instance().getString("main.frame.option.pane.wallet.communication.error.2.title"),
                    JOptionPane.ERROR_MESSAGE);
            }

            System.exit(2);
        } catch (Exception e)
        {
            Log.error("Unexpected error: ", e);
            if (e.getMessage().equals(LanguageUtil.instance().getString("duplicate.instante.detected"))) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.title"),
						JOptionPane.ERROR_MESSAGE);
				System.exit(3);
			} 
			else if(e.getMessage().contains("(code 1)")) {
				Object[] options = { LanguageUtil.instance().getString("main.frame.reindex.button.agree"),
						LanguageUtil.instance().getString("main.frame.reindex.button.disagree") };

				int option = JOptionPane.showOptionDialog(null,
						LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.code1.text",
								e.getMessage()),
						LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.title"),
						JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				if (option == 0) {
					try {
						if (initialClientCaller == null) {
							initialClientCaller = new ZCashClientCaller(OSUtil.getProgramDirectory());
						}
						initialClientCaller.stopDaemon();
						initialClientCaller.startDaemon(true);
						JOptionPane.showMessageDialog(null,
								LanguageUtil.instance().getString("wallet.reindex.restart.message"),
								LanguageUtil.instance().getString("wallet.reindex.restart.title"),
								JOptionPane.INFORMATION_MESSAGE);
						// start zcashd with -reindex.

						if (startupBar != null) {
							startupBar.dispose();
						}
						for (int i = 0; i < 5; ++i) {
							Log.info("Check if Daemon already start with reindex option");
							initialInstallationObserver = new ZCashInstallationObserver(OSUtil.getProgramDirectory());
							zcashdInfo = initialInstallationObserver.getDaemonInfo();
							initialInstallationObserver = null;
							if (zcashdInfo.status == DAEMON_STATUS.RUNNING) {
								Log.info("Daemon started.");
								break;
							}
							Thread.sleep(ZCashUI.THREAD_WAIT_1_SECOND);
						}
						Thread.sleep(ZCashUI.THREAD_WAIT_5_SECONDS);
						Log.info("Call stop Daemon.");
						initialClientCaller.stopDaemon();
						Thread.sleep(ZCashUI.THREAD_WAIT_5_SECONDS);
						for (int i = 0; i < 5; ++i) {
							Log.info("Check if Daemon is stopped");
							initialInstallationObserver = new ZCashInstallationObserver(OSUtil.getProgramDirectory());
							zcashdInfo = initialInstallationObserver.getDaemonInfo();
							initialInstallationObserver = null;
							if (zcashdInfo.status != DAEMON_STATUS.RUNNING) {
								Log.info("Daemon stopped.");
								break;
							}
							Thread.sleep(ZCashUI.THREAD_WAIT_1_SECOND);
						}
						Log.info("Restarting the wallet.");
						ZCashUI.main(null);
					} catch (Exception errr) {
						JOptionPane.showMessageDialog(null,
								LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.2.text",
										errr.getMessage()),
								LanguageUtil.instance()
										.getString("main.frame.option.pane.wallet.critical.error.2.title"),
								JOptionPane.ERROR_MESSAGE);
						System.exit(5);
					} catch (Error errX) {
						// Last resort catch for unexpected problems - just to inform the user
						errX.printStackTrace();
						JOptionPane.showMessageDialog(null,
								LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.2.text",
										errX.getMessage()),
								LanguageUtil.instance()
										.getString("main.frame.option.pane.wallet.critical.error.2.title"),
								JOptionPane.ERROR_MESSAGE);
						System.exit(6);
					}
				} else {
					System.exit(3);
				}	
			}
			else {
				Log.error("Unexpected error: ", e);
	            JOptionPane.showMessageDialog(
	                null,
	                LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.text", e.getMessage()),
	                LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.title"),
	                JOptionPane.ERROR_MESSAGE);
	            System.exit(3);
			}

		} catch (Error err) {
			// Last resort catch for unexpected problems - just to inform the user
			err.printStackTrace();
			JOptionPane.showMessageDialog(null,
					LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.2.text",
							err.getMessage()),
					LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.2.title"),
					JOptionPane.ERROR_MESSAGE);
			System.exit(4);
		}
	}
    
    
     public static void possiblyCreateZCashConfigFile()
        throws IOException
    {
    	String blockchainDir = OSUtil.getBlockchainDirectory();
    	File dir = new File(blockchainDir);
    	
		if (!dir.exists())
		{
			if (!dir.mkdirs())
			{
				Log.error("ERROR: Could not create settings directory: " + dir.getCanonicalPath());
				throw new IOException("Could not create settings directory: " + dir.getCanonicalPath());
			}
		}
		
		File zcashConfigFile = new File(dir, "zcash.conf");
		
		if (!zcashConfigFile.exists())
		{
			Log.info("Zcash configuration file " + zcashConfigFile.getCanonicalPath() + 
					 " does not exist. It will be created with default settings.");
			
			Random r = new Random(System.currentTimeMillis());
			
			PrintStream configOut = new PrintStream(new FileOutputStream(zcashConfigFile));
			
			configOut.println("#############################################################################");
			configOut.println("#                         Zcash configuration file                            #");
			configOut.println("#############################################################################");
			configOut.println("# This file has been automatically generated by the Zcash GUI wallet with #");
			configOut.println("# default settings. It may be further cutsomized by hand only.              #");
			configOut.println("#############################################################################");
			configOut.println("# Creation date: " + new Date().toString());
			configOut.println("#############################################################################");
			configOut.println("");
			configOut.println("# The rpcuser/rpcpassword are used for the local call to zcashd");
			configOut.println("rpcuser=User" + Math.abs(r.nextInt()));
			configOut.println("rpcpassword=Pass" + Math.abs(r.nextInt()) + "" + 
			                                       Math.abs(r.nextInt()) + "" + 
					                               Math.abs(r.nextInt()));
			configOut.println("");
			configOut.println("# Well-known nodes to connect to - to speed up acquiring initial connections");
			configOut.println("addnode=explorer.zcha.in"); 
			configOut.println("addnode=zcash.blockexplorer.com");
			configOut.println("addnode=explorer.zec.zelcore.io");
			configOut.close();
		}
    }

    private static void installShutdownHook() {
 	    Runnable runner = new Runnable() {
	        @Override
	        public void run() {
	            AppLock.unlock();
	        }
	    };
	    Runtime.getRuntime().addShutdownHook(new Thread(runner, "Window Prefs Hook"));
	}
    
}
