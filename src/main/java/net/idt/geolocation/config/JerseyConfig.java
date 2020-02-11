package net.idt.geolocation.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import net.idt.geolocation.api.GeolocationController;

@Component
public class JerseyConfig extends ResourceConfig {
  public JerseyConfig() {
    register(GeolocationController.class);
  }
}
