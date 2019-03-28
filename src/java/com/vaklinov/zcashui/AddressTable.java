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

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.cabecinha84.zcashui.ZcashJFrame;
import com.cabecinha84.zcashui.ZcashJMenuItem;
import com.cabecinha84.zcashui.ZcashQRCodeDialog;


/**
 * Table to be used for addresses - specifically.
 */
public class AddressTable 
	extends DataTable 
{	
	private LabelStorage labelStorage;
	private ZCashInstallationObserver installationObserver;
	private ZcashJFrame parentFrame;
	
	public AddressTable(final Object[][] rowData, final Object[] columnNames, 
			            final ZCashClientCaller caller, LabelStorage labelStorage, 	ZCashInstallationObserver installationObserver,
			            ZcashJFrame parentFrame)
	{
		super(rowData, columnNames);
		this.parentFrame = parentFrame;
		this.labelStorage = labelStorage;
		this.installationObserver = installationObserver;
		
		int accelaratorKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		final LanguageUtil langUtil = LanguageUtil.instance();
		ZcashJMenuItem obtainPrivateKey = new ZcashJMenuItem(langUtil.getString("table.address.option.obtain.private.key.label"));
		obtainPrivateKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, accelaratorKeyMask));
        popupMenu.add(obtainPrivateKey);
        
        obtainPrivateKey.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					int row = AddressTable.this.convertRowIndexToModel(AddressTable.this.getSelectedRow());
					
					String address = AddressTable.this.getModel().getValueAt(row, 3).toString();
					boolean isZAddress = Util.isZAddress(address);
					
					// Check for encrypted wallet
					final boolean bEncryptedWallet = caller.isWalletEncrypted();
					if (bEncryptedWallet)
					{
						boolean passwordOk = false;
						int retrys = 0;
						while(!passwordOk && retrys<3) {
							++retrys;
							PasswordDialog pd = new PasswordDialog((ZcashJFrame)(AddressTable.this.getRootPane().getParent()));
							pd.setVisible(true);

							if (!pd.isOKPressed())
							{
								return;
							}
							try {
								caller.unlockWallet(pd.getPassword());
								passwordOk = true;
							}
							catch (Exception ex) {
								Log.error("Error unlocking wallet:"+ex.getMessage());
								JOptionPane.showMessageDialog(
										AddressTable.this.getRootPane().getParent(), 
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
					
					String privateKey = isZAddress ?
						caller.getZPrivateKey(address) : caller.getTPrivateKey(address);
						
					// Lock the wallet again 
					if (bEncryptedWallet)
					{
						//caller.lockWallet();
					}
						
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(privateKey), null);
					
					JOptionPane.showMessageDialog(
						AddressTable.this.getRootPane().getParent(), 
						(isZAddress ? langUtil.getString("table.address.option.pane.text.private") : langUtil.getString("table.address.option.pane.text.transparent")) +
							langUtil.getString("table.address.option.pane.text.rest", address, privateKey),
							langUtil.getString("table.address.option.pane.title"), JOptionPane.INFORMATION_MESSAGE);

					
				} catch (Exception ex){
					Log.error("Unexpected error: ", ex);
		            JOptionPane.showMessageDialog(
		                AddressTable.this.getRootPane().getParent(),
				        langUtil.getString("table.address.option.pane.error.text", ex.getMessage()),
				        langUtil.getString("table.address.option.pane.error.title"),
				        JOptionPane.ERROR_MESSAGE);
				}
			}
		});
        
        ZcashJMenuItem qrCode = new ZcashJMenuItem(langUtil.getString("data.table.menu.item.qrcode"));
        popupMenu.add(qrCode);
        
        qrCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, accelaratorKeyMask));
        qrCode.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				int row = AddressTable.this.convertRowIndexToModel(AddressTable.this.getSelectedRow());
				
				String address = AddressTable.this.getModel().getValueAt(row, 3).toString();
				ZcashQRCodeDialog ad;
				try {
					ad = new ZcashQRCodeDialog(address, AddressTable.this.parentFrame);
					ad.setVisible(true);
				} catch (IOException e1) {
					Log.error("Error caused by"+e1.getMessage());
				}		

			}
		});
        
		ZcashJMenuItem setLabel = new ZcashJMenuItem("Set label...");
		setLabel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, accelaratorKeyMask));
        popupMenu.add(setLabel);
        
        setLabel.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{

				try
				{
		            TableModel model = AddressTable.this.getModel();
		            int row = AddressTable.this.convertRowIndexToModel(AddressTable.this.getSelectedRow());
					
		            String oldLabel = (String)model.getValueAt(row, 0);
					String label = (String) JOptionPane.showInputDialog(AddressTable.this,
		                    "Please enter a label for the address:",
		                    "Label of the address...",
		                    JOptionPane.PLAIN_MESSAGE, null, null, oldLabel);

					if (!Util.stringIsEmpty(label))
					{
						model.setValueAt(label, row, 0);
					}
		            
		            AddressTable.this.invalidate();
		            AddressTable.this.repaint();
					
				} catch (Exception ex)
				{
					Log.error("Unexpected error: ", ex);
		            JOptionPane.showMessageDialog(
		                AddressTable.this.getRootPane().getParent(),
				        "Error in setting label:" + "\n" + ex.getMessage() + "\n\n",
				        "Error in obtaining private key!",
				        JOptionPane.ERROR_MESSAGE);
				}
			}
		});

        ZcashJMenuItem showInExplorer = new ZcashJMenuItem(langUtil.getString("table.address.show.in.explorer"));
		showInExplorer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, accelaratorKeyMask));
        popupMenu.add(showInExplorer);
        
        showInExplorer.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					int row = AddressTable.this.convertRowIndexToModel(AddressTable.this.getSelectedRow());
					
					String address = AddressTable.this.getModel().getValueAt(row, 3).toString();
					address = address.replaceAll("\"", ""); // In case it has quotes
					
					if ((!AddressTable.this.installationObserver.isOnTestNet()) && Util.isZAddress(address))
					{
			           JOptionPane.showMessageDialog(
			               AddressTable.this.getRootPane().getParent(),
			               langUtil.getString("table.address.show.in.explorer.zaddress.message", address),
			               langUtil.getString("table.address.show.in.explorer.zaddress.title"),
				           JOptionPane.ERROR_MESSAGE);

						return;
					}
					
					Log.info("Address for block explorer is: " + address);
					
					String urlPrefix = "https://explorer.zecmate.com/address/";
					if (AddressTable.this.installationObserver.isOnTestNet())
					{
						urlPrefix = "https://explorer.testnet.z.cash/address/";
					}
					
					Desktop.getDesktop().browse(new URL(urlPrefix + address).toURI());
				} catch (Exception ex)
				{
					Log.error("Unexpected error: ", ex);
					// TODO: report exception to user
				}
			}
		});
        
        // Model listener for labels
        this.getModel().addTableModelListener(new TableModelListener() 
        {	
			@Override
			public void tableChanged(TableModelEvent e) 
			{
				// Make sure we respond only to editing labels
				if ((e.getType() == TableModelEvent.UPDATE) &&
					(e.getFirstRow() == e.getLastRow()) &&
					(e.getColumn() == 0))
				{
					TableModel model = AddressTable.this.getModel();
					String address = model.getValueAt(e.getFirstRow(), 3).toString();
					String newLabel = model.getValueAt(e.getFirstRow(), 0).toString();
					
					try
					{
						AddressTable.this.labelStorage.setLabel(address, newLabel);
					}
					catch (Exception ex)
					{
						Log.error("Unexpected error: ", ex);
				           JOptionPane.showMessageDialog(
				               AddressTable.this.getRootPane().getParent(),
					           "Error in editing label:" + "\n" +
					           ex.getMessage() + "\n\n",
					           "Error in editing label!",
					           JOptionPane.ERROR_MESSAGE);
					}		
				}
			}
		});
        
	} // End constructor

	
	// Make sure labels may be edited
	@Override
    public boolean isCellEditable(int row, int column) 
    {                
        return column == 0;
    }
}
