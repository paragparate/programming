package com.pparate.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    /* public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }*/

    private static Logger logger = LogManager.getLogger();

    public void test001() {
        logger.error("Parag's error");
    }
}
