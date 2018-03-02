class Codel
{
	int size;
	String colorVal;
	String colorName;

	int leftCol; 	int leftTopRow;		int leftBottomRow;
	int rightCol;	int rightTopRow;		int rightBottomRow;
	
	int topRow;		int topLeftCol;		int topRightCol;
	int bottomRow; 	int bottomLeftCol;	int bottomRightCol;

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
		System.out.println("Codel's right top-most block is at [" + this.rightTopRow + ", " + this.rightCol + "] "
				+ "and right bottom-most block is at [" + this.rightBottomRow + ", " + this.rightCol + "]");
		System.out.println("Codel's left top-most block is at [" + this.leftTopRow + ", " + this.leftCol + "] "
				+ " and left bottom-most block is at [" + this.leftBottomRow + ", " + this.leftCol + "]");
		System.out.println("Codel's upper left-most row is at [" + this.topRow + ", " + this.topLeftCol + "] "
				+ " and upper right-most block is at [" + this.topRow + ", " + this.topRightCol + "]");
		System.out.println("Codel's lowest left-most row is at [" + this.bottomRow + ", " + this.topLeftCol + "] "
				+ " and lowest right-most block is at [" + this.bottomRow + ", " + this.bottomRightCol + "]");
	}
}