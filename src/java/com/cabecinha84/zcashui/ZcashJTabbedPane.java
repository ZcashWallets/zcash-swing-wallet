package com.cabecinha84.zcashui;

import java.awt.Color;

import javax.swing.JTabbedPane;

public class ZcashJTabbedPane extends JTabbedPane {
	private Color backGroundColor = ZcashXUI.tabbedpane;
	private Color textColor = ZcashXUI.text;
	public ZcashJTabbedPane() {
		super();
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
	}

	public ZcashJTabbedPane(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
	}

	public ZcashJTabbedPane(int tabPlacement) {
		super(tabPlacement);
		this.setBackground(backGroundColor);
		this.setForeground(textColor);
	}
}

