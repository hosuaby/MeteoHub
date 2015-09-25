package io.hosuaby.cars.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Input data from MeteoPlus.
 *
 * @author Alexei KLENIN
 */
public class MeteoPlusInput {

  private UUID uuid;
  private ZonedDateTime mesureDateTime;
  private List<Double> cooridnates;
  private Double temperature;
  private Double humidity;

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public ZonedDateTime getMesureDateTime() {
    return mesureDateTime;
  }

  public void setMesureDateTime(ZonedDateTime mesureDateTime) {
    this.mesureDateTime = mesureDateTime;
  }

  public List<Double> getCooridnates() {
    return cooridnates;
  }

  public void setCooridnates(List<Double> cooridnates) {
    this.cooridnates = cooridnates;
  }

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
}
