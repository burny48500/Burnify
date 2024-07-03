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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    private List<File> sortedFileArray;
    private List<String> songList = new ArrayList<>();
    private boolean playingStatus = false;
    private String finalUrl;
    private TilePane tilePane;
    private Stage stage;
    private String ytUrl;
    private YoutubeDownloader youtubeDownloader;
    private int exitCode;
    private String labelText;
    private boolean count = true;


    private void labelPause(Label label) {
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(5)
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
        tilePane = new TilePane();
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
        imageView.fitWidthProperty().bind(scrollPane.widthProperty().divide(3)); // Adjust width to fill the scroll pane
        imageView.setFitHeight(200); // Set a fixed height for each image

        tilePane.getChildren().add(imageView);

        imageView.setPickOnBounds(true);

        System.out.println(imageView);

        imageView.setOnMouseClicked((MouseEvent e) -> {
            if (count) {
                imagesToSongs(file);
                soundCreator(finalUrl);
                count = false;
                System.out.println(count);
            } else {
                mediaPlayer.stop();
                count = true;
                System.out.println(count);

            }
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
        mediaPlayer.setOnEndOfMedia(() ->
                previousNextSong(songList.indexOf(finalUrl) + 1 <= songList.size() - 1, -1, 0, true));
        playingStatus = true;
    }

    public void addFile() throws IOException {
        playingStatus = true;

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

        searchBarLabel.setText(labelText);
        labelPause(searchBarLabel);
        searchBar.setText("");

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        count = false;
        showPlayPauseButton();
        soundCreator(finalUrl);

    }


    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        musicPlayerVBox = new VBox();
        musicPlayerHBox = new HBox();
        searchBarHBox = new HBox();
        tilePane = new TilePane();
        scrollPane = new ScrollPane();

        playPauseButton = new Button("Pause");
        playPauseButton.setOnMouseClicked((MouseEvent e) -> {
            if (!count) {
                mediaPlayer.pause();
                count = true;
                playPauseButton.setText("Play");
                System.out.println(count);
            } else {
                mediaPlayer.play();
                count = false;
                playPauseButton.setText("Pause");
                System.out.println(count);
            }
        });

        stopButton = new Button("Stop");
        stopButton.setOnMouseClicked((MouseEvent e) -> {
            mediaPlayer.stop();
            count = true;

            System.out.println(count);
            showPlayPauseButton();
        });

        showImages();

        previousButton = new Button("Previous");
        previousButton.setOnMouseClicked((MouseEvent e) -> {
            previousNextSong(songList.indexOf(finalUrl) - 1 >= 0, 1, songList.size() - 1, false);
            System.out.println(count);
        });

        nextButton = new Button("Next");
        nextButton.setOnMouseClicked((MouseEvent e) -> {
            previousNextSong(songList.indexOf(finalUrl) + 1 <= songList.size() - 1, -1, 0, true);
            showPlayPauseButton();
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
            public void handle(ActionEvent e) {
                ytUrl = searchBar.getText();
                youtubeDownloader = new YoutubeDownloader();
                exitCode = youtubeDownloader.downloadSong(ytUrl);
                if (exitCode == 0) {
                    //searchBarLabel.setText("Song downloaded \uD83D\uDC4D");
                    try {
                        YoutubeDownloader.iterateSongs();
                        labelText = "Song downloaded \uD83D\uDC4D";
                        addFile();
                    } catch (IOException ex) {
                        labelText = "Error downloading song ❌";
                        System.out.println(labelText);
                        searchBarLabel.setText(labelText);
                        throw new RuntimeException(ex);
                    }
                }
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

        tilePane.setPrefColumns(3); // Set number of columns
        tilePane.setHgap(0); // Remove horizontal gap
        tilePane.setVgap(0); // Remove vertical gap

        scrollPane.setContent(tilePane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hide vertical scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hide horizontal scrollbar
        scrollPane.setStyle("-fx-background: transparent;"); // Make ScrollPane background transparent

        scrollPane.addEventFilter(ScrollEvent.SCROLL, event1 -> {
            double deltaY = event1.getDeltaY() * 2; // Adjust the scroll speed multiplier as needed
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / scrollPane.getHeight());
            event1.consume();
        });

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        musicPlayerVBox.getChildren().addAll(searchBarHBox, scrollPane, musicPlayerHBox);

        Scene scene = new Scene(musicPlayerVBox, 960, 540);
        stage.setTitle("Burnify");
        stage.setScene(scene);
        stage.show();
    }

    private void previousNextSong(boolean a, int b, int getThis, boolean c) {
        count = false;
        if (mediaPlayer.getCurrentTime().lessThan(Duration.millis(5000)) || c) {
            mediaPlayer.stop();
            if (a) {
                mediaPlayer.stop();
                String previousSongString = songList.get(songList.indexOf(finalUrl) - (b));
                soundCreator(previousSongString);
                finalUrl = previousSongString;
            } else {
                mediaPlayer.stop();
                soundCreator(songList.get(getThis));
                finalUrl = songList.get(getThis);
            }
        } else {
            mediaPlayer.stop();
            mediaPlayer.play();
        }
        showPlayPauseButton();
    }


    private void showPlayPauseButton() {
        musicPlayerHBox.getChildren().clear();
        if (count) {
            playPauseButton.setText("Play");
        } else {
            playPauseButton.setText("Pause");
        }
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