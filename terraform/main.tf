module "networking" {
  source     = "./modules/networking"
  project_id = var.project_id
  region     = var.region
}

module "artifact_registry" {
  source   = "./modules/artifact_registry"
  region   = var.region
  app_name = var.app_name
}

module "iam" {
  source     = "./modules/iam"
  project_id = var.project_id
}

module "compute" {
  source       = "./modules/compute"
  zone         = var.zone
  app_name     = var.app_name
  network_link = module.networking.network_self_link
  subnet_link  = module.networking.subnetwork_self_link
  vm_sa_email  = module.iam.vm_sa_email
  ssh_user     = var.ssh_user
  ssh_pub_key  = var.ssh_pub_key
}