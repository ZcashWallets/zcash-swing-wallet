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
package com.vaklinov.zcashui.msg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.cabecinha84.zcashui.ZcashJButton;
import com.cabecinha84.zcashui.ZcashJDialog;
import com.cabecinha84.zcashui.ZcashJFrame;
import com.cabecinha84.zcashui.ZcashJLabel;
import com.cabecinha84.zcashui.ZcashJPanel;
import com.cabecinha84.zcashui.ZcashJTextArea;
import com.cabecinha84.zcashui.ZcashJTextField;

import com.vaklinov.zcashui.LanguageUtil;


/**
 * Dialog showing the information about a user's identity
 */
public class IdentityInfoDialog
	extends ZcashJDialog
{
	protected ZcashJFrame parentFrame;
	protected MessagingIdentity identity;
	
	protected ZcashJLabel infoLabel;
	
	protected ZcashJPanel buttonPanel;
	

	protected ZcashJTextField nicknameTextField;
	protected ZcashJTextArea sendreceiveaddressTextField;
	protected ZcashJTextField senderidaddressTextField;
	protected ZcashJTextField firstnameTextField;
	protected ZcashJTextField middlenameTextField;
	protected ZcashJTextField surnameTextField;
	protected ZcashJTextField emailTextField;
	protected ZcashJTextField streetaddressTextField;
	protected ZcashJTextField facebookTextField;
	protected ZcashJTextField twitterTextField;
	
	private static LanguageUtil langUtil = LanguageUtil.instance();
	
	public IdentityInfoDialog(ZcashJFrame parentFrame, MessagingIdentity identity)
	{

		this.parentFrame = parentFrame;
		this.identity    = identity;
		
		this.setTitle(langUtil.getString("dialog.identity.info.title",  identity.getDiplayString()));
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
		this.getContentPane().setLayout(new BorderLayout(0, 0));
			
		ZcashJPanel tempPanel = new ZcashJPanel(new BorderLayout(0, 0));
		tempPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		infoLabel = new ZcashJLabel(langUtil.getString("dialog.identity.info.infolabel", identity.getNickname()));
	    tempPanel.add(infoLabel, BorderLayout.CENTER);
		this.getContentPane().add(tempPanel, BorderLayout.NORTH);
			
		ZcashJPanel detailsPanel = new ZcashJPanel();
		detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
		
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.nickname"),  nicknameTextField = new ZcashJTextField(40));
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.firstname"), firstnameTextField = new ZcashJTextField(40));
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.middlename"), middlenameTextField = new ZcashJTextField(40));
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.surname"),    surnameTextField = new ZcashJTextField(40));

	
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.email"),         emailTextField = new ZcashJTextField(40));
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.streetaddress"), streetaddressTextField = new ZcashJTextField(40));
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.facebook"),  facebookTextField = new ZcashJTextField(40));
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.twitter"),   twitterTextField = new ZcashJTextField(40));
	
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.sendert"), senderidaddressTextField = new ZcashJTextField(40));
		addFormField(detailsPanel, langUtil.getString("dialog.identity.info.senderz"), sendreceiveaddressTextField = new ZcashJTextArea(2, 40));
		sendreceiveaddressTextField.setLineWrap(true);
		

		nicknameTextField.setText(this.identity.getNickname());
		firstnameTextField.setText(this.identity.getFirstname());
		middlenameTextField.setText(this.identity.getMiddlename());
		surnameTextField.setText(this.identity.getSurname());
		emailTextField.setText(this.identity.getEmail());
		streetaddressTextField.setText(this.identity.getStreetaddress());
		facebookTextField.setText(this.identity.getFacebook());
		twitterTextField.setText(this.identity.getTwitter());
		senderidaddressTextField.setText(this.identity.getSenderidaddress());
		sendreceiveaddressTextField.setText(this.identity.getSendreceiveaddress());
		
		nicknameTextField.setEditable(false);
		firstnameTextField.setEditable(false);
		middlenameTextField.setEditable(false);
		surnameTextField.setEditable(false);
		emailTextField.setEditable(false);
		streetaddressTextField.setEditable(false);
		facebookTextField.setEditable(false);
		twitterTextField.setEditable(false);
		senderidaddressTextField.setEditable(false);
		sendreceiveaddressTextField.setEditable(false);
		
		detailsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.getContentPane().add(detailsPanel, BorderLayout.CENTER);

		// Lower buttons - by default only close is available
		buttonPanel = new ZcashJPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 3));
		ZcashJButton closeButon = new ZcashJButton(langUtil.getString("dialog.identity.info.button.close"));
		buttonPanel.add(closeButon);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		closeButon.addActionListener(new ActionListener()
		{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					IdentityInfoDialog.this.setVisible(false);
					IdentityInfoDialog.this.dispose();
				}
		});

		this.pack();
		this.setLocation(100, 100);
		this.setLocationRelativeTo(parentFrame);
	}

	
	
	private void addFormField(ZcashJPanel detailsPanel, String name, JComponent field)
	{
		ZcashJPanel tempPanel = new ZcashJPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
		ZcashJLabel tempLabel = new ZcashJLabel(name, JLabel.RIGHT);
		// TODO: hard sizing of labels may not scale!
		final int width = new ZcashJLabel(langUtil.getString("dialog.identity.info.sender.id")).getPreferredSize().width + 10;
		tempLabel.setPreferredSize(new Dimension(width, tempLabel.getPreferredSize().height));
		tempPanel.add(tempLabel);
		tempPanel.add(field);
		detailsPanel.add(tempPanel);
	}
	
} // End public class IdentityInfoDialog
