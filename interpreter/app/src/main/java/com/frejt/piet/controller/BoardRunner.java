package com.frejt.piet.controller;

import com.frejt.piet.command.CommandRunner;
import com.frejt.piet.director.CC;
import com.frejt.piet.director.DP;
import com.frejt.piet.director.Director;
import com.frejt.piet.entity.Board;
import com.frejt.piet.entity.Codel;
import com.frejt.piet.util.Block;
import com.frejt.piet.util.BlockSet;
import com.frejt.piet.util.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BoardRunner {

    private static final Logger log = LogManager.getLogger(BoardRunner.class);

    private static Program program = Program.getInstance();
    private static Director director = program.getDirector();

    /**
     * Given a {@link Board}, move through the board in order and perform the
     * commands.
     *
     * @param board the Board, holding a grid of {@link Codel}s to be traversed
     */
    public static void runBoard(Board board) {

        BlockSet blocks = prepareForRun(board);

        int size;

        // the program will end on it's own (hypothetically)
        while (!program.getEnd()) {

            blocks.rotateBlocks();

            // get the coords of the next Codel
            Codel nextCodel = getNextCodel(board, blocks.getFirst(), 0);

            // initiate the newest Codel
            blocks.addBlock(new Block(board, nextCodel));
            size = 1 + findSizeCodel(board, blocks.getLast(), nextCodel);
            board.setVisitedAll(false);

            blocks.getLast().setSize(size);

            // White codels are a special case
            if (blocks.getLast().getColor().equals(Color.WHITE)) {
                Codel nextNonWhite = getNextCodelWhite(board, nextCodel, blocks.getFirst().getRightTop(), 0);
                blocks.set(0, new Block(board, nextNonWhite));
                blocks.getFirst().setSize(1 + findSizeCodel(board, blocks.getFirst(), nextNonWhite));
                blocks.remove(1);

                board.setVisitedAll(false);
            } else {

                CommandRunner runner = new CommandRunner(program.getStack(), blocks.getFirst(), blocks.getLast());
                runner.run();

                // shift the new Codel to the old one's spot
                blocks.set(0, blocks.getLast());
            }
        }

        program.end();
    }

    /**
     * Prepares to run the board by finding the initial block in the program.
     * 
     * @param board the board being ran during execution
     * @return a list holding the first {@link Block} of the program.
     */
    public static BlockSet prepareForRun(Board board) {

        BlockSet blockSet;

        // The Piet language interpreter begins executing a program in the colour block
        // which includes the upper left codel of the program.
        Codel first = new Codel(0, 0);

        Block initBlock = new Block(board, first);
        initBlock.setSize(1 + findSizeCodel(board, initBlock, first));

        board.setVisitedAll(false);

        blockSet = new BlockSet(initBlock);

        return blockSet;

    }

    /**
     * Finds the size of a Block of Codels.
     * 
     * @param board the Board being used during the traversal of the program
     * @param block the Block the size is being found for
     * @param coord the Codel in the block to stat execution from
     * @return the size of the Block
     */
    public static Integer findSizeCodel(Board board, Block block, Codel coord) {
        // codel is uninitiated, so I need to set its corners
        if (block.getRightTop().getX() == -1) {
            log.debug("Setting init corners");
            setCorners(block, coord);
        }

        Integer count = 0;
        board.setVisited(coord, true);

        // 4 directions the program will search in for more of the same color
        Codel newCoordinate = new Codel();
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                // WEST
                newCoordinate = new Codel(coord.getX(), coord.getY() - 1);
            } else if (i == 1) {
                // EAST
                newCoordinate = new Codel(coord.getX(), coord.getY() + 1);
            } else if (i == 2) {
                // SOUTH
                newCoordinate = new Codel(coord.getX() - 1, coord.getY());
            } else if (i == 3) {
                // NORTH
                newCoordinate = new Codel(coord.getX() + 1, coord.getY());
            }

            // Not valid if not in bounds or previously visited
            if (!newCoordinate.isInBounds(board) || board.getVisited(newCoordinate)) {
                continue;
            }

            if (board.getColor(newCoordinate) == block.getColor()) {
                setCorners(block, newCoordinate);
                count++;
                count += findSizeCodel(board, block, newCoordinate);
            }
        }

        return count;
    }

    /**
     * Based on the Director's DP and CC values, finds the next codel from the
     * passed in Block's corner Codels.
     * 
     * @param block the current block
     * @return the next Codel to be part of the execution
     */
    public static Codel getNextCodel(Block block) {

        DP dp = director.getDP();
        CC cc = director.getCC();

        // TODO: remove null instantiation
        // TODO: throw errors on non-valid dp/cc
        Codel next = null;

        switch (dp) {

            case RIGHT:
                switch (cc) {
                    case LEFT:
                        next = block.getRightTop();
                        break;
                    case RIGHT:
                        next = block.getRightBottom();
                        break;
                    default:
                        break;
                }
                next.setY(next.getY() + 1);
                break;

            case DOWN:
                switch (cc) {
                    case LEFT:
                        next = block.getBottomRight();
                        break;
                    case RIGHT:
                        next = block.getBottomLeft();
                        break;
                    default:
                        break;
                }
                next.setX(next.getX() + 1);
                break;

            case LEFT:
                switch (cc) {
                    case LEFT:
                        next = block.getLeftBottom();
                        break;
                    case RIGHT:
                        next = block.getLeftTop();
                        break;
                    default:
                        break;
                }
                next.setY(next.getY() - 1);
                break;

            case UP:
                switch (cc) {
                    case LEFT:
                        next = block.getTopLeft();
                        break;
                    case RIGHT:
                        next = block.getTopRight();
                        break;
                    default:
                        break;
                }
                next.setX(next.getX() - 1);
                break;

        }

        return next;
    }

    // we're getting the newest codel, from the old one, c
    public static Codel getNextCodel(Board board, Block block, int attempt) {

        // We are not able to find another valid Codel in any of the current
        // blocks Codels.
        if (attempt > 8) {
            program.end();
        }

        Codel next = new Codel(0, 0);

        // Try to find the next Codel given the current block and director.
        // If the next Codel is not valid, rotate the director and try again.
        switch (director.getDP()) {
            case RIGHT:
                next = getNextCodel(block);

                if (!next.isInBounds(board) || board.getColor(next).equals(Color.BLACK)) {
                    director.rotateByAttempt(attempt);

                    next.setY(next.getY() - 1);
                    return getNextCodel(board, block, attempt + 1);
                }
                break;

            case DOWN:
                next = getNextCodel(block);

                if (!next.isInBounds(board) || board.getColor(next).equals(Color.BLACK)) {
                    director.rotateByAttempt(attempt);

                    next.setX(next.getX() - 1);
                    return getNextCodel(board, block, attempt + 1);
                }
                break;

            case LEFT:
                next = getNextCodel(block);

                if (!next.isInBounds(board) || board.getColor(next).equals(Color.BLACK)) {
                    director.rotateByAttempt(attempt);

                    next.setY(next.getY() + 1);
                    return getNextCodel(board, block, attempt + 1);
                }
                break;

            case UP:
                next = getNextCodel(block);

                if (!next.isInBounds(board) || board.getColor(next).equals(Color.BLACK)) {
                    director.rotateByAttempt(attempt);

                    next.setX(next.getX() + 1);
                    return getNextCodel(board, block, attempt + 1);
                }
                break;
        }

        return next;
    }

    /**
     * White colour blocks are "free" zones through which the interpreter passes
     * unhindered.
     * 
     * If it moves from a colour block into a white area, the interpreter "slides"
     * through the white codels in the direction of the DP until it reaches a
     * non-white colour block.
     * 
     * If the interpreter slides into a black block or an edge, if is considered
     * restricted, otherwise it moves into the colour block so encountered.
     * Sliding across white blocks into a new colour does not cause a command to be
     * executed.
     * 
     * In this way, white blocks can be used to change the current colour without
     * executing a command, which is very useful for coding loops.
     * 
     * Sliding across white blocks takes the interpreter in a straight line until it
     * hits a coloured pixel or edge.
     * 
     * It does not use the procedure described above for determining where the
     * interpreter emerges from non-white coloured blocks.
     * 
     * @param board         the board being traversed
     * @param coordinate    the Codel to start traversal from
     * @param previousColor the most recent non-white/non-block Codel to have been
     *                      ran
     * @param attempt       how many attempts have been made to escape the current
     *                      white space
     * @return the next non-white Codel to start the program at
     */
    public static Codel getNextCodelWhite(Board board, Codel coordinate, Codel previousColor, int attempt) {

        // We are not able to find another valid Codel in any of the current
        // blocks Codels.
        if (attempt > 8) {
            program.end();
        }

        int nextRow = coordinate.getX();
        int nextCol = coordinate.getY();
        Codel next = new Codel(nextRow, nextCol);
        Codel newNext;

        // The interpreter "slides" across the white block in a straight line
        switch (director.getDP()) {

            case RIGHT:
                while (board.getColor(next).equals(Color.WHITE)) {
                    newNext = new Codel(nextRow, nextCol + 1);

                    // If it hits a restriction, the CC is toggled. Since this results in no
                    // difference in where the interpreter is trying to go, the DP is immediately
                    // stepped clockwise.
                    if (!newNext.isInBounds(board) || board.getColor(newNext).equals(Color.BLACK)
                            || newNext.equals(previousColor)) {
                        director.rotateCC(1);
                        director.rotateDP(1);
                        return getNextCodelWhite(board, next, previousColor, attempt + 1);
                    }

                    nextCol++;
                    next = newNext;
                }
                break;

            case LEFT:
                while (board.getColor(next).equals(Color.WHITE)) {
                    newNext = new Codel(nextRow, nextCol - 1);

                    if (!newNext.isInBounds(board) || board.getColor(newNext).equals(Color.BLACK)
                            || newNext.equals(previousColor)) {
                        director.rotateCC(1);
                        director.rotateDP(1);
                        return getNextCodelWhite(board, next, previousColor, attempt + 1);
                    }

                    nextCol--;
                    next = newNext;
                }
                break;

            case DOWN:
                while (board.getColor(next).equals(Color.WHITE)) {
                    newNext = new Codel(nextRow + 1, nextCol);

                    if (!newNext.isInBounds(board) || board.getColor(newNext).equals(Color.BLACK)
                            || newNext.equals(previousColor)) {
                        director.rotateCC(1);
                        director.rotateDP(1);
                        return getNextCodelWhite(board, next, previousColor, attempt + 1);
                    }

                    nextRow++;
                    next = newNext;
                }
                break;

            case UP:
                while (board.getColor(next).equals(Color.WHITE)) {
                    newNext = new Codel(nextRow - 1, nextCol);

                    if (!newNext.isInBounds(board) || board.getColor(newNext).equals(Color.BLACK)
                            || newNext.equals(previousColor)) {
                        director.rotateCC(1);
                        director.rotateDP(1);

                        return getNextCodelWhite(board, next, previousColor, attempt + 1);
                    }

                    nextRow--;
                    next = newNext;
                }
                break;
        }

        return next;
    }

    // Piet relies entirely on moving from a corner to a new color, need to set two
    // corners (l, r) for each direction (l, r, u, d)
    public static void setCorners(Block block, Codel coordinate) {

        // potential for new right column value
        if (coordinate.getY() >= block.getRightTop().getY() || block.getRightTop().getY() == -1) {
            // if further right
            if (coordinate.getY() > block.getRightTop().getY() || block.getRightTop().getY() == -1) {
                block.setRightTop(coordinate);
            }
            // if further up
            if (coordinate.getX() < block.getRightTop().getX() || block.getRightTop().getX() == -1) {
                block.setRightTop(coordinate);
            }
            // if further right
            if (coordinate.getY() > block.getRightBottom().getY() || block.getRightBottom().getY() == -1) {
                block.setRightBottom(coordinate);
            }
            // if further down
            if (coordinate.getX() > block.getRightBottom().getX() || block.getRightBottom().getX() == -1) {
                block.setRightBottom(coordinate);
            }
        }

        // potential for new bottom row value
        if (coordinate.getX() >= block.getBottomRight().getX() || block.getBottomRight().getX() == -1) {
            // if further down
            if (coordinate.getX() > block.getBottomRight().getX() || block.getBottomRight().getX() == -1) {
                block.setBottomRight(coordinate);
            }
            // if further right
            if (coordinate.getY() > block.getBottomRight().getY() || block.getBottomRight().getY() == -1) {
                block.setBottomRight(coordinate);
            }
            // if further down
            if (coordinate.getX() > block.getBottomLeft().getX() || block.getBottomLeft().getX() == -1) {
                block.setBottomLeft(coordinate);
            }
            // if further left
            // TODO: investigate why this has three conditions
            if (coordinate.getY() < block.getBottomLeft().getY() || block.getBottomLeft().getY() == -1
                    || block.getBottomLeft().getX() == -1) {
                block.setBottomLeft(coordinate);
            }
        }

        // potential for new left column value
        if (coordinate.getY() <= block.getLeftBottom().getY() || block.getLeftBottom().getY() == -1) {
            // if further left
            if (coordinate.getY() < block.getLeftBottom().getY() || block.getLeftBottom().getY() == -1) {
                block.setLeftBottom(coordinate);
            }
            // if further down
            if (coordinate.getX() > block.getLeftBottom().getX() || block.getLeftBottom().getX() == -1) {
                block.setLeftBottom(coordinate);
            }
            // if further left
            if (coordinate.getY() < block.getLeftTop().getX() || block.getLeftTop().getX() == -1) {
                block.setLeftTop(coordinate);
            }
            // if further up
            if (coordinate.getX() < block.getLeftTop().getX() || block.getLeftTop().getX() == -1) {
                block.setLeftTop(coordinate);
            }
        }

        // potential for upper-most row
        if (coordinate.getX() <= block.getTopLeft().getX() || block.getTopLeft().getX() == -1) {
            // if further up
            if (coordinate.getX() < block.getTopLeft().getX() || block.getTopLeft().getX() == -1) {
                block.setTopLeft(coordinate);
            }
            // if further left
            if (coordinate.getY() < block.getTopLeft().getY() || block.getTopLeft().getY() == -1) {
                block.setTopLeft(coordinate);
            }
            // if further up
            if (coordinate.getX() < block.getTopRight().getX() || block.getTopLeft().getY() == -1) {
                block.setTopRight(coordinate);
            }
            // if further right
            if (coordinate.getY() > block.getTopRight().getY() || block.getTopRight().getY() == -1) {
                block.setTopRight(coordinate);
            }
        }
    }
}
