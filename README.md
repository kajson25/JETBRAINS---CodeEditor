# 🖥️ Kotlin/Swift Script Editor

A minimal yet powerful desktop editor for executing Kotlin and Swift scripts — designed with productivity in mind. Built using Jetpack Compose for Desktop.

---

## 🎥 Demo Video

Check out the full walkthrough of the app:

▶️ [Watch the Demo on Google Drive](https://drive.google.com/file/d/1-Wl5PAibp0LywCrDGsXTCzK_9dCXG-iq/view?usp=drive_link)


## ✨ Features

- ✅ Script execution (`kotlinc -script` for Kotlin, `/usr/bin/env swift` for Swift)
- ✅ Live script output with error streaming
- ✅ Clickable error messages (jump to source line)
- ✅ Auto-indentation (especially after `{`)
- ✅ Bracket matching highlight (`()`, `{}`, `[]`)
- ✅ Syntax highlighting for Kotlin keywords
- ✅ Search bar (`Ctrl+F`) with match navigation
- ✅ Undo/Redo support (`Ctrl+Z`, `Ctrl+Shift+Z`)
- ✅ Light/Dark mode toggle
- ✅ Script Management Sidebar:
  - Load `.kts` / `.swift` files
  - Save current script as a named file
- ✅ Language switcher (Kotlin/Swift)

---

## 🚀 How to Run

### 🔧 Prerequisites

- **Java 17+** (for Compose Desktop)
- **Gradle** (comes with project)
- **Kotlin Compiler**
  - Download from [Kotlin Releases](https://github.com/JetBrains/kotlin/releases)
  - Extract and **bundle into your project**, for example in:
    ```
    kotlin-compiler-2.1.20/kotlinc/bin/kotlinc.bat
    ```
- **Swift (macOS only)**
  - No setup needed — uses native `/usr/bin/env swift`
  - ⚠️ Swift execution not available on Windows

### 🏃 Build & Run

1. Clone the repo
2. Run the app using Gradle:

```bash
./gradlew run
