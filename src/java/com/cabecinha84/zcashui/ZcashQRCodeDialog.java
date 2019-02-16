package com.cabecinha84.zcashui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vaklinov.zcashui.LanguageUtil;
import com.vaklinov.zcashui.Log;

/**
 * Dialog showing QR Code
 */
public class ZcashQRCodeDialog
	extends ZcashJDialog
{
	
	public ZcashQRCodeDialog(String qrCodeMessage, ZcashJFrame parentFrame)
			throws IOException
	{
		LanguageUtil langUtil = LanguageUtil.instance();
		this.setTitle(langUtil.getString("dialog.zcashqrcode.title"));
		this.setSize(255, 300);
	    this.setLocation(100, 100);
	    this.setLocationRelativeTo(parentFrame);
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);


		ZcashJPanel tempPanel = new ZcashJPanel(new BorderLayout(0, 0));
		tempPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(this.generateQRCodeImage(qrCodeMessage)));
		ZcashJLabel qrcode = new ZcashJLabel(new ImageIcon(img));
	    tempPanel.add(qrcode, BorderLayout.CENTER);
		this.getContentPane().add(tempPanel, BorderLayout.CENTER);
		

		ZcashJPanel closePanel = new ZcashJPanel();
		closePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		ZcashJButton closeButon = new ZcashJButton(langUtil.getString("dialog.about.button.close.text"));
		closePanel.add(closeButon);
		
		this.getContentPane().add(closePanel, BorderLayout.SOUTH);
		
		closeButon.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					ZcashQRCodeDialog.this.setVisible(false);
					ZcashQRCodeDialog.this.dispose();
				}
		});
		this.repaint();
		
	}
	
	private byte[] generateQRCodeImage(String myCodeText)
    {
		Log.info("Generating QR Code for text: "+myCodeText);
		int size = 250;
		byte[] pngData = null;
		try {
		
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
		    BitMatrix bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, 250, 250);
		    
		    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		    pngData = pngOutputStream.toByteArray(); 

		}
		catch (Exception ex) {
			Log.warning("Error Generating QR CODE: "+ex.getMessage());
		}
		return pngData;
	}

} 
