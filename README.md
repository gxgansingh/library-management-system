# Library Management System

A basic REST API built with Java 17, Spring Boot, Maven, Spring Web, Spring Data JPA, Bean Validation, and H2. It is designed to demonstrate the Unit 4 Spring Boot assignment requirements.

## 1. Problem description
A small library needs to keep track of books, registered members, and book loans. This application provides REST endpoints to add/list books and members, borrow books, return books, and list current loans.

## 2. Requirements covered
- Java 17, Spring Boot, Maven, executable Spring Boot application.
- REST endpoints using Spring MVC.
- Spring Boot auto-configuration for web server, JSON conversion, JPA, H2 datasource, and repository implementation.
- Three-layer architecture: Controller -> Service -> Repository -> H2 database.
- Three domain entities: `Book`, `Member`, `Loan`.
- Business rules live in the service layer.
- JUnit 5 / Mockito automated tests and a Spring Boot context test.
- Manual test cases and request flow documented below.

## 3. Architecture
See [ARCHITECTURE.md](ARCHITECTURE.md).

```text
Client (Postman/curl)
        |
        v
Controller Layer: BookController / MemberController / LoanController
        |
        v
Service Layer: BookService / MemberService / LoanService
        |
        v
Repository Layer: BookRepository / MemberRepository / LoanRepository
        |
        v
H2 in-memory database
```

## 4. Domain model

### Book
- `id`: generated primary key
- `title`, `author`, `isbn`
- `totalCopies`, `availableCopies`

### Member
- `id`: generated primary key
- `name`, `email`, `phone`

### Loan
- `id`: generated primary key
- `book`, `member`
- `loanDate`, `dueDate`, `returnDate`
- `status`: `ACTIVE` or `RETURNED`

Relationships: one book and one member can each be linked to many loan records; each loan references one book and one member.

## 5. Business rules
1. **Availability rule:** a book can only be borrowed when `availableCopies > 0`. Borrowing decrements available copies.
2. **Loan limit rule:** a member may have at most three active loans.
3. **Return rule:** a loan may only be returned once. Returning it increments the book's available copy count.
4. A new book must have at least one copy; ISBNs and member emails must be unique.
5. A new loan is due 14 days after the loan date.

## 6. Package structure

```text
src/main/java/com/example/library/
├── LibraryManagementApplication.java
├── controller/
│   ├── BookController.java
│   ├── MemberController.java
│   └── LoanController.java
├── exception/
│   ├── ApiExceptionHandler.java
│   ├── BusinessRuleException.java
│   └── ResourceNotFoundException.java
├── model/
│   ├── Book.java
│   ├── Member.java
│   └── Loan.java
├── repository/
│   ├── BookRepository.java
│   ├── MemberRepository.java
│   └── LoanRepository.java
└── service/
    ├── BookService.java
    ├── MemberService.java
    └── LoanService.java
src/main/resources/application.properties
src/test/java/com/example/library/
├── LibraryManagementApplicationTests.java
└── service/LoanServiceTest.java
```

## 7. Run the application

Prerequisites: JDK 17 and Maven 3.6+.

From the folder containing `pom.xml`:

```bash
mvn test
mvn spring-boot:run
```

Or build an executable JAR:

```bash
mvn clean package
java -jar target/library-management-0.0.1-SNAPSHOT.jar
```

Base URL: `http://localhost:8080`

H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:librarydb`
- User: `sa`
- Password: leave blank

The H2 database is in-memory, so data resets when the application stops.

## 8. REST API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/books` | List books |
| GET | `/api/books/{id}` | Get a book |
| POST | `/api/books` | Add a book |
| GET | `/api/members` | List members |
| GET | `/api/members/{id}` | Get a member |
| POST | `/api/members` | Register a member |
| GET | `/api/loans` | List all loans |
| GET | `/api/loans/active` | List active loans |
| POST | `/api/loans/borrow` | Borrow a book |
| POST | `/api/loans/{id}/return` | Return a loan |

### Example requests

Create a book:

```http
POST /api/books
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "totalCopies": 3,
  "availableCopies": 3
}
```

The service resets `availableCopies` to `totalCopies` for a newly created book.

Create a member:

```http
POST /api/members
Content-Type: application/json

{
  "name": "Asha Rao",
  "email": "asha@example.com",
  "phone": "9876543210"
}
```

Borrow a book (replace IDs with the IDs returned by your requests):

```http
POST /api/loans/borrow
Content-Type: application/json

{
  "bookId": 1,
  "memberId": 1
}
```

Return a loan:

```http
POST /api/loans/1/return
```

## 9. Manual test cases

**Important:** The actual output column below is a template to complete after you run the app. Do not report these as executed results until you have tested them.

| Test | Input / Action | Expected output | Actual output | Status |
|---|---|---|---|---|
| TC-01 | `POST /api/books` with a unique ISBN and `totalCopies: 3` | `201 Created`; book returned with `availableCopies: 3` | Fill after running | Pending |
| TC-02 | Create member using a valid unique email | `201 Created`; member JSON returned | Fill after running | Pending |
| TC-03 | Borrow book with at least one available copy and valid member | `201 Created`; loan status `ACTIVE`; available copies decrease by 1 | Fill after running | Pending |
| TC-04 | Try borrowing when `availableCopies` is 0 | `400 Bad Request` with business-rule message | Fill after running | Pending |
| TC-05 | Try creating a book with `totalCopies: 0` | `400 Bad Request` from business rule | Fill after running | Pending |
| TC-06 | Return the same loan twice | First return succeeds; second returns `400 Bad Request` | Fill after running | Pending |

## 10. Automated tests
`LoanServiceTest` covers:
- Borrowing an available book succeeds and decrements copies.
- Borrowing an unavailable book is rejected.
- A member with three active loans cannot borrow another book.

`LibraryManagementApplicationTests` checks that the Spring application context loads.

Run:

```bash
mvn test
```

A successful run ends with `BUILD SUCCESS`. Record the actual test results from your run.

## 11. Spring Boot auto-configuration
`@SpringBootApplication` includes component scanning and auto-configuration. With Spring Web on the classpath, Spring Boot configures the embedded Tomcat server and JSON message converters. With Spring Data JPA and the H2 driver present, it configures a datasource, Hibernate/JPA, and repository infrastructure based on `application.properties`. The repository interfaces extend `JpaRepository`; no implementation classes or manual datasource bean are needed.

## 12. Screenshots required by the assignment
Capture your own screenshots after running the project:
1. Terminal/IDE showing `mvn spring-boot:run` and successful startup.
2. Postman/browser showing `GET http://localhost:8080/api/books` response.
3. Postman showing a successful `POST /api/books` or borrow operation.
4. Terminal showing `mvn test` with `BUILD SUCCESS`.

Screenshots are intentionally not fabricated. Capture them from your actual run.


## 13. Web UI (Bookworm)
A responsive multi-page UI is included under `src/main/resources/static/`:
- `/` or `/index.html`: Dashboard
- `/books.html`: Books
- `/members.html`: Members
- `/transactions.html`: Transactions

The UI is served by Spring Boot on the same port as the REST API. Start the app, then open `http://localhost:8080/`. The UI uses `app.js` to call the existing REST endpoints. The Add Book and Add Member buttons create backend records; New Transaction checks out a book; Return processes a loan. The interface uses browser prompts for these forms to keep the supplied design intact.


## 13. Web UI (Bookworm)
The application now serves a responsive web UI from Spring Boot static resources:
- `/` or `/index.html`: live dashboard with catalogue, member, loan and overdue statistics.
- `/books.html`: searchable/filterable inventory, stock bars, add-book modal and issue shortcut.
- `/members.html`: member registration, search and active-loan counts.
- `/transactions.html`: issue/return workflow, status filtering, due-date and overdue tracking.

The forms call the same REST API endpoints described above. No separate Node.js frontend server is required.

### UI quick start
1. Run `LibraryManagementApplication` in IntelliJ.
2. Visit `http://localhost:8080/`.
3. Add a book and a member.
4. Issue a book from the dashboard or Transactions page.
5. Return it from the Transactions table.
6. The database is in-memory, so records reset when the application stops.

The UI displays live values from the backend rather than hard-coded sample counts. The current backend supports create/list books and members, issue/return loans. Edit/delete workflows are not exposed because the current backend has no corresponding endpoints.

## 14. Expanded management operations
- Books: `PUT /api/books/{id}` updates title, author, ISBN and total copies while preserving the count of copies currently borrowed; `DELETE /api/books/{id}` is rejected if transaction history exists.
- Members: `PUT /api/members/{id}` updates a member and enforces unique email; `DELETE /api/members/{id}` is rejected if transaction history exists.
- Loans: `GET /api/loans/{id}` returns a single transaction; the existing borrow and return endpoints remain the circulation workflow.
- UI book and member tables now include edit and delete actions. Destructive actions ask for confirmation, and API business-rule errors are shown in notifications.


## Shared UI consistency fix
All four pages now use the same sidebar markup, canonical shared CSS rules, and a single JavaScript active-navigation rule based on each page's `data-page`. Only the active navigation item changes between pages. Page-specific CSS files are limited to content components, not the sidebar or overall shell.
