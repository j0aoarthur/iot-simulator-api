variable "region" {}
variable "app_name" {}

resource "google_artifact_registry_repository" "repo" {
  location      = var.region
  repository_id = "${var.app_name}-repo"
  description   = "Docker repository for IoT Simulator"
  format        = "DOCKER"
}

output "repo_url" {
  value = "${var.region}-docker.pkg.dev/${google_artifact_registry_repository.repo.project}/${google_artifact_registry_repository.repo.repository_id}"
}