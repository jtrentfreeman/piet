class Codel
{
	int size;
	String colorVal;
	String colorName;

//	int leftCol; 	int leftTopRow;		int leftBottomRow;
//	int rightCol;	int rightTopRow;		int rightBottomRow;
	
//						row, col
	int[] rightTop = 	{-1, -1};
	int[] bottomRight = 	{-1, -1};
	int[] leftBottom = 	{-1, -1};
	int[] topLeft = 		{-1, -1};
	
	int topRow;		int topLeftCol;		int topRightCol;
	int bottomRow; 	int bottomLeftCol;	int bottomRightCol;
	
	int[][] yesBoard = new int[30][30];
	
	Codel()
	{
		
	}

	Codel(String colorVal, String colorName)
	{
		this.colorVal = colorVal;
		this.colorName = colorName;
	}
	
	public void printCodel()
	{
		System.out.println("Codel size: " + this.size);
//		System.out.println("Codel color value: " + this.colorVal);
		System.out.println("Codel color: " + this.colorName);
		System.out.println("Codel's right-most & up-most block is at " + this.rightTop[0] + ", " + this.rightTop[1]);
		System.out.println("Codel's bottom-most & right-most block is at " + this.bottomRight[0] + ", " + this.bottomRight[1]);
		System.out.println("Codel's left-most & bottom-most block is at " + this.leftBottom[0] + ", " + this.leftBottom[1]);
		System.out.println("Codel's top-most & left-most block is at " + this.topLeft[0] + ", " + this.topLeft[1]);
	}
	
	public void printYesBoard(int numR, int numC)
	{
		for(int i = 0; i < numR; i++)
		{
			for(int j = 0; j < numC; j++)
			{
				System.out.print("i = " + i + "\tj = " + j + "\t");
				System.out.print(this.yesBoard[numR][numC] + "\t\t");
			}
			System.out.println();
		}
	}
}