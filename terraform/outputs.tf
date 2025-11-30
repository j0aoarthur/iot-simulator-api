output "vm_ip" { value = module.compute.vm_public_ip }
output "repo_url" { value = module.artifact_registry.repo_url }
output "gh_sa_email" { value = module.iam.gh_sa_email }