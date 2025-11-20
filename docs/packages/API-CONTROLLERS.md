# API Controllers Guide

**Package:** `net.idt.geolocation.api`

## Files in This Package

- `GeolocationController.java` - REST API (3 endpoints)
- `GreetingController.java` - Web UI controller

---

## Adding a New Endpoint

```java
@RequestMapping(value = "/path", method = RequestMethod.GET)
@ResponseBody
public List<Geolocation> yourMethod(@PathVariable String param) {
    return repository.findByIp(param);
}
```

For repository methods: see `REPOSITORY.md`

---

## Date Format in Controllers

Always use `MM.dd.yyyy` (periods, not slashes):

```java
String pattern = "MM.dd.yyyy";
SimpleDateFormat sdf = new SimpleDateFormat(pattern);
Date date = sdf.parse("11.20.2025");
```

---

## Replacing URLConnection (Lines 42-79)

**Current:** Uses URLConnection
**Better:** Use RestTemplate (requires adding bean to `GeolocationApplication.java`)

```java
@Autowired
private RestTemplate restTemplate;

@GetMapping("/{ip}")
public List<Geolocation> getGeolocation(@PathVariable String ip) {
    String url = "https://ipapi.co/" + ip + "/json/";
    HttpHeaders headers = new HttpHeaders();
    headers.set("User-Agent", "java-ipapi-client");

    Geolocation geo = restTemplate.exchange(
        url, HttpMethod.GET, new HttpEntity<>(headers), Geolocation.class
    ).getBody();

    geo.setTimestamp(new Date());
    repository.save(geo);
    return Collections.singletonList(geo);
}
```

---

## Adding Logging

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(GeolocationController.class);

// Replace printStackTrace
logger.error("Failed to fetch geolocation for IP: {}", ip, e);
```

---

## Testing Controllers

See `TESTING.md` for MockMvc examples.
