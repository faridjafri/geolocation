package net.idt.geolocation.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.websocket.server.PathParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import net.idt.geolocation.model.Geolocation;
import net.idt.geolocation.repository.GeolocationRepository;

@RestController
@RequestMapping("/geolocation")
public class GeolocationController {

  @Autowired
  private GeolocationRepository geolocationRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  @RequestMapping(value = "/{ip}", method = RequestMethod.GET)
  @ResponseBody
  public ArrayList<Geolocation> getGeolocation(@PathVariable("ip") String ip)
      throws JsonProcessingException {
    URL ipapi = null;
    try {
      ipapi = new URL("https://ipapi.co/" + ip + "/json/");
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }

    URLConnection c;
    BufferedReader reader;
    String location = "";
    String line;
    try {
      c = ipapi.openConnection();
      c.setRequestProperty("User-Agent", "java-ipapi-client");
      reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
      while ((line = reader.readLine()) != null && line.length() != 0) {
        location += line;
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Geolocation geolocation = null;
    try {
      geolocation = new ObjectMapper().readValue(location, Geolocation.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    geolocation.setTimestamp(new Date());
    geolocationRepository.save(geolocation);
    ArrayList<Geolocation> geolocations = new ArrayList<>();
    geolocations.add(geolocation);
    return geolocations;
  }

  @RequestMapping(value = "/{ip}/history", method = RequestMethod.GET)
  @ResponseBody
  public List<Geolocation> getGeolocationHistoryBetween(@PathParam("ip") String ip,
      @RequestParam(value = "start", required = false) String start,
      @RequestParam(value = "end", required = false) String end) {
    if (StringUtils.isAnyBlank(start, end)) {
      return geolocationRepository.findByIp(ip);
    }
    String pattern = "MM.dd.yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Date startDate = null;
    Date endDate = null;
    try {
      startDate = simpleDateFormat.parse(start);
      endDate = simpleDateFormat.parse(end);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return geolocationRepository.findByIpAndTimestampBetween(ip, startDate, endDate);
  }

  @RequestMapping(value = "/history", method = RequestMethod.GET)
  @ResponseBody
  public List<Geolocation> getLocationHistoryBetween(
      @RequestParam(value = "start", required = false) String start,
      @RequestParam(value = "end", required = false) String end,
      @RequestParam(value = "n", required = false) String previousNLocations)
      throws JsonProcessingException {
    List<String> distinctIps =
        Lists.newArrayList(mongoTemplate.getCollection("geolocation").distinct("ip", String.class));
    PageRequest request = PageRequest.of(1, Integer.valueOf(previousNLocations),
        Sort.by(Sort.Direction.DESC, "timestamp"));
    if (StringUtils.isAnyBlank(start, end)) {
      return geolocationRepository.findFirstDistinctByIpIn(distinctIps, request);
    }
    String pattern = "MM.dd.yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Date startDate = null;
    Date endDate = null;
    try {
      startDate = simpleDateFormat.parse(start);
      endDate = simpleDateFormat.parse(end);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return geolocationRepository.findByTimestampBetween(startDate, endDate, request);
  }

}
