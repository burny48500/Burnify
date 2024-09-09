# Burnify - Java Media Player with YouTube Downloader

https://github.com/user-attachments/assets/4d4374d2-ec47-4dc2-8a3e-2b7917b3d7aa

## Overview

Burnify is a Java-based media player that allows users to play songs, view images, and download songs from YouTube using `yt-dlp`.

## Requirements

- **Java Development Kit (JDK)**: Java 11 or higher.
- **JavaFX**: Required for the GUI.
- **yt-dlp**: Required for downloading songs from YouTube.

### Install Java

1. Download and install Java from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [AdoptOpenJDK](https://adoptopenjdk.net/).

### Install yt-dlp

1. Install `yt-dlp` via pip:
   pip install yt-dlp

2. Verify `yt-dlp` installation:
   yt-dlp --version

## Setup Instructions

1. Clone the Repository

2. Set Up JavaFX:

   - Download JavaFX from [OpenJFX](https://openjfx.io/).

3. Compile the Application:

   javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")

   Replace `/path/to/javafx/lib` with the path to the JavaFX `lib` directory.

4. Run the Application:

   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp out de.tum.in.ase.burnifyinterface.HelloApplication

## Usage Instructions

- **Download Songs**: Enter a YouTube URL and click the search button.
- **Control Playback**: Use play/pause, next, and previous buttons, or click on the image to play and pause.
- **Adjust Volume**: Use the volume slider.
