# Model Guide

**Package:** `net.idt.geolocation.model`

## Files in This Package

- `Geolocation.java` - IP geolocation data model

---

## Adding a Field

```java
private String newField;

public String getNewField() {
    return newField;
}

public void setNewField(String newField) {
    this.newField = newField;
}
```

**Remember:** No Lombok in this project - write getters/setters manually

---

## Field Naming

Use snake_case for JSON fields that come from external API:

```java
private String country_name;  // Matches ipapi.co response

public String getCountry_name() {
    return country_name;
}
```

---

## Adding Validation

```java
import javax.validation.constraints.*;

@NotBlank(message = "IP is required")
@Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")
private String ip;
```

---

## Creating a New Model

```java
package net.idt.geolocation.model;

import org.springframework.data.annotation.Id;

public class YourModel {
    @Id
    private String id;
    private String field;

    // Constructor
    public YourModel() {}

    // Getters/setters
}
```

For repository: see `REPOSITORY.md`

---

## Key Annotations

- `@Id` - MongoDB primary key
- `@JsonIgnoreProperties(ignoreUnknown = true)` - Ignore extra API fields (always use this)
