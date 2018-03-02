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
		System.out.println("Codel's right top-most block is at [" + this.rightCol + ", " + this.rightTopRow + "] "
				+ "and right bottom-most block is at [" + this.rightCol + ", " + this.rightBottomRow + "]");
		System.out.println("Codel's left top-most block is at [" + this.leftCol + ", " + this.leftTopRow + "] "
				+ " and left bottom-most block is at [" + this.leftCol + ", " + this.leftBottomRow + "]");
		System.out.println("Codel's upper most row: " + this.topRow);
		System.out.println("Codel's lowest row: " + this.bottomRow);
	}
}