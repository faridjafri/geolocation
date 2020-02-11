package net.idt.geolocation.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.idt.geolocation.model.Geolocation;
import net.idt.geolocation.repository.GeolocationRepository;

@Path("/geolocation")
public class GeolocationController {

  @Autowired
  private GeolocationRepository goelocationRepository;

  @GET
  @Path("/{ip}")
  @Produces("application/json")
  public Geolocation getGeolocation(@PathParam("ip") String ip)
      throws JsonMappingException, JsonProcessingException {
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
    Geolocation geolocation = new ObjectMapper().readValue(location, Geolocation.class);
    geolocation.setTimestamp(new Date().getTime());
    goelocationRepository.save(geolocation);
    return geolocation;
  }

}
