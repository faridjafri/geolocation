# Model Guide

**Package:** `net.idt.geolocation.model`

## Current Model

### Geolocation.java
- Fields: searchId (ID), ip, city, region, country_name, postal, latitude, longitude, timezone, timestamp
- Annotations: `@Id`, `@JsonIgnoreProperties(ignoreUnknown = true)`

---

## Modifying Models

**Add field:**

```java
private String newField;

public String getNewField() {
    return newField;
}

public void setNewField(String newField) {
    this.newField = newField;
}
```

**Remember:** No Lombok - write getters/setters manually

---

## Field Naming

**Use snake_case for JSON fields:**

```java
private String country_name;  // Matches API response

public String getCountry_name() {
    return country_name;
}
```

---

## Adding New Models

```java
package net.idt.geolocation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "collection_name")
public class YourModel {

    @Id
    private String id;
    private String field1;

    // Constructor
    public YourModel() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
```

---

## Validation

```java
import javax.validation.constraints.*;

@NotBlank(message = "IP is required")
@Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")
private String ip;

@Size(max = 100)
private String city;
```

---

## Indexes

```java
import org.springframework.data.mongodb.core.index.Indexed;

@Indexed
private String ip;
```

---

## Best Practices

1. **Always use @JsonIgnoreProperties** - protects against API changes
2. **Provide default constructor** - required for Jackson
3. **Use appropriate types** - Current uses String for lat/long, consider Double
4. **Override toString()** - helpful for debugging

```java
@Override
public String toString() {
    return "Geolocation{ip='" + ip + "', city='" + city + "'}";
}
```

---

## Related Docs

- Controllers: `API-CONTROLLERS.md`
- Repositories: `REPOSITORY.md`
