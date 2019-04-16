// Code was originally written by developer - https://github.com/zlatinb
// Taken from repository https://github.com/zlatinb/Zcash-swing-wallet-ui under an MIT licemse
package com.vaklinov.zcashui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.cabecinha84.zcashui.ZcashJButton;
import com.cabecinha84.zcashui.ZcashJLabel;
import com.cabecinha84.zcashui.ZcashJMenuItem;
import com.cabecinha84.zcashui.ZcashJPanel;
import com.cabecinha84.zcashui.ZcashJPopupMenu;
import com.cabecinha84.zcashui.ZcashJScrollPane;
import com.cabecinha84.zcashui.ZcashJTabbedPane;
import com.cabecinha84.zcashui.ZcashJTable;
import com.cabecinha84.zcashui.ZcashJTextField;

public class AddressBookPanel extends ZcashJPanel {
    
    private static class AddressBookEntry {
        final String name,address;
        AddressBookEntry(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }

    private LanguageUtil langUtil;

    private final List<AddressBookEntry> entries =
            new ArrayList<>();

    private final Set<String> names = new HashSet<>();
    
    private ZcashJTable table;
    
    private ZcashJButton sendCashButton, deleteContactButton,copyToClipboardButton;
    
    private final SendCashPanel sendCashPanel;
    private final ZcashJTabbedPane tabs;
    
    private LabelStorage labelStorage;
    
    private ZcashJPanel buildButtonsPanel() {
        ZcashJPanel panel = new ZcashJPanel();
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
        
        ZcashJButton newContactButton = new ZcashJButton(langUtil.getString("panel.address.book.new.contact.button.text"));
        newContactButton.addActionListener(new NewContactActionListener());
        panel.add(newContactButton);
                
        sendCashButton = new ZcashJButton(langUtil.getString("panel.address.book.send.zcash.button.text"));
        sendCashButton.addActionListener(new SendCashActionListener());
        sendCashButton.setEnabled(false);
        panel.add(sendCashButton);
        
        copyToClipboardButton = new ZcashJButton(langUtil.getString("panel.address.book.copy.clipboard.button.text"));
        copyToClipboardButton.setEnabled(false);
        copyToClipboardButton.addActionListener(new CopyToClipboardActionListener());
        panel.add(copyToClipboardButton);
        
        deleteContactButton = new ZcashJButton(langUtil.getString("panel.address.book.delete.contact.button.text"));
        deleteContactButton.setEnabled(false);
        deleteContactButton.addActionListener(new DeleteAddressActionListener());
        panel.add(deleteContactButton);
        
        return panel;
    }

    private ZcashJScrollPane buildTablePanel() {
        table = new ZcashJTable(new AddressBookTableModel(),new DefaultTableColumnModel());
        TableColumn nameColumn = new TableColumn(0);
        TableColumn addressColumn = new TableColumn(1);
        table.addColumn(nameColumn);
        table.addColumn(addressColumn);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // one at a time
        table.getSelectionModel().addListSelectionListener(new AddressListSelectionListener());
        table.addMouseListener(new AddressMouseListener());
        
        // TODO: isolate in utility
		TableCellRenderer renderer = table.getCellRenderer(0, 0);
		Component comp = renderer.getTableCellRendererComponent(table, "123", false, false, 0, 0);
		table.setRowHeight(new Double(comp.getPreferredSize().getHeight()).intValue() + 2);
        
        ZcashJScrollPane scrollPane = new ZcashJScrollPane(table);
        return scrollPane;
    }

    public AddressBookPanel(SendCashPanel sendCashPanel, ZcashJTabbedPane tabs, LabelStorage labelStorage) 
    	throws IOException 
    {
    	this.labelStorage = labelStorage;
        this.sendCashPanel = sendCashPanel;
        this.tabs = tabs;
        langUtil = LanguageUtil.instance();
        BoxLayout boxLayout = new BoxLayout(this,BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        add(buildTablePanel());
        add(buildButtonsPanel());
       
        loadEntriesFromDisk();
    }
    
    private void loadEntriesFromDisk() throws IOException {
        File addressBookFile = new File(OSUtil.getSettingsDirectory(),"addressBook.csv");
        if (!addressBookFile.exists())
            return;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(addressBookFile))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                // format is address,name - this way name can contain commas ;-)
                int addressEnd = line.indexOf(',');
                if (addressEnd < 0)
                    throw new IOException(langUtil.getString("panel.address.book.error.corrupted"));
                String address = line.substring(0, addressEnd);
                String name = line.substring(addressEnd + 1);
                if (!names.add(name))
                    continue; // duplicate
                entries.add(new AddressBookEntry(name,address));
            }
        }
        
        Log.info("loaded "+entries.size()+" address book entries");
    }
    
    private void saveEntriesToDisk() {
    	Log.info("Saving "+entries.size()+" addresses");
        try {
            File addressBookFile = new File(OSUtil.getSettingsDirectory(),"addressBook.csv");
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(addressBookFile))) {
                for (AddressBookEntry entry : entries) 
                    printWriter.println(entry.address+","+entry.name);
            }
        } catch (IOException bad) {
        	// TODO: report error to the user!
        	Log.error("Saving Address Book Failed!!!!", bad);
        }
    }
    
    private class DeleteAddressActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            AddressBookEntry entry = entries.get(row);
            entries.remove(row);
            names.remove(entry.name);
            deleteContactButton.setEnabled(false);
            sendCashButton.setEnabled(false);
            copyToClipboardButton.setEnabled(false);
            table.repaint();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    saveEntriesToDisk();
                }
            });
        }
    }
    
    private class CopyToClipboardActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            AddressBookEntry entry = entries.get(row);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(entry.address), null);
        }
    }
    
    private class NewContactActionListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
        	ZcashJPanel myPanel = new ZcashJPanel();
        	myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        	ZcashJTextField name;
        	ZcashJTextField address;
    		addFormField(myPanel, langUtil.getString("panel.address.book.option.pane.new.contact.msg"),  name = new ZcashJTextField(50));
    		addFormField(myPanel, langUtil.getString("panel.address.book.option.pane.new.contact.address"),  address = new ZcashJTextField(50));
    		            
            int result = JOptionPane.showConfirmDialog(AddressBookPanel.this,
            		myPanel,
                    langUtil.getString("panel.address.book.option.pane.new.contact.title"),
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
            	return;
            }
            if (name == null || "".equals(name.getText()) || address == null || "".equals(address.getText()))
                return; // cancelled

            // TODO: check for dupes
            names.add(name.getText());
            
            
            entries.add(new AddressBookEntry(name.getText(),address.getText()));
            
            // Add the address also to the label storage
            try
            {
            	AddressBookPanel.this.labelStorage.setLabel(address.getText(), name.getText());
            } catch (IOException ioe)
            {
            	Log.error("Saving labels from within address book failed!", ioe);
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    table.invalidate();
                    table.revalidate();
                    table.repaint();
                	
                    saveEntriesToDisk();
                }
            });
        }
    }
    
    private class SendCashActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            AddressBookEntry entry = entries.get(row);
            sendCashPanel.prepareForSending(entry.address);
            tabs.setSelectedIndex(3);
        }
    }

    private class AddressMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isConsumed() || (!e.isPopupTrigger()))
                return;

            int row = table.rowAtPoint(e.getPoint());
            int column = table.columnAtPoint(e.getPoint());
            table.changeSelection(row, column, false, false);
            AddressBookEntry entry = entries.get(row);
            
            ZcashJPopupMenu menu = new ZcashJPopupMenu();
            
            ZcashJMenuItem sendCash = new ZcashJMenuItem(langUtil.getString("panel.address.book.menuitem.sendcash.text", entry.name));
            sendCash.addActionListener(new SendCashActionListener());
            menu.add(sendCash);
            
            ZcashJMenuItem copyAddress = new ZcashJMenuItem(langUtil.getString("panel.address.book.menuitem.copy.address.text"));
            copyAddress.addActionListener(new CopyToClipboardActionListener());
            menu.add(copyAddress);
            
            ZcashJMenuItem deleteEntry = new ZcashJMenuItem(langUtil.getString("panel.address.book.menuitem.delete.entry.text", entry.name));
            deleteEntry.addActionListener(new DeleteAddressActionListener());
            menu.add(deleteEntry);
            
            menu.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
            e.consume();
        }
        
        public void mouseReleased(MouseEvent e)
        {
        	if ((!e.isConsumed()) && e.isPopupTrigger())
        	{
        		mousePressed(e);
            }
        }
    }
    
    private class AddressListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int row = table.getSelectedRow();
            if (row < 0) {
                sendCashButton.setEnabled(false);
                deleteContactButton.setEnabled(false);
                copyToClipboardButton.setEnabled(false);
                return;
            }
            String name = entries.get(row).name;
            sendCashButton.setText(langUtil.getString("panel.address.book.button.sendcash.text", name));
            sendCashButton.setEnabled(true);
            deleteContactButton.setText(langUtil.getString("panel.address.book.button.delete.contact.text", name));
            deleteContactButton.setEnabled(true);
            copyToClipboardButton.setEnabled(true);
        }
        
    }
    
    private void addFormField(ZcashJPanel detailsPanel, String name, JComponent field)
	{
		ZcashJPanel tempPanel = new ZcashJPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
		ZcashJLabel tempLabel = new ZcashJLabel(name, JLabel.RIGHT);
		// TODO: hard sizing of labels may not scale!
		final int width = new ZcashJLabel("Sender identification T address:").getPreferredSize().width + 10;
		tempLabel.setPreferredSize(new Dimension(width, tempLabel.getPreferredSize().height));
		tempPanel.add(tempLabel);
		tempPanel.add(field);
		detailsPanel.add(tempPanel);
	}

    private class AddressBookTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return entries.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch(columnIndex) {
            case 0 : return langUtil.getString("panel.address.book.table.name");
            case 1 : return langUtil.getString("panel.address.book.table.address");
            default:
                throw new IllegalArgumentException("invalid column "+columnIndex);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            AddressBookEntry entry = entries.get(rowIndex);
            switch(columnIndex) {
            case 0 : return entry.name;
            case 1 : return entry.address;
            default:
                throw new IllegalArgumentException("bad column "+columnIndex);
            }
        }
    }
}