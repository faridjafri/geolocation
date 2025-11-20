# Testing Guide

## Current State

**GeolocationApplicationTests.java** - Only has context load test (basic)

**Missing:**
- Unit tests for business logic
- Integration tests for API endpoints
- Repository tests
- Mocking external API calls

---

## Dependencies

Add to `pom.xml` if needed:

```xml
<!-- Embedded MongoDB for tests -->
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <scope>test</scope>
</dependency>

<!-- WireMock for HTTP mocking -->
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.27.2</version>
    <scope>test</scope>
</dependency>
```

---

## Unit Testing Models

```java
public class GeolocationTest {

    @Test
    public void testSettersAndGetters() {
        Geolocation geo = new Geolocation();
        geo.setIp("8.8.8.8");
        geo.setCity("Mountain View");

        assertEquals("8.8.8.8", geo.getIp());
        assertEquals("Mountain View", geo.getCity());
    }
}
```

---

## Testing Repositories

```java
@DataMongoTest
public class GeolocationRepositoryTest {

    @Autowired
    private GeolocationRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void testFindByIp() {
        Geolocation geo = new Geolocation();
        geo.setIp("8.8.8.8");
        geo.setTimestamp(new Date());
        repository.save(geo);

        List<Geolocation> results = repository.findByIp("8.8.8.8");

        assertEquals(1, results.size());
        assertNotNull(results.get(0).getSearchId());
    }
}
```

---

## Testing Controllers

```java
@WebMvcTest(GeolocationController.class)
public class GeolocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeolocationRepository repository;

    @MockBean
    private MongoTemplate mongoTemplate;

    @Test
    public void testGetHistory() throws Exception {
        Geolocation geo = new Geolocation();
        geo.setIp("8.8.8.8");

        when(repository.findByIp("8.8.8.8")).thenReturn(Arrays.asList(geo));

        mockMvc.perform(get("/geolocation/8.8.8.8/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].ip").value("8.8.8.8"));
    }
}
```

---

## Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GeolocationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GeolocationRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void testGetHistoryEndpoint() {
        Geolocation geo = new Geolocation();
        geo.setIp("8.8.8.8");
        geo.setTimestamp(new Date());
        repository.save(geo);

        ResponseEntity<List> response = restTemplate.getForEntity(
            "/geolocation/8.8.8.8/history",
            List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }
}
```

---

## Mocking External APIs

```java
public class ExternalApiMockTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testMockIpapiResponse() {
        stubFor(get(urlEqualTo("/8.8.8.8/json/"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"ip\":\"8.8.8.8\",\"city\":\"Mountain View\"}")));

        // Test code here
    }
}
```

---

## Test Coverage (JaCoCo)

Add to `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

Generate report:
```bash
mvn test jacoco:report
```

---

## Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=GeolocationRepositoryTest

# Specific method
mvn test -Dtest=GeolocationRepositoryTest#testFindByIp

# Skip tests
mvn install -DskipTests
```

---

## Best Practices

1. **AAA Pattern** - Arrange, Act, Assert
2. **Descriptive names** - `testFindByIpReturnsCorrectResults()`
3. **Test edge cases** - null values, empty lists, invalid input
4. **Clean up** - Use `@BeforeEach` and `@AfterEach`
5. **Mock external calls** - Never make real HTTP calls in tests

---

## Related Docs

- Controllers: `API-CONTROLLERS.md`
- Repositories: `REPOSITORY.md`
- Models: `MODEL.md`
