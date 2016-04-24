package io.hosuaby.meteohub.mocks;

import io.hosuaby.meteohub.dto.lazysmog.LazySmogDto;
import io.hosuaby.meteohub.dto.lazysmog.LazySmogMesure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Simulator of REST API of LazySmog.
 *
 * @author Alexei KLENIN
 */
@RestController
@RequestMapping("/lazysmog")
public class LazySmogMock {

  private static final double[][] PARIS_BOUNDS = {
      {48.824384, 2.284298},
      {48.872054, 2.409782} };

  private static final int NB_STATIONS = 5;

  private static final double[] TEMPERATURE_BOUNDS = { 295,15, 308,15 };
  private static final double[] DELTA_TEMPERATURE_BOUNDS = { 0.1, 1.5 };

  private static final double[] HUMIDITY_BOUNDS = { 40.0, 80.0 };
  private static final double[] DELTA_HUMIDITY_BOUNDS = { 1.0, 5.0 };

  private static final int MEASURE_RATE = 5000;    // make measure every 5 min

  @Autowired
  private Random random;

  private Station[] stations;

  @RequestMapping(
      value = "/{date}",
      method = RequestMethod.GET)
  public LazySmogDto getData(
      @PathVariable LocalDate date) {
    LazySmogMesure[] temperature = Arrays
        .asList(stations)
        .stream()
        .flatMap(station -> station
            .getMeasures()
            .stream()
            .map(measure -> {
              LazySmogMesure measureDto = new LazySmogMesure();
              measureDto.setTime(measure.getMoment().toLocalTime());
              measureDto.setCoordinates(station.getCoordinates());
              measureDto.setValue(measure.getTemperature());
              measureDto.setZone(ZoneId.of("Europe/Paris"));
              return measureDto;
            }))
        .collect(Collectors.toList())
        .toArray(new LazySmogMesure[]{});

    LazySmogMesure[] humidity = Arrays
        .asList(stations)
        .stream()
        .flatMap(station -> station
            .getMeasures()
            .stream()
            .map(measure -> {
              LazySmogMesure measureDto = new LazySmogMesure();
              measureDto.setTime(measure.getMoment().toLocalTime());
              measureDto.setCoordinates(station.getCoordinates());
              measureDto.setValue(measure.getHumidity());
              measureDto.setZone(ZoneId.of("Europe/Paris"));
              return measureDto;
            }))
        .collect(Collectors.toList())
        .toArray(new LazySmogMesure[]{});

    LazySmogDto dto = new LazySmogDto();
    dto.setDate(date);
    dto.setTemperature(temperature);
    dto.setHumidity(humidity);

    return dto;
  }

  /**
   * Initializes stations.
   */
  @PostConstruct
  public void initStations() {
    this.stations = new Station[NB_STATIONS];

    for (int i = 0; i < NB_STATIONS; i++) {
      double latitude = randomDoubleBetween(
          PARIS_BOUNDS[0][0], PARIS_BOUNDS[1][0]);
      double longitude = randomDoubleBetween(
          PARIS_BOUNDS[1][0], PARIS_BOUNDS[1][1]);
      stations[i] = new Station(latitude, longitude);
    }
  }

  /**
   * Makes measurements with fixed rate.
   */
  @Scheduled(fixedRate = MEASURE_RATE)
  public void getMeasures() {
    for (int i = 0; i < NB_STATIONS; i++) {
      double deltaTemperature = randomDoubleBetween(
          DELTA_TEMPERATURE_BOUNDS[0], DELTA_TEMPERATURE_BOUNDS[1]);
      double deltaHumidity = randomDoubleBetween(
          DELTA_HUMIDITY_BOUNDS[0], DELTA_HUMIDITY_BOUNDS[1]);
      double lastTemperature;
      double lastHumidity;

      if (stations[i].getLastMeasure() != null) {
        lastTemperature = stations[i].getLastMeasure().getTemperature();
        lastHumidity = stations[i].getLastMeasure().getHumidity();
      } else {
        lastTemperature = randomDoubleBetween(
            TEMPERATURE_BOUNDS[0], TEMPERATURE_BOUNDS[1]);
        lastHumidity = randomDoubleBetween(
            HUMIDITY_BOUNDS[0], HUMIDITY_BOUNDS[1]);
      }

      double newTemperature = lastTemperature
          + deltaTemperature * (random.nextBoolean() ? 1 : -1);
      double newHumidity = lastHumidity
          + deltaHumidity * (random.nextBoolean() ? 1 : -1);

      stations[i].addMesure(new Measure(newTemperature, newHumidity));
    }
  }

  private double randomDoubleBetween(double min, double max) {
    return min + Math.random() * (max - min);
  }

  private class Station {
    private double[] coordinates;
    private List<Measure> measures;
    private Measure lastMeasure;

    public Station(double latitude, double longitude) {
      this.coordinates = new double[]{ latitude, longitude };
      this.measures = new ArrayList<>();
    }

    public double[] getCoordinates() {
      return coordinates;
    }

    public void addMesure(Measure measure) {
      this.measures.add(measure);
      this.lastMeasure = measure;
    }

    public Measure getLastMeasure() {
      return lastMeasure;
    }

    public List<Measure> getMeasures() {
      return measures;
    }
  }

  private class Measure {
    private LocalDateTime moment;
    private Double temperature;   // ° kelvin
    private Double humidity;      // %

    public Measure(Double temperature, Double humidity) {
      this(LocalDateTime.now(), temperature, humidity);
    }

    public Measure(LocalDateTime moment, Double temperature, Double humidity) {
      this.moment = moment;
      this.temperature = temperature;
      this.humidity = humidity;
    }

    public LocalDateTime getMoment() {
      return moment;
    }

    public Double getTemperature() {
      return temperature;
    }

    public Double getHumidity() {
      return humidity;
    }
  }

}
