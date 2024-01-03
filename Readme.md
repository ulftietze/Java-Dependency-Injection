# Java Dependency Injection 

## Overview
This Java project introduces a small but robust Dependency Injection (DI) mechanism,
designed to facilitate object management and dependency resolution in Java 
applications. This DI framework offers a simple yet powerful approach to manage
class dependencies, object creation, and lifecycle, enhancing modularity and 
testability in your Java projects.

## Features
- **Lazy Loading:** Objects are created on-demand, optimizing resource usage.
- **Class Mapping:** Easily map interfaces or abstract classes to concrete implementations.
- **Factory Support:** Integrate complex object creation logic with custom factories.
- **Field Injection:** Automate the injection of dependencies into your classes.
- **Custom Object Creation:** Control instance creation with custom logic.

## Getting Started

### Prerequisites
- Java Development Kit (JDK)
- An IDE supporting Java (e.g., IntelliJ IDEA, Eclipse).

### Installation
1. Clone the repository:
   ```
   git clone [repository URL]
   ```
2. Import the project into your IDE.

### Basic Usage
1. **Object Retrieval:**
   ```java
   MyClass myObject = ObjectManager.get(MyClass.class);
   ```

2. **Class Mapping:**
   ```java
   ObjectManager.set(MyInterface.class, MyImplementation.class);
   ```

3. **Class Mapping:**
   ```java
   ObjectManager.set(MyInterface.class, new MyClass());
   ```

3. **Object Creation with Arguments:**
   ```java
   MyClass myObject = ObjectManager.create(MyClass.class, arg1, arg2);
   ```

4. **Using Factories:**
   ```java
   ObjectManager.setFactory(MyClass.class, new MyFactory());
   ```

## Example
Here's a simple example to demonstrate the usage of the DI mechanism:

1. Define an interface and its implementation:
   ```java
   public interface Logger {
       void log(String message);
   }

   public class ConsoleLogger implements Logger {
       @Override
       public void log(String message) {
           System.out.println(message);
       }
   }
   ```

2. Configure the DI system:
   ```java
   ObjectManager.set(Logger.class, ConsoleLogger.class);
   ```

3. Use the interface in your application:
   ```java
   public class MyApp {
       @Inject
       private Logger logger;

       public void run() {
           logger.log("Application is running");
       }
   }
   ```

4. Retrieve and use the `MyApp` instance:
   ```java
   MyApp app = ObjectManager.get(MyApp.class);
   app.run();
   ```

> You can find another working example in src/app/Main.java and in the `model`-package.

## License
This project is licensed under the [MIT License](LICENSE).
