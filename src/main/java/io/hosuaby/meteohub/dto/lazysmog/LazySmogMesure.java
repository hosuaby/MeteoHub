package io.hosuaby.meteohub.dto.lazysmog;

import java.time.LocalTime;
import java.time.ZoneId;

/**
 * One particular measure inside {@link LazySmogDto}.
 *
 * @author Alexei KLENIN
 */
public class LazySmogMesure {

  private LocalTime time;
  private Double value;
  private double[] coordinates;
  private ZoneId zone;

  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public double[] getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(double[] coordinates) {
    this.coordinates = coordinates;
  }

  public ZoneId getZone() {
    return zone;
  }

  public void setZone(ZoneId zone) {
    this.zone = zone;
  }
}
