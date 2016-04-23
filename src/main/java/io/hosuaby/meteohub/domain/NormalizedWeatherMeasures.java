package io.hosuaby.meteohub.domain;

import java.time.ZonedDateTime;

/**
 * Normalized weather measurements.
 */
public class NormalizedWeatherMeasures {

  private Double temperature;         // ° celsius
  private Double humidity;            // %
  private ZonedDateTime mesureMoment;
  private Double[] coordinates;       // latitude & longitude
  private String source;              // device id or API

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

  public Double getHumidity() {
    return humidity;
  }

  public void setHumidity(Double humidity) {
    this.humidity = humidity;
  }

  public ZonedDateTime getMesureMoment() {
    return mesureMoment;
  }

  public void setMesureMoment(ZonedDateTime mesureMoment) {
    this.mesureMoment = mesureMoment;
  }

  public Double[] getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Double[] coordinates) {
    this.coordinates = coordinates;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
