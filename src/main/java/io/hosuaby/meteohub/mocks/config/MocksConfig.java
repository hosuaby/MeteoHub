package io.hosuaby.meteohub.mocks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Random;

/**
 * Configuration for mocks.
 *
 * @author Alexei KLENIN
 */
@Configuration
@EnableScheduling
public class MocksConfig {

  @Bean
  public Random random() {
    return new Random();
  }

}
