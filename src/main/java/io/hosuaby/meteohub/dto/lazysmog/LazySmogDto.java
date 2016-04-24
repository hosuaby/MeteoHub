package io.hosuaby.meteohub.dto.lazysmog;

import java.time.LocalDate;

/**
 * Data transferred by LazySmog API.
 *
 * @author Alexei KLENIN
 */
public class LazySmogDto {

  private LocalDate date;
  private LazySmogMesure[] temperature;
  private LazySmogMesure[] humidity;

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LazySmogMesure[] getTemperature() {
    return temperature;
  }

  public void setTemperature(LazySmogMesure[] temperature) {
    this.temperature = temperature;
  }

  public LazySmogMesure[] getHumidity() {
    return humidity;
  }

  public void setHumidity(LazySmogMesure[] humidity) {
    this.humidity = humidity;
  }
}
