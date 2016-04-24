package io.hosuaby.meteohub.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.hosuaby.meteohub.domain.NormalizedWeatherMeasures;
import io.hosuaby.meteohub.dto.meteoplus.MeteoPlusDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.mongodb.outbound.MongoDbStoringMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Configuration of Spring Integration.
 */
@Configuration
@EnableIntegration
public class IntegrationConfig implements InitializingBean {

  private static final String LOG_MESSAGE_HEADER = "LOG_MSG";

  private final Log logger = LogFactory.getLog(getClass());

  @Autowired
  private MongoDbFactory mongoDbFactory;

  @Autowired
  private ObjectMapper objectMapper;

  @Bean
  public HttpRequestHandlingMessagingGateway MeteoPlusInboundGateway() {
    HttpRequestHandlingMessagingGateway gateway =
        new HttpRequestHandlingMessagingGateway();

    RequestMapping mapping = new RequestMapping();
    mapping.setMethods(HttpMethod.POST);
    mapping.setPathPatterns("/meteoplus");

    gateway.setRequestMapping(mapping);
    gateway.setRequestChannel(meteoPlusInboundChannel());
    gateway.setRequestPayloadType(MeteoPlusDto.class);

    return gateway;
  }

  @Bean
  public MessageChannel meteoPlusInboundChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  public IntegrationFlow meteoPlusFlow() {
    return IntegrationFlows
        .from(meteoPlusInboundChannel())
        .enrichHeaders(h -> h
            .header(LOG_MESSAGE_HEADER, "Received data from MeteoPlus"))
        .wireTap(logChannel())
        .<MeteoPlusDto, NormalizedWeatherMeasures>transform(input -> {
          NormalizedWeatherMeasures nwm = new NormalizedWeatherMeasures();
          nwm.setTemperature(input.getTemperature());
          nwm.setHumidity(input.getHumidity());
          nwm.setMesureMoment(input.getMesureDateTime());
          nwm.setCoordinates(input.getCoordinates());
          nwm.setSource(input.getDeviceId().toString());
          return nwm;
        })
        .enrichHeaders(h -> h
            .header(LOG_MESSAGE_HEADER, "Data from MeteoPlus was normilized"))
        .wireTap(logChannel())
        .channel(normalizedMeasuresChannel())
        .get();
  }

  @Bean
  public IntegrationFlow meteoPlusRawInputPersistenceFlow() {
    return IntegrationFlows
        .from(meteoPlusInboundChannel())
        .handle(new MongoDbStoringMessageHandler(mongoDbFactory), c -> {
          c.get().getT2().setCollectionNameExpression(
              new LiteralExpression("meteoPlusRawInput"));
        })
        .get();
  }

  @Bean
  public MessageChannel normalizedMeasuresChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  public IntegrationFlow persistanceFlow() {
    return IntegrationFlows
        .from(normalizedMeasuresChannel())
        .handle(new MongoDbStoringMessageHandler(mongoDbFactory), c -> {
          c.get().getT2().setCollectionNameExpression(
                  new LiteralExpression("nwm"));
        })
        .get();
  }

  /**
   * Direct channel to print logged messages immediately after sent to
   * logChannel.
   */
  @Bean
  public MessageChannel logChannel() {
    return MessageChannels.direct("logChannel").get();
  }

  @Bean
  @ServiceActivator(inputChannel = "logChannel")
  public MessageHandler jsonLogger() {
    return message -> {
      String json = "";

      try {
        json = objectMapper.writeValueAsString(message.getPayload());
      } catch (JsonProcessingException unexpectedException) {
        logger.error("Can not serialize object as JSON", unexpectedException);
      }

      logger.info(message.getHeaders().get(LOG_MESSAGE_HEADER) + "\n" + json);
    };
  }

  /**
   * Enables pretty-print of JSON.
   *
   * @throws Exception
   *    can not be thrown
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
  }
}
