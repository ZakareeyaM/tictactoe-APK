# Tic Tac Toe (Android)

A simple Tic Tac Toe game for Android, written in Kotlin. You play as **X** against an AI playing **O**.

## Features

- Play against an AI opponent that uses the minimax algorithm
- The AI plays a random move 20% of the time, so it can be beaten
- Score tracking for wins, losses and draws
- New Game button to restart at any time
- No external dependencies and no internet permission needed

## How to play

1. Tap any empty square to place your **X**.
2. The AI responds with an **O**.
3. Get three in a row (horizontally, vertically or diagonally) to win.
4. Tap **New Game** to play again. Your score is kept until you close the app.

## Build

**Requirements:** Android Studio (or the Android SDK) and JDK 17.

**With Android Studio:** open the project folder and press **Run**, or choose **Build > Build APK(s)**.

**With Gradle:**

```
gradle assembleDebug
```

The APK is created at `app/build/outputs/apk/debug/app-debug.apk`.

**With GitHub Actions:** every push to `main` runs the **Build APK** workflow, which builds a debug APK and uploads it as a build artifact. You can also start it manually from the **Actions** tab with **Run workflow**.

## Install on a phone

Copy `app-debug.apk` to your Android device and open it. You may need to allow "Install unknown apps" for your file manager or browser. This is a debug build, so it is signed with a debug key and is not intended for the Play Store.

## Tech

- Kotlin
- Android SDK (min SDK 24, target SDK 34)
- Gradle (Kotlin DSL)

## Project structure

```
app/src/main/java/com/zak/tictactoe/MainActivity.kt   Game logic and UI
app/src/main/AndroidManifest.xml                       App manifest
.github/workflows/build-apk.yml                        CI workflow that builds the APK
```
