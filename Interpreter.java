import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class Interpreter {

	private static int totRow;		// total number of rows
	private static int totCol;		// total number of columns

	private static String dp = "right";
	private static String cc = "left";
	
	private static Stack<Integer> stack = new Stack<Integer>();
	
	private static int lastRotate = 0;
	private static boolean end = false;
	private static boolean clearQ = false;

	// Takes in a .ppm file as an argument, and runs it as a Piet program
	// The Piet program is described at http://www.dangermouse.net/esoteric/piet.html
	public static void main(String[] args) throws IOException
	{
			
		// input file is passed in as an argument, guess it could be passed in without the .extension
		File oldfile = new File(args[0]);
		String firstBit = oldfile.getName().split("\\.")[0];
		File newfile = new File(firstBit + ".txt");
		
		// here we're renaming it in order to get the colors as an RGB trio
		oldfile.renameTo(newfile);
		
		// passing in the file name to get parsed
		convertFile(newfile.getName());
		
		String[][] board = readFile("tmp.txt");

		// performing the actual file 
		readBoard(board);
		
		// want our original file back
		File endfile = new File(firstBit + ".ppm");
		newfile.renameTo(endfile);
	}
	
	// takes in a .txt file and converts the colors into my specified format
	public static void convertFile(String s) throws IOException, FileNotFoundException
	{
		File in = new File(s);
		PrintWriter writer = new PrintWriter("tmp.txt", "UTF-8");
		Scanner sc = new Scanner(in);
		
		// skipping the first few lines
//		sc.nextLine(); // the .ppm title
	
//		String newLine = sc.nextLine();
		
		// ignoring the comments
//		if(newLine.contains("#")) 
////		System.out.println(sc.nextLine()); 
//			newLine = sc.nextLine();
//		else
//			newLine = newLine;
		
		String[] threeLines = new String[3];

//		sc.nextLine();
		String newLine = "";
		int firstThreeLines = 0;
		while(firstThreeLines < 3)
		{
			newLine = sc.nextLine();
			if(newLine.contains("#"))
				newLine = sc.nextLine();
			
			threeLines[firstThreeLines] = newLine;
			firstThreeLines++;
			
		}
		
//		System.out.println(threeLines[0] + "\n" + threeLines[1] + "\n" + threeLines[2]);
		String[] colrow = threeLines[1].split("\\s");
		totCol = Integer.parseInt(colrow[0]);
		totRow = Integer.parseInt(colrow[1]);
		
		String appendThis = "";
		for(int i = 0; i < totCol*totRow; i++)
		{
			if(i % totCol == 0 && i != 0)
				appendThis += "\n";

			int r, g, b;
			r = sc.nextInt();
			g = sc.nextInt();
			b = sc.nextInt();
			
			if(r == 0x00)
			{
				if(g == 0x00)
				{
					if(b == 0x00)		appendThis += "00 ";
					else if(b == 0xC0)	appendThis += "52 ";
					else if(b == 0xFF)	appendThis += "51 ";
				}
				else if(g == 0xC0)
				{
					if(b == 0x00)		appendThis += "32 ";
					else if(b == 0xC0)	appendThis += "42 ";
				}
				else if(g == 0xFF)
				{
					if(b == 0x00)		appendThis += "31 ";
					else if(b == 0xFF)	appendThis += "41 ";
				}
			}
			else if(r == 0xC0)
			{
				if(g == 0x00)
				{
					if(b == 0x00)		appendThis += "12 ";
					else if(b == 0xC0)	appendThis += "62 ";
				}
				else if(g == 0xC0)
				{
					if(b == 0x00)		appendThis += "22 ";
					else if(b == 0xFF)	appendThis += "50 ";
				}
				else if(g == 0xFF)
				{
					if(b == 0xC0)		appendThis += "30 ";
					else if(b == 0xFF)	appendThis += "40 ";
				}
			}
			else if(r == 0xFF)
			{
				if(g == 0x00)
				{
					if(b == 0x00)		appendThis += "11 ";
					else if(b == 0xFF)	appendThis += "61 ";
				}
				else if(g == 0xC0)
				{
					if(b == 0xC0)		appendThis += "10 ";
					else if(b == 0xFF)	appendThis += "60 ";
				}
				else if(g == 0xFF)
				{
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
	public static String[][] readFile(String s) throws FileNotFoundException
	{
		
		File infile = new File(s);
		
		Scanner s2 = new Scanner(infile);

		String[] wholeLine = new String[totRow];
		for(int i = 0; i < totRow; i++)
		{
			wholeLine[i] = s2.nextLine();
			String[] brokenLine = wholeLine[i].split(" ");
			totCol = (totCol > brokenLine.length) ? totCol : brokenLine.length;
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

		s2.close();

		return board;
	}
	
	// by now we've read in the file and pass it in as board
	public static void readBoard(String[][] board)
	{

		boolean[][] visited = new boolean[totRow][totCol];
		for(int i = 0; i < totRow; i++)
			for(int j = 0; j < totCol; j++)
				visited[i][j] = false;

		int nextRow = 0;
		int nextCol = 0;
		
		Codel[] codels = new Codel[2];
	
		// initiate the very first Codel
		int[] f = {nextRow, nextCol};
		codels[0] = new Codel(f, board);
		int initSize = 1 + findSizeCodel(board, visited, codels[0], f[0], f[1]);
		codels[0].size = initSize;
		codels[0].printCodel();
		
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
			codels[1].size = initSize;
			
			if(codels[1].colorName.equals("white"))
			{
				System.out.println("one is white\n\n\n");
				System.out.println("dp: " + dp);
				codels[0] = codels[1];
				continue;
			}
			
			// perform the command given by the two Codel's
			System.out.println();
			codels[1].printCodel();

			System.out.println("Computing command for blocks: " + codels[0].colorName + " and " + codels[1].colorName);
			getCommand(codels[0], codels[1]);
			
			// shift the new Codel to the old one's spot
			codels[0] = codels[1];
						
			// if I want to print the stack for debuggin
			System.out.println(Arrays.toString(stack.toArray()));
			
		}
		
		System.out.println("guess we're done here");
		
	}
	
	public static int findSizeCodel(String[][] board, boolean[][] visited, Codel c, int a, int b)
	{
		if(c.rightTop[0] == -1)
		{
			setCorners(c, a, b);
		}
		int count = 0;
		visited[a][b] = true;
		
		int[] left = {a, b-1};
		int[] right = {a, b+1};
		int[] up = {a-1, b};
		int[] down = {a+1, b};
		int[] newCoords = {-1, -1};
		for(int i = 0; i < 4; i++)
		{
			if(i == 0)
				newCoords = left;
			else if(i == 1)
				newCoords = right;
			else if(i == 2)
				newCoords = up;
			else if(i == 3)
				newCoords = down;
			
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

	// Here, col1 represents the last color, col2 is the newest color
	public static void getCommand(Codel cod1, Codel cod2)
	{

		String col1 = cod1.colorVal;
		String col2 = cod2.colorVal;
		
		if(Integer.parseInt(cod1.colorVal) == 1 || Integer.parseInt(cod1.colorVal) == 1)
		{	
			clearQ = true;
			return;
		}
		
		int hueChange = Character.getNumericValue(col2.charAt(0)) - Character.getNumericValue(col1.charAt(0));
		int lightChange = Character.getNumericValue(col2.charAt(1)) - Character.getNumericValue(col1.charAt(1));

		if(hueChange < 0)
			hueChange += 6;
		if(lightChange < 0)
			lightChange += 3;
				
		int val1, val2;
		
	try {
		switch(hueChange)
		{
			case 0:
				switch(lightChange)
				{
					case 0:
						return;
					case 1:	// push
						System.out.println("pushing " + cod1.size);
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
						System.out.println("add");
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
						System.out.println("multiply");
						val2 = stack.pop();
						val1 = stack.pop();
						stack.push(val1 * val2);
						return;
				}
			case 2:
				switch(lightChange)
				{
					case 0: // divide
						System.out.println("divide");
						val2 = stack.pop();
						val1 = stack.pop();
						stack.push(val1 / val2);
						return;
					case 1: // mod 
						System.out.println("mod");
						val2 = stack.pop();
						val1 = stack.pop();
						stack.push(correctMod(val1, val2));
						return;
					case 2: // not
						System.out.println("not");
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
						System.out.println("greater");
						val2 = stack.pop();
						val1 = stack.pop();
						if(val1 > val2)
							stack.push(1);
						else
							stack.push(0);
						return;
					case 1: // pointer
						System.out.println("pointer");
						val2 = stack.pop();
						rotateDP(val2);
						return;
					case 2: // switch
						System.out.println("switch");
						val2 = stack.pop();
						rotateCC(val2);
						return;
				}
			case 4:
				switch(lightChange)
				{
					case 0:	// duplicate
						System.out.println("duplicate");
						val2 = stack.pop();
						stack.push(val2);
						stack.push(val2);
						return;
					case 1: // roll
						System.out.println("roll");
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
						int j = i;
						stack.push(j);
						return;
					case 1: // outNum
						System.out.println("out num");
						int k = stack.pop();
						System.out.print(k);
						return;
					case 2: // outChar
						System.out.println("out char");
						int l = stack.pop();
						char m = (char) l;
						System.out.print(m);
						return;
				}
		}
	} catch(EmptyStackException e) {	}
	
		return;
	}
	
	// newX = row [0], newY = column [1]
	public static void setCorners(Codel c, int newX, int newY)
	{
//		System.out.println("Testing new corner at " + newX + ", " + newY);
		
		// potential for new right column value
		if(newY >= c.rightTop[1] || c.rightTop[1] == -1)
		{
			// if further right
			if(newY > c.rightTop[1] || c.rightTop[1] == -1) 
			{
				c.rightTop[1] = newY;
				c.rightTop[0] = newX;
			}
			// if further up
			if(newX < c.rightTop[0] || c.rightTop[0] == -1)
			{
				c.rightTop[1] = newY;
				c.rightTop[0] = newX;
			}
			// if further right
			if(newY > c.rightBottom[1] || c.rightBottom[1] == -1)
			{
				c.rightBottom[1] = newY;
				c.rightBottom[0] = newX;
			}
			// if further down
			if(newX > c.rightBottom[0] || c.rightBottom[0] == -1)
			{
				c.rightBottom[1] = newY;
				c.rightBottom[0] = newX;
			}
		}

		// potential for new bottom row value
		if(newX >= c.bottomRight[0] || c.bottomRight[0] == -1)
		{
			// if further down
			if(newX > c.bottomRight[0] || c.bottomRight[0] == -1)
			{
				c.bottomRight[0] = newX;
				c.bottomRight[1] = newY;
			}
			// if further right
			if(newY > c.bottomRight[1] || c.bottomRight[1] == -1)
			{
				c.bottomRight[1] = newY;
				c.bottomRight[0] = newX;
			}
			// if further down
			if(newX > c.bottomLeft[1] || c.bottomLeft[1] == -1)
			{
				c.bottomLeft[1] = newY;
				c.bottomLeft[0] = newX;
			}
			// if further left
			if(newY < c.bottomLeft[1] || c.bottomLeft[1] == -1 || c.bottomLeft[0] == -1)
			{
				c.bottomLeft[1] = newY;
				c.bottomLeft[0] = newX;
			}
		}
		
		// potential for new left column value
		if(newY <= c.leftBottom[1] || c.leftBottom[1] == -1)
		{
			// if further left
			if(newY < c.leftBottom[1] || c.leftBottom[1] == -1)
			{
				c.leftBottom[1] = newY;
				c.leftBottom[0] = newX;
			}
			// if further down
			if(newX > c.leftBottom[0] || c.leftBottom[0] == -1)
			{
				c.leftBottom[1] = newY;
				c.leftBottom[0] = newX;
			}
			// if further left
			if(newY < c.leftTop[0] || c.leftTop[0] == -1)
			{
				c.leftTop[0] = newX;
				c.leftTop[1] = newY;
			}
			// if further up
			if(newX < c.leftTop[0] || c.leftTop[0] == -1)
			{
				c.leftTop[0] = newX;
				c.leftTop[1] = newY;
			}
		}

		// potential for upper-most row
		if(newX <= c.topLeft[0] || c.topLeft[0] == -1)
		{
			// if further up
			if(newX < c.topLeft[0] || c.topLeft[0] == -1)
			{
				c.topLeft[0] = newX;
				c.topLeft[1] = newY;
			}	
			// if further left
			if(newY < c.topLeft[1] || c.topLeft[1] == -1)
			{
				c.topLeft[1] = newY;
				c.topLeft[0] = newX;
			}
			// if further up
			if(newX < c.topRight[0] || c.topRight[1] == -1)
			{
				c.topRight[0] = newX;
				c.topRight[1] = newY;
			}
			// if further right
			if(newY > c.topRight[1] || c.topRight[1] == -1)
			{
				c.topRight[1] = newY;
				c.topRight[0] = newX;
			}
		}
	}

	// return whether the point will be in bounds
	public static boolean inBounds(int i, int j)
	{
		return ((i >= 0) && (i < totRow) && (j >= 0) && (j < totCol));
	}


	// Java can't do modulo arithmetic correctly, so I made this
	public static int correctMod(int dividend, int divisor)
	{
		while(dividend < 0)
			dividend += divisor;

		return dividend % divisor;
	}
	
	// the rotateDP command takes up a lot of space, which could be eliminated by making dp an int	
	public static void rotateDP(int val)
	{
		lastRotate = 0;
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
	
	// If rotateDP gets its own method then so does rotateCC
	public static void rotateCC(int val)
	{
		lastRotate = 1;
		if(correctMod(val, 2) == 2)
			return;
		else
			if(cc.equals("right"))
				cc = "left";
			else
				cc = "right";
	}
	
	// we're getting the newest codel, from the old one, c
	// Still don't know exactly how exiting works, gonna work on that
	public static int[] getNextBlock(String[][] board, Codel c, int attempt)
	{
		
		int nextBlock[] = new int[2];

		// we've tried every orientation and can't find a new Codel
		if(attempt > 8)
		{
			System.out.println("DONE");
			int[] fin = {0, 0};
			end = true;
			return fin;
		}
		
		int nextRow = 0, nextCol = 0;
		switch(dp)
		{
			// if we're going right
			case "right":
				switch(cc)
				{
					// we want to start from the right-top
					case "left":
						nextRow = c.rightTop[0];
						nextCol = c.rightTop[1];
						break;
					// we want to start from the right-bottom
					case "right":
						nextRow = c.rightBottom[0];
						nextCol = c.rightBottom[1];
						break;
				}
				nextCol++;
				
////				 theoretical, do this for all dp directions
////				 just moves through white space
//				while(Integer.parseInt(board[nextRow][nextCol]) == 1)
//				{
//					nextCol++;
//				}
				if(!inBounds(nextRow, nextCol) || Integer.parseInt(board[nextRow][nextCol]) == 0)
				{
					if(attempt % 2 == 0)
						rotateCC(1);
					else
						rotateDP(1);
					
					nextCol--;
					return getNextBlock(board, c, attempt + 1);
				}
				break;
			case "down":
				switch(cc)
				{
					case "left":
						nextRow = c.bottomRight[0];
						nextCol = c.bottomRight[1];
						break;
					case "right":
						nextRow = c.bottomLeft[0];
						nextCol = c.bottomLeft[1];
						break;
				}
				nextRow++;
//				while(Integer.parseInt(board[nextRow][nextCol]) == 1)
//				{
//					nextRow++;
//				}
				if(!inBounds(nextRow, nextCol) || Integer.parseInt(board[nextRow][nextCol]) == 0)
				{
					if(attempt % 2 == 0)
						rotateCC(1);
					else
						rotateDP(1);
					nextRow--;
					return getNextBlock(board, c, attempt + 1);
					
				}
				break;
			case "left":
				switch(cc)
				{
					case "left":
						nextRow = c.leftBottom[0];
						nextCol = c.leftBottom[1];
						break;
					case "right":
						nextRow = c.leftTop[0];
						nextCol = c.leftTop[1];
						break;
				}
				nextCol--;
//				while(Integer.parseInt(board[nextRow][nextCol]) == 1)
//				{
//					nextCol--;
//				}
				if(!inBounds(nextRow, nextCol) || Integer.parseInt(board[nextRow][nextCol]) == 0)
				{
					if(attempt % 2 == 0)
						rotateCC(1);
					else
						rotateDP(1);
					
					nextCol++;
					return getNextBlock(board, c, attempt + 1);
				}
				break;
			case "up":
				switch(cc)
				{
					case "left":
						nextRow = c.topLeft[0];
						nextCol = c.topLeft[1];
						break;
					case "right":
						nextRow = c.topRight[0];
						nextCol = c.topRight[1];
						break;
				}
				nextRow--;
//				while(Integer.parseInt(board[nextRow][nextCol]) == 1)
//				{
//					nextRow--;
//				}
				if(!inBounds(nextRow, nextCol) || Integer.parseInt(board[nextRow][nextCol]) == 0)
				{
					if(attempt % 2 == 0)
						rotateCC(1);
					else
						rotateDP(1);
					nextRow++;
					return getNextBlock(board, c, attempt + 1);
				}
				break;
		}

		nextBlock[0] = nextRow;
		nextBlock[1] = nextCol;

		return nextBlock;
	}
}
