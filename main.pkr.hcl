variable "gcp_project_id" {
  type    = string
  default = "csye6225-414320"
}

variable "gcp_source_image" {
  type    = string
  default = "centos-stream-8"
}

variable "gcp_zone" {
  type    = string
  default = "us-east1-c"
}

variable "gcp_ssh_username" {
  type    = string
  default = "centos"
}


variable "network" {
  type    = string
  default = "default"
}

packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = ">=1.1"
    }
  }
}

source "googlecompute" "custom-app-image" {
  project_id          = var.gcp_project_id
  source_image_family = var.gcp_source_image
  image_name          = "custom-app-image1-${formatdate("YYYYMMDDHHMM", timestamp())}"
  zone                = var.gcp_zone
  network             = var.network
  ssh_username        = var.gcp_ssh_username
  image_family        = "custom-app-image-family"
  image_description   = "creating Custom Image with SpringBoot Dependencies and Database."
  image_labels        = { created-by = "packer" }

}

build {
  sources = ["source.googlecompute.custom-app-image"]

  provisioner "shell" {
    script = "JavaInstallation.sh"
  }

  provisioner "shell" {
    script = "MySqlInstallation.sh"
  }
  provisioner "shell" {
    script = "Installation.sh"
  }

  provisioner "file" {
    source      = "target/Cloud-Web-App-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/"
  }


  provisioner "file" {
    source      = "springboot.service"
    destination = "/tmp/"
  }

  // // post-processor "googlecompute" {
  // //   project_id       = var.project_id
  // //   image_name          = "custom-app-image1-${formatdate("YYYYMMDDHHMM", timestamp())}"
  // //   image_family        = "custom-app-image-family"
  // //   zone             = var.zone
  // //   image_visibility = "private"
  // // }



  # Uncomment the following provisioner blocks if needed
  # provisioner "file" {
  #   source      = "webapp-0.0.1-SNAPSHOT.jar"
  #   destination = "webapp-0.0.1-SNAPSHOT.jar"
  # }

  # provisioner "file" {
  #   source      = "webservice.service"
  #   destination = "/tmp/"
  # }

  # Add other provisioning steps as needed

  # For example, uncomment and adjust the following block if you need to run additional commands on the created image
  # provisioner "shell" {
  #   inline = [
  #     "sudo chmod 770 /home/your-ssh-username/webapp-0.0.1-SNAPSHOT.jar",
  #     "sudo cp /tmp/webservice.service /etc/systemd/system",
  #     "sudo chmod 770 /etc/systemd/system/webservice.service",
  #     "sudo systemctl start webservice.service",
  #     "sudo systemctl enable webservice.service",
  #     "sudo systemctl restart webservice.service",
  #     "sudo systemctl status webservice.service",
  #     "echo '****** Copied webservice! *******'"
  #   ]
  # }




}