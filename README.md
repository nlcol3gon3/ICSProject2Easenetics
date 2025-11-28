<div align="center">


  # Easenetics
  ### Bridging the Digital Divide for the Aging Population

  **A Native Android Learning Ecosystem Engineered for Digital Inclusion**

  <p>
    <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
    <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white" alt="Jetpack Compose">
    <img src="https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase">
    <img src="https://img.shields.io/badge/AI-Dialogflow%20%2F%20Gemini-FF9800?style=for-the-badge&logo=google-cloud&logoColor=white" alt="Dialogflow">
    <img src="https://img.shields.io/badge/Status-Completed-success?style=for-the-badge" alt="Status">
  </p>

  <p>
    <a href="#-about-the-project">About</a> •
    <a href="#-key-features">Features</a> •
    <a href="#-tech-stack">Tech Stack</a> •
    <a href="#-architecture">Architecture</a> •
    <a href="#-getting-started">Getting Started</a>
  </p>
</div>

---

## About the Project

**Easenetics** is an accessibility-first Android application designed to facilitate autonomous digital skill acquisition for older adults (aged 60+). 

Unlike standard tutorial apps, Easenetics recognizes that the "digital divide" is often a cognitive divide. Grounded in **User-Centered Design (UCD)** principles, the system addresses age-related barriers—such as declining working memory and motor dexterity—by integrating **Generative AI** for real-time mentorship and **Gamified Cognitive Exercises** for mental reinforcement.

The project was developed as a final-year Capstone to demonstrate how **Human-Computer Interaction (HCI)** and **Artificial Intelligence** can be harmonized to create inclusive technology.

---

## Key Features

### 1. Adaptive Learning Modules
* **Structured Pedagogy:** Step-by-step interactive lessons on essential skills (Smartphone usage, Social Media, Online Safety).
* **Offline Capability:** Full functionality in low-connectivity environments.

### 2. Cognitive Gamification
* **Memory Reinforcement:** Integrated games designed to boost mental agility and retention.
* **Cognitive Loading:** Exercises are calibrated to improve the user's ability to recall digital workflows.

### 3. AI-Powered Chatbot
* **Context-Aware Chatbot:** Powered by **Gemini Pro**.
* **Voice-First Interface:** Users can speak natural language queries ("How do I send a photo?") and receive voice-guided answers, bypassing complex menus.

### 4. Accessibility Suite
* **High-Contrast UI:** WCAG-compliant visuals for users with declining visual acuity.
* **Text-to-Speech (TTS):** Full voice narration for all lessons and instructions.
* **Large Typography:** Adjustable text sizes and simplified navigation patterns.

---

## Tech Stack

The system utilizes a modern Android tech stack to ensure performance and scalability.

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | **Kotlin** | 100% Kotlin codebase. |
| **UI Framework** | **Jetpack Compose** | Modern, declarative UI toolkit. |
| **Architecture** | **MVVM** | Model-View-ViewModel with Clean Architecture principles. |
| **Backend** | **Firebase** | Firestore (Database), Auth (Login), Storage (Media). |
| **AI** | **Google AI Studio** | Artificial Intelligence (AI) agent. |
| **Navigation** | **Compose Navigation** | Single-activity architecture. |
| **Asynchronous** | **Coroutines & Flow** | For handling background tasks and data streams. |

---

## Architecture

The application follows the **Model-View-ViewModel (MVVM)** architectural pattern to separate concerns and ensure testability.

* **Data Layer:** Handles data sourcing from Firebase Firestore and local Room database (for offline caching).
* **Domain Layer:** Contains business logic and Use Cases.
* **UI Layer:** Jetpack Compose screens that observe ViewModels.

---

## Getting Started

To run this project locally, follow these steps:

### Prerequisites
* Android Studio Ladybug (or newer).
* JDK 17.

### Installation

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/your-username/easenetics.git](https://github.com/your-username/easenetics.git)
    ```
2.  **Open in Android Studio**
    * Open Android Studio -> File -> Open -> Select the cloned folder.
3.  **Firebase Setup (Crucial Step)**
    * This project uses Firebase. You must provide your own `google-services.json` file.
    * Create a project in the [Firebase Console](https://console.firebase.google.com/).
    * Add an Android App with package name: `com.example.easenetics` (check your manifest).
    * Download `google-services.json` and place it in the `app/` root directory.
4.  **Sync Gradle**
    * Allow Gradle to download dependencies.
5.  **Run the App**
    * Connect a physical device or use an Emulator.

---

## Contribution & Impact

This project addresses the **"Second-Level Digital Divide"**—the gap where users have access to devices but lack the skills to use them.

By empowering older adults to navigate technology independently, this mobile application aims to:
1.  **Reduce Social Isolation**.
2.  **Enhance Autonomy** in healthcare and banking.
3.  **Provide a scalable model** for Gerontechnology applications in Africa.

---


<div align="center">
  Developed with ❤️ by <strong>Nicole Gone</strong><br>
  <em>Bachelor of Science in Informatics and Computer Science, Class of 2026</em><br>
  <strong>Strathmore University</strong>
</div>
