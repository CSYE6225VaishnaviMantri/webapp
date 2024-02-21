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
    inline = [
      "sudo adduser csye6225 --shell /usr/sbin/nologin",
      "sudo usermod -aG csye6225 csye6225"
    ]
  }

  provisioner "shell" {
    script = "./MySqlInstallation.sh"
  }

  provisioner "shell" {
    script = "./SqlInstallation.sh"
  }

  provisioner "file" {
    source      = "target/Cloud-Web-App-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/"
  }

  provisioner "file" {
    source      = "springboot.service"
    destination = "/tmp/"
  }

  provisioner "shell" {
    inline = [
      "sudo chown csye6225: /tmp/Cloud-Web-App-0.0.1-SNAPSHOT.jar",
      "sudo chown csye6225: /tmp/springboot.service",
      "sudo mv /tmp/springboot.service /etc/systemd/system",
      "sudo systemctl start springboot.service",
      "sudo systemctl enable springboot.service",
      "sudo systemctl restart springboot.service",
      "sudo systemctl status springboot.service",
    ]
  }

}