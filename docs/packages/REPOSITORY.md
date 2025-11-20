# Repository Guide

**Package:** `net.idt.geolocation.repository`

## Files in This Package

- `GeolocationRepository.java` - MongoDB repository interface

---

## Adding Query Methods

Spring Data auto-implements methods based on naming:

```java
// Find by field
List<Geolocation> findByCity(String city);

// Multiple fields
List<Geolocation> findByCityAndRegion(String city, String region);

// Comparison
List<Geolocation> findByTimestampAfter(Date date);

// Existence
boolean existsByIp(String ip);

// Top N
List<Geolocation> findTop10ByOrderByTimestampDesc();
```

---

## Query Keywords

| Keyword | Example | Query |
|---------|---------|-------|
| And | `findByIpAndCity` | Both conditions |
| Or | `findByIpOrCity` | Either condition |
| Between | `findByTimestampBetween` | Range |
| After/Before | `findByTimestampAfter` | Comparison |
| Like | `findByCityLike` | Pattern match |

---

## Custom Queries

```java
@Query("{ 'country_name': ?0, 'city': { $ne: null } }")
List<Geolocation> findByCountryWithCity(String country);
```

---

## Pagination

```java
// In repository
List<Geolocation> findByCountry_name(String country, Pageable pageable);

// Usage in controller
PageRequest request = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"));
List<Geolocation> results = repository.findByCountry_name("United States", request);
```

---

## Testing

See `TESTING.md` for @DataMongoTest examples.
