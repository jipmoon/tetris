import java.awt.AWTException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Tetris extends JDialog{

	public static void main(String[] args) throws AWTException {
		
		new Tetris().setVisible(true);
	}

	private JLabel  setSpeedLevel;
	private JSlider sliderSpeedLevel;
	private JButton startButton;

	private int speedLevel;

	public static final Color[] COLORS = { Color.RED, Color.ORANGE,
			Color.YELLOW, Color.GREEN, Color.BLUE, Color.decode("#4B0082"),
			Color.decode("#800080") };
	
	public Tetris() {
		
		initWholeSetting();
		initMembers();
		setEvents();
	}
	
	public enum Status {
		PLAYING, END, PAUSE
	}

	public interface Direction {
		
		public static final int SIZE = 4;
		public static final int UP = 0;
		public static final int RIGHT = 1;
		public static final int DOWN = 2;
		public static final int LEFT = 3;
	}

	public enum TypeOfProcess {
		
		DIRECTION, DIRECT_DOWN, AUTO
	}

	public interface KeyCode {
		
		public static final int UP = 38;
		public static final int LEFT = 37;
		public static final int RIGHT = 39;
		public static final int DOWN = 40;
		public static final int SPACE_BAR = 32;
		public static final int ESC = 27;
	}

	public enum BoardType {
		
		EMPTY, MOVING_BLOCK, DROPPED_BLOCK, LEFT_WALL, RIGHT_WALL, BOTTOM_WALL, TOP_WALL, LEFT_TOP_EDGE, RIGHT_TOP_EDGE, LEFT_BOTTOM_EDGE, RIGHT_BOTTOM_EDGE
	}

	public interface MainMenu {
		
		public static final int START = 1;
		public static final int RANKING = 2;
		public static final int SETTING = 3;
		public static final int EXIT = 4;
	}

	public interface PauseMenu {
		
		public static final int RESUME = 1;
		public static final int MAIN_MENU = 2;
	}

	public interface EndMenu {
		
		public static final int RANKING = 1;
		public static final int MAIN_MENU = 2;
		public static final int EXIT = 3;
	}

	private void initWholeSetting() {
		
		setTitle("TETRIS GAME");
		getContentPane().setLayout(null);
		setSize(200, 200);
		setLocation(TetrisView.getCenterPosition(this));
		setResizable(false);
	}

	private void initMembers() {
		setSpeedLevel = new JLabel("Set speed level");
		setSpeedLevel.setBounds(56, 25, 86, 15);
		getContentPane().add(setSpeedLevel);
		sliderSpeedLevel = new JSlider();
		sliderSpeedLevel.setMajorTickSpacing(1);
		sliderSpeedLevel.setPaintLabels(true);
		sliderSpeedLevel.setPaintTicks(true);
		sliderSpeedLevel.setPaintTrack(false);
		sliderSpeedLevel.setValue(1);
		sliderSpeedLevel.setMinimum(1);
		sliderSpeedLevel.setMaximum(10);
		sliderSpeedLevel.setBounds(23, 50, 148, 62);
		getContentPane().add(sliderSpeedLevel);
		startButton = new JButton("START");
		startButton.setBounds(23, 122, 148, 40);
		getRootPane().setDefaultButton(startButton); 
		getContentPane().add(startButton);
	}

	private void setEvents() {
		sliderSpeedLevel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!sliderSpeedLevel.getValueIsAdjusting()) {
					speedLevel = (int) sliderSpeedLevel.getValue();
				}
			}
		});
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new TetrisView(speedLevel).setVisible(true);
				dispose();
			}
		});
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}
}
