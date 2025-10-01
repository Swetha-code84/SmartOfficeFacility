# EXERCISE 1

## BEHAVIORAL DESIGN PATTERN

### 1. Mediator Pattern (Advanced Transaction Coordinator)

**1.1) Use Case**
This application simulates an advanced transaction coordination system in which multiple independent services participate in a distributed transaction. The system ensures that all services either commit or roll back together.

**1.2) Design Pattern**
Mediator Pattern is used to centralize the control of complex interactions among services. Instead of services communicating directly, they interact only with the mediator, which enforces synchronization.

**1.3) Relation to Use Case**

* **Mediator:** `AdvancedTransactionCoordinator` acts as the mediator, coordinating all participant services.
* **Colleagues:** Independent transaction services register with the coordinator and follow its commit/rollback instructions.

**1.4) Classes**

* `AdvancedTransactionCoordinator`: Central mediator ensuring atomic commit/rollback.
* `TransactionService` (abstract): Defines the interface for participating services.
* `PaymentService, InventoryService, NotificationService`: Concrete services participating in the transaction.
* `TransactionClient`: Main class initiating transactions and handling results.

---

### 2. Strategy Pattern (Discount Calculator)


**2.1) Use Case**
A shopping cart system calculates discounts based on different strategies, such as Loyalty Tier discounts or Seasonal Sale discounts. The discount algorithm can be switched dynamically.

**2.2) Design Pattern**
Strategy Pattern is applied to encapsulate discount algorithms. The shopping cart executes the selected strategy at runtime, allowing flexible price calculations.

**2.3) Relation to Use Case**

* **Context:** `ShoppingCart` holds items and applies a chosen discount strategy.
* **Strategy Interface:** `DiscountStrategy` defines the calculation contract.
* **Concrete Strategies:** `LoyaltyTierStrategy, SeasonalSaleStrategy, FlashSaleStrategy` implement the strategy differently.

**2.4) Classes**

* `DiscountStrategy` (interface): Declares method for discount calculation.
* `LoyaltyTierStrategy, SeasonalSaleStrategy, FlashSaleStrategy`: Concrete implementations of discount calculation.
* `ShoppingCart`: Context class applying the chosen discount strategy.
* `Main`: Main class to simulate discount calculations.

---

## CREATIONAL DESIGN PATTERNS

### 3. Factory Method Pattern (Interactive Document Creator)

**3.1) Use Case**
The system allows users to create different types of documents, such as Text Documents and Spreadsheet Documents, based on interactive input.

**3.2) Design Pattern**
Factory Method Pattern delegates document creation to specialized subclasses, ensuring the correct document type is instantiated and initialized according to the user’s choice.

**3.3) Relation to Use Case**

* **Creator:** `DocumentApplication` defines the factory method.
* **Concrete Creators:** `TextEditor` and `SpreadsheetApp` override the factory method.
* **Product:** `IDocument` interface represents all document types.

**3.4) Classes**

* `IDocument` (interface): Defines methods for creating and displaying documents.
* `TextDocument, SpreadsheetDocument`: Concrete document classes.
* `DocumentApplication` (abstract): Declares factory method.
* `TextEditor, SpreadsheetApp`: Concrete creators.
* `Main`: Main application class interacting with users.

---

### 4. Singleton Pattern (System Health Monitor)

**4.1) Use Case**
A system health monitoring service runs periodic checks on CPU, memory, and network resources. Only one monitor should exist to ensure consistency and avoid conflicts.

**4.2) Design Pattern**
Singleton Pattern ensures that only a single instance of the health monitor exists. It controls the thread-safe background process for system health checks.

**4.3) Relation to Use Case**

* **Singleton:** `HealthMonitor` manages global access.
* **Client:** Any service requesting health status uses the same instance.

**4.4) Classes**

* `HealthMonitor`: Singleton class performing periodic checks.
* `HealthCheckService`: Uses the monitor to query system status.
* `Main`: Demonstrates running health checks.

---

## STRUCTURAL DESIGN PATTERNS

### 5. Adapter Pattern (Integrating a New HR Payroll System)

**5.1) Use Case**
An organization migrates from a legacy HR system where employee records are stored as raw strings, to a modern payroll system expecting structured data (First Name, Last Name, and numeric Salary).

**5.2) Design Pattern**
Adapter Pattern converts incompatible legacy record formats into the standardized format expected by the payroll system.

**5.3) Relation to Use Case**

* **Adaptee:** `LegacyEmployeeRecord` holds raw string data.
* **Adapter:** `LegacyToStandardAdapter` converts raw data into structured form.
* **Target:** `PayrollSystem` expects standardized records.

**5.4) Classes**

* `LegacyEmployeeRecord`: Provides employee details in raw string format.
* `LegacyToStandardAdapter`: Translates raw string data into the structured `IStandardEmployee` target format.
* `IStandardEmployee` (Interface): Standardized representation with first name, last name, and salary.
* `PayrollSystem`: Consumes standardized employee data.
* `Main`: Demonstrates integration.

---

### 6. Decorator Pattern (Notification Service)

**6.1) Use Case**
A messaging service sends SMS notifications. Over time, cross-cutting features like timestamping, encryption, compression, and audit logging need to be added without modifying the core SMS sender.

**6.2) Design Pattern**
Decorator Pattern is used to wrap the core notifier dynamically with additional functionalities. Features can be stacked or removed at runtime.

**6.3) Relation to Use Case**

* **Component:** `Notifier` defines the base send operation.
* **Concrete Component:** `SMSNotifier` sends plain notifications.
* **Decorators:** `TimestampDecorator, EncryptionDecorator, CompressionDecorator, AuditDecorator` wrap the notifier.

**6.4) Classes**

* `Notifier` (Interface): Declares the core send method.
* `SMSNotifier`: Core notification sender.
* `TimestampDecorator, EncryptionDecorator, CompressionDecorator, AuditDecorator`: Concrete decorator classes that dynamically add cross-cutting features (like logging and security).
* `Main`: Demonstrates combining decorators dynamically based on user selection.

---

# EXERCISE 2

## Smart Office Facility Management System

The Smart Office Facility Management System is a console-based application designed to simulate centralized management of a modern office environment. It enables efficient resource allocation, security, and environmental control through automated processes while strictly adhering to modern design principles.

### Use Case Overview

The system functions as a central hub for managing office resources, room scheduling, and automation. It ensures seamless operations with integrated safety mechanisms, strict role-based access, and persistent storage of all essential records.

### Functionalities

* **User Management:** Register users with unique IDs and passwords, supporting Admin and Manager roles. Each Manager is assigned a unique Manager ID, which is mandatory for accessing role-specific functionalities.
* **Room Configuration:** Configure the initial number of rooms, update their capacities, list available rooms, and add new rooms dynamically as required.
* **Time-Based Scheduling:** Book and cancel rooms based on date, time, and duration while preventing overlapping reservations.
* **Automation & Safety:**

  * Observer-driven automation for controlling lights and AC based on occupancy status.
  * Two-stage auto-release mechanism for unused bookings (2-minute warning, followed by a 5-minute cancellation).
  * Emergency mode that cancels all bookings and enforces safety defaults (Lights ON, AC OFF).
* **Role-Based Access:** Admins manage configuration, while Managers can view detailed usage statistics only with secondary verification via their unique Manager ID.
* **Persistent Storage:** Stores all user data, room configurations, and booking records. Booking time and duration are stored for accurate analysis and auditing.

---

### Design Patterns Used

#### 1. Behavioral Patterns

* **Command Pattern:** Encapsulates user actions (Login, Book, Cancel, Admin Menu) as objects.

  * Classes: `ICommand`, `SmartOfficeApp`, `BookRoomCommand`, `CancelRoomCommand`.
* **Observer Pattern:** Automates environmental controls. A `Room` notifies observers such as `ACSystem` and `LightingSystem` when occupancy changes.

  * Classes: `Room`, `IControlObserver`, `ACSystem`, `LightingSystem`.

#### 2. Creational Patterns

* **Singleton Pattern:** Ensures one global instance of core managers such as `OfficeFacility` and `AutomationScheduler`.

  * Classes: `OfficeFacility`, `AutomationScheduler`.
* **Factory Method Pattern:** Creates room objects with required dependencies like sensors and observers automatically attached.

  * Classes: `IRoomFactory`, `SimpleRoomFactory`, `OfficeFacility`.
* **Builder Pattern:** Constructs complex `User` objects step by step, ensuring mandatory attributes (ID, password) are included, while optional roles such as Admin or Manager can be assigned.

  * Classes: `UserBuilder`, `User`, `MockAuthenticationService`.

#### 3. Structural Patterns

* **Decorator Pattern:** Extends command functionality by incorporating logging and monitoring without altering existing logic.

  * Classes: `ICommand`, `CommandLoggerDecorator`, `SmartOfficeApp`.
* **Adapter Pattern:** Facilitates integration of an external SMS gateway by adapting it to the system’s notification interface.

  * Classes: `INotificationService`, `NotificationServiceAdapter`, `ExternalSMSGateway`.

---

### Classes and Their Responsibilities

* `User.java`: Represents office users, their credentials, and roles.
* `UserBuilder.java`: Provides step-by-step construction of user objects.
* `Room.java`: Defines room attributes including capacity, occupancy, and booking state.

* `OfficeFacility.java`: Central controller managing users, rooms, and booking processes.
* `ACSystem.java / LightingSystem.java`: Observer components that handle automation of devices.
* `CommandLoggerDecorator.java`: Adds logging capabilities to command execution.
* `ManagerViewStatusCommand.java`: Allows managers to view detailed usage statistics after validation through their unique Manager ID.
* `SmartOfficeApp.java`: Entry point and primary orchestrator of the application.

---

### Compiling and Executing

**Compiling (Linux/Terminal)**
Source files are stored within experiment_* directories, and compiled classes are directed into a classes directory.
```bash
find experiment_* -name "*.java" | xargs javac -d classes
```

**Running the Application**

```bash
java -cp classes com.smartoffice.facility.main.SmartOfficeApp
```

---

### Tech Stack

* **Programming Language:** Java (leveraging object-oriented design principles)
* **JDK Version:** JDK 17
* **Build Tools:** Java Compiler (`javac`) and Java Runtime (`java`)
* **Version Control:** Git for repository management and collaboration

---

# Why Linux and IntelliJ Are Chosen

## 1. Why Linux is Chosen Over Windows ?

The decision to use Linux is primarily driven by stability, performance, and environment consistency rather than feature sets.

- **Server Alignment**: Linux powers over 90% of global cloud and enterprise servers, including AWS, Google Cloud, and Azure.  
  **Benefit**: Developing on Linux ensures the deployment environment matches production, preventing environment-specific bugs related to threads, file paths, and system calls.

- **Stability & Uptime**: Linux efficiently handles long-running processes such as daemons and cron jobs with minimal overhead.  
  **Benefit**: Essential for the AutomationScheduler and background monitoring, allowing the system to run 24/7 without reboots or OS interference.

- **Resource Efficiency**: Linux is lightweight and dedicates more system resources to running applications.  
  **Benefit**: Java code and background processes run faster and more stably without the OS consuming excessive CPU or RAM.

## 2. Benefits of IntelliJ Integrated with Linux

IntelliJ IDEA is cross-platform, but when combined with Linux, it provides additional performance and utility advantages:

- **Seamless Terminal Integration**: IntelliJ’s built-in terminal is a full Bash terminal, enabling developers to run complex commands (`javac`, `find`, `git`) directly within the IDE.

- **Faster Tooling**: Tools like the Java compiler and CLI-based tools start faster on Linux, improving the development feedback loop.

- **Project Setup Control**: Linux allows easier configuration of environment variables and custom scripts, ensuring IntelliJ reliably finds external dependencies without relying on Windows registry configurations.

- **System Keyring and Security**: Linux natively manages SSH keys and system-level credentials, often integrating directly with IntelliJ for smooth authentication during Git operations.

 ## 📞 Contact Information

- **Gmail**: [kswetha281@gmail.com](mailto:kswetha281@gmail.com)
- **GitHub**: [Swetha Kannan](https://github.com/Swetha-code84)
