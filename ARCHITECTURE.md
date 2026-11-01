# Architecture and Request Flow

```mermaid
flowchart TD
    A[Client / Postman / Browser] --> B[Controller Layer]
    B --> C[Service Layer]
    C --> D[Repository Layer]
    D --> E[(H2 Database)]
    C --> F[Business Rules and Validation]
    F --> C
```

## Layers
- **Presentation:** `BookController`, `MemberController`, `LoanController` receive HTTP requests and return JSON.
- **Business:** `BookService`, `MemberService`, `LoanService` enforce application rules.
- **Data access:** `BookRepository`, `MemberRepository`, `LoanRepository` use Spring Data JPA.
- **Data:** H2 in-memory database stores books, members, and loans.

## Domain relationships
- A `Book` can appear in many `Loan` records over time.
- A `Member` can have many `Loan` records over time.
- Every `Loan` points to one `Book` and one `Member`.
