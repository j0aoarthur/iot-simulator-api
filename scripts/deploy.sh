#!/bin/bash
set -e

# Arguments passed from GitHub Actions
IMAGE_TAG=$1
REPO_URL=$2

# Construct the full image URL
export BACKEND_IMAGE="$REPO_URL/iot-simulator:$IMAGE_TAG"

echo "🚀 Starting Deployment on VM..."
echo "🎯 Target Image: $BACKEND_IMAGE"

# 1. Authenticate Docker with GCP
# Assumes the VM Service Account has permissions (set up in Terraform)
gcloud auth configure-docker $(echo $REPO_URL | cut -d/ -f1) --quiet

# 2. Pull the specific backend image
echo "📥 Pulling backend image..."
docker pull $BACKEND_IMAGE

# 3. Pull infra images (Prometheus/Grafana) to ensure they are fresh
echo "📥 Pulling infrastructure images..."
docker compose -f docker-compose.prod.yml pull prometheus grafana

# 4. Deploy Stack
# -d: Detached mode
# --remove-orphans: Cleans up old containers if configs changed
echo "🔥 updating stack..."
docker compose -f docker-compose.prod.yml up -d --remove-orphans

# 5. Cleanup unused images (Save disk space on VM)
echo "🧹 Cleaning up old images..."
docker image prune -f || true

# 6. Health Check
echo "🏥 Waiting for service to stabilize..."
sleep 15
if curl --silent --fail http://localhost:8080/actuator/health; then
    echo "✅ Application is HEALTHY"
    echo "📊 Prometheus running at port 9090"
    echo "📈 Grafana running at port 3000"
else
    echo "❌ Deployment Failed: Application check failed."
    exit 1
fi