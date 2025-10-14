# 🧘‍♂️ Stillpoint

**Stillpoint** is a modern, minimalist Android application designed to help users calmly manage their digital content backlog.  
It’s a *“read-it-later”* app built with the core philosophy of turning content chaos into a focused, organized library.  
Built with a **100% Kotlin**, modern Android tech stack.

---

## ✨ Features
- **MVP Complete** — The app is feature-complete with a strong foundation for future development.
- **Universal Save via Share Sheet**  
  Save links from any app (browser, YouTube, or social media) using the native Android Share Sheet.
- **Manual Content Addition**  
  Add URLs manually through a prominent Floating Action Button for quick entry.
- **Automatic Metadata Scraping**  
  Automatically fetches title, description, primary image, and estimated reading time for articles using *Jsoup*.
- **Time-Based Filtering**  
  Instantly filter your reading queue by available time (e.g., *~5 min*, *~15 min*, *1 hr+*).
- **Intuitive Gesture Management**  
  - **Swipe Right** → Archive (mark as “done”)  
  - **Swipe Left** → Delete (permanently remove)
- **Dedicated Archive Screen**  
  View all consumed and archived items separately, keeping your main queue focused.
- **In-App Reader**  
  Tap an item to open it in a minimalist WebView for seamless reading without leaving the app.

---

## Tech Stack & Architecture

Stillpoint follows **modern Android best practices** with a clean, scalable architecture.

### Architecture
- **Pattern:** Clean **MVVM (Model–View–ViewModel)** for separation of concerns.  
- **Dependency Injection:** *Hilt* for managing and decoupling dependencies.  
- **Asynchronous Programming:** *Kotlin Coroutines & Flows* for smooth, non-blocking UI.  
- **Database:** *Room* for local persistence with safe migration (v1 → v2).  
- **Navigation:** *Jetpack Navigation for Compose* with type-safe, serializable routes.

### Networking & Scraping
- **HTML Parsing:** *Jsoup* for fetching and extracting metadata.  
- **Network Security:** Configured to safely handle HTTP cleartext traffic.  

### Image Loading
- **Coil 3** for efficient image loading, caching, and placeholder/error handling.

### UI
- **Jetpack Compose** for declarative, reactive, and modern UI development.

---

## Setup & Installation
To build and run the project locally:

### 1. Clone the repository
```bash
git clone <your-repo-url>
```

### 2. Open in Android Studio
Launch the latest stable version of Android Studio.

Go to File → Open and select the cloned repository folder.

### 3. Sync Gradle

Android Studio will automatically sync dependencies.

### 4. Run the App
Select an emulator or connect a physical device.

Click Run ▶️ to build and launch.

### 📈 Project Status

Status: MVP Complete

- The core user journey — saving, filtering, viewing, and managing content — is fully implemented with a polished UI.
- Future development may include:
    1. Direct API integrations
    2. Tagging and categorization
    3. Enhanced reader modes

***Stillpoint turns digital clutter into mindful focus 🧘 — one article at a time.***