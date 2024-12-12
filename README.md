# Android Kotlin Project: DreamGuard

## **Project Overview**
DreamGuard is an Android application aimed at improving sleep health for users in Indonesia by utilizing machine learning to detect sleep disorders such as insomnia and sleep apnea. The app serves as a mobile sleep companion, offering features like sleep scheduling, predictions, and tracking statistics.

---

## **Features**
### Core Features
1. **User Authentication**
   - Login, Register, and Forgot Password using Firebase Authentication.
2. **Sleep Predictions**
   - AI-based prediction system to analyze sleep patterns and provide personalized recommendations.
3. **Sleep Scheduler**
   - Allows users to set sleep and wake-up schedules with reminders.
4. **User Profile Management**
   - Update profile details including name, email, age, gender, occupation, and profile picture.
5. **Prediction History**
   - Track all past predictions and insights.
6. **Statistics Dashboard**
   - Overview of sleep quality, activity levels, stress levels, and goal progress.

## **Tech Stack**
### Languages and Frameworks
- Kotlin (MVVM Architecture)
- Jetpack Compose
- Material Design 3
- Retrofit for API calls
- Room for local database
- Glide for image loading
- DataStore and preferences

### APIs and Services
- Firebase Authentication
- Custom REST APIs (for predictions, user data, and statistics)

## **Installation and Setup**

### Prerequisites
1. **Android Studio** (Iguana or latest stable version)
2. **Java Development Kit** (JDK 11 or higher)
3. Internet Connection (for Firebase and API calls)

### Steps to Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/username/dreamguard.git
   ```
2. Open the project in Android Studio.
4. Sync Gradle and build the project.
5. Run the app on an emulator or connected device.
