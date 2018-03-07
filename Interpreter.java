import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Interpreter {

	private static int totRow;		// total number of rows
	private static int totCol;		// total number of columns

	private static String dp = "right";
	private static String cc = "left";

	// This assumes we have a Piet photo that has been transcribed into numbers (I will, at some point, make this program separate)
	// The Piet program is described at http://www.dangermouse.net/esoteric/piet.html
	public static void main(String[] args) throws FileNotFoundException
	{
		Codel cod1 = new Codel("11", "red");
		Codel cod2 = new Codel("12", "dark red");
		System.out.println(getCommand(cod1, cod2));

		String[][] board = readFile("pietcode.txt");

		for(int i = 0; i < totRow; i++)
		{
			for(int j = 0; j < totCol; j++)
				System.out.print("[" + i + ", " + j + "]");
			System.out.println();
		}

		readBoard(board);
	}

	// get the size of the codel being input
	public static int findSizeCodel(String[][] board, boolean[][] visited, Codel c, int nextX, int nextY, int i, int j)
	{
		if(c.topRow == 0 && c.bottomRow == 0)
		{
			c.topRow = nextX;
			c.topRow = nextX;
			c.topLeftCol = nextY;
			c.topRightCol = nextY;
			c.bottomLeftCol = nextY;
			c.bottomRightCol = nextY;
		}
		
//		System.out.println("nextX: " + nextX + "\nnextY: " + nextY);

		System.out.println("We're in bounds and haven't been visited at [" + nextX + ", " + nextY + "]");
		visited[i][j] = true;

		int[][] moves = { {0, -1}, {0, 1}, {-1, 0}, {1, 0} };
		int newX, newY;
		
		int count = 0;
		for(int m = 0; m < 4; m++)
		{
			newX = nextX + moves[m][0];
			newY = nextY + moves[m][1];

			if(!inBounds(newX, newY))
				continue;

			if(visited[newX][newY])
				continue;

			System.out.println("We're looking for colorCode " + c.colorVal + ". At [" + newX + " " + newY + "] is colorCode " + board[newX][newY]);
			System.out.println("newest colorcode : " + board[newX][newY]);
			
			String colorCode = c.colorVal;
			String s1 = colorCode;
			String s2 = board[newX][newY];
			System.out.println("newX : " + newX + "\t\tnewY : " + newY);
			
			if(!s1.equals(s2))
				continue;
			
			System.out.println("Our colors match at row " + newX + " and column " + newY);
			System.out.println("[0][1] : " + board[0][1]);
			setCorners(c, newX, newY);
			
			count++;
//			System.out.println("Yes");
//			System.out.println("About to return with 1+");
			return 1 + findSizeCodel(board, visited, c, newX, newY, newX, newY);
		}
		
//		for(int w = 0; w < totRow; w++)
//		{
//			for(int q = 0; q < totCol; q++)
//				System.out.print(visited[w][q] + " ");
//			System.out.println();
//		}

		return count;
	}


	// based on the directions of the DP and the CC, return the direction of the codel that will be chosen
	public static int codelChosen()
	{
		switch(dp)
		{
			case "right":
				switch(cc)
				{
					case "left": 	return 1;
					case "right":	return 0;
				}
			case "down":
				switch(cc)
				{
					case "left":		return 3;
					case "right":	return 2;
				}
			case "left":
				switch(cc)
				{
					case "left":		return 5;
					case "right":	return 4;
				}
			case "up":
				switch(cc)
				{
					case "left":		return 7;
					case "right":	return 6;
				}
		}

		return 0;
	}

	//
	public static String codelIntoString(String val)
	{
		switch(val)
		{
			case "0": return "black";			case "1": return "white";
			case "10": return "light red";		case "11": return "red";			case "12": return "dark red";
			case "20": return "light yellow";	case "21": return "yellow"; 		case "22": return "dark yellow";
			case "30": return "light green";		case "31": return "green"; 		case "32": return "dark green";
			case "40": return "light cyan";		case "41": return "cyan";		case "42": return "dark cyan";
			case "50": return "light blue"; 		case "51": return "blue";		case "52": return "dark blue";
			case "60": return "light magenta";	case "61": return "magenta";		case "62": return "dark magenta";
		}

		return "white";

		// red -> yellow -> green -> cyan -> blue -> magenta
		//  11		  21       31      41      51         61

		// black / white
		//	     0       1

	}

	// Here, col1 represents the last color, col2 is the newest color
	public static String getCommand(Codel cod1, Codel cod2)
	{

		String col1 = cod1.colorVal;
		String col2 = cod2.colorVal;

		int hueChange = Character.getNumericValue(col2.charAt(0)) - Character.getNumericValue(col1.charAt(0));
		int lightChange = Character.getNumericValue(col2.charAt(1)) - Character.getNumericValue(col1.charAt(1));

		if(lightChange == -2)
			lightChange = 1;
		else if(lightChange == -1)
			lightChange = 2;

		if(hueChange == -5)
			hueChange = 1;
		else if(hueChange == -4)
			hueChange = 2;
		else if(hueChange == -3)
			hueChange = 3;
		else if(hueChange == -2)
			hueChange = 4;
		else if(hueChange == -1)
			hueChange = 5;
			
		switch(hueChange)
		{
			case 0:
				switch(lightChange)
				{
					case 0:
						return "nop";
					case 1:
						return "push";
					case 2:
						return "pop";
				}
			case 1:
				switch(lightChange)
				{
					case 0:
						return "add";
					case 1:
						return "subtact";
					case 2:
						return "multiply";
				}
			case 2:
				switch(lightChange)
				{
					case 0:
						return "divide";
					case 1:
						return "mod";
					case 2:
						return "not";
				}
			case 3:
				switch(lightChange)
				{
					case 0:
						return "greater";
					case 1:
						return "pointer";
					case 2:
						return "switch";
				}
			case 4:
				switch(lightChange)
				{
					case 0:
						return "duplicate";
					case 1:
						return "roll";
					case 2:
						return "inNum";
				}
			case 5:
				switch(lightChange)
				{
					case 0:
						return "inChar";
					case 1:
						return "outNum";
					case 2:
						return "outChar";
				}
		}

		return "";
	}

	// by now we've read in the file and pass it in through board
	public static void readBoard(String[][] board)
	{

		boolean[][] visited = new boolean[totRow][totCol];
		for(int i = 0; i < totRow; i++)
			for(int j = 0; j < totCol; j++)
				visited[i][j] = false;

		// initiate
		Codel init = new Codel(board[0][1], codelIntoString(board[0][1]));
		init.printCodel();
		int initSize = 1 + findSizeCodel(board, visited, init, 0, 1, 0, 1);
//		System.out.println(num);

		init.size = initSize;
		init.printCodel();

		int nextCodel;
		nextCodel = codelChosen();
		
		int[] priorityXY = new int[3];
		priorityXY = getPriorityXY(nextCodel, init);
		
		System.out.println("Looking for next Codel from row " + priorityXY[2] + " and column " + priorityXY[1] + " while prioritizing " + ((priorityXY[0] == 1) ? "x" : "y"));
		/*
		 * Do:
		 * 0: Get the block furthest in the direction of the dp
		 * 			If dp: -> then get block furthest in right direction
		 * A: Find the next Codel using DP / CC
		 * B: Get the difference between the last two Codels
		 * C: Perform the operation
		 * GOTO: A
		 */

	}
	
	public static void setCorners(Codel c, int newX, int newY)
	{
		System.out.println("Assigning corners:");
		System.out.println("Current topRow is " + c.topRow + ". Newest row is " + newX);
		if(c.topRow >= newX)
		{
			System.out.println("Changing top row");
			c.topRow = newX;
			if(c.topLeftCol > newY)
				c.topLeftCol = newY;
			if(c.topRightCol < newY)
				c.topRightCol = newY;
		}
		System.out.println("Current bottomRow is " + c.bottomRow + ". Newest row is " + newX);
		if(c.bottomRow <= newX)
		{
			System.out.println("Changing bottom row");
			c.bottomRow = newX;
			if(c.topLeftCol > newY)
				c.bottomLeftCol = newY;
			if(c.topRightCol < newY)
				c.bottomRightCol = newY;
		}
//		System.out.println("Current leftCol is " + c.leftCol + ". Newest column is " + newY);			
//		if(c.leftCol >= newY)
//		{
//			if(c.topRow == newX)
//			{
//				if(c.leftTopRow > newX)
//					c.leftTopRow = newX;
//			}
//			if(c.bottomRow == newX)
//			{
//				if(c.leftBottomRow < newX)
//					c.leftBottomRow = newX;
//			}
//			c.leftCol = newY;
//		}
//		System.out.println("Current rightCol is " + c.rightCol + ". Newest column is " + newY);
//		if(c.rightCol <= newY)
//		{
//			if(c.topRow == newX)
//				if(c.rightTopRow > newX)
//					c.rightTopRow = newX;
//			if(c.bottomRow == newX)
//				if(c.rightBottomRow < newX)
//					c.rightBottomRow = newX;
//			c.rightCol = newY;
//		}
	}
	
	public static int[] getPriorityXY(int nextCodel, Codel init)
	{
		int nextBoardPlaceX = -1, nextBoardPlaceY = -1;
		boolean prioritizeX = false;
		if(nextCodel == 0) {
			prioritizeX = true;
			nextBoardPlaceX = init.bottomRightCol;
			nextBoardPlaceY = init.bottomRow;
		} else if(nextCodel == 1)
		{
			prioritizeX = true;
			nextBoardPlaceX = init.topRightCol;
			nextBoardPlaceY = init.topRow;
		} else if(nextCodel == 2)
		{
			prioritizeX = false;
			nextBoardPlaceX = init.bottomLeftCol;
			nextBoardPlaceY = init.bottomRow;
		} else if(nextCodel == 3)
		{
			prioritizeX = false;
			nextBoardPlaceX = init.bottomRightCol;
			nextBoardPlaceY = init.bottomRow;
		} else if(nextCodel == 4)
		{
			prioritizeX = true;
			nextBoardPlaceX = init.topLeftCol;
			nextBoardPlaceY = init.topRow;
		} else if(nextCodel == 5)
		{
			prioritizeX = true;
			nextBoardPlaceX = init.bottomLeftCol;
			nextBoardPlaceY = init.bottomRow;
		} else if(nextCodel == 6)
		{
			prioritizeX = false;
			nextBoardPlaceX = init.topRightCol;
			nextBoardPlaceY = init.topRow;
		} else if(nextCodel == 7)
		{
			prioritizeX = false;
			nextBoardPlaceX = init.topLeftCol;
			nextBoardPlaceY = init.topRow;
		}
		
		int priX = 0;
		if(prioritizeX)
			priX = 1;
		
		int[] temp = {priX, nextBoardPlaceX, nextBoardPlaceY};
		return temp;
	}
	
	// reading in the file and returning it as a 2d string array
	public static String[][] readFile(String s) throws FileNotFoundException
	{
		File infile = new File(s);

		Scanner s1 = new Scanner(infile);
		Scanner s2 = new Scanner(infile);

		String readLines;

		totRow = 0;
		totCol = 0;
		while(s1.hasNextLine())
		{
			readLines = s1.nextLine();
			if(readLines.trim().isEmpty())
				break;

			totRow++;
		}

		String[] wholeLine = new String[totRow];
		for(int i = 0; i < totRow; i++)
		{
			wholeLine[i] = s2.nextLine();
			String[] brokenLine = wholeLine[i].split(" ");
			totCol = max(totCol, brokenLine.length);
		}

		String[][] board = new String[totRow][totCol];
		for(int i = 0; i < totRow; i++)
		{
			for(int j = 0; j < totCol; j++)
			{
				String[] brokenLine = wholeLine[i].split(" ");
				board[i][j] = brokenLine[j];
			}
		}

		s1.close();
		s2.close();

		return board;
	}

	// return whether the point will be in bounds
	public static boolean inBounds(int i, int j)
	{
		return ((i >= 0) && (i < totRow) && (j >= 0) && (j < totCol));
	}

	// return the larger of the two ints
	private static int max(int num1, int num2) {
		if(num1 > num2)
			return num1;
		else
			return num2;
	}

	// returns the absolute value of an int (java.lang is overrated)
	public static int abs(int num)
	{
		if(num < 0)
			return -num;
		else
			return num;
	}

	// Java can't do modulo arithmetic correctly, so I made this
	public static int correctMod(int dividend, int divisor)
	{
		if(dividend < 0)
			return correctMod(dividend+divisor, divisor);

		return dividend % divisor;
	}
}
