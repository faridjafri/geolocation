# Testing Guide

**Location:** `src/test/java/net/idt/geolocation/`

---

## Testing Models

```java
public class GeolocationTest {
    @Test
    public void testSettersAndGetters() {
        Geolocation geo = new Geolocation();
        geo.setIp("8.8.8.8");
        assertEquals("8.8.8.8", geo.getIp());
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

## Testing Controllers

```java
@WebMvcTest(GeolocationController.class)
public class GeolocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeolocationRepository repository;

    @Test
    public void testGetHistory() throws Exception {
        when(repository.findByIp("8.8.8.8"))
            .thenReturn(Arrays.asList(new Geolocation()));

        mockMvc.perform(get("/geolocation/8.8.8.8/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
```

---

## Running Tests

```bash
mvn test                                    # All tests
mvn test -Dtest=GeolocationRepositoryTest   # One class
mvn install -DskipTests                     # Skip tests
```
