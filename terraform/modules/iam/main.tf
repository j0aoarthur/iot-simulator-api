variable "project_id" {}

# 1. Service Account for the VM (to pull images)
resource "google_service_account" "vm_sa" {
  account_id   = "iot-vm-sa"
  display_name = "IoT VM Service Account"
}

resource "google_project_iam_member" "vm_sa_pull" {
  project = var.project_id
  role    = "roles/artifactregistry.reader"
  member  = "serviceAccount:${google_service_account.vm_sa.email}"
}

# 2. Service Account for GitHub Actions (to push images)
resource "google_service_account" "gh_actions_sa" {
  account_id   = "github-actions-sa"
  display_name = "GitHub Actions Service Account"
}

resource "google_project_iam_member" "gh_actions_push" {
  project = var.project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.gh_actions_sa.email}"
}

output "vm_sa_email" { value = google_service_account.vm_sa.email }
output "gh_sa_email" { value = google_service_account.gh_actions_sa.email }