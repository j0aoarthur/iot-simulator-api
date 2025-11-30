variable "project_id" { description = "GCP Project ID" }
variable "region" { default = "us-central1" }
variable "zone" { default = "us-central1-a" }
variable "app_name" { default = "iot-simulator" }
variable "ssh_user" { default = "deploy" }
variable "ssh_pub_key" { description = "Public SSH key for the VM" }