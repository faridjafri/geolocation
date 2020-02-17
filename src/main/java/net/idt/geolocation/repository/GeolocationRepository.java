package net.idt.geolocation.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import net.idt.geolocation.model.Geolocation;

public interface GeolocationRepository extends MongoRepository<Geolocation, String> {

  List<Geolocation> findByIp(String ip);

  List<Geolocation> findByTimestampBetween(Date startDate, Date endDate);

  List<Geolocation> findByIpAndTimestampBetween(String ip, Date startDate, Date endDate);

  List<Geolocation> findFirstDistinctByIpIn(List<String> ips, Pageable pageable);

  List<Geolocation> findFirstDistinctByIpAndTimestampBetween(int previousNLocations, Date startDate,
      Date endDate);

}
