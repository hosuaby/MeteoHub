package io.hosuaby.meteohub.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Input data from MeteoPlus.
 *
 * @author Alexei KLENIN
 */
public class MeteoPlusInput {

  private UUID deviceId;
  private ZonedDateTime mesureDateTime;
  private List<Double> coordinates;
  private Double temperature;
  private Double humidity;

  public UUID getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(UUID deviceId) {
    this.deviceId = deviceId;
  }

  public ZonedDateTime getMesureDateTime() {
    return mesureDateTime;
  }

  public void setMesureDateTime(ZonedDateTime mesureDateTime) {
    this.mesureDateTime = mesureDateTime;
  }

  public List<Double> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<Double> coordinates) {
    this.coordinates = coordinates;
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
