## Java Based Web Application Integrated WITH MySQL Database

## Git Forking Workflow
> [!WARNING]
> Ensure you have both the organization repository and your forked repository set up; otherwise, the GitHub workflow will not function properly.

To begin, create an organization on GitHub and establish a repository within that organization. Next, fork the repository into your personal GitHub account. Follow these steps to configure your backend:

Follow below steps to set it up in your backend:

     1. git clone <clone_link_to_your_forked_repo>
     2. cd <repo_name>
     3. git remote -v -> This will show you the link of your repo as origin
     4. git remote add upstream <clone_link_to_your_organization_repo>

Now each time you are making changes in your repository, you can create a merge request to your organization repo from a different branch:

    1. git checkout -b "BRANCH_NAME"
    2. Make all the changes needed
    3. git add .
    4. git commit -m "COMMIT_MESSAGE"
    5. git push origin <BRANCH_NAME>


## GitHub Setup for webapp repository:
Add below to your GitHub Environment secrets:

- Repository Secrets:
  1. DATABASE_PASSWORD
  2. DATABASE_URL
  3. DATABASE_USERNAME
  4. DEVICE_NAME
  5. GCP_SERVICE_ACCOUNT_CREDENTIALS
  6. INSTANCE_GROUP_NAME
  7. INSTANCE_TEMPLATE_NAME
  8. IT_DISK_AUTO_DELETE
  9. IT_DISK_BOOT
  10. IT_TEMPLATE_DESCRIPTION
  11. KMS_KEY_SELF_LINK
  12. MAINTENANCE_POLICY
  13. NETWORK_TIER
  14. PACKER_BUILD_IMAGE_ID
  15. PROJECT_ID
  16. PROVISIONING_MODEL
  17. PUBSUB_PROJECT_ID
  18. PUBSUB_TOPIC_ID
  19. REGION
  20. SCOPES
  21. SERVICE_ACCOUNT
  22. SPRING_JPA_HIBERNATE_DDL_AUTO
  23. STARTUP_SCRIPT
  24. SUBNET
  25. VIRTUAL_MACHINE_DISK_SIZE_GB
  26. VIRTUAL_MACHINE_DISK_TYPE
  27. VIRTUAL_MACHINE_TYPE
  28. VM_TAG

- Add branch protection rules on your organization WEBAPP repository and check `Require status checks to pass before merging` and `Require branches to be up to date before merging` options and select three workflows from the dropdown:

    1. Integration Tests Workflow
    2. Building-Jar
    3. Packer-Fmt-and-Validate

## Application Endpoints:
- The application supports below endpoints:

   - To check app health(Get method):       https://<YOUR_DNS>/healthz
   - To create the user(Post method):       https://<YOUR_DNS>/v5/user/self
   - To get user data (Get method):         https://<YOUR_DNS>/v5/user/self
   - To update user data (Put method):      https://<YOUR_DNS>/v5/user/self
   - To verify user account(Get method):    https://<YOUR_DNS>/verify-email?token=<UUID>


## Application Testing:

- Integration Tests for testing the application.
    - Test 1 - Create an Account
        Creates a new user account using the /v1/user endpoint.
        Executes a GET call to validate that the created account exists.

    - Test 2 - Update an Account
        Updates an existing user account using the /v1/user endpoint.
        Executes a GET call to validate that the updated account information reflects the changes.

##  Builiding Machine Image using Packer.

- The main.pkr.hcl file consists of the dependencies and database setup needed to run your application all this setup is done using commands and  it runs web application on CentOS Stream 8 in the Google Cloud Platform (GCP) environment.
- The image also contains installation of OPS Agent for logging and metrics on the console.
- Post building the image the image is saved on a file named as manifest.json.


## For Running the Application Locally.
1. Java Development Kit (JDK) should be installed on your local machine.
2. Maven should be installed on your local machine.
3. MySQL database should be installed on your machine.
4. Build the clean Maven project using the following command: mvn clean install
5. Start your Spring Boot application using the following command:java -jar target/your-application-name.jar
6. Now open a Postman or any other appropriate tools for checking the endpoints or you can open a terminal and check it using curl  command.
