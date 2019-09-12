package com.util;

import com.entity.Codel;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.entity.Board;

/**
 * The basic unit of Piet code is the colour block. A colour block is a contiguous block of any number of codels of one colour, bounded by 
 *   blocks of other colours of by the edge of the program graphic.
 * Blocks of colour adjacent only diagonally are not considered contiguous.
 * A colour block may be any shape and may have "holes" of other colours inside it, which are not considered part of the block.
 */
public class Block {

	private static final Logger log = LoggerFactory.getLogger(Block.class);

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

	public Color codelIntoString(String val) {
		for(Color c : Color.values()) {
			if(c.getRBG().equals(val)) {
				return c;
			}
		}

		return Color.WHITE;
	}
}
