# IoT Data Simulator & Backend

This project is a Spring Boot backend that simulates an IoT environment (Smart Energy Meters). It automatically generates sensor readings, detects anomalies, triggers alerts, and exposes metrics to Prometheus and Grafana.

## 🏗 Architecture

```text
[ Simulator/Scheduler ] -> [ DB (H2) ]
          |
          v
[ REST API ] <-----> [ User ]
          |
          v
[ Micrometer ] -> [ Prometheus ] -> [ Grafana ]