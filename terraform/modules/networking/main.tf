variable "project_id" {}
variable "region" {}

resource "google_compute_network" "vpc" {
  name                    = "iot-vpc"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = "iot-subnet"
  ip_cidr_range = "10.0.1.0/24"
  region        = var.region
  network       = google_compute_network.vpc.id
}

resource "google_compute_firewall" "allow_web" {
  name    = "allow-web-traffic"
  network = google_compute_network.vpc.name

  allow {
    protocol = "tcp"
    # Added 3000 (Grafana) and 9090 (Prometheus)
    ports    = ["80", "443", "8080", "3000", "9090"]
  }
  source_ranges = ["0.0.0.0/0"]
}

resource "google_compute_firewall" "allow_ssh" {
  name    = "allow-ssh"
  network = google_compute_network.vpc.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }
  source_ranges = ["0.0.0.0/0"] # In prod, restrict to specific IPs
}

output "network_self_link" { value = google_compute_network.vpc.self_link }
output "subnetwork_self_link" { value = google_compute_subnetwork.subnet.self_link }