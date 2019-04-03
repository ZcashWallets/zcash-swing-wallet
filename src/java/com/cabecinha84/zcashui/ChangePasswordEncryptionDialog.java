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
package com.cabecinha84.zcashui;


import java.awt.Font;

import javax.swing.JOptionPane;

import com.cabecinha84.zcashui.ZcashJFrame;
import com.cabecinha84.zcashui.ZcashJLabel;
import com.cabecinha84.zcashui.ZcashJPasswordField;
import com.vaklinov.zcashui.LanguageUtil;
import com.vaklinov.zcashui.PasswordDialog;


/**
 * Dialog to get the user password - to encrypt a wallet.
 */
public class ChangePasswordEncryptionDialog
	extends PasswordDialog
{
	protected ZcashJPasswordField newPasswordConfirmationField = null;
	protected ZcashJPasswordField passwordConfirmationField = null;
	protected String  newPassword    = null;

	private LanguageUtil langUtil;

	public ChangePasswordEncryptionDialog(ZcashJFrame parent)
	{
		super(parent);
		langUtil = LanguageUtil.instance();
		this.passwordLabel.setText(langUtil.getString("dialog.change.password.current.password"));

		this.upperLabel.setText(langUtil.getString("dialog.change.password.encryption.upper.label.text"));

		ZcashJLabel newLabel = new ZcashJLabel(langUtil.getString("dialog.change.password.new.password"));
		this.freeSlotPanel.add(newLabel);
		this.freeSlotPanel.add(newPasswordConfirmationField = new ZcashJPasswordField(50));
		newLabel.setPreferredSize(passwordLabel.getPreferredSize());

		ZcashJLabel confLabel = new ZcashJLabel(langUtil.getString("dialog.password.encryption.confirmation.label.text"));
		this.freeSlotPanel3.add(confLabel);
		this.freeSlotPanel3.add(passwordConfirmationField = new ZcashJPasswordField(50));
		confLabel.setPreferredSize(passwordLabel.getPreferredSize());

		ZcashJLabel dividerLabel = new ZcashJLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 8));
		this.freeSlotPanel2.add(dividerLabel);

		this.setSize(620, 220);
		this.validate();
		this.repaint();
	}


	protected void processOK()
	{
		String password     = this.newPasswordConfirmationField.getText();
		String confirmation = this.passwordConfirmationField.getText(); 

		if (password == null)
		{
			password = "";
		}

		if (confirmation == null)
		{
			confirmation = "";
		}

		if (!password.equals(confirmation))
		{
			JOptionPane.showMessageDialog(
				this.getParent(), 
				langUtil.getString("dialog.password.encryption.option.pane.mismatch.text"),
				langUtil.getString("dialog.password.encryption.option.pane.mismatch.title")	,
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(password.length()==0) {
			JOptionPane.showMessageDialog(
					this.getParent(), 
					langUtil.getString("dialog.password.option.pane.process.text"),
					langUtil.getString("dialog.password.option.pane.process.title"),
					JOptionPane.ERROR_MESSAGE);
				return;
		}

		super.processOK();
		this.newPassword = password;
	}

	public String getNewPassword()
	{
		return this.newPassword;
	}
}