package cbn.webscreen.app;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class LoginWindow extends JFrame{
	
	private JTextField server = new JTextField();
	private JTextField login = new JTextField();
	private JPasswordField password = new JPasswordField();
	private JLabel loginButton = new JLabel("login");
	private JLabel statusText = new JLabel("");

	LoginAction loginListener = null;

	public LoginWindow() {
		setTitle("login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setResizable(false);
		setMinimumSize(new Dimension(320, 240));
		setSize(320, 320);
		setLocationRelativeTo(null);
		setBackground(new Color(0, 0, 0, 0));
		
		@SuppressWarnings("serial")
		JPanel loginPanel = new JPanel() {
			
			@Override
			public void setBackground(Color bg) {
				super.setBackground(new Color(0, 0, 0, 0));
			}
			
			@Override
			public void paintComponent(Graphics g) {
				g.setColor(new Color(0x10, 0x20, 0x40, 0xD0)); 
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(new Color(255, 255, 255, 255));
				super.paintComponent(g);
				
				Rectangle cb = new Rectangle(getWidth()-24, 5, 18, 18);
				int w = cb.width;
				int h = cb.height;
				int x = cb.x;
				int y = cb.y;
				int t = 3;
				
				int[] xs = {x+t,x+w/2,x+w-t,x+w,x+w-(h/2-t),x+w,x+w-t,x+w/2,x+t,x+0,x+h/2-t,x+0};
				int[] ys = {y+0,y+w/2-t,y+0,y+t,y+h/2,y+h-t,y+h,y+h-(w/2-t),y+h,y+h-t,y+h/2,y+t};
				
				g.fillPolygon(xs, ys, 12);
				
			}
		};
		
		LoginWindowMouseListener loginWindowMouseListener = new LoginWindowMouseListener();
		loginPanel.addMouseListener(loginWindowMouseListener);
		loginPanel.addMouseMotionListener(loginWindowMouseListener);
		
		
		loginPanel.setLayout(new GridBagLayout());
		
		server.setBorder(new EmptyBorder(0,8,0,8));
		server.setPreferredSize(new Dimension(200, 32));

		login.setBorder(new EmptyBorder(0,8,0,8));
		login.setPreferredSize(new Dimension(200, 32));
		
		password.setBorder(new EmptyBorder(0,8,0,8));
		password.setPreferredSize(new Dimension(200, 32));

		statusText.setForeground(Color.WHITE);
		
		JLabel serverLabel = new JLabel("server");
		serverLabel.setForeground(Color.WHITE);
		
		JLabel loginLabel = new JLabel("login");
		loginLabel.setForeground(Color.WHITE);
		
		JLabel passwordLabel = new JLabel("password");
		passwordLabel.setForeground(Color.WHITE);
		
		loginButton.setForeground(Color.WHITE);
		loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Font loginfont = new Font(loginButton.getFont().getName(), loginButton.getFont().getStyle(), loginButton.getFont().getSize() + 8); 
		loginButton.setFont(loginfont);
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		loginPanel.add(serverLabel, c);
		
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		loginPanel.add(server, c);
		
		c.gridy = 3;
		c.insets = new Insets(16, 0, 8, 0);
		c.anchor = GridBagConstraints.SOUTH;
		loginPanel.add(statusText, c);
		c.anchor = GridBagConstraints.SOUTHWEST;
		
		c.gridy = 4;
		c.insets = new Insets(8, 0, 0, 0);
		loginPanel.add(loginLabel, c);
		
		c.gridy = 5;
		c.insets = new Insets(0, 0, 0, 0);
		loginPanel.add(login, c);
		
		c.gridy = 6;
		c.insets = new Insets(8, 0, 0, 0);
		loginPanel.add(passwordLabel, c);
		
		c.gridy = 7;
		c.insets = new Insets(0, 0, 0, 0);
		loginPanel.add(password, c);

		c.gridy = 8;
		c.insets = new Insets(8, 0, 0, 0);
		c.anchor = GridBagConstraints.NORTHEAST;
		loginPanel.add(loginButton, c);
		
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (loginListener != null) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							loginListener.login();
						}
					}).start();
				}
			}
			
		});

		add(loginPanel);
		
		login.setText("test");
		server.setText("http://localhost:8080");
		password.setText("test123");
	}
	
	public String getServer() {
		return server.getText();
	}
	
	public String getLogin() {
		return login.getText();
	}

	public String getPassword() {
		return new String(password.getPassword());
	}
	
	public void addLoginListener(LoginAction loginListener) {
		this.loginListener = loginListener;
	}
	
	public interface LoginAction {
		public void login();
	}
	
	public void setStatusText(String text) {
		statusText.setText(text);
		repaint();
	}
	
	public class LoginWindowMouseListener implements MouseListener, MouseMotionListener{

		private JFrame frame = LoginWindow.this;
		
		private Point cursorStartLocation = null;
		private Point frameStartLocation = null;
		private Integer operation = null;
		
		private static final int OPERATION_MOVE = 111;
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (cursorStartLocation == null || frameStartLocation == null || operation == null) {
				return;
			}
			
			int deltaX = MouseInfo.getPointerInfo().getLocation().x - cursorStartLocation.x;
			int deltaY = MouseInfo.getPointerInfo().getLocation().y - cursorStartLocation.y;
			
			if (operation == OPERATION_MOVE) {
				frame.setLocation(frameStartLocation.x + deltaX, frameStartLocation.y + deltaY); 
			} else {
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			cursorStartLocation = MouseInfo.getPointerInfo().getLocation();
			frameStartLocation = frame.getLocation();
			
			Dimension c = e.getComponent().getSize();
			Rectangle closeBtnArea = new Rectangle(e.getComponent().getWidth()-24, 5, 18, 18);
			Rectangle moveArea = new Rectangle(5, 5, c.width - 10, 24);
			
			
			
			if (closeBtnArea.contains(e.getPoint())) {
				//nothing
			} else if (moveArea.contains(e.getPoint())) {
				operation = OPERATION_MOVE;
			} else {
				
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			cursorStartLocation = null;
			frameStartLocation = null;
			operation = null;
			
			Rectangle closeBtnArea = new Rectangle(e.getComponent().getWidth()-24, 5, 18, 18);
			if (closeBtnArea.contains(e.getPoint())) {
				frame.dispose();
			}
		}
		
	}

}
