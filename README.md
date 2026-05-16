# Kafka Demo - Spring Boot + Kafka + Avro + Kubernetes

## Overview

This project demonstrates a production-style event-driven microservice architecture using:

* Spring Boot
* Apache Kafka
* Avro Schema
* Confluent Schema Registry
* Docker
* Kubernetes (Kind + Rancher Desktop)

The application publishes and consumes `OrderEvent` messages using Kafka topics with schema evolution, retry, DLQ, consumer groups, and Kubernetes deployment.

---

# Architecture

```text
Client (curl/Postman)
        ↓
Kubernetes Service
        ↓
Spring Boot Pods
        ↓
Kafka Topic (orders)
        ↓
Consumer Group
        ↓
Kafka Consumers
```

---

# Tech Stack

| Technology      | Purpose                |
| --------------- | ---------------------- |
| Spring Boot     | Microservice framework |
| Apache Kafka    | Event streaming        |
| Avro            | Schema serialization   |
| Schema Registry | Schema management      |
| Docker          | Containerization       |
| Kubernetes      | Orchestration          |
| Kafka UI        | Monitoring             |
| SLF4J           | Logging                |

---

# Features Implemented

## Kafka Producer + Consumer

* Producer publishes `OrderEvent`
* Consumer consumes using consumer groups

## Avro + Schema Registry

* Strongly typed event contracts
* Schema evolution support
* Backward compatibility validation

## Retry + DLQ

* Retry with backoff
* Failed messages routed to `orders-dlq`

## Kafka Scaling

* Multiple partitions
* Multiple consumers
* Consumer group rebalancing

## Dockerization

* Spring Boot app containerized
* Docker Hub image publishing

## Kubernetes Deployment

* Kind Kubernetes cluster
* Deployment + Service
* Scaling replicas
* Port forwarding

---

# Sample Event Payload

```json
{
  "orderId": "1001",
  "customerId": "200",
  "status": "CREATED",
  "priority": "High"
}
```

---

# Kafka Topics

| Topic      | Purpose           |
| ---------- | ----------------- |
| orders     | Main order events |
| orders-dlq | Dead Letter Queue |

---

# Local Setup

## Start Kafka

```bash
docker network create kafka-net
```

```bash
docker run -d --name kafka --network kafka-net ^
-p 9092:9092 ^
-p 29092:29092 ^
-p 39092:39092 ^
-e KAFKA_NODE_ID=1 ^
-e KAFKA_PROCESS_ROLES=broker,controller ^
-e KAFKA_LISTENERS=PLAINTEXT://:9092,PLAINTEXT_HOST://:29092,K8S://:39092,CONTROLLER://:9093 ^
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092,K8S://host.docker.internal:39092 ^
-e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT,K8S:PLAINTEXT ^
-e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT ^
-e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER ^
-e KAFKA_CONTROLLER_QUORUM_VOTERS=1@kafka:9093 ^
confluentinc/cp-kafka
```

---

# Start Schema Registry

```bash
docker run -d --name schema-registry --network kafka-net ^
-p 8085:8081 ^
-e SCHEMA_REGISTRY_HOST_NAME=schema-registry ^
-e SCHEMA_REGISTRY_LISTENERS=http://0.0.0.0:8081 ^
-e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS=PLAINTEXT://kafka:9092 ^
confluentinc/cp-schema-registry
```
Validation:

```text
http://localhost:8085/subjects
http://localhost:8085/subjects/orders-value/versions/latest
```

---

# Start Kafka UI

```bash
docker run -d --name kafka-ui --network kafka-net -p 8080:8080 ^
-e KAFKA_CLUSTERS_0_NAME=local ^
-e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka:9092 ^
provectuslabs/kafka-ui
```

Open:

```text
http://localhost:8080
```

---

# Build Application

```bash
mvn clean package
```

---

# Build Docker Image

```bash
docker build -t kafka-demo:latest .
docker build --no-cache -t kafka-demo:latest .
```

---

# Push Image To Docker Hub

```bash
docker tag kafka-demo:latest nabin0508/kafka-demo:1.0
docker push nabin0508/kafka-demo:1.0
```

---

# Kubernetes Deployment

```bash
kubectl apply -f kafka-demo-deployment.yaml
kubectl apply -f kafka-demo-service.yaml
```

---

# Scale Pods

```bash
kubectl scale deployment kafka-demo --replicas=3
```

---

# Port Forward

```bash
kubectl port-forward service/kafka-demo-service 8081:8081
```

---

# Test API

```bash
curl -X POST http://localhost:8081/orders ^
-H "Content-Type: application/json" ^
-d "{\"orderId\":\"1001\",\"customerId\":\"200\",\"status\":\"CREATED\",\"priority\":\"High\"}"
```

---

# Ports Used

| Component           | Internal Port | External Port | Purpose                    |
| ------------------- | ------------- | ------------- | -------------------------- |
| Kafka Broker        | 9092          | 29092         | Local Spring Boot apps     |
| Kafka Broker (K8s)  | 9092          | 39092         | Kubernetes access          |
| Kafka Controller    | 9093          | Internal      | Kafka controller quorum    |
| Schema Registry     | 8081          | 8085          | Schema management          |
| Kafka UI            | 8080          | 8080          | Kafka monitoring           |
| Spring Boot App     | 8081          | 8081 / 30081  | REST API                   |
| Kubernetes NodePort | 8081          | 30081         | External Kubernetes access |

---

# Kafka Listener Configuration

| Listener                   | Used By                    |
| -------------------------- | -------------------------- |
| kafka:9092                 | Docker containers          |
| localhost:29092            | Local IntelliJ/Spring Boot |
| host.docker.internal:39092 | Kubernetes Pods            |

---

# Network Flow

```text
Windows Host Machine
│
├── Kafka Docker Container
│      ├── Internal Docker Port: 9092
│      ├── Exposed Host Port: 39092
│      └── Advertised to Kubernetes as:
│             host.docker.internal:39092
│
├── Schema Registry Docker Container
│      ├── Internal Docker Port: 8081
│      ├── Exposed Host Port: 8085
│      └── Accessed from Kubernetes as:
│             http://host.docker.internal:8085
│
└── Kind Kubernetes Cluster
       │
       └── Spring Boot Pod
              ├── REST API Port: 8081
              ├── Kafka Connection:
              │      host.docker.internal:39092
              │
              └── Schema Registry:
                     http://host.docker.internal:8085
```

---
# Learning Outcomes

* Kafka producer/consumer
* Consumer groups
* Partition scaling
* Avro schema evolution
* Schema Registry
* Retry and DLQ
* Docker networking
* Kubernetes deployment
* Pod scaling
* Kafka rebalancing

---

# Future Enhancements

* Rolling deployments
* ConfigMaps and Secrets
* Prometheus + Grafana
* Helm charts
* CI/CD pipeline
* Kafka Streams
* OpenTelemetry tracing
* Exactly-once semantics
