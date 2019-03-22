package com.cabecinha84.zcashui;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultEditorKit;

public class ZcashJTable extends JTable {
	private Color backGroundColor = ZcashXUI.table;
	private Color headerBackGroundColor = ZcashXUI.tableHeader;
	private Color textColor = ZcashXUI.text;
	public ZcashJTable() {
		super();
		this.setBackground(backGroundColor);
		this.getTableHeader().setBackground(headerBackGroundColor);
		this.setForeground(textColor);
		this.getTableHeader().setForeground(textColor);
		addClipBoardMenuOptions();
	}

	public ZcashJTable(int numRows, int numColumns) {
		super(numRows, numColumns);
		this.setBackground(backGroundColor);
		this.getTableHeader().setBackground(headerBackGroundColor);
		this.setForeground(textColor);
		this.getTableHeader().setForeground(textColor);
		addClipBoardMenuOptions();
	}

	public ZcashJTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		this.setBackground(backGroundColor);
		this.getTableHeader().setBackground(headerBackGroundColor);
		this.setForeground(textColor);
		this.getTableHeader().setForeground(textColor);
		addClipBoardMenuOptions();
	}

	public ZcashJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		this.setBackground(backGroundColor);
		this.getTableHeader().setBackground(headerBackGroundColor);
		this.setForeground(textColor);
		this.getTableHeader().setForeground(textColor);
		addClipBoardMenuOptions();
	}

	public ZcashJTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		this.setBackground(backGroundColor);
		this.getTableHeader().setBackground(headerBackGroundColor);
		this.setForeground(textColor);
		this.getTableHeader().setForeground(textColor);
		addClipBoardMenuOptions();
	}

	public ZcashJTable(TableModel dm) {
		super(dm);
		this.setBackground(backGroundColor);
		this.getTableHeader().setBackground(headerBackGroundColor);
		this.setForeground(textColor);
		this.getTableHeader().setForeground(textColor);
		addClipBoardMenuOptions();
	}

	public ZcashJTable(Vector<? extends Vector> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
		this.setBackground(backGroundColor);
		this.getTableHeader().setBackground(headerBackGroundColor);
		this.setForeground(textColor);
		this.getTableHeader().setForeground(textColor);
		addClipBoardMenuOptions();	
	}

	private void addClipBoardMenuOptions() {
		this.getInputMap(javax.swing.JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), DefaultEditorKit.copyAction);
		ActionListener listener = new ActionListener() {
		  public void actionPerformed(ActionEvent event) {
			  doCopy();
		  }
		};

		final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

		this.registerKeyboardAction(listener, "Copy", stroke, JComponent.WHEN_FOCUSED);
	}
	
	private void doCopy() {
	    int col = this.getSelectedColumn();
	    int row = this.getSelectedRow();
	    if (col != -1 && row != -1) {
	        Object value = this.getValueAt(row, col);
	        String data;
	        if (value == null) {
	            data = "";
	        } else {
	            data = value.toString();
	        }

	        final StringSelection selection = new StringSelection(data);     

	        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboard.setContents(selection, selection);
	    }
	}
}

