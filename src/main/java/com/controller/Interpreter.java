package com.controller;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.command.Command;
import com.entity.Board;
import com.entity.Coordinate;
import com.entity.Metadata;
import com.util.Director;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.Codel;
import com.util.Color;
import com.util.ColorNotFoundException;

public class Interpreter {

	// So in the future we can handle pictures whose "pixels" are larger than 1. 
	// Although in theory I think that the size of the pixels shouldn't matter, since everything would scale. Hmm...
	private static int sizePix = 1;

	private static int printLevel = 3;

	public static Director director = new Director();

	private static Stack<Integer> stack = new Stack<>();

	private static boolean end = false;

	private static final Logger log = LoggerFactory.getLogger(Interpreter.class);

  /**
   * Takes in a .ppm file as an argument, and runs it as a Piet program
   * The Piet program is described at http://www.dangermouse.net/esoteric/piet.html
   */
	public static void main(String[] args) throws IOException {

		String log4jConfPath = "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

		log.debug("HEYOYO");

		// red -> dark blue
		// Codel red = new Codel("11", "red");
		// Codel dbl = new Codel("52", "dark blue");
		// stack.push(10); stack.push(100); stack.push(108); stack.push(108); stack.push(3); stack.push(3); stack.push(3); stack.push(2);
		// getCommand(red, dbl);

		String runFile = args[0];
		Scanner sc;

		if(args.length == 0 && runFile == null) {
			System.err.println("Please enter a .ppm file to run.");
			sc = new Scanner(System.in);
			runFile = sc.nextLine();
		}

		// input file is passed in as an argument, guess it could be passed in without the .extension
		File oldfile = new File(runFile);

		// size of each block in the .ppm file, 1 implies 1 pixel -> 1 block, 4 implies 4 pix -> 1 block
		if(args.length > 1) {
			sizePix = Integer.parseInt(args[1]);
		}

		String firstBit = oldfile.getPath().split("\\.")[0];
		File newfile = new File(firstBit + ".txt");

		// here we're renaming it in order to get the colors as an RGB trio
		oldfile.renameTo(newfile);

		// Board board = fileToBoard("tmp.txt");
		Path tmpFile = Paths.get(args[0]);
		Board board = readFile(tmpFile);
		//System.out.println("THE BOARD");
		//System.out.println(board);

		// want our original file back
		File endfile = new File(firstBit + ".ppm");
		newfile.renameTo(endfile);

		// performing the actual file
		readBoard(board);
	}

	/**
	 * Takes in a .ppm file and converts it into a {@link Board}
	 * @param file
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
						Coordinate coordinate = new Coordinate(i, j);
						board.setColor(coordinate, color);
					} catch(ColorNotFoundException e) {
						e.printStackTrace();
					}						
				}
			}

			return board;
		} catch (FileNotFoundException e) {
			//System.out.println(e);
			System.exit(0);
			return null;
		}
	}

	// by now we've read in the file and pass it in as board
	public static void readBoard(Board board) {
		//System.out.println("STARTING TO READ BOARD");

		List<Codel> codels = new ArrayList<>();
		// Codel[] codels = new Codel[2];

		// initiate the very first Codel
		Coordinate first = new Coordinate(0, 0);
		codels.add(new Codel(board, first));
		codels.add(new Codel(board, first));

		log.info(codels.get(0).toString());

		int initSize = 1 + findSizeCodel(board, codels.get(0), first);
		board.setVisitedAll(false);

		codels.get(0).setSize(initSize);
//		codels[0].printCodel();

		// the program will end on it's own (hypothetically)
		while(!end) {

			codels.set(0, codels.get(1));
			codels.remove(1);

			// get the coords of the next Codel
			Coordinate nextBlock = getNextBlock(board, codels.get(0), 0);

			// initiate the newest Codel
			codels.add(new Codel(board, nextBlock));
			initSize = 1 + findSizeCodel(board, codels.get(1), nextBlock);
			board.setVisitedAll(false);

			codels.get(1).setSize(initSize);
			//System.out.println(codels.get(1).toString());
			//System.out.println("DP = " + director.getDP() + " \tCC = " + director.getCC());

			// white codels act as a nop, meaning they don't go into the queue at all
			if(codels.get(1).getColor().equals(Color.WHITE)) {
				//System.out.println("FOUND A WHITE CODEL");
				Coordinate nextNonWhite = getNextBlockWhite(board, codels.get(1), nextBlock, 0);
				codels.set(0, new Codel(board, nextNonWhite));
				codels.get(0).setSize(1+findSizeCodel(board, codels.get(1), nextNonWhite));
				
				board.setVisitedAll(false);
			}
			else { 
				// perform the command given by the two Codel's
				getCommand(codels.get(0), codels.get(1));

				//  shift the new Codel to the old one's spot
				codels.set(0, codels.get(1));

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
	public static int findSizeCodel(Board board, Codel c, Coordinate coord) { 
		// codel is uninitiated, so I need to set its corners
		if(c.getRightTop().getX() == -1) {
			setCorners(c, coord);
		}

		int count = 0;
		board.setVisited(coord, true);

		// 4 directions the program will search in for more of the same color
		Coordinate newCoordinate = new Coordinate();
		for(int i = 0; i < 4; i++)
		{
			if(i == 0)
				newCoordinate = new Coordinate(coord.getX(), coord.getY()-1);
			else if(i == 1)
				newCoordinate = new Coordinate(coord.getX(), coord.getY()+1);
			else if(i == 2)
				newCoordinate = new Coordinate(coord.getX()-1, coord.getY());
			else if(i == 3)
				newCoordinate = new Coordinate(coord.getX()+1, coord.getY());

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
	public static void getCommand(Codel cod1, Codel cod2) {

		Integer hue1 = cod1.getColor().getHue();
		Integer light1 = cod1.getColor().getLight();

		Integer hue2 = cod2.getColor().getHue();
		Integer light2 = cod2.getColor().getLight();
		
		Integer hueChange = hue2 - hue1;
		//System.out.println("change in light: " + light1 + " - " + light2);
		//System.out.println(light2 - light1);
		Integer lightChange = light2 - light1;
		//System.out.println(lightChange);

		while(hueChange < 0) {
			hueChange += 6;
		}
		while(lightChange < 0) {
			lightChange += 3;
		}

		//System.out.println("Got command");
		Command command = Command.getCommand(hueChange, lightChange);
		stack = command.calculate(stack, cod1, cod2);
		//System.out.println("Command calculated");
		return;
	}

	// Piet relies entirely on moving from a corner to a new color, need to set two corners (l, r) for each direction (l, r, u, d)
	public static void setCorners(Codel c, Coordinate coordinate) {

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
	public static boolean inBounds(Board board, Coordinate coordinate) {
		return ((coordinate.getX() >= 0) && coordinate.getX() < board.getSizeRow()) && 
			(coordinate.getY() >= 0 && (coordinate.getY() < board.getSizeCol()));
	}

	// the interpreter moves in a straight line when fed a white block, as opposed to a colored block
	public static Coordinate getNextBlockWhite(Board board, Codel c, Coordinate coordinate, int attempt ) {

		int nextRow = coordinate.getX();
		int nextCol = coordinate.getY();
		Coordinate next = new Coordinate(nextRow, nextCol);


		// moving in a straight line, unless it goes out of bounds or hits a black box
		switch(director.getDP()) {
			case RIGHT:
				while(board.getColor(coordinate) == Color.WHITE) {
					//System.out.println("new val is " + board.getColor(coordinate) + " at " + coordinate.getX() + " " + coordinate.getY());
					if(!inBounds(board, new Coordinate(nextRow, nextCol+1))) {
						//System.out.println("Rotating here @ right: ");
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(board.getColor(new Coordinate(nextRow, nextCol+1)) == Color.BLACK) {
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
					if(!inBounds(board, new Coordinate(nextRow, nextCol-1))) {
						//System.out.println("Rotating here @ left: ");
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(board.getColor(new Coordinate(nextRow, nextCol-1)) == Color.BLACK) {
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
					if(!inBounds(board, new Coordinate(nextRow+1, nextCol))) {
						//System.out.println("Rotating here @ down: ");
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(board.getColor(new Coordinate(nextRow+1, nextCol)) == Color.BLACK) {
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
					if(!inBounds(board, new Coordinate(nextRow-1, nextCol))) {
						//System.out.println("Rotating here @ up: ");
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(board.getColor(new Coordinate(nextRow-1, nextCol)) == Color.BLACK) {
						//System.out.println("Rotating here2 @ up: ");
						director.rotateCC(1);
						director.rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}
					nextRow--;
				}
				break;
		}

		Coordinate nextCoord = new Coordinate(nextRow, nextCol);
		return nextCoord;
	}

	// we're getting the newest codel, from the old one, c
	public static Coordinate getNextBlock(Board board, Codel c, int attempt) {

		//System.out.println("PREVIOUS CODEL: " + c.toString());

		// we've tried every orientation and can't find a new Codel
		//System.out.println("ATTEMPT #" + attempt);
		if(attempt > 8) {
			//System.out.println("DONE");
			end = true;
			//System.out.println("NEW COORD(0, 0)");
			return null;
		}

		Coordinate next = new Coordinate(0, 0);
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