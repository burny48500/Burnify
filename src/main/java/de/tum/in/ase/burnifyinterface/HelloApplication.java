package de.tum.in.ase.burnifyinterface;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class HelloApplication extends Application {
    private static VBox musicPlayerVBox;
    private HBox musicPlayerHBox;
    private Button playPauseButton;
    private Button stopButton;
    private Button previousButton;
    private Button nextButton;
    private Slider volumeSlider;
    private TextField searchBar;
    private Button searchBarButton;
    private HBox searchBarHBox;
    private Label searchBarLabel;
    private TextFlow textFlow;
    private ImageView imageView;
    private ScrollPane scrollPane;
    private Media media;
    private MediaPlayer mediaPlayer;
    private AtomicInteger buttonCount = new AtomicInteger();
    private List<File> sortedFileArray;
    private List<String> songList = new ArrayList<>();
    private boolean playingStatus = false;
    private String finalUrl;
    private boolean next;


    private void labelPause(Label label) {
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(2)
        );
        visiblePause.setOnFinished(
                event -> label.setVisible(false)
        );
        visiblePause.play();
    }

    //GitHub trial

    public void showImages() throws FileNotFoundException {
        textFlow = new TextFlow();
        songList = new ArrayList<>();
        sortedFileArray = new ArrayList<>();
        File imagesDir = new File("images/");
        sortedFileArray = Arrays.stream(imagesDir.listFiles()).toList();
        for (File file : sortedFileArray) {
            String fileInputStreamPath = "images/"+file.getName();
            createImages(file, fileInputStreamPath);
            songList.add(file.toString().replace("images", "songs").replace("jpg", "mp3"));
        }
        System.out.println(sortedFileArray);
        System.out.println(songList);
    }

    public void createImages(File file, String inputStream) throws FileNotFoundException {
        FileInputStream inputStreamPath = new FileInputStream(inputStream);
        Image image = new Image(inputStreamPath);
        imageView = new ImageView(image);

        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(musicPlayerVBox.widthProperty().divide(3));
        imageView.fitHeightProperty().bind(musicPlayerVBox.heightProperty().divide(3));

        textFlow.getChildren().add(imageView);

        imageView.setPickOnBounds(true);

        System.out.println(imageView);

        imageView.setOnMouseClicked((MouseEvent e) -> {
            if (buttonCount.get() % 2 == 0) {
                imagesToSongs(file);
                soundCreator(finalUrl);
            } else {
                mediaPlayer.stop();
                playingStatus = true;
            }
            buttonCount.addAndGet(1);
            showPlayPauseButton();
        });

    }

    private void imagesToSongs(File file) {
        String result = file.getName().replaceAll("\\.jpg$", ".mp3");
        finalUrl = "songs/"+result;
    }

    public void soundCreator(String url) {
        media = new Media(new File(url).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        playingStatus = true;
    }

    public void addFile() throws IOException {
        Stage stage = new Stage();
        File dir = new File("images/");
        File[] files = dir.listFiles();

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }

        String fileInputStreamPath = lastModifiedFile.getPath();
        File file = new File(fileInputStreamPath);
        imagesToSongs(file);
        start(stage);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        soundCreator(finalUrl);

        playingStatus = true;
        showPlayPauseButton();


    }


    @Override
    public void start(Stage stage) throws IOException {
        musicPlayerVBox = new VBox();
        musicPlayerHBox = new HBox();
        searchBarHBox = new HBox();
        textFlow = new TextFlow();
        scrollPane = new ScrollPane();


        playPauseButton = new Button("Pause");
        playPauseButton.setOnMouseClicked((MouseEvent e) -> {
            next = false;
            playingStatus = true;
            showPlayPauseButton();
            if (buttonCount.get() % 2 != 0) {
                mediaPlayer.pause();
                playPauseButton.setText("Play");
            } else {
                mediaPlayer.play();
                playPauseButton.setText("Pause");
            }
            buttonCount.addAndGet(1);
        });

        stopButton = new Button("Stop");
        stopButton.setOnMouseClicked((MouseEvent e) -> {
            mediaPlayer.stop();
            if (buttonCount.get() % 2 != 0) {
                buttonCount.addAndGet(1);
            }
            playingStatus = false;
            showPlayPauseButton();
        });

        showImages();

        previousButton = new Button("Previous");
        previousButton.setOnMouseClicked((MouseEvent e) -> {
            next=true;
            previousNextSong(songList.indexOf(finalUrl)-1 >= 0, 1, songList.size() - 1, false);
        });


        nextButton = new Button("Next");
        nextButton.setOnMouseClicked((MouseEvent e) -> {
            next=true;
            previousNextSong(songList.indexOf(finalUrl)+1 <= songList.size()-1, -1, 0, true);
        });

        volumeSlider = new Slider(0, 100, 1);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setValue(100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });

        searchBar = new TextField();
        searchBar.setPromptText("Insert Youtube URL");
        searchBarButton = new Button("\uD83D\uDD0D");
        searchBarLabel = new Label();

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                //labelPause(searchBarLabel);
                String ytUrl = searchBar.getText();
                YoutubeDownloader youtubeDownloader = new YoutubeDownloader();
                int exitCode = youtubeDownloader.downloadSong(ytUrl);
                if (exitCode == 0) {
                    searchBarLabel.setText("Song downloaded \uD83D\uDC4D");
                    try {
                        YoutubeDownloader.iterateSongs();
                        addFile();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else
                    searchBarLabel.setText("Error downloading song ❌");
                searchBar.setText("");

            }
        };


        searchBarButton.setOnAction(event);

        HBox.setHgrow(previousButton, Priority.ALWAYS);
        HBox.setHgrow(playPauseButton, Priority.ALWAYS);
        HBox.setHgrow(stopButton, Priority.ALWAYS);
        HBox.setHgrow(nextButton, Priority.ALWAYS);
        HBox.setHgrow(volumeSlider, Priority.ALWAYS);

        previousButton.setMaxWidth(Double.MAX_VALUE);
        playPauseButton.setMaxWidth(Double.MAX_VALUE);
        stopButton.setMaxWidth(Double.MAX_VALUE);
        nextButton.setMaxWidth(Double.MAX_VALUE);
        volumeSlider.setMaxWidth(Double.MAX_VALUE);

        showPlayPauseButton();
        musicPlayerHBox.setAlignment(Pos.BOTTOM_CENTER);

        searchBarHBox.getChildren().addAll(searchBarLabel, searchBar, searchBarButton);
        searchBarHBox.setAlignment(Pos.TOP_RIGHT);

        //scrollPane.setContent(textFlow);
        //scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        musicPlayerVBox.getChildren().addAll(searchBarHBox, textFlow, musicPlayerHBox);

        // Set VBox to grow with window
        VBox.setVgrow(musicPlayerHBox, Priority.ALWAYS);
        VBox.setVgrow(imageView, Priority.ALWAYS);

        Scene scene = new Scene(musicPlayerVBox, 960, 540);
        stage.setTitle("Burnify");
        stage.setScene(scene);
        stage.show();
    }

    //previousNextSong(songList.indexOf(finalUrl)-1 >= 0, 1, songList.size() - 1);

    //previousNextSong(songList.indexOf(finalUrl)+1 <= songList.size()-1, -1, 0);

    private void previousNextSong(boolean a, int b, int getThis, boolean c) {
        if (mediaPlayer.getCurrentTime().lessThan(Duration.millis(5000)) || c) {
            mediaPlayer.stop();
            if (a) {
                String previousSongString = songList.get(songList.indexOf(finalUrl) - (b));
                soundCreator(previousSongString);
                finalUrl = previousSongString;
            } else {
                soundCreator(songList.get(getThis));
                finalUrl = songList.get(getThis);
            }
        } else {
            mediaPlayer.stop();
            mediaPlayer.play();
        }
        playingStatus = true;
        showPlayPauseButton();
    }


    private void showPlayPauseButton() {
        musicPlayerHBox.getChildren().clear();
        if (playingStatus){
            musicPlayerHBox.getChildren().addAll(previousButton, playPauseButton, stopButton,
                    nextButton, volumeSlider);
        } else {
            musicPlayerHBox.getChildren().addAll(previousButton, stopButton,
                    nextButton, volumeSlider);
        }
    }

    public static void main(String[] args) {
        launch();


    }
}

