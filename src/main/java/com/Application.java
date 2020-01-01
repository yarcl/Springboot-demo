package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 *
 */
@EnableAutoConfiguration
@ComponentScan({"com"})
public class Application
{
    public static void main( String[] args )
    {
        SpringApplication.run(Application.class, args);

    }
}
