package com.frejt.piet.util;

import java.util.ArrayList;
import java.util.List;

public class BlockSet {

    // two blocks max
    List<Block> blocks;

    public BlockSet(Block block) {
        blocks = new ArrayList<>();

        blocks.add(block);
        blocks.add(block);
    }

    public Block getFirst() {
        return blocks.get(0);
    }

    public Block getLast() {
        return blocks.get(1);
    }

    public void addBlock(Block newBlock) {

        if(blocks.size() > 1) {
            rotateBlocks();
        }

        blocks.add(newBlock);

    }

    public void rotateBlocks() {

        if(blocks.size() > 1) {
            blocks.set(0, blocks.get(1));
            blocks.remove(1);
        }

    }

    public int size() {
        return blocks.size();
    }

    public Block remove(int index) {
        return blocks.remove(index);
    }

    public void set(int index, Block block) {
        blocks.set(index, block);
    }

}