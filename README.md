# BloodBank — Blood Bank Inventory and Donor Eligibility Tracker

A complete, production-ready Java Spring Boot & MySQL REST API project for managing blood bank inventory, tracking donor eligibility, recording donations, and issuing blood using FEFO (First Expiring, First Out) rules while strictly excluding expired blood stock.

---

## Project Structure

```
bloodbank/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           └── bloodbank/
    │   │               ├── BloodBankApplication.java
    │   │               ├── controller/
    │   │               │   ├── BloodBankController.java
    │   │               │   └── DonorController.java
    │   │               ├── dto/
    │   │               │   ├── BloodUnitResponse.java
    │   │               │   ├── DonationRequest.java
    │   │               │   ├── DonationResponse.java
    │   │               │   ├── DonorRequest.java
    │   │               │   ├── DonorResponse.java
    │   │               │   ├── EligibilityResponse.java
    │   │               │   ├── ErrorResponse.java
    │   │               │   ├── IssueRequest.java
    │   │               │   ├── IssueResponse.java
    │   │               │   └── StockResponse.java
    │   │               ├── entity/
    │   │               │   ├── BloodGroup.java
    │   │               │   ├── BloodUnit.java
    │   │               │   ├── Donation.java
    │   │               │   ├── Donor.java
    │   │               │   └── IssueRecord.java
    │   │               ├── exception/
    │   │               │   ├── BusinessException.java
    │   │               │   ├── GlobalExceptionHandler.java
    │   │               │   └── ResourceNotFoundException.java
    │   │               ├── repository/
    │   │               │   ├── BloodUnitRepository.java
    │   │               │   ├── DonationRepository.java
    │   │               │   ├── DonorRepository.java
    │   │               │   └── IssueRecordRepository.java
    │   │               └── service/
    │   │                   ├── BloodBankService.java
    │   │                   └── DonorService.java
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── bloodbank/
        │               ├── BloodBankApplicationTests.java
        │               └── service/
        │                   └── BloodBankServiceTest.java
        └── resources/
            └── application-test.properties
```

---

## Technology Stack

- **Java Version:** Java 17 / Java 21
- **Framework:** Spring Boot 3.3.4
- **Build Tool:** Maven
- **Modules:** Spring Web, Spring Data JPA, Hibernate, Jakarta Validation
- **Database:** MySQL 8 (H2 in-memory for testing)
- **IDE:** VS Code / IntelliJ IDEA / Eclipse

---

## System Architecture

```
Client (Postman / Browser)
         │
         ▼
 ┌───────────────┐
 │ Controllers   │ (DonorController, BloodBankController)
 └───────┬───────┘
         │
         ▼
 ┌───────────────┐
 │ Services      │ (DonorService, BloodBankService) - Handles Business Logic & @Transactional
 └───────┬───────┘
         │
         ▼
 ┌───────────────┐
 │ Repositories  │ (DonorRepo, DonationRepo, BloodUnitRepo, IssueRecordRepo) - JPQL Queries
 └───────┬───────┘
         │
         ▼
 ┌───────────────┐
 │ MySQL 8 DB    │ (Tables: donors, donations, blood_units, issue_records)
 └───────────────┘
```

- **Controller Layer:** Receives HTTP requests, executes DTO validation with `@Valid`, and returns standard HTTP responses.
- **Service Layer:** Encapsulates business logic (eligibility gap calculation, FEFO stock issue, automatic expiry date generation, and error checking).
- **Repository Layer:** Extends Spring Data JPA repositories and executes specialized JPQL queries.
- **Exception Layer:** Global exception handler (`@RestControllerAdvice`) intercepts all runtime exceptions and converts them into standardized JSON error responses.

---

## Business Rules Implemented

1. **Donor Eligibility (Rule 1):** A donor cannot donate again within **90 days** (`bloodbank.donation.minimum-gap-days=90`) of their previous donation. Attempted donations within 90 days are rejected with HTTP 400.
2. **Blood Expiry (Rule 2):** Blood shelf life is automatically calculated as **42 days** (`bloodbank.blood.default-shelf-life-days=42`) from the collection date. Expiry date input is not required from the API client.
3. **Expiring Blood Alert (Rule 3):** Blood units expiring within **7 days** (`bloodbank.blood.expiry-warning-days=7`) are retrieved via GET `/api/bloodbank/expiring`.
4. **Expiry Exclusion (Rule 4):** Expired blood (`expiryDate < currentDate`) is strictly excluded from stock calculation and issue processing.
5. **Blood Issuance & FEFO (Rule 5):** Blood is issued based on **FEFO (First Expiring, First Out)**. All database modifications occur inside `@Transactional` boundaries. If usable stock is insufficient, the transaction rolls back completely without partial issuance.
6. **Current Stock (Rule 6):** GET `/api/bloodbank/stock` returns usable (non-expired) available units aggregated across all 8 blood groups (`A_POSITIVE`, `A_NEGATIVE`, `B_POSITIVE`, `B_NEGATIVE`, `AB_POSITIVE`, `AB_NEGATIVE`, `O_POSITIVE`, `O_NEGATIVE`).

---

## How to Configure MySQL Database

1. Ensure MySQL Server 8.0 is running on `localhost:3006`.
2. Open `src/main/resources/application.properties` and configure your credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bloodbank?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

bloodbank.donation.minimum-gap-days=90
bloodbank.blood.default-shelf-life-days=42
bloodbank.blood.expiry-warning-days=7
```

3. MySQL automatically creates the database `bloodbank` and all tables (`donors`, `donations`, `blood_units`, `issue_records`) upon initial startup.

---

## How to Run in VS Code

### Method A: Maven Command (Terminal)
Open terminal in the project root (`d:\yugesh`) and execute:

```powershell
mvn clean spring-boot:run
```
*(Or if using full Maven executable path: `& "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.4.1\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run`)*

### Method B: VS Code Java Extension
1. Install **Extension Pack for Java** and **Spring Boot Extension Pack** in VS Code.
2. Open `BloodBankApplication.java`.
3. Click **Run** or press `F5`.

---

## API Testing & Demonstration Sequence

### Step 1: Create a Donor
**Request:** `POST /api/donors`
```json
{
  "name": "Arun Kumar",
  "phone": "9876543210",
  "bloodGroup": "O_POSITIVE",
  "age": 25
}
```
**Expected Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Arun Kumar",
  "phone": "9876543210",
  "age": 25,
  "bloodGroup": "O_POSITIVE"
}
```

---

### Step 2: Check Donor Eligibility
**Request:** `GET /api/bloodbank/donors/1/eligibility`

**Expected Response:** `200 OK`
```json
{
  "donorId": 1,
  "eligible": true,
  "message": "Eligible to donate. No previous donation record found.",
  "lastDonationDate": null,
  "daysSinceLastDonation": null,
  "minimumGapDays": 90
}
```

---

### Step 3: Record a Donation
**Request:** `POST /api/bloodbank/donations`
```json
{
  "donorId": 1,
  "collectionDate": "2026-09-28",
  "units": 5
}
```
**Expected Response:** `201 Created`
```json
{
  "id": 1,
  "donorId": 1,
  "donorName": "Arun Kumar",
  "bloodGroup": "O_POSITIVE",
  "collectionDate": "2026-09-28",
  "expiryDate": "2026-11-09",
  "units": 5,
  "message": "Donation recorded successfully."
}
```

---

### Step 4: Check Current Stock
**Request:** `GET /api/bloodbank/stock`

**Expected Response:** `200 OK`
```json
{
  "stock": {
    "A_POSITIVE": 0,
    "A_NEGATIVE": 0,
    "B_POSITIVE": 0,
    "B_NEGATIVE": 0,
    "AB_POSITIVE": 0,
    "AB_NEGATIVE": 0,
    "O_POSITIVE": 5,
    "O_NEGATIVE": 0
  },
  "totalAvailableUnits": 5
}
```

---

### Step 5: Check Expiring Blood Units
**Request:** `GET /api/bloodbank/expiring`

**Expected Response:** `200 OK`
```json
[]
```
*(Returns units whose expiry date is within the next 7 days).*

---

### Step 6: Issue Blood
**Request:** `POST /api/bloodbank/issues`
```json
{
  "bloodGroup": "O_POSITIVE",
  "patientName": "Patient A",
  "units": 2
}
```
**Expected Response:** `201 Created`
```json
{
  "patientName": "Patient A",
  "bloodGroup": "O_POSITIVE",
  "totalUnitsIssued": 2,
  "issueDate": "2026-09-28",
  "message": "Blood issued successfully.",
  "issueRecordIds": [1]
}
```

---

### Step 7: Check Stock Again After Issuance
**Request:** `GET /api/bloodbank/stock`

**Expected Response:** `200 OK`
```json
{
  "stock": {
    "A_POSITIVE": 0,
    "A_NEGATIVE": 0,
    "B_POSITIVE": 0,
    "B_NEGATIVE": 0,
    "AB_POSITIVE": 0,
    "AB_NEGATIVE": 0,
    "O_POSITIVE": 3,
    "O_NEGATIVE": 0
  },
  "totalAvailableUnits": 3
}
```

---

### Step 8: Attempt Invalid Second Donation (< 90 Days Gap)
**Request:** `POST /api/bloodbank/donations`
```json
{
  "donorId": 1,
  "collectionDate": "2026-10-15",
  "units": 2
}
```
**Expected Response:** `400 Bad Request`
```json
{
  "timestamp": "2026-09-28T20:47:00",
  "status": 400,
  "message": "Donation rejected. Donor must wait at least 90 days between donations. Days remaining: 73."
}
```

---

### Step 9: Attempt Issuing More Blood Than Available
**Request:** `POST /api/bloodbank/issues`
```json
{
  "bloodGroup": "O_POSITIVE",
  "patientName": "Patient B",
  "units": 10
}
```
**Expected Response:** `400 Bad Request`
```json
{
  "timestamp": "2026-09-28T20:47:00",
  "status": 400,
  "message": "Insufficient usable stock for O_POSITIVE. Required: 10 units, but only 3 usable units available. Expired units are excluded."
}
```
