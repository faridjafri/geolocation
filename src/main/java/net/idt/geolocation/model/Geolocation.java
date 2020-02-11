package net.idt.geolocation.model;

import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geolocation {

  @Id
  private String searchId;
  private String ip;
  private String city;
  private String region;
  private String country_name;
  private String postal;
  private String latitude;
  private String longitude;
  private String timezone;
  private Long timestamp;

  public String getIp() {
    return ip;
  }

  public String getCity() {
    return city;
  }

  public String getRegion() {
    return region;
  }

  public String getCountry_name() {
    return country_name;
  }

  public String getPostal() {
    return postal;
  }

  public String getLatitude() {
    return latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public String getTimezone() {
    return timezone;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }


}
