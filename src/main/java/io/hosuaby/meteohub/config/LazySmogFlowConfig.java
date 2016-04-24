package io.hosuaby.meteohub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;

/**
 * Configuration of LazySmog integration flow.
 *
 * @author Alexei KLENIN
 */
@Configuration
@EnableIntegration
public class LazySmogFlowConfig {

  private static final String LAZYSMOG_API_URL = "http://localhost:8080/lazysmog/";

  @Bean
  public HttpRequestExecutingMessageHandler LazySmogInboundGateway() {
    HttpRequestExecutingMessageHandler gateway =
        new HttpRequestExecutingMessageHandler("");



    return gateway;
  }

}
