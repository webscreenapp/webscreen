package cbn.webscreen.app.screen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cbn.webscreen.app.Data;

public class ShowCapture extends JFrame implements Runnable{
	
	private JPanel panel = null;  
	
	public ShowCapture() {
		setTitle("debug");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(Data.captureArea.getSize());
		setLocationRelativeTo(null);
		
		panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.drawImage(Data.screenImage, 0, 0, null);
			}
		};
		
		add(panel);
		
		start();
		
		setVisible(true);
	}
	
	private boolean running = false;
	private boolean run = false;
	
	public void start() {
		stop();
		run = true;
		new Thread(this).start();
	}
	
	public void stop() {
		run = false;
		while(running) {
			run = false;
			try { Thread.sleep(8); } catch (InterruptedException e) {}
		}
	}
	
	@Override
	public void run() {
		running = true;
		
		try {

			while (run && running) {
				setSize(Data.captureArea.getSize());
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						panel.repaint();
					}
				}).start();
				
				Thread.sleep(20);
			}

		} catch (InterruptedException ex) {
			
		}
		
		running = false;
	}
}
