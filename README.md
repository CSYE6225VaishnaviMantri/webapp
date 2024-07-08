# Cloud-Native Springboot API on Google Cloud Platform using IaC
This project focuses on creating a user management system with a strong backend emphasis, designed for scalability and efficiency using cloud technology. It supports CRUD (Create, Read, Update) operations, ensuring a dynamic and user-friendly experience. By integrating with Google Cloud Platform (GCP), the system offers secure account verification and sends automated email notifications to inform users about their account status.

## About this repository

This repository provides the code for developing a cloud-native web application. It includes steps for creating a machine image, setting up the Google Cloud Ops Agent, and configuring the web application to automatically start using Systemd on a VM instance. Additionally, it covers the implementation of GitHub Actions workflows for integration testing and continuous integration/continuous deployment (CI/CD) in a production environment.

## Tech Stack

| **Category**                 | **Technology/Tool**                                     |
|------------------------------|---------------------------------------------------------|
| **Programming Language**     | Java (Springboot)                                       |
| **Database**                 | MySQL                                              |
| **Cloud Services**           | GCP (Compute Engine, SQL, VPC network, IAM & Admin, Network Services,Cloud Functions,Cloud Storage) |
| **Infrastructure as Code**   | Terraform                                                  |
| **Image Creation**           | Packer (Custom Machine Images)                                     |
| **Version Control**          | Git                                                     |
| **CI/CD**                    | GitHub Actions                                          |
| **Additional Tools**         | Mailgun                     |

## Setting up Application,Infrastructure as Code, and serverless repositiories
1. Clone webapp repository (assuming that it is set up as guided in its [documentation](./webapp.md))
2. Clone tf-gcp-infra repository and follow documentation in [terraform.md](https://github.com/CSYE6225VaishnaviMantri/tf-gcp-infra.git)
3. Clone serverless respository and follow instructions in its [serverless.md](https://github.com/CSYE6225VaishnaviMantri/serverless.git)

## How do these repositories work together:
> Clone all three repositories. You just need to have prerequisites for terraform on your local.

1. Set up everything as explained in [terraform.md](https://github.com/CSYE6225VaishnaviMantri/tf-gcp-infra.git) locally
2. You need to have following things on your system:
     - Terraform
     - Google Cloud CLI
3. Go to tf-gcp-infra repository folder that you have cloned in the local and copy paste the zip of the serverless repository and rename it as function-source.zip.

4. Run the `terraform apply` command from your terraform code folder and it should build your infrastructure.

5. Once the infrastructure is up, if you now push any changes to webapp repository and merge the pull request, GitHub Worflow Action will build a new machine Image for you.
