variable "zone" {}
variable "app_name" {}
variable "network_link" {}
variable "subnet_link" {}
variable "vm_sa_email" {}
variable "ssh_user" {}
variable "ssh_pub_key" {}

resource "google_compute_instance" "vm" {
  name         = "${var.app_name}-vm"
  machine_type = "e2-small"
  zone         = var.zone
  tags         = ["http-server", "https-server"]

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2204-lts"
      size  = 20
    }
  }

  network_interface {
    network    = var.network_link
    subnetwork = var.subnet_link
    access_config {
    }
  }

  service_account {
    email  = var.vm_sa_email
    scopes = ["cloud-platform"]
  }

  metadata = {
    ssh-keys = "${var.ssh_user}:${var.ssh_pub_key}"
  }

  metadata_startup_script = <<-EOF
    #!/bin/bash
    sudo apt-get update
    sudo apt-get install -y ca-certificates curl gnupg
    sudo install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    sudo chmod a+r /etc/apt/keyrings/docker.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    sudo usermod -aG docker ${var.ssh_user}

    # Configure Docker to authenticate with GCP Artifact Registry
    gcloud auth configure-docker ${split("-", var.zone)[0]}-docker.pkg.dev --quiet
  EOF
}

output "vm_public_ip" { value = google_compute_instance.vm.network_interface[0].access_config[0].nat_ip }