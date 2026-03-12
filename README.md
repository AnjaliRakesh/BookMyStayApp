# Book My Stay App

## Hotel Booking Management System (Console Application)

This project demonstrates the design and implementation of a Hotel Booking Management System using Core Java and fundamental object-oriented principles.  
The system is developed incrementally through multiple use cases, each introducing a new concept that reflects real-world software engineering challenges such as structured application flow, domain modeling, and centralized state management.

---

## Implemented Use Cases

### UC1 — Application Entry & Welcome Message

**Goal:** Establish a clear and predictable starting point for the application.

**Features:**
- Demonstrates Java program entry point using `main()` method
- Displays application name and version
- Produces console output confirming successful startup
- Establishes linear execution flow

---

### UC2 — Basic Room Types & Static Availability

**Goal:** Introduce object-oriented domain modeling using abstraction and inheritance.

**Features:**
- Defines an abstract `Room` class with shared attributes
- Implements concrete room types:
    - Single Room
    - Double Room
    - Suite Room
- Demonstrates inheritance and polymorphism
- Uses simple variables to represent room availability
- Displays room details and availability in the console

---

### UC3 — Centralized Room Inventory Management

**Goal:** Replace scattered availability variables with a centralized data structure.

**Features:**
- Introduces `RoomInventory` component
- Uses `HashMap<String, Integer>` to store availability
- Provides controlled methods to:
    - Retrieve availability
    - Update availability
    - Display current inventory
- Demonstrates single source of truth for system state
- Improves scalability and consistency

---

## Technologies Used

- Java (Core Java)
- Object-Oriented Programming (OOP)
- Collections Framework (HashMap)
- IntelliJ IDEA
- Git & GitHub

---

## How to Run

1. Compile the program:

### UC4 — Room Search & Availability Check

**Goal:** Allow guests to view available room options without modifying system state.

**Features:**
- Retrieves availability from centralized inventory
- Displays only room types with availability greater than zero
- Uses room domain objects for details and pricing
- Implements read-only search logic
- Filters out unavailable room types
- Maintains separation between search and booking operations
### UC5 — Booking Request

**Goal:** Collect booking requests fairly using a queue structure without modifying inventory.

**Features:**
- Accepts booking requests from guests
- Stores requests using a FIFO queue
- Preserves arrival order
- Decouples request intake from allocation
- Ensures fairness during peak demand
- Does not alter room availability
### UC6 — Reservation Confirmation & Room Allocation

**Goal:** Confirm booking requests by assigning rooms safely while maintaining inventory consistency.

**Features:**
- Processes booking requests from the queue in FIFO order
- Checks room availability before allocation
- Generates unique room IDs for each reservation
- Prevents duplicate assignments using a Set
- Maps room types to allocated room IDs
- Updates inventory immediately after allocation
- Ensures system consistency and prevents double-booking
### UC7 — Add-On Service Selection

**Goal:** Support optional services for existing reservations without altering core booking logic.

**Features:**
- Allows multiple services per reservation
- Uses a map from reservation ID to list of services
- Calculates total additional cost
- Maintains separation from booking and inventory logic
- Supports easy addition of new service types
### UC8 — Booking History & Reporting

**Goal:** Track confirmed bookings and generate operational reports.

**Features:**
- Stores confirmed reservations in chronological order
- Maintains booking history using a List
- Supports retrieval for administrative review
- Generates summary reports from stored data
- Separates storage and reporting responsibilities
- Provides audit-ready historical records