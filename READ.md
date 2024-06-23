# XML Event Parser

This is a Spring Boot application that reads XML event files, extracts specific elements, stores them into a database, filters the events based on a set of criteria, and reports the events in JSON format.

## Project Structure

The project follows the Domain-Driven Design (DDD) principles and is structured as follows:

- **domain**: Contains the domain model and repository interface.
- **service**: Contains the service layer with business logic.
- **controller**: Contains the REST controller for handling HTTP requests.
- **exception**: Contains the global exception handler.
- **resources**: Contains application configuration files.

## Prerequisites

- Java 11 or higher
- Maven
- Spring Boot

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/your-repo/xml-event-parser.git
cd xml-event-parser
```
### Build the Project
```bash
mvn clean install
```
### Run the Application
```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080.

### API Endpoints

#### 1. Read and Save Events

Reads the XML event file and stores the events in the H2 database.

* URL: /api/events/read
* Method: POST
* Params: filePath (The path to the XML file)
* Success Response:
  * Code: `200 OK`
  * Content: List of saved events

Example Request:
```bash
curl -X POST "http://localhost:8080/api/events/process"
```

#### 2. Filter Events

Filters the events based on predefined criteria and returns the result in JSON format.

* URL: /api/events/filter
* Method: GET
* Success Response:
  * Code: `200 OK`
  * Content: List of filtered events
  
Example Request
```bash
curl -X GET "http://localhost:8080/api/events/filter"
```

### Configuration
The application uses an H2 in-memory database for storage. The database configuration can be found in the application.properties file:
```properties
spring.datasource.url=jdbc:h2:mem:xmlparser
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```
You can access the H2 console at http://localhost:8080/h2-console with the following credentials:

* **JDBC URL: jdbc:h2:mem:testdb**
* **Username: sa**
* **Password: password**

### Running Tests
The project includes unit tests for the service layer. To run the tests, use the following command:
```bash
mvn test
```

