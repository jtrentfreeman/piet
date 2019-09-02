package com.controller;

import java.io.*;
import java.util.*;

import com.entity.Coordinate;
import com.util.CC;
import com.util.DP;
import com.util.Codel;

public class Interpreter {

	private static int totRow;		// total number of rows
	private static int totCol;		// total number of columns
	private static int sizePix = 1;

	private static int printLevel = 0;

	private static DP dp = DP.RIGHT;
	private static CC cc = CC.LEFT;

	private static Stack<Integer> stack = new Stack<Integer>();

	private static boolean end = false;

  /**
   * Takes in a .ppm file as an argument, and runs it as a Piet program
   * The Piet program is described at http://www.dangermouse.net/esoteric/piet.html
   */
	public static void main(String[] args) throws IOException
	{

//		red -> dark blue
//		Codel red = new Codel("11", "red");
//		Codel dbl = new Codel("52", "dark blue");
//		stack.push(10); stack.push(100); stack.push(108); stack.push(108); stack.push(3); stack.push(3); stack.push(3); stack.push(2);
//		getCommand(red, dbl);

		String runFile = args[0];
		Scanner sc;

		if(args.length == 0) {
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

		// passing in the file name to get parsed
		convertFile(newfile.getPath());

		String[][] board = fileToBoard("tmp.txt");

		// want our original file back
		File endfile = new File(firstBit + ".ppm");
		newfile.renameTo(endfile);

		// performing the actual file
		readBoard(board);

	}

  /**
   * Takes in a .txt file and converts the colors into my specified format
   * Eventually I might omit comments from the hex values too, but right now comments can only exist in the header
   */
	public static void convertFile(String s)
	{
		try {
			File in = new File(s);
			PrintWriter writer = new PrintWriter("tmp.txt", "UTF-8");
			Scanner sc = new Scanner(in);
		
		// skipping the first 3 lines (these contain file information)
		String[] threeLines = new String[3];
		String newLine = "";
		int firstThreeLines = 0;
		while(firstThreeLines < 3)
		{
			newLine = sc.nextLine();
			while(newLine.contains("#"))
				newLine = sc.nextLine();

			threeLines[firstThreeLines] = newLine;
			firstThreeLines++;
		}

		String[] colrow = threeLines[1].split("\\s");
		totCol = Integer.parseInt(colrow[0]);
		totRow = Integer.parseInt(colrow[1]);
		String appendThis = "";

		// getting all the colors, number crunching
		for(int i = 0; i < totCol*totRow; i++)
		{
			if(i % totCol == 0 && i != 0) {
				appendThis += "\n";
			}

			int r, g, b;
			r = sc.nextInt();
			g = sc.nextInt();
			b = sc.nextInt();

			if(r == 0x00) {
				if(g == 0x00) {
					if(b == 0x00)		appendThis += "00 ";
					else if(b == 0xC0)	appendThis += "52 ";
					else if(b == 0xFF)	appendThis += "51 ";
				} else if(g == 0xC0) {
					if(b == 0x00)		appendThis += "32 ";
					else if(b == 0xC0)	appendThis += "42 ";
				}
				else if(g == 0xFF) {
					if(b == 0x00)		appendThis += "31 ";
					else if(b == 0xFF)	appendThis += "41 ";
				}
			}
			else if(r == 0xC0) {
				if(g == 0x00) {
					if(b == 0x00)		appendThis += "12 ";
					else if(b == 0xC0)	appendThis += "62 ";
				}
				else if(g == 0xC0) {
					if(b == 0x00)		appendThis += "22 ";
					else if(b == 0xFF)	appendThis += "50 ";
				}
				else if(g == 0xFF) {
					if(b == 0xC0)		appendThis += "30 ";
					else if(b == 0xFF)	appendThis += "40 ";
				}
			}
			else if(r == 0xFF) {
				if(g == 0x00) {
					if(b == 0x00)		appendThis += "11 ";
					else if(b == 0xFF)	appendThis += "61 ";
				}
				else if(g == 0xC0) {
					if(b == 0xC0)		appendThis += "10 ";
					else if(b == 0xFF)	appendThis += "60 ";
				}
				else if(g == 0xFF) {
					if(b == 0x00)		appendThis += "21 ";
					else if(b == 0xC0)	appendThis += "20 ";
					else if(b == 0xFF)	appendThis += "01 ";
				}
			}
		}

		writer.print(appendThis);
		writer.close();
		sc.close();

	} catch (FileNotFoundException e) {
		System.out.println(e);
	} catch (UnsupportedEncodingException e) {
		System.out.println(e);
	}
	}

	// reading in the file and returning it as a 2d string array
	public static String[][] fileToBoard(String s) throws FileNotFoundException
	{

		File infile = new File(s);
		Scanner s2 = new Scanner(infile);
		totRow = totRow / sizePix;
		String[] wholeLine = new String[totRow];
		for(int i = 0; i < totRow; i ++)
		{
			wholeLine[i] = s2.nextLine();
			String[] brokenLine = wholeLine[i].split(" ");
			totCol = (totCol > brokenLine.length) ? totCol : brokenLine.length;
		}

		String[][] board = new String[totRow][totCol];
		for(int i = 0; i < totRow; i++)
		{
			for(int j = 0; j < totCol; j += sizePix)
			{
				String[] brokenLine = wholeLine[i].split(" ");
				board[i][j] = brokenLine[j];
			}
		}

		s2.close();

		return board;
	}

	// by now we've read in the file and pass it in as board
	public static void readBoard(String[][] board)
	{
		boolean[][] visited = new boolean[totRow][totCol];
		markUnvisited(visited);

		int nextRow = 0;
		int nextCol = 0;

		List<Codel> codels = new ArrayList<>();
		// Codel[] codels = new Codel[2];

		// initiate the very first Codel
		Coordinate first = new Coordinate(nextRow, nextCol);
		codels.add(new Codel(first));
		int initSize = 1 + findSizeCodel(board, visited, codels.get(0), first);
		markUnvisited(visited);

		codels.get(0).setSize(initSize);
//		codels[0].printCodel();

		// the program will end on it's own (hypothetically)
		while(!end)
		{
			// get the coords of the next Codel
			int[] nextBlock = getNextBlock(board, codels.get(0), 0);
			nextRow = nextBlock[0];
			nextCol = nextBlock[1];

			Coordinate g = new Coordinate(nextRow, nextCol);

			// initiate the newest Codel
			codels.add(new Codel(g));
			initSize = 1+findSizeCodel(board, visited, codels.get(1), g);
			markUnvisited(visited);

			codels.get(1).setSize(initSize);
			codels.get(1).printCodel(printLevel);
			printCommand("DP = " + dp + " \tCC = " + cc, 3);

			// white codels act as a nop, meaning they don't go into the queue at all
			if(codels.get(1).getColorName().equals("white"))
			{
				Coordinate nextNonWhite = getNextBlockWhite(board, codels.get(1), g, 0);
				codels.set(0, new Codel(nextNonWhite));
				codels.get(0).setSize(1+findSizeCodel(board, visited, codels.get(1), nextNonWhite));

				markUnvisited(visited);

//				codels[0].printCodel();
			}
			else
			{ 
				// perform the command given by the two Codel's
				getCommand(codels.get(0), codels.get(1));

				//  shift the new Codel to the old one's spot
				codels.set(0, codels.get(1));

				//  if I want to print the stack for debuggin and slow down time
				printCommand(Arrays.toString(stack.toArray()), 2);
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
	public static int findSizeCodel(String[][] board, boolean[][] visited, Codel c, Coordinate coord)
	{ 
		// codel is uninitiated, so I need to set its corners
		if(c.getRightTop().getX() == -1)
		{
			setCorners(c, coord);
		}

		int count = 0;
		visited[coord.getX()][coord.getY()] = true;

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
			if(!inBounds(newCoordinate.getX(), newCoordinate.getY()))
				continue;

			if(visited[newCoordinate.getX()][newCoordinate.getY()])
				continue;

			if(Integer.parseInt(board[newCoordinate.getX()][newCoordinate.getY()]) == c.getColorValue())
			{
				count++;
				count += findSizeCodel(board, visited, c, newCoordinate);
				setCorners(c, newCoordinate);
			}
		}

		return count;
	}

	public static void printCommand(String command, int level) {
		if(printLevel >= level) {
			System.out.println(command);
		}
	}

	// Here, col1 represents the last color, col2 is the newest color
	public static void getCommand(Codel cod1, Codel cod2)
	{

		Integer col1 = cod1.getColorValue();
		Integer col2 = cod2.getColorValue();

		if(col1 == 1 || col2 == 1)
		{
			return;
		}

		// specific color doesn't matter, it's about the positive net change between two colors
		Integer change = col1 - col2;
		int hueChange = (change/10)%10;
		int lightChange = (change)%10;

		if(hueChange < 0)
			hueChange += 6;
		if(lightChange < 0)
			lightChange += 3;

		// max of 2 values being popped
		int valB, valT;
//			val1, val2
		try {
			switch(hueChange)
			{
				case 0:
					switch(lightChange)
					{
						case 0:
							printCommand("nop", 1);
							return;
						case 1:	// push
							printCommand("pushing " + cod1.getSize(), 1);
							stack.push(cod1.getSize());
							return;
						case 2: // pop
							printCommand("pop", 1);
							stack.pop();
							return;
					}
				case 1:
					switch(lightChange)
					{
						case 0: // add
							printCommand("add", 1);
							valT = stack.pop();
							valB = stack.pop();
							stack.push(valB + valT);
							return;
						case 1: // subtract
							printCommand("sub", 1);
							valT = stack.pop();
							valB = stack.pop();
							stack.push(valB - valT);
							return;
						case 2: // multiply
							printCommand("multiply", 1);
							valT = stack.pop();
							valB = stack.pop();
							stack.push(valB * valT);
							return;
					}
				case 2:
					switch(lightChange)
					{
						case 0: // divide
							printCommand("divide", 1);
							valT = stack.pop();
							valB = stack.pop();
							stack.push(valB / valT);
							return;
						case 1: // mod
							printCommand("mod", 1);
							valT = stack.pop();
							valB = stack.pop();
							stack.push(correctMod(valB, valT));
							return;
						case 2: // not
							printCommand("not", 1);
							valT = stack.pop();
							if(valT == 0) {
								stack.push(1);
							} else {
								stack.push(0);
							}
							return;
					}
				case 3:
					switch(lightChange)
					{
						case 0: // greater
							printCommand("greater", 1);
							valT = stack.pop();
							valB = stack.pop();
							if(valB > valT) {
								stack.push(1);
							} else {
								stack.push(0);
							}
							return;
						case 1: // pointer
							valT = stack.pop();
							printCommand("DP pointer " + valT, 1);
							rotateDP(valT);
							return;
						case 2: // switch
							printCommand("switch", 1);
							valT = stack.pop();
							rotateCC(valT);
							return;
					}
				case 4:
					switch(lightChange)
					{
						case 0:	// duplicate
							printCommand("duplicate", 1);
							valT = stack.pop();
							stack.push(valT);
							stack.push(valT);
							return;
						case 1: // roll
							printCommand("roll", 1);

							valT = stack.pop();	// # rolls
							valB = stack.pop(); // Depth of roll
							if(valT == 0) {
								return;
							}

							boolean reverseFlag = false;
							int size = stack.size();
							if(valT < 0) {
								reverseFlag = true;
								int vals[] = new int[size];
								for(int i = 0; i < size; i++)
									vals[i] = stack.pop();
								for(int i = 0; i < size; i++)
								{
									printCommand("Pushing " + vals[i], 2);
									stack.push(vals[i]);
								}
								valT = -valT;
							}
							printCommand("Stack after reverse: ", 2);

							printCommand(Arrays.toString(stack.toArray()), 2);

							while(valT > 0)
							{
								printCommand("Rolling stack " + valT + " times, " + valB + " deep", 2);
								int rollNum = stack.pop();
								int[] vals = new int[size];
								for(int i = 0; i < valB-1; i++) {
									vals[i] = stack.pop();
									printCommand("Popped : " + vals[i], 2);
								}
								stack.push(rollNum);
								printCommand("Pushed " + rollNum, 2);
								printCommand(Arrays.toString(stack.toArray()), 2);
								for(int i = valB - 2; i >= 0; i--)
									stack.push(vals[i]);
								valT--;
//								return;
							}
							printCommand("After roll: " + Arrays.toString(stack.toArray()), 2);
							if(reverseFlag) {
								printCommand("Reversed", 2);
								int[] vals = new int[size];
								for(int i = 0; i < size; i++)
								{
									vals[i] = stack.pop();
									printCommand("stack at "+ i + " " + vals[i], 2);

								}
								for(int i = 0 ; i < size; i++)
									stack.push(vals[i]);

								reverseFlag = false;
							}

							printCommand("After re-reverse: " + Arrays.toString(stack.toArray()), 2);

							return;
						case 2: // inNum
							printCommand("in num", 1);
							Scanner s = new Scanner(System.in);
							int i = s.nextInt();
							stack.push(i);
							return;
					}
				case 5:
					switch(lightChange)
					{
						case 0: // inChar
							printCommand("in char", 1);
							Scanner s = new Scanner(System.in);
							char i = s.next().charAt(0);
							int j = i;
							stack.push(j);
							return;
						case 1: // outNum
							printCommand("out num", 1);
							int k = stack.pop();
							System.out.print(k);
							return;
						case 2: // outChar
							printCommand("out char", 1);
							int l = stack.pop();
							char m = (char) l;
							System.out.print(m);
							return;
					}
			}
		} catch(EmptyStackException e) {
			System.err.println(e.getMessage());
		}

		return;
	}

	// Piet relies entirely on moving from a corner to a new color, need to set two corners (l, r) for each direction (l, r, u, d)
	public static void setCorners(Codel c, Coordinate coordinate)
	{

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
	public static boolean inBounds(int i, int j) {
		return ((i >= 0) && (i < totRow) && (j >= 0) && (j < totCol));
	}


	// Java can't do modulo arithmetic correctly, so I made this
	public static int correctMod(int dividend, int divisor) {
		while(dividend < 0)
			dividend += divisor;

		return dividend % divisor;
	}

	// the rotateDP command takes up a lot of space, which could be eliminated by making DP an int
	public static void rotateDP(int val) {
		printCommand("Rotating DP " + val, 2);

		if(val == 0) {
			return;
		}

		if(val > 0) {
			while(val > 0) {
				if(dp == DP.RIGHT) {
					dp = DP.DOWN;
				}
				else if(dp == DP.DOWN) {
					dp = DP.LEFT;
			}
				else if(dp == DP.LEFT) {
					dp = DP.UP;
				}
				else if(dp == DP.UP) {
					dp = DP.RIGHT;
				}
				val--;
			}
		}
		if(val < 0) {
			while(val < 0) {
				if(dp == DP.RIGHT) {
					dp = DP.UP;
        } else if(dp == DP.UP) {
					dp = DP.LEFT;
				} else if(dp == DP.LEFT) {
					dp = DP.DOWN;
				} else {
					dp = DP.RIGHT;
				}
				val++;
			}
		}

		return;
	}

	// If rotateDP gets its own method then so does rotateCC
	public static void rotateCC(int val) {
		if(correctMod(val, 2) == 2)
			return;
		else
			if(cc == CC.RIGHT)
				cc = CC.LEFT;
			else
				cc = CC.RIGHT;
	}

	// the interpreter moves in a straight line when fed a white block, as opposed to a colored block
	public static Coordinate getNextBlockWhite(String[][] board, Codel c, Coordinate coordinate, int attempt ) {


		int[] nextBlock = new int[2];
		int nextRow = coordinate.getX();
		int nextCol = coordinate.getY();


		// moving in a straight line, unless it goes out of bounds or hits a black box
		switch(dp) {
			case RIGHT:
				while(Integer.parseInt(board[nextRow][nextCol]) == 1) {
					printCommand("new val is " + board[nextRow][nextCol] + " at " + nextRow + " " + nextCol, 2);
					if(!inBounds(nextRow, nextCol+1)) {
						printCommand("Rotating here @ right: ", 4);
						rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(Integer.parseInt(board[nextRow][nextCol+1]) == 0) {
						printCommand("Rotating here2 @ right: ", 4);
						rotateCC(1);
						rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}

					nextCol++;
				}
				break;
			case LEFT:
				while(Integer.parseInt(board[nextRow][nextCol]) == 1) {
					if(!inBounds(nextRow, nextCol-1)) {
						printCommand("Rotating here @ left: ", 4);
						rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(Integer.parseInt(board[nextRow][nextCol-1]) == 0) {
						printCommand("Rotating here2 @ left: ", 4);
						rotateCC(1);
						rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}
					nextCol--;
				}
				break;
			case DOWN:
				while(Integer.parseInt(board[nextRow][nextCol]) == 1) {
					if(!inBounds(nextRow+1, nextCol)) {
						printCommand("Rotating here @ down: ", 4);
						rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(Integer.parseInt(board[nextRow+1][nextCol]) == 0) {
						printCommand("Rotating here2 @ down: ", 4);
						rotateCC(1);
						rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}
					nextRow++;
				}
				break;
			case UP:
				while(Integer.parseInt(board[nextRow][nextCol]) == 1) {
					if(!inBounds(nextRow-1, nextCol)) {
						printCommand("Rotating here @ up: ", 4);
						rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					} else if(Integer.parseInt(board[nextRow-1][nextCol]) == 0) {
						printCommand("Rotating here2 @ up: ", 4);
						rotateCC(1);
						rotateDP(1);
						return getNextBlockWhite(board, c, coordinate, attempt);
					}
					nextRow--;
				}
				break;
		}

		nextBlock[0] = nextRow;
		nextBlock[1] = nextCol;
		Coordinate nextCoord = new Coordinate(nextRow, nextCol);
		return nextCoord;
	}

	// we're getting the newest codel, from the old one, c
	public static int[] getNextBlock(String[][] board, Codel c, int attempt) {

		int nextBlock[] = new int[2];

		// we've tried every orientation and can't find a new Codel
		if(attempt > 8) {
			printCommand("DONE", 2);
			end = true;
			return new int[] {0, 0};
		}

		int nextRow = 0, nextCol = 0;
		switch(dp) {
			// if we're going right
			case RIGHT:
				switch(cc) {
					// we want to start from the right-top
					case LEFT:
						nextRow = c.getRightTop().getX();
						nextCol = c.getRightTop().getY();
						break;
					// we want to start from the right-bottom
					case RIGHT:
						nextRow = c.getRightBottom().getX();
						nextCol = c.getRightBottom().getY();
						break;
				}
				nextCol++;

				// TODO: what is attempt doing here? maybe bug
				if(!inBounds(nextRow, nextCol) || Integer.parseInt(board[nextRow][nextCol]) == 0) {
					printCommand("Maybe rotate DP", 2);
					if(attempt % 2 == 0) {
						rotateCC(1);
					} else {
						rotateDP(1);
					}

					nextCol--;
					return getNextBlock(board, c, attempt + 1);
				}
				break;
			case DOWN:
				switch(cc) {
					case LEFT:
						nextRow = c.getBottomRight().getX();
						nextCol = c.getBottomRight().getY();
						break;
					case RIGHT:
						nextRow = c.getBottomLeft().getX();
						nextCol = c.getBottomLeft().getY();
						break;
				}
				nextRow++;

				if(!inBounds(nextRow, nextCol) || Integer.parseInt(board[nextRow][nextCol]) == 0) {
					printCommand("Rotating here @ up: ", 4);
					if(attempt % 2 == 0) {
						rotateCC(1);
					} else {
						rotateDP(1);
					}
					nextRow--;
					return getNextBlock(board, c, attempt + 1);

				}
				break;
			case LEFT:
				switch(cc) {
					case LEFT:
						nextRow = c.getLeftBottom().getX();
						nextCol = c.getLeftBottom().getY();
						break;
					case RIGHT:
						nextRow = c.getLeftTop().getX();
						nextCol = c.getLeftTop().getY();
						break;
				}
				nextCol--;

				if(!inBounds(nextRow, nextCol) || Integer.parseInt(board[nextRow][nextCol]) == 0) {
					printCommand("Rotating here @ up: ", 4);
					if(attempt % 2 == 0) {
						rotateCC(1);
					} else {
						rotateDP(1);
					}

					nextCol++;
					return getNextBlock(board, c, attempt + 1);
				}
				break;
			case UP:
				switch(cc) {
					case LEFT:
						nextRow = c.getTopLeft().getX();
						nextCol = c.getTopLeft().getY();
						break;
					case RIGHT:
						nextRow = c.getTopRight().getX();
						nextCol = c.getTopRight().getY();
						break;
				}
				nextRow--;

				if(!inBounds(nextRow, nextCol) || Integer.parseInt(board[nextRow][nextCol]) == 0) {
					printCommand("Rotating here @ up: ", 4);
					if(attempt % 2 == 0) {
						rotateCC(1);
					} else {
						rotateDP(1);
					}
					nextRow++;
					return getNextBlock(board, c, attempt + 1);
				}
				break;
		}

		nextBlock[0] = nextRow;
		nextBlock[1] = nextCol;

		return nextBlock;
	}

	/**
	 *
	 */
	public static boolean[][] markUnvisited(boolean[][] board) {
		for(int i = 0; i < totRow - 1; i++) {
			for(int j = 0; j < totCol - 1; j++) {
				board[i][j] = false;
			}
		}

		return board;
	}

}


// thank you, Larry Tesler
// realized that ^ may sound like I was copy+pasting from outside sources, I wasn't
// some of the code (converting numbers -> colors) was repetitive, where I c+p'd a lot
// Larry Tesler was the creator of the copy & paste commands, according to a quick Google search
// all of this code was written by myself (Trent Freeman), and no code was taken from any outside sources
