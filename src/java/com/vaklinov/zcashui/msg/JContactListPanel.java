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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cabecinha84.zcashui.ZcashJButton;
import com.cabecinha84.zcashui.ZcashJFrame;
import com.cabecinha84.zcashui.ZcashJLabel;
import com.cabecinha84.zcashui.ZcashJMenuItem;
import com.cabecinha84.zcashui.ZcashJPanel;
import com.cabecinha84.zcashui.ZcashJPopupMenu;
import com.cabecinha84.zcashui.ZcashJScrollPane;
import com.cabecinha84.zcashui.ZcashXUI;

import com.vaklinov.zcashui.LanguageUtil;
import com.vaklinov.zcashui.Log;
import com.vaklinov.zcashui.StatusUpdateErrorReporter;


/**
 * Main panel for messaging
 */
public class JContactListPanel
	extends ZcashJPanel
{
	private MessagingPanel   parent;
	private MessagingStorage mesagingStorage;
	private ContactList      list;
	private StatusUpdateErrorReporter errorReporter;
	private ZcashJFrame           parentFrame;
	
	private ZcashJPopupMenu popupMenu;

	private static LanguageUtil langUtil = LanguageUtil.instance();
	
	public JContactListPanel(MessagingPanel parent, 
			                 ZcashJFrame parentFrame,
			                 MessagingStorage messagingStorage, 
			                 StatusUpdateErrorReporter errorReporter)
		throws IOException
	{
		super();
		
		this.parent = parent;
		this.parentFrame     = parentFrame;
		this.mesagingStorage = messagingStorage;
		this.errorReporter   = errorReporter;
		
		this.setLayout(new BorderLayout(0, 0));
		
		list = new ContactList();
		list.setIdentities(this.mesagingStorage.getContactIdentities(true));
		this.add(new ZcashJScrollPane(list), BorderLayout.CENTER);
		
		ZcashJPanel upperPanel = new ZcashJPanel(new BorderLayout(0, 0));
		upperPanel.add(new ZcashJLabel(
			"<html><span style=\"font-size:1.2em;font-style:italic;\">Contact list: &nbsp;</span></html>"),
			BorderLayout.WEST);
		URL addIconUrl = this.getClass().getClassLoader().getResource("images/add12.png");
        ImageIcon addIcon = new ImageIcon(addIconUrl);
        URL removeIconUrl = this.getClass().getClassLoader().getResource("images/remove12.png");
        ImageIcon removeIcon = new ImageIcon(removeIconUrl);
        ZcashJButton addButton = new ZcashJButton(addIcon);
        addButton.setToolTipText(langUtil.getString("contact.list.add.contact"));
        ZcashJButton removeButton = new ZcashJButton(removeIcon);
        removeButton.setToolTipText(langUtil.getString("contact.list.remove.contact"));
        ZcashJButton addGroupButton = new ZcashJButton(
        	langUtil.getString("contact.list.button.group"), addIcon);
        addGroupButton.setToolTipText(langUtil.getString("contact.list.add.group"));
        ZcashJPanel tempPanel = new ZcashJPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        tempPanel.add(removeButton);
        tempPanel.add(addButton);
        tempPanel.add(addGroupButton);
        upperPanel.add(tempPanel, BorderLayout.EAST);
        
        upperPanel.add(new ZcashJLabel(
    			"<html><span style=\"font-size:1.6em;font-style:italic;\">&nbsp;</span>"),
    			BorderLayout.CENTER);
		upperPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		this.add(upperPanel, BorderLayout.NORTH);
		
		// Add a listener for adding a contact
		addButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JContactListPanel.this.parent.importContactIdentity();
			}
		});
		
		// Add a listener for adding a group
		addGroupButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JContactListPanel.this.parent.addMessagingGroup();
			}
		});

		
		// Add a listener for removing a contact
		removeButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JContactListPanel.this.parent.removeSelectedContact();
			}
		});
		
		// Take care of updating the messages on selection
		list.addListSelectionListener(new ListSelectionListener() 
		{	
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				try
				{
					if (e.getValueIsAdjusting())
					{
						return; // Change is not final
					}
					
					MessagingIdentity id = JContactListPanel.this.list.getSelectedValue();
					
					if (id == null)
					{
						return; // Nothing selected
					}
					
					Cursor oldCursor = JContactListPanel.this.parentFrame.getCursor();
					try
					{
						JContactListPanel.this.parentFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	     				JContactListPanel.this.parent.displayMessagesForContact(id);
					} finally
					{
						JContactListPanel.this.parentFrame.setCursor(oldCursor);
					}
				} catch (IOException ioe)
				{
					Log.error("Unexpected error: ", ioe);
					JContactListPanel.this.errorReporter.reportError(ioe, false);
				}
			}
		});
		
		// Mouse listener is used to show the popup menu
		list.addMouseListener(new MouseAdapter()
        {
        	public void mousePressed(MouseEvent e)
        	{
                if ((!e.isConsumed()) && e.isPopupTrigger())
                {
                    ContactList list = (ContactList)e.getSource();
                    if (list.getSelectedValue() != null)
                    {
                    	popupMenu.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
                    	e.consume();
                    }
                }
        	}
        	
            public void mouseReleased(MouseEvent e)
            {
            	if ((!e.isConsumed()) && e.isPopupTrigger())
            	{
            		mousePressed(e);
            	}
            }
        });
		
		
		// Actions of the popup menu
		this.popupMenu = new ZcashJPopupMenu();
		int accelaratorKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		ZcashJMenuItem showDetails = new ZcashJMenuItem(langUtil.getString("contact.list.show.details"));
        popupMenu.add(showDetails);
        showDetails.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, accelaratorKeyMask));
        showDetails.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// Show a messaging identity dialog
				if (list.getSelectedValue() != null)
				{
					IdentityInfoDialog iid = new IdentityInfoDialog(
						JContactListPanel.this.parentFrame, list.getSelectedValue());
					iid.setVisible(true);
				}
			}
		});
        
		ZcashJMenuItem removeContact = new ZcashJMenuItem(langUtil.getString("contact.list.remove"));
        popupMenu.add(removeContact);
        removeContact.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, accelaratorKeyMask));
        removeContact.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JContactListPanel.this.parent.removeSelectedContact();
			}
		});

		ZcashJMenuItem sendContactDetails = new ZcashJMenuItem(langUtil.getString("contact.list.send.contact.details"));
        popupMenu.add(sendContactDetails);
        sendContactDetails.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, accelaratorKeyMask));
        sendContactDetails.addActionListener(new ActionListener() 
        {	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JContactListPanel.this.sendContactDetailsToSelectedContact();
			}
		});
	}
	
	
	public void sendContactDetailsToSelectedContact()
	{
		try
		{
			MessagingIdentity id = this.list.getSelectedValue();
			
			if (id == null)
			{
		        JOptionPane.showMessageDialog(
			        this.parentFrame,
			         langUtil.getString("contact.list.no.messaging.contact.message"),
					 langUtil.getString("contact.list.no.messaging.contact"), JOptionPane.ERROR_MESSAGE);					
				return;
			}
			
			if (id.isAnonymous())
			{
		        Object[] options = 
		        	{ 
		        		langUtil.getString("button.option.yes"),
		        		langUtil.getString("button.option.no")
		        	};
				int reply = JOptionPane.showOptionDialog(
			        this.parentFrame, 
			        langUtil.getString("contact.list.confirm.anonymous", id.getDiplayString()),
					langUtil.getString("contact.list.are.you.sure"), 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					JOptionPane.NO_OPTION);
			        
			    if (reply == JOptionPane.NO_OPTION) 
			    {
			      	return;
			    }
			}
			
			this.parent.sendIdentityMessageTo(id);
			
		} catch (Exception ioe)
		{
			Log.error("Unexpected error: ", ioe);
			JContactListPanel.this.errorReporter.reportError(ioe, false);
		}
	}
	
	
	public void reloadMessagingIdentities()
		throws IOException
	{
		list.setIdentities(this.mesagingStorage.getContactIdentities(true));
		list.revalidate();
	}
	
	
	public int getNumberOfContacts()
	{
		return list.getModel().getSize();
	}
	
	
	// Null if nothing selected
	public MessagingIdentity getSelectedContact()
	{
		return this.list.getSelectedValue();
	}
	
	
	private static class ContactList
		extends JList<MessagingIdentity>
	{
		ImageIcon contactBlackIcon;
		ImageIcon contactGroupBlackIcon;
		ZcashJLabel    renderer;
		private Color backGroundColor = ZcashXUI.startup;
		
		public ContactList()
		{
			super();
			this.setBackground(backGroundColor);
			
			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
	        URL iconUrl = this.getClass().getClassLoader().getResource("images/contact-black.png");
	        contactBlackIcon = new ImageIcon(iconUrl);
	        URL groupIconUrl = this.getClass().getClassLoader().getResource("images/contact-group-black.png");
	        contactGroupBlackIcon = new ImageIcon(groupIconUrl);
	        
	        renderer = new ZcashJLabel();
	        renderer.setOpaque(true);
		}
		
		
		public void setIdentities(List<MessagingIdentity> identities)
		{
			List<MessagingIdentity> localIdentities = new ArrayList<MessagingIdentity>();
			localIdentities.addAll(identities);
			
			Collections.sort(
				localIdentities,
				new Comparator<MessagingIdentity>() 
				{ 
					@Override
					public int compare(MessagingIdentity o1, MessagingIdentity o2) 
					{
						if (o1.isGroup() != o2.isGroup())
						{
							return o1.isGroup() ? -1 : +1;
						} else
						{						
							return o1.getDiplayString().toUpperCase().compareTo(
								   o2.getDiplayString().toUpperCase());
						}
					}
				}
			);
			
			DefaultListModel<MessagingIdentity> newModel = new DefaultListModel<MessagingIdentity>();
			for (MessagingIdentity id : localIdentities)
			{
				newModel.addElement(id);
			}
			
			this.setModel(newModel);
		}
		
		
		@Override
		public ListCellRenderer<MessagingIdentity> getCellRenderer() 
		{
			return new ListCellRenderer<MessagingIdentity>() 
			{
				@Override
				public Component getListCellRendererComponent(JList<? extends MessagingIdentity> list,
						MessagingIdentity id, int index, boolean isSelected, boolean cellHasFocus) 
				{					
					renderer.setText(id.getDiplayString());
					if (!id.isGroup())
					{
						renderer.setIcon(contactBlackIcon);
					} else
					{
						renderer.setIcon(contactGroupBlackIcon);
					}
					
					if (isSelected) 
					{
						renderer.setBackground(list.getSelectionBackground());
					} else 
					{
						// TODO: list background issues on Linux - if used directly
						renderer.setBackground(new Color(list.getBackground().getRGB()));  
					}
					
					return renderer;
				}
			};
		}
	} // End private static class ContactList
	
} // End public class JContactListPanel
