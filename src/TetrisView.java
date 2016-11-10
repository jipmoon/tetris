import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Random;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TetrisView extends JFrame {

	private final Object startMonitor = new Object(); 

	private long currentTime;
	private int startingSpeedLevel;
	private TetrisController tetrisController;
	private Tetris.TypeOfProcess typeOfProcess;
	private int direction;
	private Tetris.Status status;
	private boolean keyPressed;
	private int countAvailableMoves; 
	private static final int BLOCK_SIZE = 7;
	private static final int POSITION_SIZE = 4;
	private Point positions[][];
	private int current;
	private int next;
	private int color;
	
	private static final Point BLOCK_EXAMPLES[][][] = {
			{
					{ new Point(0, 5), new Point(0, 6), new Point(0, 7),
							new Point(0, 8) },
					{ new Point(-1, 6), new Point(0, 6), new Point(1, 6),
							new Point(2, 6) },
					{ new Point(0, 5), new Point(0, 6), new Point(0, 7),
							new Point(0, 8) },
					{ new Point(-1, 6), new Point(0, 6), new Point(1, 6),
							new Point(2, 6) } },
			{
					{ new Point(0, 8), new Point(1, 6), new Point(1, 7),
							new Point(1, 8) },
					{ new Point(-1, 7), new Point(0, 7), new Point(1, 7),
							new Point(1, 8) },
					{ new Point(0, 6), new Point(0, 7), new Point(0, 8),
							new Point(1, 6) },
					{ new Point(-1, 6), new Point(-1, 7), new Point(0, 7),
							new Point(1, 7) } },
			{
					{ new Point(0, 7), new Point(0, 8), new Point(1, 6),
							new Point(1, 7) },
					{ new Point(-1, 6), new Point(0, 6), new Point(0, 7),
							new Point(1, 7) },
					{ new Point(0, 7), new Point(0, 8), new Point(1, 6),
							new Point(1, 7) },
					{ new Point(-1, 6), new Point(0, 6), new Point(0, 7),
							new Point(1, 7) } },
			{
					{ new Point(0, 6), new Point(0, 7), new Point(1, 7),
							new Point(1, 8) },
					{ new Point(-1, 8), new Point(0, 8), new Point(0, 7),
							new Point(1, 7) },
					{ new Point(0, 6), new Point(0, 7), new Point(1, 7),
							new Point(1, 8) },
					{ new Point(-1, 8), new Point(0, 8), new Point(0, 7),
							new Point(1, 7) } },
			{
					{ new Point(0, 7), new Point(1, 6), new Point(1, 7),
							new Point(1, 8) },
					{ new Point(-1, 7), new Point(0, 7), new Point(0, 8),
							new Point(1, 7) },
					{ new Point(0, 6), new Point(0, 7), new Point(0, 8),
							new Point(1, 7) },
					{ new Point(-1, 7), new Point(0, 6), new Point(0, 7),
							new Point(1, 7) } },
			{
					{ new Point(0, 6), new Point(1, 6), new Point(1, 7),
							new Point(1, 8) },
					{ new Point(-1, 8), new Point(-1, 7), new Point(0, 7),
							new Point(1, 7) },
					{ new Point(0, 6), new Point(0, 7), new Point(0, 8),
							new Point(1, 8) },
					{ new Point(-1, 7), new Point(0, 7), new Point(1, 7),
							new Point(1, 6) } },
			{
					{ new Point(0, 6), new Point(0, 7), new Point(1, 6),
							new Point(1, 7) },
					{ new Point(0, 6), new Point(0, 7), new Point(1, 6),
							new Point(1, 7) },
					{ new Point(0, 6), new Point(0, 7), new Point(1, 6),
							new Point(1, 7) },
					{ new Point(0, 6), new Point(0, 7), new Point(1, 6),
							new Point(1, 7) } } };

	public TetrisView(int newStartingSpeedLevel) {
		
		startingSpeedLevel = newStartingSpeedLevel;
		initWholeSetting();
		initMembers();
		setEvents();
	}

	public TetrisView(TetrisView block) {
		
		if (block == null) {
			
			current = new Random().nextInt(BLOCK_SIZE);
		} 
		else {
			
			current = block.getNext();
		}
		positions = new Point[POSITION_SIZE][POSITION_SIZE];
		for (int i = 0; i < POSITION_SIZE; i++) {
			
			for (int j = 0; j < POSITION_SIZE; j++) {
				
				positions[i][j] = new Point(BLOCK_EXAMPLES[current][i][j]);
			}
		}
		next = new Random().nextInt(BLOCK_SIZE);
		direction = Tetris.Direction.UP;
		color = new Random().nextInt(Tetris.COLORS.length);
	}

	public void copyOf(TetrisView src) {
		
		for (int i = 0; i < POSITION_SIZE; i++) {
			
			for (int j = 0; j < POSITION_SIZE; j++) {
				
				positions[i][j] = new Point(src.getPositions()[i][j]);
			}
		}
		current = src.getCurrent();
		next = src.getNext();
		direction = src.getDirection();
	}
	
	public TetrisView() {
		
		positions = new Point[POSITION_SIZE][POSITION_SIZE];
		current = 0;
		next = 0;
		direction = Tetris.Direction.UP;
		color = 0;
	}
	
	public void start() {

		repaint();
	}

	public void process(Tetris.TypeOfProcess type, int direction) {
		
		if (type == Tetris.TypeOfProcess.DIRECTION) {
			
			tetrisController.changeByDirection(direction);
		} 
		
		else if (type == Tetris.TypeOfProcess.DIRECT_DOWN) {
			
			tetrisController.processDirectDown();
		} 
		
		else if (type == Tetris.TypeOfProcess.AUTO) {
			
			tetrisController.changeByAuto();
		}
		
		if (tetrisController.isReachedToBottom()) {
			
			if (countAvailableMoves == 2) {
				
				if (tetrisController.processReachedCase() == Tetris.Status.END) {
					
					end();
				}
				countAvailableMoves = 0;
			} 
			
			else {
				
				countAvailableMoves++;
			}
		}
		repaint();
		tetrisController.processDeletingLines(getGraphics());
	}

	public void end() {
		
		status = Tetris.Status.END;
		JOptionPane.showMessageDialog(null, "               level : "
				+ tetrisController.getSpeedLevel() + "          deleted lines : "
				+ tetrisController.getDeletedLineCount(), "TETRIS GAME",
				JOptionPane.PLAIN_MESSAGE);
		dispose();
	}

	public void pause() {
		status = Tetris.Status.PAUSE;
		JOptionPane.showMessageDialog(null, "  Press 'OK' to continue.",
				"TETRIS - PAUSE", JOptionPane.PLAIN_MESSAGE);
		status = Tetris.Status.PLAYING;
		synchronized (startMonitor) {
			startMonitor.notifyAll();
		}
	}

	@Override
	public void paint(Graphics g) {
		Image buffer = createImage(getWidth(), getHeight());
		Graphics graphics = buffer.getGraphics();
		graphics.setColor(Color.black);
		Font font = graphics.getFont();
		graphics.setFont(new Font(font.getName(), Font.BOLD, 30));
		tetrisController.print(graphics);
		g.drawImage(buffer, 0, 0, this);
	}

	private void initWholeSetting() {
		setTitle("TETRIS - ING...");
		getContentPane().setLayout(null);
		setSize(800, 700);
		setLocation(getCenterPosition(this));
		setResizable(false);
	}

	private void initMembers() {

		tetrisController = new TetrisController(startingSpeedLevel);
		new Thread(new Runnable() {

			@Override
			public void run() {
				start();
				status = Tetris.Status.PLAYING;
				keyPressed = false;
				currentTime = System.currentTimeMillis();
				while (status != Tetris.Status.END) {
					
					if (status == Tetris.Status.PAUSE) {
						
						synchronized (startMonitor) {
							
							try {
								
								startMonitor.wait();
							} 
							
							catch (InterruptedException e) {
								
							}
						}
					}
					typeOfProcess = Tetris.TypeOfProcess.AUTO;
					direction = Tetris.Direction.DOWN;
					
					while (true) {
						
						if (keyPressed) {
							
							keyPressed = false;
							break;
						}
						
						if (!keyPressed && System.currentTimeMillis() - currentTime > getDown()) {
							
							typeOfProcess = Tetris.TypeOfProcess.AUTO;
							direction = Tetris.Direction.DOWN;
							currentTime = System
									.currentTimeMillis();
							break;
						}
						
						try {
							
							Thread.sleep(1);
						} 
						
						catch (InterruptedException e) {
							
							e.printStackTrace();
						}
					}
					process(typeOfProcess, direction);
				}
			}
		}).start();
	}

	private void setEvents() {
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				
				if (e.getKeyCode() == Tetris.KeyCode.UP) {
					
					keyPressed = true;
					typeOfProcess = Tetris.TypeOfProcess.DIRECTION;
					direction = Tetris.Direction.UP;
				} 
				
				else if (e.getKeyCode() == Tetris.KeyCode.DOWN) {
					
					keyPressed = true;
					typeOfProcess = Tetris.TypeOfProcess.DIRECTION;
					direction = Tetris.Direction.DOWN;
					currentTime = System.currentTimeMillis();
				} 
				
				else if (e.getKeyCode() == Tetris.KeyCode.LEFT) {
					
					keyPressed = true;
					typeOfProcess = Tetris.TypeOfProcess.DIRECTION;
					direction = Tetris.Direction.LEFT;
				}
				
				else if (e.getKeyCode() == Tetris.KeyCode.RIGHT) {
					
					keyPressed = true;
					typeOfProcess = Tetris.TypeOfProcess.DIRECTION;
					direction = Tetris.Direction.RIGHT;
				} 
				
				else if (e.getKeyCode() == Tetris.KeyCode.SPACE_BAR) {
					
					keyPressed = true;
					typeOfProcess = Tetris.TypeOfProcess.DIRECT_DOWN;
					currentTime = System.currentTimeMillis();
				} 
				
				else if (e.getKeyCode() == Tetris.KeyCode.ESC) {
					
					keyPressed = true;
					typeOfProcess = Tetris.TypeOfProcess.AUTO;
					currentTime = System.currentTimeMillis();
					pause();
				}
			}
		});
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				
				end();
			}
		});
	}

	public long getDown() {
		
		return tetrisController.getDown();
	}
	
	public void move(int direction) {
		
		switch (direction) {
		
		case Tetris.Direction.LEFT:
			moveToLeft();
			break;
		case Tetris.Direction.RIGHT:
			moveToRight();
			break;
		case Tetris.Direction.DOWN:
			moveToDown();
			break;
		case Tetris.Direction.UP:
			rotateRight();
			break;
		}
	}

	public void printNext(Graphics graphics, int x, int y) {
		
		graphics.drawString("[Next Block]", x, y);
		y += 30;
		graphics.setColor(Color.LIGHT_GRAY);
		switch (next) {
		case 0:
			graphics.fill3DRect(x, y, 20, 20, true);
			graphics.fill3DRect(x + 20, y, 20, 20, true);
			graphics.fill3DRect(x + 40, y, 20, 20, true);
			graphics.fill3DRect(x + 60, y, 20, 20, true);
			break;
		case 1:
			graphics.fill3DRect(x + 40, y, 20, 20, true);
			graphics.fill3DRect(x, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 20, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 40, y + 20, 20, 20, true);
			break;
		case 2:
			graphics.fill3DRect(x + 20, y, 20, 20, true);
			graphics.fill3DRect(x + 40, y, 20, 20, true);
			graphics.fill3DRect(x, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 20, y + 20, 20, 20, true);
			break;
		case 3:
			graphics.fill3DRect(x, y, 20, 20, true);
			graphics.fill3DRect(x + 20, y, 20, 20, true);
			graphics.fill3DRect(x + 20, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 40, y + 20, 20, 20, true);
			break;
		case 4:
			graphics.fill3DRect(x + 20, y, 20, 20, true);
			graphics.fill3DRect(x, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 20, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 40, y + 20, 20, 20, true);
			break;
		case 5:
			graphics.fill3DRect(x, y, 20, 20, true);
			graphics.fill3DRect(x, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 20, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 40, y + 20, 20, 20, true);
			break;
		case 6:
			graphics.fill3DRect(x, y, 20, 20, true);
			graphics.fill3DRect(x + 20, y, 20, 20, true);
			graphics.fill3DRect(x, y + 20, 20, 20, true);
			graphics.fill3DRect(x + 20, y + 20, 20, 20, true);
			break;
		}
		graphics.setColor(Color.BLACK);
	}
	
	public static Point getCenterPosition(Window window) {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = window.getSize();
		int left = (screenSize.width / 2) - (windowSize.width / 2);
		int top = (screenSize.height / 2) - (windowSize.height / 2);
		return new Point(left, top);
	}

	public void setPositions(Point[][] newPositions) {
		
		positions = newPositions;
	}

	public Point[][] getPositions() {
		return positions;
	}

	public void setCurrent(int newCurrent) {
		current = newCurrent;
	}

	public int getCurrent() {
		return current;
	}

	public void setNext(int newNext) {
		
		next = newNext;
	}

	public int getNext() {
		
		return next;
	}

	public void setDirection(int newDirection) {
		
		direction = newDirection;
	}

	public int getDirection() {
		
		return direction;
	}

	public void setColor(int newColor) {
		
		color = newColor;
	}

	public int getColor() {
		
		return color;
	}

	private void moveToDown() {
		
		for (int i = 0; i < POSITION_SIZE; i++) {
			
			for (int j = 0; j < POSITION_SIZE; j++) {
				
				positions[i][j].x++;
			}
		}
	}

	private void moveToLeft() {
		
		for (int i = 0; i < POSITION_SIZE; i++) {
			
			for (int j = 0; j < POSITION_SIZE; j++) {
				
				positions[i][j].y--;
			}
		}
	}

	private void moveToRight() {
		
		for (int i = 0; i < POSITION_SIZE; i++) {
			
			for (int j = 0; j < POSITION_SIZE; j++) {
				
				positions[i][j].y++;
			}
		}
	}

	private void rotateRight() {
		
		direction = (direction + 1) % Tetris.Direction.SIZE;
	}
}
