# CLAUDE.md - AI Assistant Guide for Geolocation Application

## Project Overview

**Application Name:** Geolocation IP Lookup
**Technology:** Spring Boot 2.2.4 / Java 8 / MongoDB
**Purpose:** Retrieve and store geolocation information for IP addresses
**Status:** Partially complete - backend functional, UI has known issues
**Group ID:** `net.idt`
**Artifact ID:** `geolocation`
**Version:** 0.0.1-SNAPSHOT

### Quick Context
This is a web application that queries the ipapi.co external API to retrieve geolocation data for IP addresses and stores the history in MongoDB. It provides both a web UI (Thymeleaf-based) and REST API endpoints.

**Important Note:** The original developer acknowledged this is incomplete and has known issues that need resolution (see Known Issues section).

---

## Codebase Structure

```
geolocation/
├── src/main/java/net/idt/geolocation/
│   ├── GeolocationApplication.java          # Main Spring Boot entry point
│   ├── api/
│   │   ├── GeolocationController.java       # REST API controller (3 endpoints)
│   │   └── GreetingController.java          # Web UI controller
│   ├── model/
│   │   └── Geolocation.java                 # MongoDB data model
│   └── repository/
│       └── GeolocationRepository.java       # Spring Data MongoDB repository
├── src/main/resources/
│   ├── application.properties               # Thymeleaf configuration
│   ├── templates/
│   │   └── greeting.html                    # Main UI (Bootstrap + jQuery)
│   └── postman/
│       └── Geolocation.postman_collection.json  # API test collection
├── src/test/java/net/idt/geolocation/
│   └── GeolocationApplicationTests.java     # Basic context load test
├── docs/packages/                           # Package-specific development guides
│   ├── API-CONTROLLERS.md                   # Guide for API controller development
│   ├── MODEL.md                             # Guide for model/entity development
│   ├── REPOSITORY.md                        # Guide for repository development
│   ├── FRONTEND.md                          # Guide for UI/frontend development
│   └── TESTING.md                           # Comprehensive testing guide
├── pom.xml                                  # Maven build configuration
├── CLAUDE.md                                # AI assistant guide (this file)
└── README.md                                # User-facing documentation
```

---

## Technology Stack

### Backend
- **Framework:** Spring Boot 2.2.4.RELEASE
- **Java Version:** 1.8
- **Build Tool:** Maven (with wrapper)
- **Database:** MongoDB (Spring Data MongoDB)
- **Libraries:**
  - Google Guava 19.0 (collections utilities)
  - Apache Commons Lang3 3.9 (string utilities)
  - Jackson (JSON processing)

### Frontend
- **Template Engine:** Thymeleaf
- **CSS Framework:** Bootstrap 4.3.1
- **JavaScript:** jQuery 3.3.1
- **Data Tables:** Bootstrap Table 1.16.0

### External Dependencies
- **IP Geolocation API:** ipapi.co (requires internet access)
- **MongoDB:** Local instance required on default port (27017)

---

## Development Setup

### Prerequisites
1. **Java 8** or higher installed
2. **MongoDB** running locally on port 27017
3. **Internet access** for ipapi.co API calls
4. **Maven** (or use included wrapper `./mvnw`)

### Build & Run
```bash
# Build the application
mvn clean install

# Run the application
java -jar target/geolocation-0.0.1-SNAPSHOT.jar

# Or use Maven wrapper
./mvnw clean install
./mvnw spring-boot:run

# Access the UI
http://localhost:8080/app/greeting
```

### Running Tests
```bash
mvn test
```
Note: Currently only has a basic context load test.

---

## API Endpoints

### 1. Lookup IP Geolocation
```http
GET /geolocation/{ip}
```
**Description:** Fetches geolocation for an IP address from ipapi.co and stores in MongoDB
**Response:** Array containing single Geolocation object
**Side Effect:** Creates new database record with timestamp

**Example:**
```bash
curl http://localhost:8080/geolocation/8.8.8.8
```

### 2. Get IP-Specific History
```http
GET /geolocation/{ip}/history?start={MM.dd.yyyy}&end={MM.dd.yyyy}
```
**Description:** Retrieves all historical lookups for a specific IP
**Query Parameters:**
- `start` (optional): Start date in MM.dd.yyyy format
- `end` (optional): End date in MM.dd.yyyy format

**Example:**
```bash
curl "http://localhost:8080/geolocation/8.8.8.8/history?start=01.01.2024&end=12.31.2024"
```

### 3. Get All History with Filters
```http
GET /geolocation/history?start={MM.dd.yyyy}&end={MM.dd.yyyy}&n={number}
```
**Description:** Retrieves historical lookups across all IPs with filtering
**Query Parameters:**
- `start` (optional): Start date in MM.dd.yyyy format
- `end` (optional): End date in MM.dd.yyyy format
- `n` (optional): Limit to last N locations

**Example:**
```bash
curl "http://localhost:8080/geolocation/history?n=10"
```

### 4. Web UI
```http
GET /app/greeting
```
**Description:** Serves the main Thymeleaf-based web interface

---

## Database Schema

### Collection: `geolocation`

**Document Structure:**
```json
{
  "_id": "auto-generated-id",
  "searchId": "String (MongoDB @Id)",
  "ip": "String",
  "city": "String",
  "region": "String",
  "country_name": "String",
  "postal": "String",
  "latitude": "String",
  "longitude": "String",
  "timezone": "String",
  "timestamp": "Date"
}
```

**Notes:**
- `searchId` is the MongoDB document ID
- `timestamp` is set when saving to database (not from API)
- All geolocation fields (except timestamp) come from ipapi.co response
- Uses `@JsonIgnoreProperties(ignoreUnknown = true)` to ignore extra API fields

### Repository Methods
Located in `GeolocationRepository.java`:
- `findByIp(String ip)` - All records for an IP
- `findByIpAndTimestampBetween(String ip, Date start, Date end)` - IP history in date range
- `findByTimestampBetween(Date start, Date end, Pageable pageable)` - All history in date range
- `findFirstDistinctByIpIn(List<String> ips, Pageable pageable)` - Distinct IPs with pagination

---

## Code Conventions

### General Patterns
1. **Controller Pattern:** REST controllers use `@RestController` and `@RequestMapping`
2. **Repository Pattern:** Uses Spring Data MongoDB interfaces with custom query methods
3. **Service Layer:** **MISSING** - Controllers call repository and external API directly
4. **Error Handling:** **MINIMAL** - Only printStackTrace() calls, no proper exception handling

### Naming Conventions
- **Packages:** `net.idt.geolocation.{api|model|repository}`
- **Controllers:** Suffix with `Controller` (e.g., `GeolocationController`)
- **Models:** Match domain concept (e.g., `Geolocation`)
- **Repositories:** Suffix with `Repository` (e.g., `GeolocationRepository`)

### Code Style
- **Indentation:** Appears to use 2 spaces
- **Braces:** K&R style (opening brace on same line)
- **Fields:** Use snake_case for JSON mapping (e.g., `country_name`)
- **No Lombok:** Uses standard getters/setters

### Data Handling
- **Date Format:** `MM.dd.yyyy` (note: unusual period separators, not slashes)
- **JSON Processing:** Jackson ObjectMapper
- **Collections:** Uses Google Guava Lists utilities
- **String Validation:** Apache Commons StringUtils

---

## Known Issues and Limitations

### Critical Issues (Author-Acknowledged)
1. **UI Data Refresh:** Find Geolocation tab does not refresh table with newly entered IP
   - Data IS saved to backend
   - Workaround: User must manually refresh page

2. **Enter Key Behavior:** Hitting Enter key refreshes entire page instead of submitting form

3. **Exception Handling:** No proper error handling to display UI errors
   - Backend only uses `printStackTrace()`
   - No user-friendly error messages

4. **Top N Locations:** Error handling not implemented for "n" parameter

### Functional Limitations
1. **UI Missing Features:**
   - "Last N locations" functionality not in UI (backend has it)
   - "Date range filtering" not in UI (backend has it)
   - Only "Find Geolocation" tab is functional

2. **No Input Validation:**
   - No IP address format validation on backend
   - Relies on frontend regex only

3. **External API Dependency:**
   - No error handling for ipapi.co failures
   - No retry logic
   - No rate limiting

4. **No Service Layer:**
   - Business logic mixed in controllers
   - No separation of concerns

### Testing Gaps
- Only context load test exists
- No unit tests for business logic
- No integration tests for API endpoints
- No mocking of external ipapi.co calls

---

## Package-Specific Development Guides

For detailed instructions on working with specific parts of the codebase, see the package-specific guides in `docs/packages/`:

### 📦 API Controllers Package (`net.idt.geolocation.api`)
**Guide:** [`docs/packages/API-CONTROLLERS.md`](docs/packages/API-CONTROLLERS.md)

**When to read this:**
- Adding new REST API endpoints
- Modifying existing API endpoints
- Adding exception handling to controllers
- Replacing URLConnection with RestTemplate
- Creating a service layer
- Adding request validation

**Key topics covered:**
- API endpoint patterns and conventions
- Request/response handling
- Exception handling with @ControllerAdvice
- Logging best practices
- Service layer extraction

### 📊 Model Package (`net.idt.geolocation.model`)
**Guide:** [`docs/packages/MODEL.md`](docs/packages/MODEL.md)

**When to read this:**
- Adding new data models
- Modifying existing model fields
- Adding validation annotations
- Working with MongoDB indexes
- Understanding field naming conventions

**Key topics covered:**
- Field naming (snake_case vs camelCase)
- MongoDB annotations (@Id, @Document)
- Jackson annotations (@JsonIgnoreProperties)
- Model validation
- Best practices for POJOs

### 🗄️ Repository Package (`net.idt.geolocation.repository`)
**Guide:** [`docs/packages/REPOSITORY.md`](docs/packages/REPOSITORY.md)

**When to read this:**
- Adding custom query methods
- Working with MongoDB queries
- Using pagination and sorting
- Creating complex queries with @Query
- Using MongoTemplate for dynamic queries

**Key topics covered:**
- Spring Data query method naming
- Custom @Query annotations
- Pagination with Pageable
- MongoTemplate for complex operations
- Repository testing with @DataMongoTest

### 🎨 Frontend/UI (`src/main/resources/templates/`)
**Guide:** [`docs/packages/FRONTEND.md`](docs/packages/FRONTEND.md)

**When to read this:**
- Fixing the UI refresh issue (CRITICAL)
- Modifying Thymeleaf templates
- Adding new UI features
- Working with Bootstrap Table
- Handling AJAX calls and errors

**Key topics covered:**
- Fixing the table refresh bug
- Bootstrap Table methods
- AJAX patterns with jQuery
- Input validation
- Date format handling (MM.dd.yyyy)
- Loading indicators and error messages

### 🧪 Testing (`src/test/java/`)
**Guide:** [`docs/packages/TESTING.md`](docs/packages/TESTING.md)

**When to read this:**
- Writing unit tests
- Writing integration tests
- Testing controllers with MockMvc
- Testing repositories with @DataMongoTest
- Mocking external API calls
- Setting up test coverage

**Key topics covered:**
- Unit testing patterns
- Integration testing with @SpringBootTest
- Mocking with Mockito and WireMock
- Test coverage with JaCoCo
- CI/CD integration

---

## Working with External API

### ipapi.co API Details
- **Base URL:** `https://ipapi.co/{ip}/json/`
- **Method:** GET
- **Auth:** None required
- **Rate Limits:** Free tier has limits (not handled in code)
- **User-Agent Required:** Set to `"java-ipapi-client"`

### API Response Fields Used
```json
{
  "ip": "8.8.8.8",
  "city": "Mountain View",
  "region": "California",
  "country_name": "United States",
  "postal": "94035",
  "latitude": "37.386",
  "longitude": "-122.0838",
  "timezone": "America/Los_Angeles"
  // ... other fields ignored
}
```

### Implementation Location
See `GeolocationController.java:42-79` for the API integration code.

**Warning:** Uses raw `URLConnection` instead of modern HTTP clients (RestTemplate, WebClient, or HttpClient).

---

## Testing with Postman

A Postman collection is available at:
```
src/main/resources/postman/Geolocation.postman_collection.json
```

### Collection Contents
1. **Get Geolocation by IP** - Basic lookup
2. **Get History by IP with Date Range** - Filtered history
3. **Get Last N Locations** - Pagination example

Import into Postman to test all endpoints quickly.

---

## Configuration Files

### application.properties
```properties
spring.thymeleaf.cache=false              # Disable cache for development
spring.thymeleaf.enabled=true
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.application.name=Bootstrap Spring Boot
```

**Note:** No MongoDB configuration - uses Spring Boot defaults (localhost:27017)

### pom.xml Key Dependencies
```xml
<dependencies>
    <dependency>spring-boot-starter-web</dependency>
    <dependency>spring-boot-starter-thymeleaf</dependency>
    <dependency>spring-boot-starter-data-mongodb</dependency>
    <dependency>guava:19.0</dependency>
    <dependency>commons-lang3:3.9</dependency>
    <dependency>spring-boot-devtools</dependency> <!-- Runtime only -->
</dependencies>
```

---

## AI Assistant Guidelines

### When Working on This Project

1. **ALWAYS Check Package-Specific Guides First**
   - **Before modifying any code**, check the relevant package guide:
     - Working on controllers? → Read `docs/packages/API-CONTROLLERS.md`
     - Working on models? → Read `docs/packages/MODEL.md`
     - Working on repositories? → Read `docs/packages/REPOSITORY.md`
     - Working on UI? → Read `docs/packages/FRONTEND.md`
     - Writing tests? → Read `docs/packages/TESTING.md`
   - These guides contain detailed, package-specific conventions and examples

2. **Always Check Known Issues First**
   - Review the "Known Issues" section before starting work
   - The original developer noted this is incomplete
   - Focus on high-priority fixes if improving existing code

3. **Follow Existing Patterns**
   - No service layer exists (controllers call repositories directly)
   - No Lombok (use standard getters/setters)
   - Uses Guava and Commons Lang3 for utilities

4. **Database Considerations**
   - MongoDB must be running locally
   - No authentication configured
   - Collection name is `geolocation` (hardcoded in controller)

5. **Testing New Changes**
   - Start MongoDB first: `mongod`
   - Build with: `mvn clean install`
   - Run with: `java -jar target/geolocation-0.0.1-SNAPSHOT.jar`
   - Test UI at: `http://localhost:8080/app/greeting`
   - Test API with Postman collection

6. **Date Format Critical Detail**
   - **Always use `MM.dd.yyyy` format** (periods, not slashes!)
   - SimpleDateFormat pattern hardcoded in controller
   - Changing this requires updating both backend and frontend

7. **Code Quality Improvements Needed**
   - Add proper exception handling (currently just printStackTrace)
   - Extract service layer from controllers
   - Add input validation
   - Improve test coverage
   - Replace URLConnection with RestTemplate or WebClient
   - Add logging (SLF4J/Logback)

8. **Security Considerations**
   - No authentication/authorization implemented
   - No rate limiting for external API calls
   - No input sanitization (potential injection risks)
   - MongoDB connection has no authentication

### Recommended First Improvements

If asked to improve this codebase, prioritize:

1. **Fix UI refresh issue** - See `docs/packages/FRONTEND.md` for detailed fix
2. **Add proper exception handling** - See `docs/packages/API-CONTROLLERS.md` for @ControllerAdvice setup
3. **Add service layer** - See `docs/packages/API-CONTROLLERS.md` for service layer extraction
4. **Add comprehensive tests** - See `docs/packages/TESTING.md` for complete testing guide
5. **Replace URLConnection** - See `docs/packages/API-CONTROLLERS.md` for RestTemplate migration
6. **Add request validation** - See `docs/packages/API-CONTROLLERS.md` and `docs/packages/MODEL.md`
7. **Add logging** - See `docs/packages/API-CONTROLLERS.md` for SLF4J setup
8. **Implement error responses** - See `docs/packages/API-CONTROLLERS.md` for error DTOs

**Note:** Each improvement has detailed implementation examples in the referenced guides.

### What NOT to Change Without Asking

- Date format (MM.dd.yyyy) - used in API contract
- Database collection name - would break existing data
- API endpoint paths - may break existing clients
- Maven/Spring Boot versions - would require dependency audit
- Java version - may affect deployment environments

---

## Git Workflow

### Current Branch
Check git status for current development branch. All work should be done on feature branches starting with `claude/`.

### Commit Message Style
Review recent commits with:
```bash
git log --oneline -5
```

Example from history:
- "Removed test controller"
- "Update README.md"
- "Added history APIs and front end"

**Style:** Imperative mood, concise, no periods

### Pushing Changes
```bash
git add .
git commit -m "Your commit message"
git push -u origin <branch-name>
```

---

## Troubleshooting

### MongoDB Connection Issues
```
Error: MongoTimeoutException
```
**Fix:** Ensure MongoDB is running: `mongod`

### Port 8080 Already in Use
```
Error: Port 8080 is already in use
```
**Fix:** Kill process on 8080 or change port in application.properties:
```properties
server.port=8081
```

### ipapi.co API Failures
```
IOException when calling ipapi.co
```
**Fix:**
- Check internet connectivity
- Verify IP address format
- Check ipapi.co rate limits
- No retry logic exists (needs to be implemented)

### Maven Build Failures
```
Error: compilation failure
```
**Fix:** Ensure Java 8+ is installed and JAVA_HOME is set

---

## Additional Resources

- **Spring Boot Docs:** https://docs.spring.io/spring-boot/docs/2.2.4.RELEASE/reference/html/
- **Spring Data MongoDB:** https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/
- **ipapi.co API Docs:** https://ipapi.co/api/
- **Thymeleaf Docs:** https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html

---

## Version History

- **0.0.1-SNAPSHOT** (Current) - Initial incomplete implementation
  - Backend API functional
  - UI has refresh issues
  - Missing comprehensive error handling
  - Minimal test coverage

---

*Last Updated: 2025-11-20*
*This document is for AI assistants working on the geolocation application codebase.*
