package de.tum.in.ase.burnifyinterface;

import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class YoutubeDownloader {
    private int processFinished;

    public int getProcessFinished() {
        return processFinished;
    }

    public void setProcessFinished(int processFinished) {
        this.processFinished = processFinished;
    }

    public int downloadSong(String songUrl) {
        String[] cmd = {"yt-dlp", "--write-thumbnail", "--convert-thumbnails", "jpg",
                "-x", "--audio-format", "mp3", "-o", "songs/%(title)s.%(ext)s", songUrl};

        try {
            Process process = Runtime.getRuntime().exec(cmd);

            // Capture output and error streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read the output from the command
            String s;
            System.out.println("Standard output:");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read any errors from the attempted command
            System.out.println("Standard error (if any):");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            processFinished = exitCode;
            System.out.println("Process exited with code: " + exitCode);
            return exitCode;

        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public static void iterateSongs() throws IOException {
        File songsDir = new File("songs/");
        for (File file : songsDir.listFiles()) {
            if (file.getName().contains("jpg")) {
                Files.move(Paths.get(file.getPath()), Paths.get("images/"+file.getName()), REPLACE_EXISTING);
            }
        }

    }

}