package com.util;

import com.entity.Codel;
import com.google.gson.GsonBuilder;
import com.entity.Board;

/**
 * The basic unit of Piet code is the colour block. A colour block is a contiguous block of any number of codels of one colour, bounded by 
 *   blocks of other colours of by the edge of the program graphic.
 * Blocks of colour adjacent only diagonally are not considered contiguous.
 * A colour block may be any shape and may have "holes" of other colours inside it, which are not considered part of the block.
 */
public class Block {
	private Integer size;
	private Color color;

	// eight types of corner
	private Codel rightTop = new Codel();	
	private Codel rightBottom = new Codel();
	private Codel bottomRight = new Codel();	
	private Codel bottomLeft = new Codel();
	private Codel leftBottom = new Codel();	
	private Codel leftTop = new Codel();
	private Codel topLeft = new Codel();	
	private Codel topRight = new Codel();

	public Block(Board board, Codel coordinate) {
		//System.out.println("NEW CODEL");
		//System.out.println(coordinate.toString());
		this.color = board.getColor(coordinate);
		this.rightTop = coordinate;
		this.rightBottom = coordinate;
		this.bottomRight = coordinate;
		this.bottomLeft = coordinate;
		this.leftBottom = coordinate;
		this.leftTop = coordinate;
		this.topLeft = coordinate;
		this.topRight = coordinate;
	}

	public Integer getSize() {
		return this.size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getColorName() {
		return this.color.getName();
	}
	
	public String getHexValue() {
		return this.color.getRBG();
	}

	public Integer getValue() {
		return (this.color.getHue() * 10) + this.color.getLight();
	}

	public Codel getRightTop() {
		return this.rightTop;
	}

	public void setRightTop(Codel coordinate) {
		this.rightTop = coordinate;
	}

	public Codel getRightBottom() {
		return this.rightBottom;
	}

	public void setRightBottom(Codel coordinate) {
		this.rightBottom = coordinate;
	}

	public Codel getBottomRight() {
		return this.bottomRight;
	}

	public void setBottomRight(Codel coordinate) {
		this.bottomRight = coordinate;
	}

	public Codel getBottomLeft() {
		return this.bottomLeft;
	}

	public void setBottomLeft(Codel coordinate) {
		this.bottomLeft = coordinate;
	}

	public Codel getLeftBottom() {
		return this.leftBottom;
	}

	public void setLeftBottom(Codel coordinate) {
		this.leftBottom = coordinate;
	}

	public Codel getLeftTop() {
		return this.leftTop;
	}

	public void setLeftTop(Codel coordinate) {
		this.leftTop = coordinate;
	}

	public Codel getTopLeft() {
		return this.topLeft;
	}

	public void setTopLeft(Codel coordinate) {
		this.topLeft = coordinate;
	}

	public Codel getTopRight() {
		return this.topRight;
	}

	public void setTopRight(Codel coordinate) {
		this.topRight = coordinate;
	}

	@Override
	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

	public void debugCodel() {
		//System.out.println("\tCodel's right-most & upper-most block is at " + this.rightTop.getX() + ", " + this.rightTop.getY());
		//System.out.println("\tCodel's right-most & lower-most block is at " + this.rightBottom.getX() + ", " + this.rightBottom.getY());
		//System.out.println("\tCodel's bottom-most & right-most block is at " + this.bottomRight.getX() + ", " + this.bottomRight.getY());
		//System.out.println("\tCodel's bottom-most & left-most block is at " + this.bottomLeft.getX() + ", " + this.bottomLeft.getY());
		//System.out.println("\tCodel's left-most & bottom-most block is at " + this.leftBottom.getX() + ", " + this.leftBottom.getY());
		//System.out.println("\tCodel's left-most & top-most block is at " + this.leftTop.getX() + ", " + this.leftTop.getY());
		//System.out.println("\tCodel's upper-most & left-most block is at " + this.topLeft.getX() + ", " + this.topLeft.getY());
		//System.out.println("\tCodel's upper-most & right-most block is at " + this.topRight.getX()+ ", " + this.topRight.getY());
		//System.out.println();
	}

	public Color codelIntoString(String val) {
		//System.out.println("PASSING IN " + val);
		for(Color c : Color.values()) {
			if(c.getRBG().equals(val)) {
				try {
					Thread.sleep(1000);
					}
					catch(InterruptedException e) {
						//System.out.println(e);
					}
				//System.out.println("FOUND COLOR: " + c);
				return c;
			}
		}
		try {
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {
			//System.out.println(e);
		}
		return Color.WHITE;
	
		// switch(val)
		// {
		// 	case "0": return "black";			
		// 	case "1": return "white";
		// 	case "10": return "light red";		
		// 	case "11": return "red";
		// 	case "12": return "dark red";
		// 	case "20": return "light yellow";	
		// 	case "21": return "yellow"; 		
		// 	case "22": return "dark yellow";
		// 	case "30": return "light green";		
		// 	case "31": return "green"; 		
		// 	case "32": return "dark green";
		// 	case "40": return "light cyan";		
		// 	case "41": return "cyan";		
		// 	case "42": return "dark cyan";
		// 	case "50": return "light blue"; 		
		// 	case "51": return "blue";		
		// 	case "52": return "dark blue";
		// 	case "60": return "light magenta";
		// 	case "61": return "magenta";		
		// 	case "62": return "dark magenta";
		// }

		// return "white";

		// red -> yellow -> green -> cyan -> blue -> magenta
		//  11		  21       31      41      51         61

		// black / white
		//	     0       1

	}
}
