import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Timer;

public class TetrisController{

	private static final int POSITIONS_SIZE = 4;
	private static final int BOARD_ROW_SIZE = 24;
	private static final int BOARD_COL_SIZE = 14;
	
	public static final int MIN_SPEED_LEVEL = 1;
	public static final int MAX_SPEED_LEVEL = 10;
	private Tetris.BoardType board[][];
	private TetrisView block;
	private int countDeletedLines;
	private int speedLevel;
	int colorIndex = 0; 

	public TetrisController(int newSpeedLevel) {
		
		board = new Tetris.BoardType[BOARD_ROW_SIZE][BOARD_COL_SIZE];
		for (int i = 0; i < BOARD_ROW_SIZE; i++) {
			
			Arrays.fill(board[i], Tetris.BoardType.EMPTY);
		}
		clearBoard();
		block = new TetrisView(null);
		countDeletedLines = 0;
		speedLevel = newSpeedLevel;
	}

	public Tetris.BoardType checkValidPosition(int direction) {
		
		TetrisView temp = new TetrisView();
		temp.copyOf(block);
		temp.move(direction);
		for (int i = 0; i < POSITIONS_SIZE; i++) {
			
			int x = temp.getPositions()[temp.getDirection()][i].x;
			int y = temp.getPositions()[temp.getDirection()][i].y;

			if (x <= 0) {
				
				return Tetris.BoardType.TOP_WALL;
			}

			if (board[x][y] != Tetris.BoardType.EMPTY && board[x][y] != Tetris.BoardType.MOVING_BLOCK) {
				
				return board[x][y];
			}
		}
		return Tetris.BoardType.EMPTY;
	}

	public void changeByDirection(int direction) {
		
		int tempDirection = Tetris.Direction.DOWN;
		Tetris.BoardType tempCheckResult = Tetris.BoardType.EMPTY;
		clearBoard();
		Tetris.BoardType checkResult = checkValidPosition(direction);
		if (checkResult == Tetris.BoardType.EMPTY) {
			
			block.move(direction);
		} 
		
		else {
			
			if (direction == Tetris.Direction.UP) {
				
				switch (checkResult) {
				
				case TOP_WALL:
					tempDirection = Tetris.Direction.DOWN;
					tempCheckResult = Tetris.BoardType.TOP_WALL;
					break;
				case RIGHT_WALL:
					tempDirection = Tetris.Direction.LEFT;
					tempCheckResult = Tetris.BoardType.RIGHT_WALL;
					break;
				case LEFT_WALL:
					tempDirection = Tetris.Direction.RIGHT;
					tempCheckResult = Tetris.BoardType.LEFT_WALL;
					break;
				default:
					break;
				}
				do {
					
					block.move(tempDirection);
				} while (checkValidPosition(direction) == tempCheckResult);
				block.move(direction);
			}
		}
		changeByStatus(Tetris.BoardType.MOVING_BLOCK);
	}

	public void changeByAuto() {
		
		changeByDirection(Tetris.Direction.DOWN);
	}

	public void processDirectDown() {
		
		while (!isReachedToBottom()) {
			
			changeByDirection(Tetris.Direction.DOWN);
		}
	}

	public void processDeletingLines(Graphics graphics) {
		
		Color highlightingColors[] = { Color.GRAY, Color.WHITE };
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		searchLineIndexes(indexes);
		if (indexes.size() > 0) {
			
			Timer timer = new Timer(10,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							highlightLinesToDelete(graphics,
									highlightingColors[colorIndex], indexes);
							colorIndex = 1 - colorIndex;
						}
					});
			timer.start();
			try {
				
				Thread.sleep(10 * 40);
			} catch (InterruptedException e1) {

			}
			timer.stop();
			deleteLines(indexes);
			for (int i = speedLevel; i <= countDeletedLines / 3; i++) {
				
				upSpeedLevel();
			}
		}
	}

	public boolean isReachedToBottom() {
		
		for (int i = 0; i < POSITIONS_SIZE; i++) {
			
			int x = block.getPositions()[block.getDirection()][i].x;
			int y = block.getPositions()[block.getDirection()][i].y;
			if (board[x + 1][y] != Tetris.BoardType.EMPTY && board[x + 1][y] != Tetris.BoardType.MOVING_BLOCK) {
				
				return true;
			}
		}
		return false;
	}

	public Tetris.Status processReachedCase() {
		
		changeByStatus(Tetris.BoardType.DROPPED_BLOCK);
		block = new TetrisView(block);
		if (isReachedToBottom()) {
			
			return Tetris.Status.END;
		} else {
			return Tetris.Status.PLAYING;
		}
	}

	public void sleep() {
		try {
			Thread.sleep(getDown());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void print(Graphics graphics) {
		int x;
		int y = 60;
		for (int i = 0; i < BOARD_ROW_SIZE; i++) {
			x = 30;
			for (int j = 0; j < BOARD_COL_SIZE; j++) {
				switch (board[i][j]) {
				case LEFT_TOP_EDGE:
				case RIGHT_TOP_EDGE:
				case LEFT_BOTTOM_EDGE:
				case RIGHT_BOTTOM_EDGE:
				case LEFT_WALL:
				case RIGHT_WALL:
				case TOP_WALL:
				case BOTTOM_WALL:
					graphics.fill3DRect(x, y, 25, 25, true);
					break;
				case EMPTY:
					graphics.draw3DRect(x, y, 25, 25, true);
					break;
				case MOVING_BLOCK:
					graphics.setColor(Tetris.COLORS[block.getColor()]);
					graphics.fill3DRect(x, y, 25, 25, true);
					graphics.setColor(Color.BLACK);
					break;
				case DROPPED_BLOCK:
					graphics.setColor(Color.GRAY);
					graphics.fill3DRect(x, y, 25, 25, true);
					graphics.setColor(Color.BLACK);
					break;
				}
				x = x + 25;
			}
			y = y + 25;
		}
		x = 460;
		y = 130;
		Font font = graphics.getFont();
		graphics.setFont(new Font(font.getName(), Font.BOLD, 20));
		graphics.drawString("********* Tetris *********", x, y);
		y = y + 30;
		graphics.drawString("[" + speedLevel + " level / " + countDeletedLines + " lines deleted]", x, y);
		y = y + 80;
		graphics.drawString("[Key Description]", x, y);
		y = y + 30;
		graphics.drawString("←: move left", x, y);
		y = y + 30;
		graphics.drawString("→ : move right", x, y);
		y = y + 30;
		graphics.drawString("↓ : move down", x, y);
		y = y + 30;
		graphics.drawString("↑ : rotate", x, y);
		y = y + 30;
		graphics.drawString("SpaceBar : direct down", x, y);
		block.printNext(graphics, x, y + 80);
	}

	public void setBoard(Tetris.BoardType[][] newBoard) {
		
		board = newBoard;
	}

	public Tetris.BoardType[][] getBoard() {
		
		return board;
	}

	public void setBlock(TetrisView newBlock) {
		
		block = newBlock;
	}

	public TetrisView getBlock() {
		
		return block;
	}

	public void setDeletedLineCount(int deletedLineCount) {
		
		countDeletedLines = deletedLineCount;
	}

	public int getDeletedLineCount() {
		
		return countDeletedLines;
	}

	public void setSpeedLevel(int newSpeedLevel) {
		
		speedLevel = newSpeedLevel;
	}

	public int getSpeedLevel() {
		
		return speedLevel;
	}

	public long getDown() {
		
		long startingSpeed = 350;
		for (int i = MIN_SPEED_LEVEL; i < speedLevel; i++) {
			
			if (i < MAX_SPEED_LEVEL / 2) {
				
				startingSpeed = startingSpeed - 35;
			} 
			
			else {
				
				startingSpeed = startingSpeed - (35 / 5);
			}
		}
		return startingSpeed;
	}

	private void clearBoard() {
		
		for (int i = 0; i < BOARD_ROW_SIZE; i++) {
			
			board[i][0] = Tetris.BoardType.LEFT_WALL;
			board[i][BOARD_COL_SIZE - 1] = Tetris.BoardType.RIGHT_WALL;
		}
		for (int i = 0; i < BOARD_COL_SIZE; i++) {
			
			board[0][i] = Tetris.BoardType.TOP_WALL;
			board[BOARD_ROW_SIZE - 1][i] = Tetris.BoardType.BOTTOM_WALL;
		}
		for (int i = 1; i < BOARD_ROW_SIZE - 1; i++) {
			
			for (int j = 1; j < BOARD_COL_SIZE - 1; j++) {
				
				if (board[i][j] != Tetris.BoardType.DROPPED_BLOCK) {
					
					board[i][j] = Tetris.BoardType.EMPTY;
				}
			}
		}
		board[0][0] = Tetris.BoardType.LEFT_TOP_EDGE;
		board[0][BOARD_COL_SIZE - 1] = Tetris.BoardType.RIGHT_TOP_EDGE;
		board[BOARD_ROW_SIZE - 1][0] = Tetris.BoardType.LEFT_BOTTOM_EDGE;
		board[BOARD_ROW_SIZE - 1][BOARD_COL_SIZE - 1] = Tetris.BoardType.RIGHT_BOTTOM_EDGE;
	}

	private void changeByStatus(Tetris.BoardType status) {
		
		for (int i = 0; i < POSITIONS_SIZE; i++) {
			
			int x = block.getPositions()[block.getDirection()][i].x;
			int y = block.getPositions()[block.getDirection()][i].y;
			board[x][y] = status;
		}
	}

	private void upSpeedLevel() {
		
		if (speedLevel < MAX_SPEED_LEVEL) {
			
			speedLevel++;
		}
	}

	private void searchLineIndexes(ArrayList<Integer> indexes) {
		
		indexes.clear();
		for (int i = 1; i < BOARD_ROW_SIZE - 1; i++) {
			
			boolean toDelete = true;
			for (int j = 1; j < BOARD_COL_SIZE - 1; j++) {
				
				if (board[i][j] != Tetris.BoardType.DROPPED_BLOCK) {
					
					toDelete = false;
					break;
				}
			}
			
			if (toDelete) {
	
				indexes.add(i);
			}
		}
	}

	private void deleteLines(ArrayList<Integer> indexes) {
		
		int k = BOARD_ROW_SIZE - 2;
		Tetris.BoardType[][] temp = new Tetris.BoardType[BOARD_ROW_SIZE][BOARD_COL_SIZE];
		for (int i = 0; i < BOARD_ROW_SIZE; i++) {
			
			Arrays.fill(temp[i], Tetris.BoardType.EMPTY);
		}
		for (int i = BOARD_ROW_SIZE - 2; i > 0; i--) {
			
			boolean toDelete = false;
			for (int j = 0; j < indexes.size(); j++) {
				
				if (i == indexes.get(j)) {
					
					toDelete = true;
					break;
				}
			}
			if (!toDelete) {
				
				for (int j = 0; j < BOARD_COL_SIZE; j++) {
					
					temp[k][j] = board[i][j];
				}
				k--;
			}
		}
		
		for (int i = 1; i < BOARD_ROW_SIZE - 1; i++) {
			
			for (int j = 1; j < BOARD_COL_SIZE - 1; j++) {
				
				board[i][j] = temp[i][j];
			}
		}
		countDeletedLines = countDeletedLines + indexes.size();
	}

	private void highlightLinesToDelete(Graphics graphics, Color color, ArrayList<Integer> indexes) {
		
		graphics.setColor(color);
		int x = 55;
		int y = 60 + indexes.get(0) * 25;
		graphics.fill3DRect(x, y, 25 * (BOARD_COL_SIZE - 2), 25 * indexes.size(), true);
	}
	
}
