# webapp
CSYE6225-Cloud Computing

Web application built with Spring Boot

Technologies Used : Java Springboot, MySQL,Hibernate

### Prerequisites:
1. Java Development Kit (JDK) should be installed on your local machine.
2. Maven should be installed on your local machine.

### Build and Deploy Instructions:
1. **Clone the Repository**:Open Git Bash and type the following command:
git clone git@github.com:CSYE6225VaishnaviMantri/Webapp-Test.git

2. **Navigate to Project Directory**:Use the cd command to go to your project directory.

1. **Build the Project**:Build the clean Maven project using the following command:mvn clean install

1. **Start the Spring Boot Application**:Start your Spring Boot application using the following command:java -jar target/your-application-name.jar

### **View the Application**:
Open your web browser and navigate to [http://localhost:8080](http://localhost:8080) to view the application.


### **Assignment 1**:
Created an endpoint called user on port 8080 Endpoint - 'http://localhost:8080/healthz' and checked database connectivity.


### **Assignment 2**:

Create APIs as for performing operations such as Create,Update and Get user details.
Created a organization called 'CSYE6225VaishnaviMantri' and made a repo called webapp Forked the webapp from organization into my personal workspace Created a branch called 'assignment2' and pushed my Java SpringBoot application Created an endpoint called user on port 8080 Endpoint - 'http://localhost:8080/v1/user'

### **Assignment 3**:

It contains integration tests for the /v1/user endpoint of the Spring Boot web application. The tests are implemented using GitHub Actions, ensuring that they can be executed as part of pull requests, and their results can be added to the status check.

Tests
Test 1 - Create an Account
Creates a new user account using the /v1/user endpoint.
Executes a GET call to validate that the created account exists.

Test 2 - Update an Account
Updates an existing user account using the /v1/user endpoint.
Executes a GET call to validate that the updated account information reflects the changes.

### **Assignment 4**:

Custom Image Creation using Packer

This repository contains Packer templates to create custom images for running a specific web application on CentOS Stream 8 in the Google Cloud Platform (GCP) environment.
Requirements:
