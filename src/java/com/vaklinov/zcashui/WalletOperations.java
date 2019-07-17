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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.cabecinha84.zcashui.ChangePasswordEncryptionDialog;
import com.cabecinha84.zcashui.ZcashJDialog;
import com.cabecinha84.zcashui.ZcashJFileChooser;
import com.cabecinha84.zcashui.ZcashJFrame;
import com.cabecinha84.zcashui.ZcashJLabel;
import com.cabecinha84.zcashui.ZcashJProgressBar;
import com.cabecinha84.zcashui.ZcashJTabbedPane;
import com.cabecinha84.zcashui.ZcashSproutToSaplingDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;
import com.vaklinov.zcashui.arizen.models.Address;
import com.vaklinov.zcashui.arizen.repo.ArizenWallet;
import com.vaklinov.zcashui.arizen.repo.WalletRepo;


/**
 * Provides miscellaneous operations for the wallet file.
 */
public class WalletOperations
{	
	
	private static final int POLL_PERIOD = 5000;
    private static final int STARTUP_ERROR_CODE = -28;
    
	private ZCashUI parent;
	private ZcashJTabbedPane tabs;
	private DashboardPanel dashboard;
	private SendCashPanel  sendCash;
	private AddressesPanel addresses;
	private LabelStorage labelStorage;
	
	private ZCashInstallationObserver installationObserver;
	private ZCashClientCaller         clientCaller;
	private StatusUpdateErrorReporter errorReporter;
	private BackupTracker             backupTracker;

	private LanguageUtil langUtil;


	public WalletOperations(ZCashUI parent,
			                ZcashJTabbedPane tabs,
			                DashboardPanel dashboard,
			                AddressesPanel addresses,
			                SendCashPanel  sendCash,
			                
			                ZCashInstallationObserver installationObserver, 
			                ZCashClientCaller clientCaller,
			                StatusUpdateErrorReporter errorReporter,
			                BackupTracker             backupTracker,
			                LabelStorage labelStorage) 
        throws IOException, InterruptedException, WalletCallException 
	{
		this.parent    = parent;
		this.tabs      = tabs;
		this.dashboard = dashboard;
		this.addresses = addresses;
		this.sendCash  = sendCash;
		
		this.installationObserver = installationObserver;
		this.clientCaller = clientCaller;
		this.errorReporter = errorReporter;
		
		this.backupTracker = backupTracker;
		this.langUtil = LanguageUtil.instance();
		this.labelStorage = labelStorage;
	}

	public void changeWalletPassword()
	{
		try
		{			
			if (!this.clientCaller.isWalletEncrypted())
			{
		        JOptionPane.showMessageDialog(
		            this.parent,
		            langUtil.getString("wallet.operations.option.pane.wallet.not.encryppted.error.text"),
		            langUtil.getString("wallet.operations.option.pane.wallet.not.encryppted.error.title"),
		            JOptionPane.ERROR_MESSAGE);
		        return;
			}

			ChangePasswordEncryptionDialog pd = new ChangePasswordEncryptionDialog(this.parent);
			pd.setVisible(true);

			if (!pd.isOKPressed())
			{
				return;
			}

			Cursor oldCursor = this.parent.getCursor();
			try
			{

				this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.parent.stopTimers();
				if(!this.checkExperimentalFeaturesOn()) {
					this.parent.restartDaemon(false, false);
				}
				this.clientCaller.passPhraseChangeWallet(pd.getPassword(), pd.getNewPassword());

				this.parent.setCursor(oldCursor);
			} catch (WalletCallException wce)
			{
				this.parent.setCursor(oldCursor);
				Log.error("Unexpected error: ", wce);

				JOptionPane.showMessageDialog(
					this.parent, 
					langUtil.getString("encryption.error.change.password.message", wce.getMessage().replace(",", ",\n")),
					langUtil.getString("encryption.error.change.password.title"),
					JOptionPane.ERROR_MESSAGE);
				return;
			}

			JOptionPane.showMessageDialog(
				this.parent, 
				langUtil.getString("wallet.operations.option.pane.change.password.success.text"),
				langUtil.getString("wallet.operations.option.pane.change.password.success.title"),
				JOptionPane.INFORMATION_MESSAGE);

			this.parent.exitProgram();

		} catch (Exception e)
		{
			this.errorReporter.reportError(e, false);
		}
	}
	
	public void encryptWallet()
	{
		try
		{			
			if (this.clientCaller.isWalletEncrypted())
			{
		        JOptionPane.showMessageDialog(
		            this.parent,
		            langUtil.getString("wallet.operations.option.pane.already.encrypted.error.text"),
		            langUtil.getString("wallet.operations.option.pane.already.encrypted.error.title"),
		            JOptionPane.ERROR_MESSAGE);
		        return;
			}
			
			PasswordEncryptionDialog pd = new PasswordEncryptionDialog(this.parent);
			pd.setVisible(true);
			
			if (!pd.isOKPressed())
			{
				return;
			}
			
			Cursor oldCursor = this.parent.getCursor();
			try
			{
				
				this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				this.parent.stopTimers();
				if(!this.checkExperimentalFeaturesOn()) {
					this.parent.restartDaemon(false, false);
				}
				
				this.clientCaller.encryptWallet(pd.getPassword());
				
				this.parent.setCursor(oldCursor);
			} catch (WalletCallException wce)
			{
				this.parent.setCursor(oldCursor);
				Log.error("Unexpected error: ", wce);
				
				JOptionPane.showMessageDialog(
					this.parent, 
					langUtil.getString("wallet.operations.option.pane.encryption.error.text", wce.getMessage().replace(",", ",\n")),
					langUtil.getString("wallet.operations.option.pane.encryption.error.title"),
					JOptionPane.ERROR_MESSAGE);
				this.parent.exitProgram();
			}
			
			JOptionPane.showMessageDialog(
				this.parent, 
				langUtil.getString("wallet.operations.option.pane.encryption.success.text"),
				langUtil.getString("wallet.operations.option.pane.encryption.success.title"),
				JOptionPane.INFORMATION_MESSAGE);
			
			this.parent.exitProgram();
			
		} catch (Exception e)
		{
			this.errorReporter.reportError(e, false);
		}
	}
	
	private boolean checkExperimentalFeaturesOn() throws IOException {
		boolean experimentalFeaturesOn = true;
		String blockchainDir = OSUtil.getBlockchainDirectory();
		File zelcashConf = new File(blockchainDir + File.separator + "zcash.conf");
		Properties confProps = new Properties();
		FileInputStream fis = null;
		String property = null;
		FileWriter fw = null;
		try
		{
			fis = new FileInputStream(zelcashConf);
			fw = new FileWriter(zelcashConf,true); //the true will append the new data
			confProps.load(fis);
			property = confProps.getProperty("experimentalfeatures");
			if(property == null) {
				fw.write(System.getProperty("line.separator") + "experimentalfeatures=1"); 
				experimentalFeaturesOn = false;
				Log.info("Adding experimentalfeatures=1");
			}
			property = confProps.getProperty("developerencryptwallet");
			if(property == null) {
				fw.write(System.getProperty("line.separator") + "developerencryptwallet=1"); 
				experimentalFeaturesOn = false;
				Log.info("Adding developerencryptwallet=1");
			}		
		} finally
		{
			if (fw != null) {
				fw.close();
			}
			if (fis != null)
			{
				fis.close();
			}
		}
		return experimentalFeaturesOn;

	}
	
	
	public void backupWallet()
	{
		try
		{
			this.issueBackupDirectoryWarning();
			
			ZcashJFileChooser fileChooser = new ZcashJFileChooser();
			fileChooser.setDialogTitle(langUtil.getString("wallet.operations.dialog.backup.wallet.title"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(OSUtil.getUserHomeDirectory());
			 
			int result = fileChooser.showSaveDialog(this.parent);
			 
			if (result != JFileChooser.APPROVE_OPTION) 
			{
			    return;
			}
			
			File f = fileChooser.getSelectedFile();
			
			Cursor oldCursor = this.parent.getCursor();
			String path = null;
			try
			{
				this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
				path = this.clientCaller.backupWallet(f.getName());
				
				this.backupTracker.handleBackup();
				
				this.parent.setCursor(oldCursor);
			} catch (WalletCallException wce)
			{
				this.parent.setCursor(oldCursor);
				Log.error("Unexpected error: ", wce);
				
				JOptionPane.showMessageDialog(
					this.parent, 
					langUtil.getString("wallet.operations.option.pane.backup.wallet.error.text", wce.getMessage().replace(",", ",\n")),
					langUtil.getString("wallet.operations.option.pane.backup.wallet.error.title"),
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			JOptionPane.showMessageDialog(
				this.parent, 
				langUtil.getString("wallet.operations.option.pane.backup.wallet.success.text", f.getName(), path),

				langUtil.getString("wallet.operations.option.pane.backup.wallet.success.title"), JOptionPane.INFORMATION_MESSAGE);
			
		} catch (Exception e)
		{
			this.errorReporter.reportError(e, false);
		}
	}
	
	
	public void exportWalletPrivateKeys()
	{
		
		try
		{
			if (this.clientCaller.isWalletEncrypted())
			{
				boolean passwordOk = false;
				int retrys = 0;
				while(!passwordOk && retrys<3) {
					++retrys;
					PasswordDialog pd = new PasswordDialog((ZcashJFrame)(this.parent));
					pd.setVisible(true);

					if (!pd.isOKPressed())
					{
						return;
					}
					try {
						this.clientCaller.unlockWallet(pd.getPassword());
						passwordOk = true;
					}
					catch (Exception ex) {
						Log.error("Error unlocking wallet:"+ex.getMessage());
						JOptionPane.showMessageDialog(
								this.parent, 
								langUtil.getString("encryption.error.unlocking.message", ex.getMessage()),
								langUtil.getString("encryption.error.unlocking.title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
				if(!passwordOk) {
					Log.info("Failed to enter correct password for third time, wallet will close.");
					System.exit(1);
				}
			}
			
			this.issueBackupDirectoryWarning();
			
			ZcashJFileChooser fileChooser = new ZcashJFileChooser();
			fileChooser.setDialogTitle(langUtil.getString("wallet.operations.dialog.export.private.keys.title"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(OSUtil.getUserHomeDirectory());
			 
			int result = fileChooser.showSaveDialog(this.parent);
			 
			if (result != JFileChooser.APPROVE_OPTION) 
			{
			    return;
			}
			
			File f = fileChooser.getSelectedFile();
			
			Cursor oldCursor = this.parent.getCursor();
			String path = null;
			try
			{
				this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
				path = this.clientCaller.exportWallet(f.getName());
				this.backupTracker.handleBackup();
				
				this.parent.setCursor(oldCursor);
			} catch (WalletCallException wce)
			{
				this.parent.setCursor(oldCursor);
				Log.error("Unexpected error: ", wce);
				
				JOptionPane.showMessageDialog(
					this.parent, 
					langUtil.getString("wallet.operations.dialog.export.private.keys.error.text",
					"\n" + wce.getMessage().replace(",", ",\n")),
					langUtil.getString("wallet.operations.dialog.export.private.keys.error.title"),
                    JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			JOptionPane.showMessageDialog(
				this.parent,
				langUtil.getString("wallet.operations.dialog.export.private.keys.success.text", f.getName(), path ),
				langUtil.getString("wallet.operations.dialog.export.private.keys.success.title"),
                JOptionPane.INFORMATION_MESSAGE);
			
		} catch (Exception e)
		{
			this.errorReporter.reportError(e, false);
		}
	}

	public void reindexWallet() {
		Object[] options = 
        	{ 
        		langUtil.getString("send.cash.panel.option.pane.confirm.operation.button.yes"),
        		langUtil.getString("send.cash.panel.option.pane.confirm.operation.button.no")
        	};
		int option;
		option = JOptionPane.showOptionDialog(
				this.parent, 
				langUtil.getString("wallet.operations.dialog.reindex.message"),
                langUtil.getString("wallet.operations.dialog.reindex.title"),
			    JOptionPane.DEFAULT_OPTION, 
			    JOptionPane.QUESTION_MESSAGE,
			    null, 
			    options, 
			    options[1]);

		if (option == 0)
	    {
			this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			parent.restartDaemon(true, false);
			try {
				restartUI();
			} catch (IOException | InterruptedException | WalletCallException e1) {
				Log.error("Error restarting the UI, the wallet will be closed. Error:"+e1.getMessage());
				JOptionPane.showMessageDialog(null,
						LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.2.text",
								e1.getMessage()),
						LanguageUtil.instance()
								.getString("main.frame.option.pane.wallet.critical.error.2.title"),
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
	    }
	}

	public void rescanWallet() {
		Object[] options = 
        	{ 
        		langUtil.getString("send.cash.panel.option.pane.confirm.operation.button.yes"),
        		langUtil.getString("send.cash.panel.option.pane.confirm.operation.button.no")
        	};
		int option;
		option = JOptionPane.showOptionDialog(
				this.parent, 
				langUtil.getString("wallet.operations.dialog.rescan.message"),
                langUtil.getString("wallet.operations.dialog.rescan.title"),
			    JOptionPane.DEFAULT_OPTION, 
			    JOptionPane.QUESTION_MESSAGE,
			    null, 
			    options, 
			    options[1]);

		if (option == 0)
	    {
			this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			parent.restartDaemon(false, true);
			try {
				restartAfterRescan();
			} catch (IOException | InterruptedException | InvocationTargetException | WalletCallException e1) {
				Log.error("Error restarting the UI, the wallet will be closed. Error:"+e1.getMessage());
				JOptionPane.showMessageDialog(null,
						LanguageUtil.instance().getString("main.frame.option.pane.wallet.critical.error.2.text",
								e1.getMessage()),
						LanguageUtil.instance()
								.getString("main.frame.option.pane.wallet.critical.error.2.title"),
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
	    }
	}
	
	public void sproutToSaplingMigrationTool() {
		try {
			String[] zAddresses = this.clientCaller.getWalletZAddresses();
			List<String> listOfSapling = new ArrayList<String>();
			List<String> listOfSaplingWithLabels = new ArrayList<String>();
			if(zAddresses.length == 0) {
				JOptionPane.showMessageDialog(
						this.parent, 
						langUtil.getString("wallet.operations.dialog.sprouttosapling.nosapling.message"),
						langUtil.getString("wallet.operations.dialog.sprouttosapling.nosapling.title"),
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String label;
			String address;
			listOfSapling.add(langUtil.getString("dialog.zcashSprouttosaplingdialog.select"));
			listOfSaplingWithLabels.add(langUtil.getString("dialog.zcashSprouttosaplingdialog.select"));
			for(int i = 0; i<zAddresses.length; ++i) {
				if(zAddresses[i].startsWith("zs")) {
					listOfSapling.add(zAddresses[i]);
					label = this.labelStorage.getLabel(zAddresses[i]);
					address = zAddresses[i];
					if ((label != null) && (label.length() > 0))
					{
						address = label + " - " + address;
					}
					listOfSaplingWithLabels.add(address);
				}
			}
			if(zAddresses.length == 0) {
				JOptionPane.showMessageDialog(
						this.parent, 
						langUtil.getString("wallet.operations.dialog.sprouttosapling.nosapling.message"),
						langUtil.getString("wallet.operations.dialog.sprouttosapling.nosapling.title"),
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			ZcashSproutToSaplingDialog ad = new ZcashSproutToSaplingDialog(this.parent, clientCaller, installationObserver, listOfSapling, listOfSaplingWithLabels);
			ad.setVisible(true);
			
		} catch (HeadlessException | WalletCallException | IOException | InterruptedException e1) {
			this.errorReporter.reportError(e1, false);
		}
	}
	
	public void importWalletPrivateKeys()
	{
		try {
			if (this.clientCaller.isWalletEncrypted())
			{
				boolean passwordOk = false;
				int retrys = 0;
				while(!passwordOk && retrys<3) {
					++retrys;
					PasswordDialog pd = new PasswordDialog((ZcashJFrame)(this.parent));
					pd.setVisible(true);

					if (!pd.isOKPressed())
					{
						return;
					}
					try {
						this.clientCaller.unlockWallet(pd.getPassword());
						passwordOk = true;
					}
					catch (Exception ex) {
						Log.error("Error unlocking wallet:"+ex.getMessage());
						JOptionPane.showMessageDialog(
								this.parent, 
								langUtil.getString("encryption.error.unlocking.message", ex.getMessage()),
								langUtil.getString("encryption.error.unlocking.title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
				if(!passwordOk) {
					Log.info("Failed to enter correct password for third time, wallet will close.");
					System.exit(1);
				}
			}
		} catch (HeadlessException | WalletCallException | IOException | InterruptedException e1) {
			this.errorReporter.reportError(e1, false);
		}
		
	    Object[] options = 
		{ 
        		langUtil.getString("button.option.yes"),
        		langUtil.getString("button.option.no")
        	};
	    int option = JOptionPane.showOptionDialog(  
		    this.parent,
		    langUtil.getString("wallet.operations.dialog.import.private.keys.notice.text"),
		    langUtil.getString("wallet.operations.dialog.import.private.keys.notice.title"),
		    JOptionPane.YES_NO_OPTION,
	        JOptionPane.QUESTION_MESSAGE,
	        null,
	        options,
	        JOptionPane.NO_OPTION);
		if (option == JOptionPane.NO_OPTION)
		{
		  	return;
		}
		
		try
		{
			ZcashJFileChooser fileChooser = new ZcashJFileChooser();
			fileChooser.setDialogTitle(langUtil.getString("wallet.operations.file.chooser.import.private.keys.title"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			 
			int result = fileChooser.showOpenDialog(this.parent);
			 
			if (result != JFileChooser.APPROVE_OPTION) 
			{
			    return;
			}
			
			File f = fileChooser.getSelectedFile();
			
			Cursor oldCursor = this.parent.getCursor();
			try
			{
				this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
				this.clientCaller.importWallet(f.getCanonicalPath());
				
				this.parent.setCursor(oldCursor);
			} catch (WalletCallException wce)
			{
				this.parent.setCursor(oldCursor);
				Log.error("Unexpected error: ", wce);
				
				JOptionPane.showMessageDialog(
					this.parent, 
					langUtil.getString("wallet.operations.dialog.import.private.keys.error.text", wce.getMessage().replace(",", ",\n")),
					langUtil.getString("wallet.operations.dialog.import.private.keys.error.title"),
                    JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			JOptionPane.showMessageDialog(
				this.parent, 
				langUtil.getString("wallet.operations.dialog.import.private.keys.success.text",f.getCanonicalPath()),
				langUtil.getString("wallet.operations.dialog.import.private.keys.success.title"),
                JOptionPane.INFORMATION_MESSAGE);
			
		} catch (Exception e)
		{
			this.errorReporter.reportError(e, false);
		}
	}
	
	
	public void showPrivateKey()
	{
		if (this.tabs.getSelectedIndex() != 2)
		{
			JOptionPane.showMessageDialog(
				this.parent, 
				langUtil.getString("wallet.operations.option.pane.own.address.view.private.key.text"),
                langUtil.getString("wallet.operations.option.pane.own.address.view.private.key.title"),
                JOptionPane.INFORMATION_MESSAGE);
			this.tabs.setSelectedIndex(2);
			return;
		}
		
		String address = this.addresses.getSelectedAddress();
		
		if (address == null)
		{
			JOptionPane.showMessageDialog(
				this.parent, 
				langUtil.getString("wallet.operations.option.pane.address.table.view.private.key.text"),
				langUtil.getString("wallet.operations.option.pane.address.table.view.private.key.title"),
                JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		try
		{
			// Check for encrypted wallet
			final boolean bEncryptedWallet = this.clientCaller.isWalletEncrypted();
			if (bEncryptedWallet)
			{
				boolean passwordOk = false;
				int retrys = 0;
				while(!passwordOk && retrys<3) {
					++retrys;
					PasswordDialog pd = new PasswordDialog((ZcashJFrame)(this.parent));
					pd.setVisible(true);

					if (!pd.isOKPressed())
					{
						return;
					}
					try {
						this.clientCaller.unlockWallet(pd.getPassword());
						passwordOk = true;
					}
					catch (Exception ex) {
						Log.error("Error unlocking wallet:"+ex.getMessage());
						JOptionPane.showMessageDialog(
								this.parent, 
								langUtil.getString("encryption.error.unlocking.message", ex.getMessage()),
								langUtil.getString("encryption.error.unlocking.title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
				if(!passwordOk) {
					Log.info("Failed to enter correct password for third time, wallet will close.");
					System.exit(1);
				}
			}
			
			boolean isZAddress = Util.isZAddress(address);
			
			String privateKey = isZAddress ?
				this.clientCaller.getZPrivateKey(address) : this.clientCaller.getTPrivateKey(address);
				
			// Lock the wallet again 
			if (bEncryptedWallet)
			{
				//this.clientCaller.lockWallet();
			}
				
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(privateKey), null);
			String adressType = isZAddress ? langUtil.getString("wallet.operations.private.address")
                                           : langUtil.getString("wallet.operations.transparent.address");
			JOptionPane.showMessageDialog(
				this.parent, 
				langUtil.getString("wallet.operations.option.pane.address.information.text", adressType, address, privateKey),
                langUtil.getString("wallet.operations.option.pane.address.information.title"), JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex)
		{
			this.errorReporter.reportError(ex, false);
		}
	}
	
	
	public void importSinglePrivateKey()
	{
		try
		{
			if (this.clientCaller.isWalletEncrypted())
			{
				boolean passwordOk = false;
				int retrys = 0;
				while(!passwordOk && retrys<3) {
					++retrys;
					PasswordDialog pd = new PasswordDialog((ZcashJFrame)(this.parent));
					pd.setVisible(true);

					if (!pd.isOKPressed())
					{
						return;
					}
					try {
						this.clientCaller.unlockWallet(pd.getPassword());
						passwordOk = true;
					}
					catch (Exception ex) {
						Log.error("Error unlocking wallet:"+ex.getMessage());
						JOptionPane.showMessageDialog(
								this.parent, 
								langUtil.getString("encryption.error.unlocking.message", ex.getMessage()),
								langUtil.getString("encryption.error.unlocking.title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
				if(!passwordOk) {
					Log.info("Failed to enter correct password for third time, wallet will close.");
					System.exit(1);
				}
			}
			SingleKeyImportDialog kd = new SingleKeyImportDialog(this.parent, this.clientCaller);
			kd.setVisible(true);
			
		} catch (Exception ex)
		{
			this.errorReporter.reportError(ex, false);
		}
	}



	/**
	 * export to Arizen wallet
	 */
	public void exportToArizenWallet()
	{
		final ZcashJDialog dialog = new ZcashJDialog(this.parent, langUtil.getString("wallet.operations.dialog.export.arizen.title"));
		final ZcashJLabel exportLabel = new ZcashJLabel();
		final WalletRepo arizenWallet = new ArizenWallet();
		try {
			ZcashJFileChooser fileChooser = new ZcashJFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter(langUtil.getString("wallet.operations.dialog.export.arizen.filechooser.filter"), "uawd"));
			fileChooser.setDialogTitle(langUtil.getString("wallet.operations.dialog.export.arizen.filechooser.title"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(OSUtil.getUserHomeDirectory());
			int result = fileChooser.showDialog(this.parent, langUtil.getString("wallet.operations.dialog.export.arizen.filechooser.aprove.button"));

			if (result != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File chooseFile = fileChooser.getSelectedFile();
			String fullPath = chooseFile.getAbsolutePath();
			if (!fullPath.endsWith(".uawd"))
				fullPath += ".uawd";

			final File f = new File(fullPath);
			if (f.exists()) {
				Object[] options = 
		        	{ 
		        		langUtil.getString("button.option.yes"),
		        		langUtil.getString("button.option.no")
		        	};
				int r = JOptionPane.showOptionDialog((Component) null,
						langUtil.getString("wallet.operations.dialog.delete.file.confirmation", f.getName()),
                        langUtil.getString("wallet.operations.dialog.delete.file.confirmation.title"),
                        JOptionPane.YES_NO_OPTION,
    			        JOptionPane.QUESTION_MESSAGE,
    			        null,
    			        options,
    			        JOptionPane.NO_OPTION);
				if (r == 1) {
					return;
				}
				Files.delete(f.toPath());
			}
			final String strFullpath = fullPath;

			dialog.setSize(300, 75);
			dialog.setLocationRelativeTo(parent);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setLayout(new BorderLayout());

			ZcashJProgressBar progressBar = new ZcashJProgressBar();
			progressBar.setIndeterminate(true);
			dialog.add(progressBar, BorderLayout.CENTER);
			exportLabel.setText(langUtil.getString("wallet.operations.dialog.export.label"));
			exportLabel.setHorizontalAlignment(JLabel.CENTER);
			exportLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

			dialog.add(exportLabel, BorderLayout.SOUTH);
			dialog.setVisible(true);

			SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
				@Override
				public Boolean doInBackground() {
					try {
						arizenWallet.createWallet(f);
						Thread.sleep(750);
						updateProgressText(langUtil.getString("wallet.operations.dialog.export.progress.reading.text"));
						String[] zaddress = clientCaller.getWalletZAddresses();
						String[] taddress = clientCaller.getWalletAllPublicAddresses();
						String[] tAddressesWithUnspentOuts = clientCaller.getWalletPublicAddressesWithUnspentOutputs();

						Set<Address> addressPublicSet = new HashSet<Address>();
						Set<Address> addressPrivateSet = new HashSet<Address>();

						Map<String, Address> tMap = new HashMap<String, Address>();
						Map<String, Address> zMap = new HashMap<String, Address>();

						for (String straddr : taddress) {
							String pk = clientCaller.getTPrivateKey(straddr);
							String pkHex = Util.wifToHex(pk);
							String balance = clientCaller.getBalanceForAddress(straddr);
							Address addr = new Address(Address.ADDRESS_TYPE.TRANSPARENT, straddr, pkHex, balance);
							tMap.put(straddr, addr);
						}

						for (String straddr : tAddressesWithUnspentOuts) {
							String pk = clientCaller.getTPrivateKey(straddr);
							String pkHex = Util.wifToHex(pk);
							String balance = clientCaller.getBalanceForAddress(straddr);
							Address addr = new Address(Address.ADDRESS_TYPE.TRANSPARENT, straddr, pkHex, balance);
							tMap.put(straddr, addr);
						}

						for (String straddr : zaddress) {
							String pk = clientCaller.getZPrivateKey(straddr);
							String balance = clientCaller.getBalanceForAddress(straddr);
							Address addr = new Address(Address.ADDRESS_TYPE.PRIVATE, straddr, pk, balance);
							zMap.put(straddr, addr);
						}
						addressPublicSet.addAll(tMap.values());
						addressPrivateSet.addAll(zMap.values());
						Thread.sleep(500);

						updateProgressText(langUtil.getString("wallet.operations.dialog.export.progress.writing.text"));
						arizenWallet.insertAddressBatch(addressPublicSet);
						if (addressPrivateSet.size() > 0)
						{
							arizenWallet.insertAddressBatch(addressPrivateSet);
						}
						Thread.sleep(1000);

						updateProgressText(langUtil.getString("wallet.operations.dialog.export.progress.finished.text"));
						Thread.sleep(750);

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.dispose();
								JOptionPane.showConfirmDialog(parent,
										langUtil.getString("wallet.operations.option.pane.export.success.info.text", strFullpath),
										langUtil.getString("wallet.operations.option.pane.export.success.info.title"),
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
							}
						});

					} catch (Exception e) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.dispose();
							}
						});
						errorReporter.reportError(e, false);
					} finally {
						try {
							if (arizenWallet != null && arizenWallet.isOpen()) {
								arizenWallet.close();
							}
						} catch (Exception ex) {
							errorReporter.reportError(ex, false);
						}
					}
					return true;
				}

				private void updateProgressText(final String text) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							exportLabel.setText(text);
						}
					});

				}
			};

			worker.execute();

		} catch (Exception ex) {
			errorReporter.reportError(ex, false);
		}
	}



	private void issueBackupDirectoryWarning()
		throws IOException
	{
        String userDir = OSUtil.getSettingsDirectory();
        File warningFlagFile = new File(userDir + File.separator + "backupInfoShownNG.flag");
        if (warningFlagFile.exists())
        {
            return;
        } 
            
        int reply = JOptionPane.showOptionDialog(
            this.parent,
            langUtil.getString("wallet.operations.option.pane.backup.directory.warning.text", 
            		           OSUtil.getUserHomeDirectory().getCanonicalPath()),
	        langUtil.getString("wallet.operations.option.pane.backup.directory.warning.title"),
	        JOptionPane.YES_NO_OPTION,
	        JOptionPane.INFORMATION_MESSAGE, 
	        null, new String[] { langUtil.getString("wallet.operations.option.pane.backup.directory.warning.message"),
                                      langUtil.getString("wallet.operations.option.pane.backup.directory.warning.message.ok")  },
	        JOptionPane.NO_OPTION);
	        
	    if (reply == JOptionPane.NO_OPTION) 
	    {
	    	return;
	    }
	    
	    warningFlagFile.createNewFile();
	}
	
	public void restartUI() throws IOException, InterruptedException, WalletCallException {
		Log.info("Restarting the UI.");
		ZCashUI z = new ZCashUI(null);
		this.parent.setVisible(false);
		this.parent.dispose();
		this.parent = z;	
		this.parent.repaint();
		this.parent.setVisible(true);
	}

	public void restartAfterRescan() throws IOException, InterruptedException, WalletCallException, InvocationTargetException {
		Log.info("Waiting for rescan complete.");
	    while(true) {
	        Thread.sleep(POLL_PERIOD);

	        JsonObject info = null;

	        try
	        {
	        	info = clientCaller.getDaemonRawRuntimeInfo();
	        } catch (IOException e)
	        {
        		throw e;
	        }

	        JsonValue code = info.get("code");
	        Log.debug("clientCaller:"+info.toString());
	        if (code == null || (code.asInt() != STARTUP_ERROR_CODE))
	            break;        
	    }
	    Log.info("Rescan complete.");

	    JOptionPane.showMessageDialog(this.parent, 
	    		langUtil.getString("wallet.operations.dialog.rescan.complete.message"), 
	    		langUtil.getString("wallet.operations.dialog.rescan.complete.title"), 
	    		JOptionPane.INFORMATION_MESSAGE);

	    this.parent.setVisible(false);
		this.parent.dispose();

		ZCashUI z = new ZCashUI(null);
		this.parent = z;	
		this.parent.repaint();
		this.parent.setVisible(true);
	}
}
