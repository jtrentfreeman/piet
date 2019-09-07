package com.entity;

import com.util.Color;

/**
 * A board is inverted
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
 */
public class Board {

    Color[][] board;
    Boolean[][] visited;

    public Board(int sizeRow, int sizeCol) {
        board = new Color[sizeRow][sizeCol];
        visited = new Boolean[sizeRow][sizeCol];
    }

    public void setColor(Coordinate coordinate, Color color) {
        this.board[coordinate.getX()][coordinate.getY()] = color;
    }

    public Color getColor(Coordinate coordinate) {
        return this.board[coordinate.getX()][coordinate.getY()];
    }

    public void setVisited(Coordinate coordinate, Boolean visited) {
        this.visited[coordinate.getX()][coordinate.getY()] = visited;
    }

    public void setVisitedAll(Boolean visited) {
        for(int i = 0; i < this.visited.length; i++) {
            for(int j = 0; j <this.visited[i].length; j++) {
                this.visited[i][j] = visited;
            }
        }
    }

    /**
     * @return true if {@link Coordinate} has been visited
     */
    public Boolean getVisited(Coordinate coordinate) {
        return visited[coordinate.getX()][coordinate.getY()];
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < this.board.length; i++) {
            for(int j = 0; j < this.board[i].length; j++) {
                str.append(String.valueOf(this.board[i][j].getRBG()) + " ");
            }
            str.append("\n");
        }

        return str.toString();
    }

}