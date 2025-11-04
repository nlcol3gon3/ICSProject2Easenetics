Easenetics - Digital Literacy for Older Adults
<div align="center">
https://img.shields.io/badge/Easenetics-Digital%2520Literacy-brightgreen
https://img.shields.io/badge/Kotlin-1.9.0-blue
https://img.shields.io/badge/Android-13%252B-green
https://img.shields.io/badge/Firebase-Auth%2520%2526%2520Firestore-orange

Empowering older adults with accessible digital skills through an intuitive, voice-enabled Android application

Features • Screenshots • Installation • Architecture • Contributing

</div>
Overview
Easenetics is a comprehensive digital literacy application specifically designed for older adults, providing step-by-step lessons, interactive quizzes, AI-powered assistance, and voice-enabled features to help users build confidence with modern technology in a safe, accessible environment.

🎯 Mission
To bridge the digital divide by making technology education accessible, engaging, and empowering for older adults through thoughtful design and innovative features.

Features
Learning Module
Interactive Lessons: 5 comprehensive digital literacy lessons

Smartphone Basics

Internet Safety

Social Media

Online Safety

Video Calls

Embedded Quizzes: Multiple-choice questions with instant feedback

Progress Tracking: Visual progress indicators and completion tracking

Rich Content: Text, descriptions, and structured learning paths

AI Assistant
Smart Chatbot: Context-aware digital literacy support

Lesson Recommendations: AI suggests relevant content based on conversations

Quick Questions: Pre-built common queries for instant help

Conversation History: Persistent chat memory and management

Progress & Gamification
Learning Analytics: Comprehensive statistics and metrics

Achievement System: 5 unlockable achievements with progress tracking

Weekly Progress: Historical learning trends and patterns

Motivational Messages: Contextual encouragement based on performance

Skill Mastery: Tracked competencies and learning milestones

Voice & Accessibility
Text-to-Speech: Full lesson narration and content reading

Voice Input: Speak to chatbot instead of typing

Accessibility First: Large text, high contrast, simple navigation

Graceful Degradation: Works on devices without microphones

Security & User Management
Firebase Authentication: Secure user registration and login

Multi-Factor Authentication: MFA settings and security options

Profile Management: Personal information and preferences

Session Management: Persistent user sessions

User Experience
Material Design 3: Modern Android design language

Senior-Friendly UI: Large touch targets, clear typography

Intuitive Navigation: Simple, consistent navigation patterns

Responsive Design: Adaptive to different screen sizes


Technical Architecture
System Design
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Domain        │    │     Data        │
│   Layer         │    │    Layer         │    │     Layer       │
├─────────────────┤    ├──────────────────┤    ├─────────────────┤
│ • Composable    │    │ • ViewModels     │    │ • Repositories  │
│ • Screens       │    │ • Use Cases      │    │ • Data Models   │
│ • Navigation    │    │ • Services       │    │ • Firebase      │
└─────────────────┘    └──────────────────┘    └─────────────────┘

Tech Stack
Language: Kotlin

UI Framework: Jetpack Compose

Architecture: MVVM (Model-View-ViewModel)

Database: Firebase Firestore

Authentication: Firebase Auth

Async Programming: Kotlin Coroutines & Flow

Dependency Injection: Manual (Ready for Hilt)

Voice Features: Android TextToSpeech & SpeechRecognizer

Key Components
Authentication System: Secure user management with MFA

Lesson Engine: Structured content delivery with progress tracking

AI Chatbot: Intelligent conversation with lesson recommendations

Voice Service: Text-to-speech and speech recognition

Analytics Engine: Progress tracking and achievement system

Navigation System: Compose-based screen management

Installation
Prerequisites
Android Studio Hedgehog or later

Android SDK 33 (API Level 33)

Java 17 or Kotlin 1.9.0

Firebase Project (for authentication and database)

Setup Instructions
Clone the Repository

bash
git clone https://github.com/nlcol3gon3/ICSProject2Easenetics.git
cd ICSProject2Easenetics
Configure Firebase

Create a new Firebase project

Enable Authentication (Email/Password)

Enable Firestore Database

Download google-services.json and place in app/ directory

Build and Run

bash
./gradlew assembleDebug
Or use Android Studio to build and install on device/emulator

Configuration
Minimum SDK: 21 (Android 5.0)

Target SDK: 33 (Android 13)

Compile SDK: 33

Usage Guide
For End Users
First Time Setup

Complete onboarding introduction

Create account or login

Explore dashboard features

Learning Journey

Select a lesson from dashboard

Read content or use voice narration

Complete embedded quiz

Track progress in achievements

Getting Help

Use chatbot for instant assistance

Try voice input for hands-free interaction

Access suggested lessons from chatbot

Tracking Progress

View statistics in progress screen

Unlock achievements

Monitor weekly learning trends

For Developers
kotlin
// Example: Adding a new lesson
val newLesson = Lesson(
    lessonId = "lesson_email_basics",
    title = "Email Basics",
    description = "Learn to send and receive emails",
    content = "Full lesson content...",
    duration = 20,
    difficulty = DifficultyLevel.BEGINNER,
    category = LessonCategory.COMMUNICATION,
    hasQuiz = true,
    quizQuestions = listOf(quiz1, quiz2, quiz3)
)
