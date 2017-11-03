package cbn.webscreen.app;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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

public class MainWindow extends JFrame {
	private static final long serialVersionUID = -2964241313594793699L;
	
	private JLabel toggleButton = new JLabel("start");
	
	boolean runningState = false;

	ResizeListener resizeListener = null;
	MoveListener moveListener = null;
	ToggleListener toggleListener = null;
	
	public static interface ResizeListener {
		 public void resizeStart();
		 public void resizeStop();
	}

	
	public void setResizeListener(ResizeListener resizeListener) {
		this.resizeListener = resizeListener;
	}
	
	public static interface MoveListener {
		public void moved();
	}
	
	public void setMoveListener(MoveListener moveListener) {
		this.moveListener = moveListener;
	}

	public static interface ToggleListener {
		public void toggle();
	}
	
	public void setToggleListener(ToggleListener toggleListener) {
		this.toggleListener = toggleListener;
	}
	
	public void setToggleButtonText(String text) {
		this.toggleButton.setText(text);
		repaint();
	}
	
	public MainWindow() {
		setTitle("webscreen app 1.0");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setUndecorated(true);
		setMinimumSize(new Dimension(320, 240));
		setSize(640, 480);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setBackground(new Color(0, 0, 0, 0));

		@SuppressWarnings("serial")
		JPanel panel = new JPanel() {
			
			@Override
			public void setBackground(Color bg) {
				super.setBackground(new Color(0, 0, 0, 0));
			}
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(0x10, 0x20, 0x40, 0xD0)); 
				g.fillRect(5, 5, getWidth() - 10, 24);
				for (int i = 0; i < 5; i++) {
					g.drawRect(0 + i, 0 + i, getWidth() - 1 - 2 * i, getHeight() - 1 - 2 * i);
				}
				g.setColor(new Color(255, 255, 255, 255));

				// close button (x shape) 
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
		
		MainWindowMouseListener mainWindowMouseListener = new MainWindowMouseListener();
		
		panel.addMouseListener(mainWindowMouseListener);
		panel.addMouseMotionListener(mainWindowMouseListener);
		
		panel.setLayout(null);
		
		
		toggleButton.setForeground(Color.WHITE);
		toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Font font = new Font(toggleButton.getFont().getName(), toggleButton.getFont().getStyle(), toggleButton.getFont().getSize() + 5); 
		toggleButton.setFont(font);
		toggleButton.setBounds(32, 4, 100, 18);
		
		toggleButton.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (toggleListener != null) {
					toggleListener.toggle();
				}
			}
		});
		
		panel.add(toggleButton);
		
		add(panel);
	}
	
	public Rectangle getCaptureArea() {
//		System.out.println(getLocationOnScreen().x);
//		System.out.println(getLocationOnScreen().y);
//		System.out.println(getSize().width);
//		System.out.println(getSize().height);
		return new Rectangle(getLocationOnScreen(), getSize());
	}
	
	public void setRunningState(boolean runningState) {
		this.runningState = runningState;
		repaint();
	}
	
	
	public class MainWindowMouseListener implements MouseListener, MouseMotionListener{

		public Dimension corner = new Dimension(20, 20); 
		
		private JFrame frame = MainWindow.this;
		
		private Point cursorStartLocation = null;
		private Point frameStartLocation = null;
		private Dimension frameStartSize = null;
		
		private Integer operation = null;
		
		private static final int OPERATION_N_RESIZE = 101;
		private static final int OPERATION_E_RESIZE = 102;
		private static final int OPERATION_S_RESIZE = 103;
		private static final int OPERATION_W_RESIZE = 104;
		private static final int OPERATION_NE_RESIZE = 105;
		private static final int OPERATION_SE_RESIZE = 106;
		private static final int OPERATION_SW_RESIZE = 107;
		private static final int OPERATION_NW_RESIZE = 108;
		private static final int OPERATION_MOVE = 111;
		
		private void startResize() {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					if (resizeListener != null) {
						resizeListener.resizeStart();
					}
				}
			}).start();
		}

		private void stopResize() {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					if (resizeListener != null) {
						resizeListener.resizeStop();
					}
				}
			}).start();
			
		}
		
		private void moved() {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					if (moveListener != null) {
						moveListener.moved();
					}
				}
			}).start();
			
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (cursorStartLocation == null || frameStartLocation == null || operation == null) {
				return;
			}
			
			int deltaX = MouseInfo.getPointerInfo().getLocation().x - cursorStartLocation.x;
			int deltaY = MouseInfo.getPointerInfo().getLocation().y - cursorStartLocation.y;
			
			Dimension min = frame.getMinimumSize();
			
			if (operation == OPERATION_MOVE) {
				frame.setLocation(frameStartLocation.x + deltaX, frameStartLocation.y + deltaY); 
				moved();
			} else if (operation == OPERATION_N_RESIZE) {
				frame.setLocation(frameStartLocation.x, frameStartLocation.y + (deltaY < frameStartSize.height - min.height ? deltaY : frameStartSize.height - min.height));
				frame.setSize(frameStartSize.width, frameStartSize.height - deltaY);
			} else if (operation == OPERATION_E_RESIZE) {
				frame.setSize(frameStartSize.width + deltaX, frameStartSize.height);
			} else if (operation == OPERATION_S_RESIZE) {
				frame.setSize(frameStartSize.width, frameStartSize.height + deltaY);
			} else if (operation == OPERATION_W_RESIZE) {
				frame.setLocation(frameStartLocation.x + (deltaX < frameStartSize.width - min.width ? deltaX : frameStartSize.width - min.width), frameStartLocation.y);
				frame.setSize(frameStartSize.width - deltaX, frameStartSize.height);
			} else if (operation == OPERATION_NE_RESIZE) {
				frame.setLocation(frameStartLocation.x, frameStartLocation.y + (deltaY < frameStartSize.height - min.height ? deltaY : frameStartSize.height - min.height));
				frame.setSize(frameStartSize.width + deltaX, frameStartSize.height - deltaY);
			} else if (operation == OPERATION_SE_RESIZE) {
				frame.setSize(frameStartSize.width + deltaX, frameStartSize.height + deltaY);
			} else if (operation == OPERATION_SW_RESIZE) {
				frame.setLocation(frameStartLocation.x + (deltaX < frameStartSize.width - min.width ? deltaX : frameStartSize.width - min.width), frameStartLocation.y);
				frame.setSize(frameStartSize.width - deltaX, frameStartSize.height + deltaY);
			} else if (operation == OPERATION_NW_RESIZE) {
				frame.setLocation(frameStartLocation.x + (deltaX < frameStartSize.width - min.width ? deltaX : frameStartSize.width - min.width), frameStartLocation.y + (deltaY < frameStartSize.height - min.height ? deltaY : frameStartSize.height - min.height));
				frame.setSize(frameStartSize.width - deltaX, frameStartSize.height - deltaY);
			} else {
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			Dimension c = e.getComponent().getSize();
			
			Rectangle moveArea = new Rectangle(5, 5, c.width - 10, 24);
			Rectangle nArea = new Rectangle(corner.width, 0, c.width - (2 * corner.width), 5);
			Rectangle eArea = new Rectangle(c.width - 5 , corner.height, 5, c.height - (2 * corner.height));
			Rectangle sArea = new Rectangle(corner.width , c.height - 5, c.width - (2 * corner.width), 5);
			Rectangle wArea = new Rectangle(0, corner.height, 5, c.height - (2 * corner.height));
			Rectangle neArea = new Rectangle(c.width - corner.width, 0, corner.width, corner.height);
			Rectangle seArea = new Rectangle(c.width - corner.width, c.height - corner.height, corner.width, corner.height);
			Rectangle swArea = new Rectangle(0, c.height - corner.height, corner.width, corner.height);
			Rectangle nwArea = new Rectangle(0, 0, corner.width, corner.height);

			if (moveArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} else if (nArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			} else if (eArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			} else if (sArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			} else if (wArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (neArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			} else if (seArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			} else if (swArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			} else if (nwArea.contains(e.getPoint())) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			} else {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		}

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
			frameStartSize = frame.getSize();
			
			Dimension c = e.getComponent().getSize();
//			Rectangle toggleBtnArea = new Rectangle(8, 5, 18, 18);
			Rectangle closeBtnArea = new Rectangle(e.getComponent().getWidth()-24, 5, 18, 18);
			Rectangle moveArea = new Rectangle(5, 5, c.width - 10, 24);
			Rectangle nArea = new Rectangle(corner.width, 0, c.width - (2 * corner.width), 5);
			Rectangle eArea = new Rectangle(c.width - 5 , corner.height, 5, c.height - (2 * corner.height));
			Rectangle sArea = new Rectangle(corner.width , c.height - 5, c.width - (2 * corner.width), 5);
			Rectangle wArea = new Rectangle(0, corner.height, 5, c.height - (2 * corner.height));
			Rectangle neArea = new Rectangle(c.width - corner.width, 0, corner.width, corner.height);
			Rectangle seArea = new Rectangle(c.width - corner.width, c.height - corner.height, corner.width, corner.height);
			Rectangle swArea = new Rectangle(0, c.height - corner.height, corner.width, corner.height);
			Rectangle nwArea = new Rectangle(0, 0, corner.width, corner.height);
			
			if (closeBtnArea.contains(e.getPoint())) {
				//nothing
//			} else if (toggleBtnArea.contains(e.getPoint())) {
//				//nothing
			} else if (moveArea.contains(e.getPoint())) {
				operation = OPERATION_MOVE;
			} else if (nArea.contains(e.getPoint())) {
				operation = OPERATION_N_RESIZE;
				startResize();
			} else if (eArea.contains(e.getPoint())) {
				operation = OPERATION_E_RESIZE;
				startResize();
			} else if (sArea.contains(e.getPoint())) {
				operation = OPERATION_S_RESIZE;
				startResize();
			} else if (wArea.contains(e.getPoint())) {
				operation = OPERATION_W_RESIZE;
				startResize();
			} else if (neArea.contains(e.getPoint())) {
				operation = OPERATION_NE_RESIZE;
				startResize();
			} else if (seArea.contains(e.getPoint())) {
				operation = OPERATION_SE_RESIZE;
				startResize();
			} else if (swArea.contains(e.getPoint())) {
				operation = OPERATION_SW_RESIZE;
				startResize();
			} else if (nwArea.contains(e.getPoint())) {
				operation = OPERATION_NW_RESIZE;
				startResize();
			} else {
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			if (operation != null && (
				operation == OPERATION_N_RESIZE ||
			    operation == OPERATION_E_RESIZE ||
			    operation == OPERATION_S_RESIZE ||
			    operation == OPERATION_W_RESIZE ||
			    operation == OPERATION_NE_RESIZE ||
			    operation == OPERATION_SE_RESIZE ||
			    operation == OPERATION_SW_RESIZE ||
			    operation == OPERATION_NW_RESIZE )) {
				stopResize();
			}
			
			cursorStartLocation = null;
			frameStartLocation = null;
			frameStartSize = null;
			operation = null;
			
			Rectangle closeBtnArea = new Rectangle(e.getComponent().getWidth()-24, 5, 18, 18);
			if (closeBtnArea.contains(e.getPoint())) {
				frame.dispose();
			}
//			Rectangle toggleBtnArea = new Rectangle(8, 5, 18, 18);
//			if (toggleBtnArea.contains(e.getPoint())) {
////				toggle();
//			}
		}
		
//		public abstract void toggle();
	}

	
}
