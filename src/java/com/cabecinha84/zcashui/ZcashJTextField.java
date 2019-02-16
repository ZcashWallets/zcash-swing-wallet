package com.cabecinha84.zcashui;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

import com.vaklinov.zcashui.LanguageUtil;
import com.vaklinov.zcashui.Log;

public class ZcashJTextField extends JTextField {
	private Color backGroundColor = ZcashXUI.textarea;
	private Color textColor = ZcashXUI.text;
	private static LanguageUtil langUtil = LanguageUtil.instance();
	private boolean keyEventExecuted = false;
	public ZcashJTextField() {
		super();
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	public ZcashJTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	public ZcashJTextField(int columns) {
		super(columns);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	public ZcashJTextField(String text, int columns) {
		super(text, columns);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	public ZcashJTextField(String text) {
		super(text);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	private void addClipBoardMenuOptions() {

 		this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(final MouseEvent e) {
            	int accelaratorKeyMask = Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask();
            	if(SwingUtilities.isRightMouseButton(e)) {
	                if (e.isPopupTrigger()) {
	                    final ZcashJTextField component = (ZcashJTextField)e.getComponent();
	                    final ZcashJPopupMenu menu = new ZcashJPopupMenu();
	                    ZcashJMenuItem item;
	                    item = new ZcashJMenuItem(new DefaultEditorKit.CopyAction());
	                    item.setText(langUtil.getString("copy"));
	                    item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
	                    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, accelaratorKeyMask));
	                    menu.add(item);
	                    item = new ZcashJMenuItem(new DefaultEditorKit.CutAction());
	                    item.setText(langUtil.getString("cut"));
	                    item.setEnabled(component.isEditable() && component.getSelectionStart() != component.getSelectionEnd());
	                    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, accelaratorKeyMask));
	                    menu.add(item);
	                    item = new ZcashJMenuItem(new DefaultEditorKit.PasteAction());
	                    item.setText(langUtil.getString("paste"));
	                    item.setEnabled(component.isEditable());
	                    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, accelaratorKeyMask));
	                    menu.add(item);
	                    menu.show(e.getComponent(), e.getX(), e.getY());
	                }
            	}
            }
        });
 		this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C && !keyEventExecuted) {
                	keyEventExecuted = true;
                	ZcashJTextField textField = (ZcashJTextField) e.getSource();
                	textField.copy();
                	Runnable r = new Runnable() {
            		public void run() {
           	        	try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							Log.error("Error sleeping thread for processkeyAction: "+e.getMessage());
						}
           	        	 processKeyAction();
	           	         }
	           	    };
	
	           	    new Thread(r).start();
                }
				if (e.getKeyCode() == KeyEvent.VK_X && !keyEventExecuted) {
					keyEventExecuted = true;
                	ZcashJTextField textField = (ZcashJTextField) e.getSource();
                	textField.cut();
                	Runnable r = new Runnable() {
            		public void run() {
           	        	try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							Log.error("Error sleeping thread for processkeyAction: "+e.getMessage());
						}
           	        	 processKeyAction();
	           	         }
	           	    };
    	           	new Thread(r).start();
                }
                if (e.getKeyCode() == KeyEvent.VK_V && !keyEventExecuted) {
                	keyEventExecuted = true;
                	ZcashJTextField textField = (ZcashJTextField) e.getSource();
                	textField.paste();
                	Runnable r = new Runnable() {
            		public void run() {
           	        	try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							Log.error("Error sleeping thread for processkeyAction: "+e.getMessage());
						}
           	        	 processKeyAction();
	           	         }
	           	    };
   	
   	           	    new Thread(r).start();
                }
            }
        });
	}
	
	private void processKeyAction() {
		keyEventExecuted = false;
	}
		
}

