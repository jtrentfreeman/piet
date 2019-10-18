package com.director;

import com.command.CommandController;

/**
 * The Piet language interpreter beings executing a program in the colour block which includes the upper left codel of the program.
 * The Interpreter maintains a Direction Pointer (DP), initially pointing to the right.
 *   The DP may point either right, left, down, or up.
 * The interpreter also maintains a Codel Chooser (CC), initially pointing left.
 *   The CC may point either left or right.
 * The directions of the DP and CC will often change during program execution.
 * 
 * As it executes the program, the interpreter traverses the colour blocks of the program under the following rules:
 *   1. The interpreter finds the edge of the current colour block with is furthest in the direction of the DP.
 *     (This edge may be disjoint if the block is of a complex shape.)
 *   2. The interpreter finds the codel of the current block on that edge which is furthest to the CC's direction of the DP's direction of 
 *      travel.
 *     (Visualise this as standing on the program and walking in the direction of the DP.)
 *   3. The interpreter travels from that codel into the colour block containing the codel immediately in the direction of the DP.
 * 
 * The interpreter continues doing this until the program terminates.
 */
public class Director {
    private CC cc;
    private DP dp;

    public Director() {
        dp = DP.RIGHT;
        cc = CC.LEFT;
    }

    public CC getCC() {
        return this.cc;
    }

    public DP getDP() {
        return this.dp;
    }

    /**
     * Toggles the CC
     * @param val - the number of times to toggle the CC
     */
    public void rotateCC(Integer val) {
        if(CommandController.correctMod(val, 2) == 0) {
            return;
        } else {
            if(this.cc == CC.RIGHT) {
                cc = CC.LEFT;
            } else {
                cc = CC.RIGHT;
            }
        }
    }

    /**
     * Rotates the DP clockwise if val is positive (anticlockwise if val is negative)
     * @param val - the number of times to rotate the DP
     */
    public void rotateDP(Integer val) {
        if(val == 0) {
            return;
        }

        if(val > 0) {
            while(val > 0) {
                switch(dp) {
                    case RIGHT:
                        dp = DP.DOWN;
                        break;
                    case DOWN:
                        dp = DP.LEFT;
                        break;
                    case LEFT:
                        dp = DP.UP;
                        break;
                    case UP:
                        dp = DP.RIGHT;
                        break;
                    default:
                        break;
                }
                val--;
            }
        }

        if(val < 0) {
            while(val < 0) {
                switch(dp) {
                    case RIGHT:
                        dp = DP.UP;
                        break;
                    case UP:
                        dp = DP.LEFT;
                        break;
                    case LEFT:
                        dp = DP.DOWN;
                        break;
                    case DOWN:
                        dp = DP.RIGHT;
                        break;
                    default:
                        break;
                }
                val++;
            }
        }
    }
}