// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for all the necessary depencies for the Hangman project.
 */
@Configuration
@ComponentScan(basePackages = {"com.krishnanand.hangman"})
public class HangmanConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
