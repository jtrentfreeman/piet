import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Codel
{
	int size;
	String colorVal;
	String colorName;

	int rightCol;
	int rightRow;

	Codel(String colorVal, String colorName, int rightCol, int rightRow)
	{
		this.colorVal = colorVal;
		this.colorName = colorName;
		this.rightCol = rightCol;
		this.rightRow = rightRow;
	}
}


public class Interpreter {

	private static int totRow;
	private static int totCol;

	private static String dp = "right";
	private static String cc = "left";

	// This assumes we have a Piet photo that has been transcribed into numbers (I will, at some point, make this program separate)
	// The Piet program is described at http://www.dangermouse.net/esoteric/piet.html
	public static void main(String[] args) throws FileNotFoundException
	{
		Codel cod1 = new Codel("11", "red", 0, 0);
		Codel cod2 = new Codel("12", "dark red", 1, 0);
		System.out.println(getCommand(cod1, cod2));

		String[][] board = readFile("pietcode.txt");

//		for(int i = 0; i < totRow; i++)
//		{
//			for(int j = 0; j < totCol; j++)
//				System.out.print(board[i][j] + " ");
//			System.out.println();
//		}

		readBoard(board);
	}

	// get the size of the codel being input
	public static int getSizeCodel(String[][] board, boolean[][] visited, String init, int nextX, int nextY, int i, int j)
	{
		if(!inBounds(i, j))
			return 0;
		if(visited[i][j])
			return 0;

//		System.out.println("We're in bounds and haven't been visited at " + i + ", " + j);
		visited[i][j] = true;

		int[][] moves = { {0, -1}, {0, 1}, {-1, 0}, {1, 0} };
		int newI, newJ;

		int count = 0;
		for(int m = 0; m < 4; m++)
		{
			newI = i + moves[m][0];
			newJ = j + moves[m][1];

			if(!inBounds(newI, newJ))
				continue;

//			System.out.println(newI + " and " + newJ + " are still in bounds. continuing");

//			System.out.println("The new box is " + board[newI][newJ]);

			if(visited[newI][newJ])
				continue;

//			System.out.println(newI + " <- newI | newJ -> " + newJ);

//			System.out.println("init is : " + init + " – Here: " + board[newI][newJ]);

//			System.out.println(init + " is equal to " + board[newI][newJ] + "?");

			String s1 = init;
			String s2 = board[newI][newJ];

			if(!s1.equals(s2))
			{
				continue;
			}
			else
			{
//				System.out.println("No");
			}
		count++;
//			System.out.println("Yes");
//			System.out.println("About to return with 1+");
			return 1 + getSizeCodel(board, visited, init, newI, newJ, newI, newJ);
		}
		return count;
	}


	// based on the directions of the DP and the CC, return the direction of the codel that will be chosen
	public static String codelChosen(String dp, String cc)
	{
		switch(dp)
		{
			case "right":
				switch(cc)
				{
					case "left": 	return "up";
					case "right":	return "down";
				}
			case "down":
				switch(cc)
				{
					case "left":		return "right";
					case "right":	return "left";
				}
			case "left":
				switch(cc)
				{
					case "left":		return "down";
					case "right":	return "up";
				}
			case "up":
				switch(cc)
				{
					case "left":		return "left";
					case "right":	return "right";
				}
		}

		return "up";
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

	public static void readBoard(String[][] board)
	{

		boolean[][] visited = new boolean[totRow][totCol];
		for(int i = 0; i < totRow; i++)
			for(int j = 0; j < totCol; j++)
				visited[i][j] = false;

		// initiate
		int initSize = 1 + getSizeCodel(board, visited, board[0][0], 0, 0, 0, 0);
//		System.out.println(num);
		Codel init = new Codel(board[0][0], codelIntoString(board[0][0]), 0, 0);
		init.size = initSize;
		printCodel(init);

		/*
		 * TODO:
		 * A: Find the next Codel using DP / CC
		 * B: Get the difference between the last two Codels
		 * C: Perform the operation
		 * GOTO: A
		 */

	}
	
	public static void printCodel(Codel c)
	{
		System.out.println("Codel size: " + c.size);
		System.out.println("Codel color value: " + c.colorVal);
		System.out.println("Codel color: " + c.colorName);
		System.out.println("Codel's right most column: " + c.rightCol);
		System.out.println("Codel's right most row: " + c.rightRow);
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
