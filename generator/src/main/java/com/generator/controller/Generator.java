package com.generator.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.command.Command;

public class Generator {

    private static final Logger log = LoggerFactory.getLogger(Generator.class);

    public static void main(String[] args) {

        String log4jConfPath = "generator/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

        String input = args[0];

        for(String s : args) {
            log.debug(s);
        }

        // convert input to numbers
        List<Integer> nums = numbers(input);
        List<List<Integer>> numStack = new ArrayList<>();

        for(Integer num : nums) {
            numStack.add(stackBuilder(num));
        }

        log.info(numStack + "");

        // convert numbers to RANDOM mathematical formula

        // convert formula to piet board

        // generate image based on formula
    }

    public static List<Integer> numbers(String input) {
        List<Integer> numbers = new ArrayList<>();
        for(Character c : input.toCharArray()) {
            numbers.add((Integer) ((int) c));
        }


        
        return numbers;
    }

    public static List<Integer> stackBuilder(Integer number) {
        log.debug("The number passed in " + number);
        Random r = new Random();
        Integer fin = 0;
        List<Integer> stack = new ArrayList<>();
        while(fin < number) {
            Integer next = r.nextInt(number - fin + 1);
            log.debug(next + "");
            stack.add(next);
            fin += next;
            log.debug("now it's " + fin);
        }

        stack.removeAll(Arrays.asList(0));
        return stack;
    }

}