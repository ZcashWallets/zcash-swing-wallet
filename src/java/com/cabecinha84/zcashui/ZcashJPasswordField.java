package com.cabecinha84.zcashui;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

import com.vaklinov.zcashui.LanguageUtil;

public class ZcashJPasswordField extends JPasswordField {
	private Color backGroundColor = ZcashXUI.passwordfield;
	private Color textColor = ZcashXUI.text;
	private static LanguageUtil langUtil = LanguageUtil.instance();
	public ZcashJPasswordField() {
		super();
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	public ZcashJPasswordField(Document doc, String txt, int columns) {
		super(doc, txt, columns);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	public ZcashJPasswordField(int columns) {
		super(columns);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	public ZcashJPasswordField(String text, int columns) {
		super(text, columns);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
		this.addClipBoardMenuOptions();
	}

	public ZcashJPasswordField(String text) {
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

		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), DefaultEditorKit.copyAction);
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), DefaultEditorKit.cutAction);
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), DefaultEditorKit.pasteAction);
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), DefaultEditorKit.selectAllAction);

	}
}

