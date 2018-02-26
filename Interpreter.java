import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Codel
{
	int size;
	int colorVal;
	String colorName;
	
	int rightCol;
	int rightRow;
	
	Codel(int colorVal, String colorName, int rightCol, int rightRow)
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
		
		String[][] board = readFile("pietcode.txt");
		
		for(int i = 0; i < totRow; i++)
		{
			for(int j = 0; j < totCol; j++)
				System.out.print(board[i][j] + " ");
			System.out.println();
		}
		
		readBoard(board);
	}
	
	public static void readBoard(String[][] board)
	{
		int startX = 0, startY = 0;

		boolean[][] visited = new boolean[totRow][totCol];
		for(int i = 0; i < totRow; i++)
			for(int j = 0; j < totCol; j++)
				visited[i][j] = false;
		
		int num = 1 + getSizeCodel(board, visited, board[0][0], startX, startY, 0, 0);
		System.out.println(num);
	}
	
	public static int getSizeCodel(String[][] board, boolean[][] visited, String init, int nextX, int nextY, int i, int j)
	{
		if(!inBounds(i, j))
			return 0;
		if(visited[i][j])
			return 0;
		
		System.out.println("We're in bounds and haven't been visited at " + i + ", " + j);
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
			
			System.out.println(newI + " and " + newJ + " are still in bounds. continuing");
			
			System.out.println("The new box is " + board[newI][newJ]);
			
			if(visited[newI][newJ])
				continue;

			System.out.println(newI + " <- newI | newJ -> " + newJ);
			
			System.out.println("init is : " + init + " – Here: " + board[newI][newJ]);
			
			System.out.println(init + " is equal to " + board[newI][newJ] + "?");
			
			String s1 = init;
			String s2 = board[newI][newJ];
			
			if(!s1.equals(s2))
			{
				continue;
			}
			else
				System.out.println("No");
			count++;
			System.out.println("Yes");
			System.out.println("About to return with 1+");
			return 1 + getSizeCodel(board, visited, init, newI, newJ, newI, newJ);
		}
		return count;
	}
	
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

		
		return board;
	}
	
	public static String codelChosen(String dp, String cc)
	{
		switch(dp)
		{
			case "right":
				switch(cc)
				{
					case "left":
						return "up";
					case "right":
						return "down";
				}
			case "down":
				switch(cc)
				{
					case "left":
						return "right";
					case "right":
						return "left";
				}
			case "left":
				switch(cc)
				{
					case "left":
						return "down";
					case "right":
						return "up";
				}
			case "up":
				switch(cc)
				{
					case "left":
						return "left";
					case "right":
						return "right";
				}	
		}
		
		return "up";
	}
	
	public static boolean inBounds(int i, int j)
	{
		return ((i >= 0) && (i < totRow) && (j >= 0) && (j < totCol));
	}

	private static int max(int num1, int num2) {
		if(num1 > num2)
			return num1;
		else 
			return num2;
	}
}
