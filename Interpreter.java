import java.io.*;
import java.util.*;

public class Interpreter {

	private static int totRow;		// total number of rows
	private static int totCol;		// total number of columns

	private static String dp = "right";
	private static String cc = "left";
	
	private static Stack<Integer> stack = new Stack<Integer>();

	// This assumes we have a Piet photo that has been transcribed into numbers (I will, at some point, make this program separate)
	// The Piet program is described at http://www.dangermouse.net/esoteric/piet.html
	public static void main(String[] args) throws FileNotFoundException
	{
		
		String[][] board = readFile("pietcode.txt");
		
		int[] f = {0, 0};
		int[] g = {0, 1};
		Codel cod1 = new Codel(f, board);
		Codel cod2 = new Codel(g, board);

		readBoard(board);
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
		System.out.println(totCol);

		s1.close();
		s2.close();

		return board;
	}
	
	// by now we've read in the file and pass it in through board
	public static void readBoard(String[][] board)
	{

		boolean[][] visited = new boolean[totRow][totCol];
		for(int i = 0; i < totRow; i++)
			for(int j = 0; j < totCol; j++)
				visited[i][j] = false;

		int nextRow = 0;
		int nextCol = 0;
		
		Codel[] codels = new Codel[2];
	
		// initiate
		int[] f = {nextRow, nextCol};
		codels[0] = new Codel(f, board);
		int initSize = 1 + findSizeCodel(board, visited, codels[0], f[0], f[1]);

		codels[0].size = initSize;
//		codels[0].printCodel();

		int nextCodel;
		nextCodel = codelChosen();
		
		int[] priorityXY = new int[3];
		priorityXY = getPriorityXY(nextCodel, codels[0]);
//		System.out.println(priorityXY[0] + " " + priorityXY[1] + " " + priorityXY[1]);

		while(true)
		{
			nextRow = codels[0].rightTop[0];
			nextCol = codels[0].rightTop[1];
			nextCol++;

			while(Integer.parseInt(board[nextRow][nextCol]) == 1 && dp.equals("right"))
			{
				nextCol++;
			}
	
//			System.out.println(board[nextRow][nextCol]);
			int[] g = {nextRow, nextCol};
			codels[1] = new Codel(g, board);
			initSize = 1+findSizeCodel(board, visited, codels[1], g[0], g[1]);
			codels[1].size = initSize;
//			codels[1].printCodel();
			
			getCommand(codels[0], codels[1]);
//			System.out.println(Arrays.toString(stack.toArray()));
			codels[0] = codels[1];
			nextRow = codels[0].rightTop[0];
			nextCol = codels[0].rightTop[1];
			
//			System.out.println(Arrays.toString(stack.toArray()));
		}
		
//		init = init2;
//		
//		nextCol++;
//		while(Integer.parseInt(board[nextRow][nextCol]) == 1 && dp.equals("right"))
//		{
//			nextCol++;
//		}
//
//		System.out.println(board[nextRow][nextCol]);
//		g[0] = nextRow;
//		g[1] = nextCol;
//		System.out.println(nextRow + " " + nextCol);
//		init2 = new Codel(g, board);
//		System.out.println(board[g[0]][g[1]]);
//		initSize = 1 + findSizeCodel(board, visited, init2, g[0], g[1]);
//		init2.size = initSize;
//		init2.printCodel();
//		
//		getCommand(init, init2);

	}
	


	// get the size of the codel being input
	public static int findSizeCodel(String[][] board, boolean[][] visited, Codel c, int nextX, int nextY)
	{
		// this codel is uninitiated
		if(c.rightTop[0] == -1)
		{
			setCorners(c, nextX, nextY);
		}
		
		visited[nextX][nextY] = true;

		int[][] moves = { {0, -1}, {0, 1}, {-1, 0}, {1, 0} };
		int newX = 0, newY = 0;
		int count = 0;
		
		// moving in 4 cardinal directions
		for(int m = 0; m < 4; m++)
		{
			newX = nextX + moves[m][0];
			newY = nextY + moves[m][1];

			if(!inBounds(newX, newY))
				continue;

			if(visited[newX][newY])
				continue;

//			System.out.println("We're looking for colorCode " + c.colorVal + ". At [" + newX + " " + newY + "] is colorCode " + board[newX][newY]);
//			System.out.println("newest colorcode : " + board[newX][newY]);
			
			String colorCode = c.colorVal;
			String s1 = colorCode;
			String s2 = board[newX][newY];
//			System.out.println("newX : " + newX + "\t\tnewY : " + newY);
			
			if(!s1.equals(s2))
				continue;
			
//			System.out.println("We're here from " + newX + " and " + newY);
			
//			System.out.println("Our colors match at row " + newX + " and column " + newY);
//			System.out.println("[0][1] : " + board[0][1]);
			setCorners(c, newX, newY);
			
			c.yesBoard[newX][newY] = 1;
			count++;
//			System.out.println("Yes");
//			System.out.println("About to return with 1+");
			int num = findSizeCodel(board, visited, c, newX, newY);
			count += num;
		}
		
//		c.printYesBoard(totRow, totCol);
		if(inBounds(newX, newY))
			return count + findSizeCodel(board, visited, c, newX, newY);

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
					case "right":	return 0;	
					case "left": 	return 1;
				}
			case "down":
				switch(cc)
				{
					case "right":	return 2;
					case "left":		return 3;
				}
			case "left":
				switch(cc)
				{
					case "right":	return 4;
					case "left":		return 5;
				}
			case "up":
				switch(cc)
				{
					case "right":	return 6;
					case "left":		return 7;
				}
		}

		return 0;
	}

	//

	// Here, col1 represents the last color, col2 is the newest color
	public static void getCommand(Codel cod1, Codel cod2)
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

		int val1, val2;
		
//		System.out.println("hue: " + hueChange);
//		System.out.println("light: " + lightChange);
//		System.out.print("Working with codel num: " + cod2.colorVal + "\t");
		try {
		switch(hueChange)
		{
			case 0:
				switch(lightChange)
				{
					case 0:
						return;
					case 1:	// push
//						System.out.println("pushing " + cod1.size);
						stack.push(cod1.size);
//						System.out.println(cod1.size);
						return;
					case 2: // pop
						System.out.println("pop");
						stack.pop();
						
						return;
				}
			case 1:
				switch(lightChange)
				{
					case 0: // add 
//						System.out.println("add");
						val2 = stack.pop();
						val1 = stack.pop();
						stack.push(val1 + val2);
						return;
					case 1: // subtract
						System.out.println("sub");
						val2 = stack.pop();
						val1 = stack.pop();
						stack.push(val1 - val2);
						return;
					case 2: // multiply
//						System.out.println("multiply");
						val2 = stack.pop();
						val1 = stack.pop();
						stack.push(val1 * val2);
						return;
				}
			case 2:
				switch(lightChange)
				{
					case 0: // divide
						val2 = stack.pop();
						val1 = stack.pop();
						stack.push(val1 / val2);
						return;
					case 1: // mod 
						val2 = stack.pop();
						val1 = stack.pop();
						stack.push(correctMod(val1, val2));
						return;
					case 2: // not
						val2 = stack.pop();
						if(val2 == 0)
							stack.push(1);
						else
							stack.push(0);
						return;
				}
			case 3:
				switch(lightChange)
				{
					case 0: // greater
						val2 = stack.pop();
						val1 = stack.pop();
						if(val1 > val2)
							stack.push(1);
						else
							stack.push(0);
						return;
					case 1: // pointer
						val2 = stack.pop();
						rotateDP(val2);
						return;
					case 2: // switch
						val2 = stack.pop();
						rotateCC(val2);
						return;
				}
			case 4:
				switch(lightChange)
				{
					case 0:	// duplicate
						val2 = stack.pop();
						stack.push(val2);
						stack.push(val2);
						return;
					case 1: // roll
						int size = stack.size();
						val2 = stack.pop();
						val1 = stack.pop();
						while(val2 > 0)
						{
							int rollNum = stack.pop();
							int[] vals = new int[size];
							for(int i = 0; i < val1; i++)
								vals[i] = stack.pop();
							stack.push(rollNum);
							for(int i = val1 - 1; i >= 0; i--)
								stack.push(vals[i]);
							val2--;
						}
						return;
					case 2: // inNum
						System.out.println("in num");
						Scanner s = new Scanner(System.in);
						int i = s.nextInt();
						stack.push(i);
						return;
				}
			case 5:
				switch(lightChange)
				{
					case 0: // inChar
						System.out.println("in char");
						Scanner s = new Scanner(System.in);
						char i = s.next().charAt(0);
						int j = (int) i;
						stack.push(j);
						return;
					case 1: // outNum
						System.out.println("out num");
						int k = stack.pop();
						System.out.print(k);
						return;
					case 2: // outChar
//						System.out.println("out char");
						int l = stack.pop();
						char m = (char) l;
						System.out.print(m);
						return;
				}
		}
		} catch(EmptyStackException e)
		{
			
		}

		return;
	}
	
	public static void setCorners(Codel c, int newX, int newY)
	{
//		System.out.println("Testing new corner at " + newX + ", " + newY);
		
		// new right-most column
		if(newY >= c.rightTop[1] || c.rightTop[1] == -1)
		{
			if(newY > c.rightTop[1] || c.rightTop[1] == -1) 
			{
//				System.out.println("new right-most column");
				c.rightTop[1] = newY;
				c.rightTop[0] = newX;
			}
			if(newX < c.rightTop[0] || c.rightTop[0] == -1)
			{
//				System.out.println("new right-most row");
				c.rightTop[1] = newY;
				c.rightTop[0] = newX;
			}
		}

		// new bottom-most row
		if(newX >= c.bottomRight[0] || c.bottomRight[0] == -1)
		{
			if(newX > c.bottomRight[0] || c.bottomRight[0] == -1)
			{
//				System.out.println("new bottom-most row");
				c.bottomRight[0] = newX;
				c.bottomRight[1] = newY;
			}
			if(newY > c.bottomRight[1] || c.bottomRight[1] == -1)
			{
//				System.out.println("new bottom-most column");
				c.bottomRight[1] = newY;
				c.bottomRight[0] = newX;
			}
		}
		
		// new left-most column
		if(newY <= c.leftBottom[1] || c.leftBottom[1] == -1)
		{
			if(newY < c.leftBottom[1] || c.leftBottom[1] == -1)
			{
//				System.out.println("new left-most column");
				c.leftBottom[1] = newY;
				c.leftBottom[0] = newX;
			}
			if(newX > c.leftBottom[0] || c.leftBottom[0] == -1)
			{
//				System.out.println("new left-most row");
				c.leftBottom[1] = newY;
				c.leftBottom[0] = newX;
			}
		}

		// new top-most row
		if(newX <= c.topLeft[0] || c.topLeft[0] == -1)
		{
			if(newX < c.topLeft[0] || c.topLeft[0] == -1)
			{
//				System.out.println("new top-most row");
				c.topLeft[0] = newX;
				c.topLeft[1] = newY;
			}	
			if(newY < c.topLeft[1] || c.topLeft[1] == -1)
			{
//				System.out.println("new top-most column");
				c.topLeft[1] = newY;
				c.topLeft[0] = newX;
			}
		}
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
		
		int priX = prioritizeX ? 1 : 0;
		
		int[] temp = {priX, nextBoardPlaceX, nextBoardPlaceY};
		return temp;
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
	
	public static void rotateDP(int val)
	{
		if(val == 0)
			return;
		
		if(val > 0)
		{
			while(val > 0)
			{
				if(dp.equals("right"))
					dp = "down";
				else if(dp.equals("down"))
					dp = "left";
				else if(dp.equals("left"))
					dp = "up";
				else
					dp = "right";
				val--;
			}
		}
		if(val < 0)
		{
			while(val < 0)
			{
				if(dp.equals("right"))
					dp = "up";
				else if(dp.equals("up"))
					dp = "left";
				else if(dp.equals("left"))
					dp = "down";
				else
					dp = "right";
				val++;
			}
		}
	}
	
	public static void rotateCC(int val)
	{
		if(correctMod(val, 2) == 2)
			return;
		else
			if(cc.equals("right"))
				cc = "left";
			else
				cc = "right";
	}
}
