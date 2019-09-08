package com.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import com.command.Command;
import com.director.Director;
import com.entity.Board;
import com.entity.Codel;
import com.entity.Metadata;
import com.util.Block;
import com.util.Color;
import com.util.ColorNotFoundException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Interpreter {

	// So in the future we can handle pictures whose "pixels" are larger than 1. 
	// Although in theory I think that the size of the pixels shouldn't matter, since everything would scale. Hmm...
	private static int sizePix = 1;

	private static int printLevel = 3;

	public static Director director = new Director();

	/**
	 * Piet uses a stack for storage of all data values. Data values exist only as integers, though they may be read in or printed as 
	 *   Unicode character values wi1th appropriate commands.
	 * The stack is nottionally infinitely deep, but implementations may elect to provide a finite maximum stack size.
	 * If a finite stack overflows, it should be treated as a runtime error, and handling this will be implementation dependent.
	 */
	private static Stack<Integer> stack = new Stack<>();

	private static boolean end = false;

	private static final Logger log = LoggerFactory.getLogger(Interpreter.class);

    /**
	 * Takes in a .ppm file as an argument, and runs it as a Piet program
     * The Piet program is described at http://www.dangermouse.net/esoteric/piet.html 
	 * 
     * @param args
	 *   args[0] - the file to run
     * @throws IOException
     */
	public static void main(String[] args) throws IOException {

		String log4jConfPath = "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

		String runFile = null;
		Scanner sc;

		if(args.length > 0) {
			runFile = args[0];
		} else if(runFile == null) {
			System.err.println("Please enter a .ppm file to run.");
			sc = new Scanner(System.in);
			runFile = sc.nextLine();
		}

		// Board board = fileToBoard("tmp.txt");
		Path tmpFile = Paths.get(runFile);
		Board board = readFile(tmpFile);
		
		log.debug("THE BOARD");
		log.debug(board.toString());

		// performing the actual file
		readBoard(board);
	}

	/**
	 * Takes in a .ppm file and converts it into a {@link Board}
	 * @param path - path to the file to be read
	 */
	public static Board readFile(Path path) {
		Board board;

		try (Scanner sc = new Scanner(new File(path.toString()))) {

			Metadata meta = new Metadata();
			meta.setMagicNumber(sc.next());
			meta.setRow(sc.nextInt());
			meta.setColumn(sc.nextInt());
			meta.setMaxVaL(sc.nextInt());

			board = new Board(meta.getRow(), meta.getColumn());

			for(int i = 0; i < meta.getRow(); i++) {
				for(int j = 0; j < meta.getColumn(); j++) {
					Integer red, blue, green;
					
					red = sc.nextInt();
					blue = sc.nextInt();
					green = sc.nextInt();

					try {
						Color color = Color.getColorFromValues(red, blue, green);
						Codel coordinate = new Codel(i, j);
						board.setColor(coordinate, color);
					} catch(ColorNotFoundException e) {
						e.printStackTrace();
					}						
				}
			}

			return board;
		} catch (FileNotFoundException e) {
			log.info(e.toString());
			System.exit(0);
			return null;
		}
	}

	/**
	 * Given a {@link Board} holding a grid of {@link Block}, move through the board and perform the commands
	 * @param board
	 */
	public static void readBoard(Board board) {
		log.debug("STARTING TO READ BOARD");

		List<Block> blocks = new ArrayList<>();

		// initiate the very first Codel
		Codel first = new Codel(0, 0);
		blocks.add(new Block(board, first));
		blocks.add(new Block(board, first));

		log.debug(blocks.get(0).toString());

		int initSize = 1 + findSizeCodel(board, blocks.get(0), first);
		board.setVisitedAll(false);

		blocks.get(0).setSize(initSize);
		log.debug(blocks.get(0).toString());

		// the program will end on it's own (hypothetically)
		while(!end) {

			blocks.set(0, blocks.get(1));
			blocks.remove(1);

			// get the coords of the next Codel
			Codel nextBlock = getNextBlock(board, blocks.get(0), 0);

			// initiate the newest Codel
			blocks.add(new Block(board, nextBlock));
			initSize = 1 + findSizeCodel(board, blocks.get(1), nextBlock);
			board.setVisitedAll(false);

			blocks.get(1).setSize(initSize);
			//System.out.println(codels.get(1).toString());
			//System.out.println("DP = " + director.getDP() + " \tCC = " + director.getCC());

			// white codels act as a nop, meaning they don't go into the queue at all
			if(blocks.get(1).getColor().equals(Color.WHITE)) {
				//System.out.println("FOUND A WHITE CODEL");
				Codel nextNonWhite = getNextBlockWhite(board, blocks.get(1), nextBlock, 0);
				blocks.set(0, new Block(board, nextNonWhite));
				blocks.get(0).setSize(1+findSizeCodel(board, blocks.get(1), nextNonWhite));
				
				board.setVisitedAll(false);
			}
			else { 
				// perform the command given by the two Codel's
				getCommand(blocks.get(0), blocks.get(1));

				//  shift the new Codel to the old one's spot
				blocks.set(0, blocks.get(1));

				//  if I want to print the stack for debuggin and slow down time
				//System.out.println(stack.toString());
				try {
					if(printLevel > 0) {
						Thread.sleep(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//  
	public static int findSizeCodel(Board board, Block c, Codel coord) { 
		// codel is uninitiated, so I need to set its corners
		if(c.getRightTop().getX() == -1) {
			setCorners(c, coord);
		}

		int count = 0;
		board.setVisited(coord, true);

		// 4 directions the program will search in for more of the same color
		Codel newCoordinate = new Codel();
		for(int i = 0; i < 4; i++)
		{
			if(i == 0)
				newCoordinate = new Codel(coord.getX(), coord.getY()-1);
			else if(i == 1)
				newCoordinate = new Codel(coord.getX(), coord.getY()+1);
			else if(i == 2)
				newCoordinate = new Codel(coord.getX()-1, coord.getY());
			else if(i == 3)
				newCoordinate = new Codel(coord.getX()+1, coord.getY());

			// gonna skip this block if it's out of the board or visited
			if(!inBounds(board, newCoordinate)) {
				continue;
			}

			if(board.getVisited(newCoordinate)) {
				continue;
			}

			if(board.getColor(newCoordinate) == c.getColor()) {
				count++;
				count += findSizeCodel(board, c, newCoordinate);
				setCorners(c, newCoordinate);
			}
		}

		return count;
	}

	// Here, col1 represents the last color, col2 is the newest color
	public static void getCommand(Block older, Block newer) {

		Integer hue1 = older.getColor().getHue();
		Integer light1 = older.getColor().getLight();

		Integer hue2 = newer.getColor().getHue();
		Integer light2 = newer.getColor().getLight();
		
		Integer hueChange = hue2 - hue1;
		Integer lightChange = light2 - light1;

		while(hueChange < 0) {
			hueChange += 6;
		}
		while(lightChange < 0) {
			lightChange += 3;
		}

		Command command = Command.getCommand(hueChange, lightChange);
		log.debug(command.getName());
		stack = command.calculate(stack, older, newer);
		return;
	}

	// Piet relies entirely on moving from a corner to a new color, need to set two corners (l, r) for each direction (l, r, u, d)
	public static void setCorners(Block c, Codel coordinate) {

		// potential for new right column value
		if(coordinate.getY() >= c.getRightTop().getY() || c.getRightTop().getY() == -1) {
			// if further right
			if(coordinate.getY() > c.getRightTop().getY() || c.getRightTop().getY() == -1) {
				c.setRightTop(coordinate);
			}
			// if further up
			if(coordinate.getX() < c.getRightTop().getX() || c.getRightTop().getX() == -1) {
				c.setRightTop(coordinate);
			}
			// if further right
			if(coordinate.getY() > c.getRightBottom().getY() || c.getRightBottom().getY() == -1) {
				c.setRightBottom(coordinate);
			}
			// if further down
			if(coordinate.getX() > c.getRightBottom().getX() || c.getRightBottom().getX() == -1) {
				c.setRightBottom(coordinate);
			}
		}

		// potential for new bottom row value
		if(coordinate.getX() >= c.getBottomRight().getX() || c.getBottomRight().getX() == -1) {
			// if further down
			if(coordinate.getX() > c.getBottomRight().getX() || c.getBottomRight().getX() == -1) {
				c.setBottomRight(coordinate);
			}
			// if further right
			if(coordinate.getY() > c.getBottomRight().getY() || c.getBottomRight().getY() == -1) {
				c.setBottomRight(coordinate);
			}
			// if further down
			if(coordinate.getX() > c.getBottomLeft().getX() || c.getBottomLeft().getX() == -1) {
				c.setBottomLeft(coordinate);
			}
			// if further left
			// TODO: investigate why this has three conditions
			if(coordinate.getY() < c.getBottomLeft().getY() || c.getBottomLeft().getY() == -1 || c.getBottomLeft().getX() == -1) {
				c.setBottomLeft(coordinate);
			}
		}

		// potential for new left column value
		if(coordinate.getY() <= c.getLeftBottom().getY() || c.getLeftBottom().getY() == -1) {
			// if further left
			if(coordinate.getY() < c.getLeftBottom().getY() || c.getLeftBottom().getY() == -1) {
				c.setLeftBottom(coordinate);
			}
			// if further down
			if(coordinate.getX() > c.getLeftBottom().getX() || c.getLeftBottom().getX() == -1) {
				c.setLeftBottom(coordinate);
			}
			// if further left
			if(coordinate.getY() < c.getLeftTop().getX() || c.getLeftTop().getX() == -1) {
				c.setLeftTop(coordinate);
			}
			// if further up
			if(coordinate.getX() < c.getLeftTop().getX() || c.getLeftTop().getX() == -1) {
				c.setLeftTop(coordinate);
			}
		}

		// potential for upper-most row
		if(coordinate.getX() <= c.getTopLeft().getX() || c.getTopLeft().getX() == -1) {
			// if further up
			if(coordinate.getX() < c.getTopLeft().getX() || c.getTopLeft().getX() == -1) {
				c.setTopLeft(coordinate);
			}
			// if further left
			if(coordinate.getY() < c.getTopLeft().getY() || c.getTopLeft().getY() == -1) {
				c.setTopLeft(coordinate);
			}
			// if further up
			if(coordinate.getX() < c.getTopRight().getX() || c.getTopLeft().getY() == -1) {
				c.setTopRight(coordinate);
			}
			// if further right
			if(coordinate.getY() > c.getTopRight().getY() || c.getTopRight().getY() == -1) {
				c.setTopRight(coordinate);
			}
		}
	}

	// return whether the point will be in bounds
	public static boolean inBounds(Board board, Codel coordinate) {
		return ((coordinate.getX() >= 0) && coordinate.getX() < board.getSizeRow()) && 
			(coordinate.getY() >= 0 && (coordinate.getY() < board.getSizeCol()));
	}

	// the interpreter moves in a straight line when fed a white block, as opposed to a colored block
	public static Codel getNextBlockWhite(Board board, Block c, Codel coordinate, int attempt ) {

		int nextRow = coordinate.getX();
		int nextCol = coordinate.getY();
		Codel next = new Codel(nextRow, nextCol);


		// moving in a straight line, unless it goes out of bounds or hits a black box
		switch(director.getDP()) {
			case RIGHT:
				while(board.getColor(coordinate) == Color.WHITE) {
					//System.out.println("new val is " + board.getColor(coordinate) + " at " + coordinate.getX() + " " + coordinate.getY());
					if(!inBounds(board, new Codel(nextRow, nextCol+1))) {
						//System.out.println("Rotating here @ right: ");
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(board.getColor(new Codel(nextRow, nextCol+1)) == Color.BLACK) {
						//System.out.println("Rotating here2 @ right: ");
						director.rotateCC(1);
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}

					next.setX(nextCol++);
				}
				break;
			case LEFT:
				while(board.getColor(next) == Color.WHITE) {
					if(!inBounds(board, new Codel(nextRow, nextCol-1))) {
						//System.out.println("Rotating here @ left: ");
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(board.getColor(new Codel(nextRow, nextCol-1)) == Color.BLACK) {
						//System.out.println("Rotating here2 @ left: ");
						director.rotateCC(1);
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}
					nextCol--;
				}
				break;
			case DOWN:
				while(board.getColor(next) == Color.WHITE) {
					if(!inBounds(board, new Codel(nextRow+1, nextCol))) {
						//System.out.println("Rotating here @ down: ");
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(board.getColor(new Codel(nextRow+1, nextCol)) == Color.BLACK) {
						//System.out.println("Rotating here2 @ down: ");
						director.rotateCC(1);
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}
					nextRow++;
				}
				break;
			case UP:
				while(board.getColor(next) == Color.WHITE) {
					if(!inBounds(board, new Codel(nextRow-1, nextCol))) {
						//System.out.println("Rotating here @ up: ");
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(board.getColor(new Codel(nextRow-1, nextCol)) == Color.BLACK) {
						//System.out.println("Rotating here2 @ up: ");
						director.rotateCC(1);
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}
					nextRow--;
				}
				break;
		}

		Codel nextCoord = new Codel(nextRow, nextCol);
		return nextCoord;
	}

	// we're getting the newest codel, from the old one, c
	public static Codel getNextBlock(Board board, Block c, int attempt) {

		//System.out.println("PREVIOUS CODEL: " + c.toString());

		// we've tried every orientation and can't find a new Codel
		//System.out.println("ATTEMPT #" + attempt);
		if(attempt > 8) {
			//System.out.println("DONE");
			end = true;
			//System.out.println("NEW COORD(0, 0)");
			return null;
		}

		Codel next = new Codel(0, 0);
		switch(director.getDP()) {
			// if we're going right
			case RIGHT:
				switch(director.getCC()) {
					// we want to start from the right-top
					case LEFT:
						next = c.getRightTop();
						break;
					// we want to start from the right-bottom
					case RIGHT:
						next = c.getRightBottom();
						break;
				}
				next.setY(next.getY() + 1);

				// TODO: what is attempt doing here? maybe bug
				if(!inBounds(board, next) || board.getColor(next) == Color.BLACK) {
					//System.out.println("Not in bounds and next color is black?");
					//System.out.println("Maybe rotate DP");
					if(attempt % 2 == 0) {
						director.rotateCC(1);
					} else {
						director.rotateDP(1);
					}

					next.setY(next.getY() - 1);
					return getNextBlock(board, c, attempt + 1);
				}
				break;
			case DOWN:
				switch(director.getCC()) {
					case LEFT:
						next = c.getBottomRight();
						break;
					case RIGHT:
						next = c.getBottomLeft();
						break;
				}
				next.setX(next.getX() + 1);

				if(!inBounds(board, next) || board.getColor(next) == Color.BLACK) {
					//System.out.println("Rotating here @ up: ");
					if(attempt % 2 == 0) {
						director.rotateCC(1);
					} else {
						director.rotateDP(1);
					}
					next.setX(next.getX() - 1);
					return getNextBlock(board, c, attempt + 1);

				}
				break;
			case LEFT:
				switch(director.getCC()) {
					case LEFT:
						next = c.getLeftBottom();
						break;
					case RIGHT:
						next = c.getLeftTop();
						break;
				}
				next.setY(next.getY() - 1);

				if(!inBounds(board, next) || board.getColor(next) == Color.BLACK) {
					//System.out.println("Rotating here @ up: ");
					if(attempt % 2 == 0) {
						director.rotateCC(1);
					} else {
						director.rotateDP(1);
					}

					next.setY(next.getY() + 1);
					return getNextBlock(board, c, attempt + 1);
				}
				break;
			case UP:
				switch(director.getCC()) {
					case LEFT:
						next = c.getTopLeft();
						break;
					case RIGHT:
						next = c.getTopRight();
						break;
				}
				next.setX(next.getX() - 1);

				if(!inBounds(board, next) || board.getColor(next) == Color.BLACK) {
					//System.out.println("Rotating here @ up: ");
					if(attempt % 2 == 0) {
						director.rotateCC(1);
					} else {
						director.rotateDP(1);
					}
					next.setX(next.getX() - 1);
					return getNextBlock(board, c, attempt + 1);
				}
				break;
		}

		return next;
	}
}


// thank you, Larry Tesler
// realized that ^ may sound like I was copy+pasting from outside sources, I wasn't
// some of the code (converting numbers -> colors) was repetitive, where I c+p'd a lot
// Larry Tesler was the creator of the copy & paste commands, according to Wikipedia
// all of this code was written by myself (Trent Freeman), and no code was taken from any outside sources