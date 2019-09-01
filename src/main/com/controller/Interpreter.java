import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.util.CC;
import com.util.DP;

public class Interpreter {

	private static int totRow;		// total number of rows
	private static int totCol;		// total number of columns
	private static int sizePix = 1;

	private static int printLevel = 0;

	private static DP dp = DP.RIGHT;
	private static CC cc = CC.LEFT;

	private static Stack<Integer> stack = new Stack<Integer>();

	private static boolean end = false;

	//
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

		if(args.length == 0) {
			System.err.println("Please enter a .ppm file to run.");
			Scanner sc = new Scanner(System.in);
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

		Codel[] codels = new Codel[2];

		// initiate the very first Codel
		int[] f = {nextRow, nextCol};
		codels[0] = new Codel(f, board);
		int initSize = 1 + findSizeCodel(board, visited, codels[0], f[0], f[1]);
		markUnvisited(visited);

		codels[0].size = initSize;
//		codels[0].printCodel();

		// the program will end on it's own (hypothetically)
		while(!end)
		{
			// get the coords of the next Codel
			int[] nextBlock = getNextBlock(board, codels[0], 0);
			nextRow = nextBlock[0];
			nextCol = nextBlock[1];

			int[] g = {nextRow, nextCol};

			// initiate the newest Codel
			codels[1] = new Codel(g, board);
			initSize = 1+findSizeCodel(board, visited, codels[1], g[0], g[1]);
			markUnvisited(visited);

			codels[1].size = initSize;
			codels[1].printCodel(printLevel);
			printCommand("DP = " + dp + " \tCC = " + CC, 3);

			// white codels act as a nop, meaning they don't go into the queue at all
			if(codels[1].colorName.equals("white"))
			{
				int[] nextNonWhite = getNextBlockWhite(board, codels[1], g[0], g[1], 0);
				codels[0] = new Codel(nextNonWhite, board);
				codels[0].size = 1+findSizeCodel(board, visited, codels[0], nextNonWhite[0], nextNonWhite[1]);

				markUnvisited(visited);

//				codels[0].printCodel();
			}
			else
			{
				// perform the command given by the two Codel's
				getCommand(codels[0], codels[1]);

				// shift the new Codel to the old one's spot
				codels[0] = codels[1];

//				 if I want to print the stack for debuggin and slow down time
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
	public static int findSizeCodel(String[][] board, boolean[][] visited, Codel c, int a, int b)
	{
		// codel is uninitiated, so I need to set its corners
		if(c.rightTop[0] == -1)
		{
			setCorners(c, a, b);
		}

		int count = 0;
		visited[a][b] = true;

		// 4 directions the program will search in for more of the same color
		int[] newCoords = {-1, -1};
		for(int i = 0; i < 4; i++)
		{
			if(i == 0)
				newCoords = new int[] {a, b-1};
			else if(i == 1)
				newCoords = new int[] {a, b+1};
			else if(i == 2)
				newCoords = new int[] {a-1, b};
			else if(i == 3)
				newCoords = new int[] {a+1, b};

			// gonna skip this block if it's out of the board or visited
			if(!inBounds(newCoords[0], newCoords[1]))
				continue;

			if(visited[newCoords[0]][newCoords[1]])
				continue;

			if(Integer.parseInt(board[newCoords[0]][newCoords[1]]) == Integer.parseInt(c.colorVal))
			{
				count++;
				count += findSizeCodel(board, visited, c, newCoords[0], newCoords[1]);
				setCorners(c, newCoords[0], newCoords[1]);
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

		String col1 = cod1.colorVal;
		String col2 = cod2.colorVal;

		if(Integer.parseInt(cod1.colorVal) == 1 || Integer.parseInt(cod1.colorVal) == 1)
		{
			return;
		}

		// specific color doesn't matter, it's about the positive net change between two colors
		int hueChange = Character.getNumericValue(col2.charAt(0)) - Character.getNumericValue(col1.charAt(0));
		int lightChange = Character.getNumericValue(col2.charAt(1)) - Character.getNumericValue(col1.charAt(1));

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
							printCommand("pushing " + cod1.size, 1);
							stack.push(cod1.size);
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
	public static void setCorners(Codel c, int newX, int newY)
	{

		// potential for new right column value
		if(newY >= c.rightTop[1] || c.rightTop[1] == -1) {
			// if further right
			if(newY > c.rightTop[1] || c.rightTop[1] == -1) {
				c.rightTop[1] = newY;
				c.rightTop[0] = newX;
			}
			// if further up
			if(newX < c.rightTop[0] || c.rightTop[0] == -1) {
				c.rightTop[1] = newY;
				c.rightTop[0] = newX;
			}
			// if further right
			if(newY > c.rightBottom[1] || c.rightBottom[1] == -1) {
				c.rightBottom[1] = newY;
				c.rightBottom[0] = newX;
			}
			// if further down
			if(newX > c.rightBottom[0] || c.rightBottom[0] == -1) {
				c.rightBottom[1] = newY;
				c.rightBottom[0] = newX;
			}
		}

		// potential for new bottom row value
		if(newX >= c.bottomRight[0] || c.bottomRight[0] == -1) {
			// if further down
			if(newX > c.bottomRight[0] || c.bottomRight[0] == -1) {
				c.bottomRight[0] = newX;
				c.bottomRight[1] = newY;
			}
			// if further right
			if(newY > c.bottomRight[1] || c.bottomRight[1] == -1) {
				c.bottomRight[1] = newY;
				c.bottomRight[0] = newX;
			}
			// if further down
			if(newX > c.bottomLeft[0] || c.bottomLeft[0] == -1) {
				c.bottomLeft[1] = newY;
				c.bottomLeft[0] = newX;
			}
			// if further left
			if(newY < c.bottomLeft[1] || c.bottomLeft[1] == -1 || c.bottomLeft[0] == -1) {
				c.bottomLeft[1] = newY;
				c.bottomLeft[0] = newX;
			}
		}

		// potential for new left column value
		if(newY <= c.leftBottom[1] || c.leftBottom[1] == -1) {
			// if further left
			if(newY < c.leftBottom[1] || c.leftBottom[1] == -1) {
				c.leftBottom[1] = newY;
				c.leftBottom[0] = newX;
			}
			// if further down
			if(newX > c.leftBottom[0] || c.leftBottom[0] == -1) {
				c.leftBottom[1] = newY;
				c.leftBottom[0] = newX;
			}
			// if further left
			if(newY < c.leftTop[0] || c.leftTop[0] == -1) {
				c.leftTop[0] = newX;
				c.leftTop[1] = newY;
			}
			// if further up
			if(newX < c.leftTop[0] || c.leftTop[0] == -1) {
				c.leftTop[0] = newX;
				c.leftTop[1] = newY;
			}
		}

		// potential for upper-most row
		if(newX <= c.topLeft[0] || c.topLeft[0] == -1) {
			// if further up
			if(newX < c.topLeft[0] || c.topLeft[0] == -1) {
				c.topLeft[0] = newX;
				c.topLeft[1] = newY;
			}
			// if further left
			if(newY < c.topLeft[1] || c.topLeft[1] == -1) {
				c.topLeft[1] = newY;
				c.topLeft[0] = newX;
			}
			// if further up
			if(newX < c.topRight[0] || c.topRight[1] == -1) {
				c.topRight[0] = newX;
				c.topRight[1] = newY;
			}
			// if further right
			if(newY > c.topRight[1] || c.topRight[1] == -1) {
				c.topRight[1] = newY;
				c.topRight[0] = newX;
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
				if(dp.equals(DP.RIGHT))
					dp = DP.DOWN;
				else if(DP.equals(DP.DOWN))
					dp = DP.LEFT;
				else if(DP.equals(DP.LEFT))
					dp = DP.UP;
				else if(DP.equals(DP.UP))
					dp = DP.RIGHT;
				val--;
			}
		}
		if(val < 0) {
			while(val < 0) {
				if(DP.equals(DP.RIGHT)) {
					dp = DP.UP;
        } else if(DP.equals(DP.UP)) {
					dp = DP.LEFT;
				} else if(DP.equals(DP.LEFT)) {
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
			if(CC.equals(CC.RIGHT))
				cc = CC.LEFT;
			else
				cc = CC.RIGHT;
	}

	// the interpreter moves in a straight line when fed a white block, as opposed to a colored block
	public static int[] getNextBlockWhite(String[][] board, Codel c,int row, int col, int attempt ) {


		int[] nextBlock = new int[2];
		int nextRow = row;
		int nextCol = col;


		// moving in a straight line, unless it goes out of bounds or hits a black box
		switch(dp) {
			case DP.RIGHT:
				while(Integer.parseInt(board[nextRow][nextCol]) == 1) {
					printCommand("new val is " + board[nextRow][nextCol] + " at " + nextRow + " " + nextCol, 2);
					if(!inBounds(nextRow, nextCol+1)) {
						printCommand("Rotating here @ right: ", 4);
						rotateDP(1);
						return getNextBlockWhite(board, c, row, col, attempt);
					} else if(Integer.parseInt(board[nextRow][nextCol+1]) == 0) {
						printCommand("Rotating here2 @ right: ", 4);
						rotateCC(1);
						rotateDP(1);
						return getNextBlockWhite(board, c, nextRow, nextCol, attempt);
					}

					nextCol++;
				}
				break;
			case DP.LEFT:
				while(Integer.parseInt(board[nextRow][nextCol]) == 1) {
					if(!inBounds(nextRow, nextCol-1)) {
						printCommand("Rotating here @ left: ", 4);
						rotateDP(1);
						return getNextBlockWhite(board, c, row, col, attempt);
					} else if(Integer.parseInt(board[nextRow][nextCol-1]) == 0) {
						printCommand("Rotating here2 @ left: ", 4);
						rotateCC(1);
						rotateDP(1);
						return getNextBlockWhite(board, c, nextRow, nextCol, attempt);
					}
					nextCol--;
				}
				break;
			case DP.DOWN:
				while(Integer.parseInt(board[nextRow][nextCol]) == 1) {
					if(!inBounds(nextRow+1, nextCol)) {
						printCommand("Rotating here @ down: ", 4);
						rotateDP(1);
						return getNextBlockWhite(board, c, row, col, attempt);
					} else if(Integer.parseInt(board[nextRow+1][nextCol]) == 0) {
						printCommand("Rotating here2 @ down: ", 4);
						rotateCC(1);
						rotateDP(1);
						return getNextBlockWhite(board, c, nextRow, nextCol, attempt);
					}
					nextRow++;
				}
				break;
			case DP.UP:
				while(Integer.parseInt(board[nextRow][nextCol]) == 1) {
					if(!inBounds(nextRow-1, nextCol)) {
						printCommand("Rotating here @ up: ", 4);
						rotateDP(1);
						return getNextBlockWhite(board, c, row, col, attempt);
					} else if(Integer.parseInt(board[nextRow-1][nextCol]) == 0) {
						printCommand("Rotating here2 @ up: ", 4);
						rotateCC(1);
						rotateDP(1);
						return getNextBlockWhite(board, c, nextRow, nextCol, attempt);
					}
					nextRow--;
				}
				break;
		}

		nextBlock[0] = nextRow;
		nextBlock[1] = nextCol;
		return nextBlock;
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
			case DP.RIGHT:
				switch(cc) {
					// we want to start from the right-top
					case CC.LEFT:
						nextRow = c.rightTop[0];
						nextCol = c.rightTop[1];
						break;
					// we want to start from the right-bottom
					case CC.RIGHT:
						nextRow = c.rightBottom[0];
						nextCol = c.rightBottom[1];
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
			case DP.DOWN:
				switch(CC) {
					case CC.LEFT:
						nextRow = c.bottomRight[0];
						nextCol = c.bottomRight[1];
						break;
					case CC.RIGHT:
						nextRow = c.bottomLeft[0];
						nextCol = c.bottomLeft[1];
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
			case DP.LEFT:
				switch(cc) {
					case CC.LEFT:
						nextRow = c.leftBottom[0];
						nextCol = c.leftBottom[1];
						break;
					case CC.RIGHT:
						nextRow = c.leftTop[0];
						nextCol = c.leftTop[1];
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
			case DP.UP:
				switch(CC) {
					case CC.LEFT:
						nextRow = c.topLeft[0];
						nextCol = c.topLeft[1];
						break;
					case CC.RIGHT:
						nextRow = c.topRight[0];
						nextCol = c.topRight[1];
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
// all of this code was written by myself (Trent Freeman), and no code was taken from any outside sources
