package com.frejt.piet.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.frejt.piet.entity.Board;
import com.frejt.piet.exception.FileNotReadException;
import com.frejt.piet.reader.FileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interprets a Piet file.
 * 
 * This file acts as the entry poiht to the program.
 * 
 * To run:
 * {@code ./gradlew run --args="<path to file>"}
 * 
 * Known bugs:
 * - After sliding across a white space, a command can still take place
 * involving the colour of the block before the slide.
 * Seen using: loop.ppm
 * 
 * Working files:
 * - fancyhello.ppm
 * - hi.ppm
 * - loop.ppm (kind of)
 * - nhello.ppm
 * - nprime.ppm
 * - Piet_hello.png
 * 
 * Failed files:
 * - loop.ppm (kind of)
 * - nfib.ppm
 * - hw2-1.png (I think we don't fail during white space sliding)
 * - Piet-1.png
 * 
 * This program is being ran with:
 * - Java 11.0.4
 * - Gradle 7.2
 */
public class Interpreter {

    private static final Logger log = LogManager.getLogger(Interpreter.class);

    /**
     * Entry point to the program.
     * 
     * @param args
     *             args[0] - String holding n image's file path
     * @throws FileNotReadException if the file was, for any reason, not able to be
     *                              read
     */
    public static void main(String args[]) throws FileNotReadException {
        Path runFile = getRunFile(args);

        log.debug("Running file " + runFile.toString());

        FileReader reader = new FileReader(runFile);
        Board board = reader.convertFileToBoard();

        log.debug("The board:\n" + board.toString());

        // performing the actual file
        BoardRunner.runBoard(board);
    }

    /**
     * Gets the file holding the Piet board that will be run.
     * If the user passed in an argument to the program, interprets the arguments
     * as the path to the file that should be run.
     * If no argument is provided, requests that the user enters a file to run.
     * 
     * @param args the arguments provided to the program
     * @return a string holding a path to an image file
     */
    public static Path getRunFile(String[] args) {

        String runFileStr;
        Scanner sc;

        // If the user provided an argument, use it
        if (args.length > 0) {
            runFileStr = args[0];
        } else {
            System.err.println("Please enter the path to the file to be ran:");
            sc = new Scanner(System.in);
            runFileStr = sc.nextLine();
        }

        return Paths.get(runFileStr);

    }

}

/**
 * Thank you, Larry Tesler.
 * 
 * Realized that ^ may sound like I was copy+pasting from outside sources, I
 * wasn't.
 * 
 * Some of the code (converting numbers -> colors) was repetitive, where I
 * duplicated a lot of code.
 * 
 * Larry Tesler was the creator of the copy & paste commands, according to
 * Wikipedia.
 * 
 * All of this code was written by myself (Trent Freeman), and no code was taken
 * from any outside sources.
 * 
 * 
 * Also shout of to Sean Szumlanski, a CS professor at UCF.
 * 
 * He initially introduced me to Piet and got me interested in writing this
 * program, which has taken up a large chunk of time (which I've mostly
 * enjoyed).
 */