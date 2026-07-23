# 🧘‍♂️ Stillpoint

**Stillpoint** is a modern, minimalist Android application designed to help users calmly manage their digital content backlog. It turns daily link clutter—articles, blogs, news, and YouTube videos—into a focused, distraction-free reading queue and calm library.

Built with **100% Kotlin** and modern Android development practices (Jetpack Compose, MVVM, Room, Hilt, Readability, and Text-to-Speech).

---

## ✨ Features

### 📥 Universal Content Capture
- **Native Share Sheet Integration**: Save links from any app (browsers, social media, RSS readers) directly to your queue.
- **Manual Link Entry**: Easily add web links manually via a floating action button with instant URL validation.
- **Automatic Metadata Scraping**: Intelligently extracts page titles, descriptions, high-resolution preview images, host domains, and refined reading time estimates.

### 📖 Distraction-Free Reader Mode
- **Readability Engine**: Strips away advertisements, popups, sidebars, navigation headers, and clutter using the Readability algorithm.
- **Offline Article Caching**: Full article text and structure are automatically cached locally in Room database for offline reading.
- **Customizable Typography & Themes**: Adjust font size (12pt–32pt), typography (Sans-Serif, Serif, Monospace), and reader themes (Light, Dark, Sepia, System).
- **Rich HTML Rendering**: Native Jetpack Compose blocks for headings, paragraphs, formatted text, blockquotes, ordered/unordered lists, and inline images.

### 🎧 Text-to-Speech (TTS) Reader
- **Listen On-the-Go**: Integrated Android Text-to-Speech engine allows you to listen to saved articles read aloud.
- **Playback Controls & Chunking**: Sentence-level chunking for natural pauses, seamless playback controls, and automatic document language detection.

### 🎥 YouTube & Video Integration
- **YouTube Metadata Extraction**: Automatically detects YouTube links (standard, shorts, embeds) and fetches video titles, channel names, high-res thumbnails, and video durations.
- **One-Tap Streaming**: Direct launch into your external browser or YouTube app.

### ⏱️ Time-Based Filtering & Queue Management
- **Time Filters**: Filter your queue instantly by available reading/watching time (*~5 min*, *~15 min*, *1 hr+*).
- **Intuitive Gestures**: 
  - **Swipe Right** → Archive item
  - **Swipe Left** → Delete item (with confirmation)
- **Multi-Select & Bulk Actions**: Long-press to enter selection mode and perform bulk archiving or deletion.
- **Dedicated Archive Screen**: Search, organize, restore, or clear read content in a separate archived view.

### ⚙️ Personalization & Settings
- **Custom User Greeting**: Personalize the welcome header with your name.
- **App Theme Customization**: Support for Light Mode, Dark Mode, or System Default with persistent DataStore storage.
- **Default Reader Preferences**: Configure your default reader font styles and themes globally.

---

## 🛠️ Tech Stack & Architecture

Stillpoint is built using modern Android architecture guidelines for performance, scalability, and maintainability.

| Category | Technology |
| :--- | :--- |
| **Language** | 100% Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 (Expressive API) |
| **Architecture** | MVVM (Model-View-ViewModel) + Clean Architecture |
| **Dependency Injection** | Dagger Hilt |
| **Database & Persistence** | Room Database (with offline caching) + DataStore Preferences |
| **Asynchronous Programming** | Kotlin Coroutines & Flow |
| **Article Extraction & Parsing** | Readability4J + Jsoup |
| **Networking & APIs** | Ktor Client + YouTube Data API |
| **Image Loading** | Coil 3 |
| **Text-to-Speech** | Android Native TextToSpeech API |
| **Navigation** | Jetpack Navigation Compose with Animated Transitions |

---

## 📱 Installation

### Option 1: Download Pre-compiled APK (Recommended)
You can directly download and install the latest compiled `.apk` binary on your Android device:
1. Go to the [**GitHub Releases**](../../releases) page of this repository.
2. Download the latest `stillpoint-vX.Y.apk` asset.
3. Open the downloaded file on your Android device (ensure *"Install from unknown sources"* is enabled for your browser/file manager if prompted).

### Option 2: Build & Run from Source
To build Stillpoint locally using Android Studio:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/Stillpoint.git
   ```
2. **Open in Android Studio**:
   - Open Android Studio (Ladybug / Jellyfish or newer recommended).
   - Select **File → Open** and choose the `Stillpoint` folder.
3. **Sync Gradle**:
   - Let Gradle sync dependencies automatically.
4. **Run on Device / Emulator**:
   - Select an emulator or connected physical Android device (Android 12 / API 31 or higher).
   - Click **Run ▶️** (`Shift + F10`).

---

## 🧘 Summary

> *Stillpoint turns digital clutter into mindful focus — one article at a time.*