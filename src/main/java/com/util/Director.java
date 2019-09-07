package com.util;

import com.command.CommandController;

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

    public void rotateCC(Integer val) {
        if(CommandController.correctMod(val, 2) == 2) {
            return;
        } else {
            if(this.cc == CC.RIGHT) {
                cc = CC.LEFT;
            } else {
                cc = CC.RIGHT;
            }
        }
    }

    public void rotateDP(Integer val) {
        if(val == 0) {
            return;
        }

        if(val > 0) {
            while(val > 0) {
                if(dp == DP.RIGHT) {
                    dp = DP.DOWN;
                } else if(dp == DP.DOWN) {
                    dp = DP.LEFT;
                } else if (dp == DP.LEFT) {
                    dp = DP.UP;
                } else if(dp == DP.UP) {
                    dp = DP.RIGHT;
                }
                val--;
            }
        }

        if(val < 0) {
            while(val < 0) {
                if(dp == DP.RIGHT) {
                    dp = DP.UP;
                } else if(dp == DP.UP) {
                    dp = DP.LEFT;
                } else if(dp == DP.LEFT) {
                    dp = DP.DOWN;
                } else if(dp == DP.DOWN) {
                    dp = DP.RIGHT;
                }
                val++;
            }
        }
    }
}