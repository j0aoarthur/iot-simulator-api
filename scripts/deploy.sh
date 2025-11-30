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
gcloud auth configure-docker $(echo $REPO_URL | cut -d/ -f1) --quiet

# 2. Pull the specific backend image
echo "📥 Pulling backend image..."
docker pull $BACKEND_IMAGE

# 3. Pull infra images
echo "📥 Pulling infrastructure images..."
docker compose -f docker-compose.prod.yml pull prometheus grafana

# 4. Deploy Stack
echo "🔥 updating stack..."
docker compose -f docker-compose.prod.yml up -d --remove-orphans

# 5. Cleanup unused images
echo "🧹 Cleaning up old images..."
docker image prune -f || true

# 6. Smart Health Check (Retry Loop)
echo "🏥 Waiting for service to stabilize (Max 90s)..."
MAX_RETRIES=18 # 18 * 5s = 90 seconds
COUNT=0

while [ $COUNT -lt $MAX_RETRIES ]; do
    # Tenta conectar no actuator (padrão) OU no endpoint customizado que criamos
    if curl --silent --fail http://localhost:8080/actuator/health | grep "UP" || \
       curl --silent --fail http://localhost:8080/api/v1/health | grep "UP"; then
        echo "✅ Application is HEALTHY!"
        echo "📊 Prometheus running at port 9090"
        echo "📈 Grafana running at port 3000"
        exit 0
    fi

    echo "⏳ App not ready yet... retrying in 5s ($((COUNT+1))/$MAX_RETRIES)"
    sleep 5
    COUNT=$((COUNT+1))
done

echo "❌ Deployment Failed: Timeout reached."
echo "📜 Last 50 lines of logs:"
docker logs --tail 50 iot_backend
exit 1