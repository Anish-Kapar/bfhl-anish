# BFHL API — Bajaj Finserv API Round (Java/Spring Boot)

## Quick Start

### 1. Update your details in `application.properties`
```properties
app.user.full-name=anish_kapar       # your full name (lowercase, underscore)
app.user.dob=ddmmyyyy                # e.g. 01012003  ← YOUR actual DOB
app.user.email=anish@chitkara.edu.in # your actual email
app.user.roll-number=2110992059      # your actual roll number
```

### 2. Run locally
```bash
mvn spring-boot:run
# Test: POST http://localhost:8080/bfhl
```

### 3. Run tests
```bash
mvn test
```

---

## Deploy to Render (Free)

1. Push this project to a **GitHub repo** (public or private)
2. Go to https://render.com → New → Web Service
3. Connect your GitHub repo
4. Settings:
   - **Environment**: Docker  ← it auto-detects the Dockerfile
   - **Build Command**: (leave blank, Docker handles it)
   - **Start Command**: (leave blank)
   - **Port**: 8080
5. Click **Create Web Service**
6. Your URL will be: `https://your-app-name.onrender.com/bfhl`

---

## Deploy to Railway

1. Push to GitHub
2. Go to https://railway.app → New Project → Deploy from GitHub
3. Select your repo
4. Add environment variable if needed: `PORT=8080`
5. URL: `https://your-app.railway.app/bfhl`

---

## API Usage

**POST /bfhl**

Request:
```json
{
  "data": ["a", "1", "334", "4", "R", "$"]
}
```

Response:
```json
{
  "is_success": true,
  "user_id": "anish_kapar_01012003",
  "email": "anish@chitkara.edu.in",
  "roll_number": "2110992059",
  "odd_numbers": ["1"],
  "even_numbers": ["334", "4"],
  "alphabets": ["A", "R"],
  "special_characters": ["$"],
  "sum": "339",
  "concat_string": "Ra"
}
```

---

## Project Structure
```
src/
├── main/java/com/anish/bfhl/
│   ├── BfhlApplication.java          # Entry point
│   ├── controller/BfhlController.java # POST /bfhl
│   ├── dto/
│   │   ├── BfhlRequestDTO.java        # Request DTO
│   │   ├── BfhlResponseDTO.java       # Response DTO
│   │   └── ErrorResponseDTO.java      # Error DTO
│   ├── service/
│   │   ├── BfhlService.java           # Interface
│   │   └── impl/BfhlServiceImpl.java  # Implementation
│   └── exception/
│       └── GlobalExceptionHandler.java # Exception handling
└── test/java/com/anish/bfhl/
    └── BfhlApplicationTests.java       # JUnit test cases
```
