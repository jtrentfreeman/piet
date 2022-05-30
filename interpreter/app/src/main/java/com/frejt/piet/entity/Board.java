package com.frejt.piet.entity;

import com.frejt.piet.util.Color;

/**
 * A Board is an object representation of a Piet program.
 * <br>
 * Each board has a coordinate plane, where each coordinate represents a single
 * {@link Codel}.
 * <br>
 * A board is inverted, such that the x value accounts for the "x" index
 * accounts for the rows, while the "y" index accounts for the columns.
 * 
 * <pre>
 *     y
 *  
 * x   0  1  2  3  4  5  ...  sizeCol
 *     1
 *     2
 *     3
 *     4
 *     5
 *    ...
 *  sizeRow
 * </pre>
 */
public class Board {

    Color[][] board;
    Boolean[][] visited;

    private Integer sizeRow;
    private Integer sizeCol;

    public Board(int sizeRow, int sizeCol) {
        this.sizeRow = sizeRow;
        this.sizeCol = sizeCol;

        board = new Color[sizeRow][sizeCol];
        visited = new Boolean[sizeRow][sizeCol];
        this.setVisitedAll(false);
    }

    public void setColor(Codel coordinate, Color color) {
        this.board[coordinate.getX()][coordinate.getY()] = color;
    }

    public Color getColor(Codel coordinate) {
        return this.board[coordinate.getX()][coordinate.getY()];
    }

    public void setVisited(Codel coordinate, Boolean visited) {
        this.visited[coordinate.getX()][coordinate.getY()] = visited;
    }

    public void setVisitedAll(Boolean visited) {
        for (int i = 0; i < this.visited.length; i++) {
            for (int j = 0; j < this.visited[i].length; j++) {
                this.visited[i][j] = visited;
            }
        }
    }

    /**
     * @param coordinate the Codel with the coordinates to be checked for visited
     *                   stat
     * @return true if {@link Codel} has been visited
     */
    public Boolean getVisited(Codel coordinate) {
        return this.visited[coordinate.getX()][coordinate.getY()];
    }

    public Integer getSizeRow() {
        return this.sizeRow;
    }

    public Integer getSizeCol() {
        return this.sizeCol;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                str.append(String.valueOf(this.board[i][j].getRBG()) + " ");
            }
            str.append("\n");
        }

        return str.toString();
    }

}