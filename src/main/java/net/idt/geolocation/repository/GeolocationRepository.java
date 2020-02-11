package net.idt.geolocation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import net.idt.geolocation.model.Geolocation;

public interface GeolocationRepository extends MongoRepository<Geolocation, String> {

}
