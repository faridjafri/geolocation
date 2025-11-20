# Repository Guide

**Package:** `net.idt.geolocation.repository`

## Current Repository

### GeolocationRepository.java
Extends: `MongoRepository<Geolocation, String>`

**Methods:**
- `findByIp(String ip)` - All records for IP
- `findByIpAndTimestampBetween(String ip, Date start, Date end)` - Date range
- `findByTimestampBetween(Date start, Date end, Pageable pageable)` - All with pagination
- `findFirstDistinctByIpIn(List<String> ips, Pageable pageable)` - Distinct IPs

---

## Adding Query Methods

Spring Data automatically implements methods based on naming:

```java
// Find by field
List<Geolocation> findByCity(String city);

// Multiple fields (AND)
List<Geolocation> findByCityAndRegion(String city, String region);

// Comparison
List<Geolocation> findByTimestampAfter(Date date);
List<Geolocation> findByTimestampBefore(Date date);

// Count
long countByCountry_name(String country);

// Check existence
boolean existsByIp(String ip);

// Limit results
Geolocation findFirstByIpOrderByTimestampDesc(String ip);
List<Geolocation> findTop10ByOrderByTimestampDesc();
```

---

## Query Keywords

| Keyword | Example | MongoDB Query |
|---------|---------|---------------|
| And | `findByIpAndCity` | `{"ip": "...", "city": "..."}` |
| Or | `findByIpOrCity` | `{$or: [...]}` |
| Between | `findByTimestampBetween` | `{$gte: ..., $lte: ...}` |
| LessThan | `findByTimestampLessThan` | `{$lt: ...}` |
| Like | `findByCityLike` | `{$regex: "..."}` |
| In | `findByIpIn` | `{$in: [...]}` |
| IsNull | `findByCityIsNull` | `{"city": null}` |

---

## Complex Queries with @Query

```java
import org.springframework.data.mongodb.repository.Query;

@Query("{ 'country_name': ?0, 'city': { $ne: null } }")
List<Geolocation> findByCountryWithCity(String country);

@Query(value = "{ 'ip': ?0 }", sort = "{ 'timestamp': -1 }")
List<Geolocation> findLatestByIp(String ip);
```

---

## Pagination

```java
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

// In repository
List<Geolocation> findByCountry_name(String country, Pageable pageable);

// Usage
PageRequest pageRequest = PageRequest.of(
    0,  // page number
    10, // page size
    Sort.by(Sort.Direction.DESC, "timestamp")
);

List<Geolocation> results = repository.findByCountry_name("United States", pageRequest);
```

---

## Dynamic Queries with MongoTemplate

```java
@Autowired
private MongoTemplate mongoTemplate;

public List<Geolocation> dynamicSearch(String ip, String city) {
    Query query = new Query();

    if (ip != null) {
        query.addCriteria(Criteria.where("ip").is(ip));
    }
    if (city != null) {
        query.addCriteria(Criteria.where("city").regex(city, "i"));
    }

    query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
    return mongoTemplate.find(query, Geolocation.class);
}
```

---

## Testing

```java
@DataMongoTest
public class GeolocationRepositoryTest {
    @Autowired
    private GeolocationRepository repository;

    @Test
    public void testFindByIp() {
        Geolocation geo = new Geolocation();
        geo.setIp("8.8.8.8");
        repository.save(geo);

        List<Geolocation> results = repository.findByIp("8.8.8.8");
        assertEquals(1, results.size());
    }
}
```

---

## Performance Tips

1. **Add indexes** - For frequently queried fields
2. **Use exists()** - Instead of `!findBy...().isEmpty()`
3. **Use projections** - Don't fetch fields you don't need
4. **Limit results** - Use `findTop...` or Pageable

---

## Related Docs

- Models: `MODEL.md`
- Controllers: `API-CONTROLLERS.md`
- Testing: `TESTING.md`
