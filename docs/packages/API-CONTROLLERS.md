# API Controllers Guide

**Package:** `net.idt.geolocation.api`

## Controllers

### GeolocationController.java
- REST API endpoints: 3 (lookup, IP history, all history)
- Path: `/geolocation`
- Uses: GeolocationRepository, MongoTemplate

### GreetingController.java
- Web UI controller
- Path: `/app/greeting`
- Returns: Thymeleaf template

---

## Adding New Endpoints

```java
@RequestMapping(value = "/path", method = RequestMethod.GET)
@ResponseBody
public List<Geolocation> yourMethod(@PathVariable String param) {
    // Implementation
    return repository.findByIp(param);
}
```

**Steps:**
1. Add method to controller
2. Add repository method if needed (see REPOSITORY.md)
3. Test with Postman

---

## Date Format (CRITICAL)

Always use `MM.dd.yyyy` format (periods, not slashes):

```java
String pattern = "MM.dd.yyyy";
SimpleDateFormat sdf = new SimpleDateFormat(pattern);
Date date = sdf.parse("11.20.2025");
```

---

## Add Exception Handling

**Create @ControllerAdvice:**

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException ex) {
        return new ErrorResponse(400, ex.getMessage());
    }
}
```

**Create ErrorResponse class:**

```java
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    // getters
}
```

---

## Replace URLConnection with RestTemplate

**Current (bad):** Lines 42-79 in GeolocationController use URLConnection

**Better approach:**

```java
@Autowired
private RestTemplate restTemplate;

@GetMapping("/{ip}")
public List<Geolocation> getGeolocation(@PathVariable String ip) {
    String url = "https://ipapi.co/" + ip + "/json/";

    HttpHeaders headers = new HttpHeaders();
    headers.set("User-Agent", "java-ipapi-client");
    HttpEntity<String> entity = new HttpEntity<>(headers);

    Geolocation geo = restTemplate.exchange(url, HttpMethod.GET, entity, Geolocation.class).getBody();
    geo.setTimestamp(new Date());
    repository.save(geo);

    return Collections.singletonList(geo);
}
```

**Configure RestTemplate bean:**

```java
@Bean
public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(5000);
    return new RestTemplate(factory);
}
```

---

## Add Logging

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(GeolocationController.class);

// Replace printStackTrace
logger.error("Failed to fetch geolocation for IP: {}", ip, e);
```

---

## Create Service Layer

**1. Create service interface:**

```java
public interface GeolocationService {
    Geolocation lookupIp(String ip);
    List<Geolocation> getHistory(String ip, Date start, Date end);
}
```

**2. Implement service:**

```java
@Service
public class GeolocationServiceImpl implements GeolocationService {
    @Autowired
    private GeolocationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Geolocation lookupIp(String ip) {
        // Move API call logic here
    }
}
```

**3. Use in controller:**

```java
@Autowired
private GeolocationService service;

@GetMapping("/{ip}")
public List<Geolocation> getGeolocation(@PathVariable String ip) {
    return Collections.singletonList(service.lookupIp(ip));
}
```

---

## Add Request Validation

```java
// Add to pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

```java
@PostMapping
public Geolocation create(@Valid @RequestBody GeolocationRequest request) {
    // Validation happens automatically
}
```

---

## Testing

```java
@WebMvcTest(GeolocationController.class)
public class GeolocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeolocationRepository repository;

    @Test
    public void testGetHistory() throws Exception {
        when(repository.findByIp("8.8.8.8")).thenReturn(Arrays.asList(new Geolocation()));

        mockMvc.perform(get("/geolocation/8.8.8.8/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
```

---

## Related Docs

- Models: `MODEL.md`
- Repositories: `REPOSITORY.md`
- Testing: `TESTING.md`
