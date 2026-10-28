# Hermes Delivery Platform (DPL) - Modern End-to-End Test Automation

A modern, API-first test automation framework built with **Karate (v1.4.1)** and **Java**, designed to validate end-to-end integration workflows (S3, FTP, TIBCO EMS asynchronous events, and DB license management) for the Hermes delivery platform.

This repository represents the modern migration of legacy Java/Cucumber integration tests into a streamlined, high-performance Karate framework.

---

## 🚀 Tech Stack & Core Tools

*   **Test Framework:** Karate 1.4.1 (BDD / API Testing)
*   **Build Tool:** Apache Maven
*   **Language:** Java 11 / 17
*   **Asynchronous Messaging:** TIBCO EMS (Enterprise Message Service)
*   **Cloud Storage:** AWS S3 (via custom Java helpers)
*   **File Transfer:** Apache Commons Net (FTP / SFTP integration)
*   **Test Runner:** JUnit 5 Platform Runner

---

## 📁 Project Structure

```text
src/test/
├── java/
│   └── com/wiley/hermes/
│       ├── helpers/          # Java utility bridges (S3, FTP, TIBCO, Password Encryption)
│       └── runners/          # JUnit test runners (Parallel / Tag-based execution)
└── resources/
    ├── data/
    │   └── templates/        # Reusable JSON payload templates (S3 & FTP licenses)
    ├── features/
    │   ├── helpers/          # Reusable Karate feature components (Auth, Create/Delete License)
    │   └── vch/              # Verified VCH publisher integration test features
    └── karate-config.js      # Global environment configuration (qa, local, dev)