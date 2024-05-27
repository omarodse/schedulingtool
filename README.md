# Scheduling Desktop Application

## Description
This application is a GUI-based scheduling tool designed for a global consulting organization to manage customer appointments and schedules efficiently across multiple languages and locations. The application integrates with an existing MySQL database and adheres to specific business requirements to ensure functionality across global offices located in Phoenix, White Plains, Montreal, and London.

## Author
- **Name:** Omar Rodriguez
- **Contact:** orodr85@wgu.edu
- **Version:** 1.0
- **Date:** TBD

## System Requirements
- **IDE:** IntelliJ IDEA 2022.2.5
- **JDK Version:** Java SE 17.0.1
- **JavaFX Version:** JavaFX-SDK-17.0.1
- **MySQL Connector Java Version:** mysql-connector-java-8.1.25

## Installation and Setup
1. **Clone the repository:**
   ```bash
   git clone https://github.com/omarodse/schedulingtool/tree/main

# Setup and Execution Guide

## Open the Project in IntelliJ IDEA
1. **Launch IntelliJ IDEA** and select `Open` or `Import Project`.
2. **Navigate** to the directory where you cloned or downloaded the project.
3. **Select the project directory** and click `OK`.

## Ensure JavaFX SDK is Configured Correctly
To configure JavaFX in IntelliJ:
1. Go to `File` > `Project Structure` > `Libraries`.
2. Click the `+` button and select `Java`.
3. **Navigate to your JavaFX SDK installation directory**, select it, and click `OK`.
4. Ensure the library is applied to the correct module.

## Configure MySQL Connector
1. **Download the MySQL Connector/J** (JDBC driver) from the MySQL official website.
2. Place the `.jar` file in the `lib` directory inside your project's root folder.
3. Again, go to `File` > `Project Structure` > `Libraries`.
4. Click the `+` button and select `Java`.
5. **Navigate to the `lib` directory** of your project, select the MySQL Connector JAR file, and click `OK`.
6. Apply the changes to make sure the JDBC driver is included in the project dependencies.

## Database Setup
1. Create a `db.properties` file in the `src` directory of your project.
2. Add the following properties to the file:

   ```properties
   db.url=jdbc:mysql://localhost:3306/client_schedule
   db.user=sqlUser
   db.password=Pass0rd!
   
## Part A3f
The method `getCountOfAppointmentsForNextDay()` which is in the appointmentDAO class 
Counts all appointments scheduled for the next day from the current date, and the data is shown on the 
Main Screen. 

