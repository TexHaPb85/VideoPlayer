import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

import static javafx.geometry.Pos.BOTTOM_CENTER;
import static javafx.geometry.Pos.CENTER;

public class AppVideo extends Application {

    Stage window;
    TableView<VideoUri> videoTableView;


    private MediaView mediaView;
    private Slider seekSlider;
    private Label time;
    private MediaPlayer mediaPlayer;
    private Boolean flag;

    public AppVideo() {
        seekSlider = new Slider();
        mediaView = new MediaView();
        time = new Label();
        time.setPrefWidth(100);
        flag=false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        //**********************************************//Історія
        Stage secondaryStage = new Stage();
        window = secondaryStage;
        window.setTitle("History");

//        TableColumn<VideoUri, String> uriOfOpenVideo = new TableColumn<>("URI");
//        uriOfOpenVideo.setMinWidth(500);
//        uriOfOpenVideo.setCellValueFactory(new PropertyValueFactory<>("uriOfOpenVideo"));

        TableColumn<VideoUri, String> videoName = new TableColumn<>("Name of Video");
        videoName.setMinWidth(300);
        videoName.setCellValueFactory(new PropertyValueFactory<>("videoName"));

        TableColumn<VideoUri, Integer> number = new TableColumn<>("#");
        //videoName.setMinWidth(300);
        number.setCellValueFactory(new PropertyValueFactory<>("number"));

        videoTableView = new TableView<>();
        videoTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        videoTableView.getColumns().addAll(number/*,uriOfOpenVideo*/,videoName);

        Button openButton = new Button("Відкрити файл");
        final ArrayList<String> fileOpenedH = new ArrayList<>();
        openButton.setOnAction( e ->{
            if (flag==true){
                mediaPlayer.stop();
                flag = false;
            }
                fileOpenedH.add(videoTableView.getSelectionModel().getSelectedItems().get(0).getUri());
                Media media = new Media(fileOpenedH.get(fileOpenedH.size()-1));
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                //System.out.println(videoTableView.getSelectionModel().getSelectedItems().get(0).getUri());
                if(primaryStage.isFullScreen()==false){
                    DoubleProperty width = mediaView.fitWidthProperty();
                    DoubleProperty height = mediaView.fitHeightProperty();
                    width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
                    height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
                }
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    seekSlider.setValue(newValue.toSeconds());
                    int hours = (int)newValue.toSeconds()/3600;
                    int minutes = (int)(newValue.toSeconds()%3600)/60;
                    int seconds = (int)(newValue.toSeconds()%3600)%60;
                    if (hours<10 && minutes<10 && seconds<10)
                    {
                        time.setText("0"+hours+" : "+"0"+minutes+" : "+"0"+seconds);
                    }
                    else if (hours<10 && minutes<10)
                    {
                        time.setText("0"+hours+" : "+"0"+minutes+" : "+seconds);
                    }
                    else if (hours<10 && seconds<10)
                    {
                        time.setText("0"+hours+" : "+minutes+" : "+"0"+seconds);
                    }
                    else if (minutes<10 && seconds<10)
                    {
                        time.setText("0"+hours+" : "+"0"+minutes+" : "+seconds);
                    }
                    else if (hours<10){
                        time.setText("0"+hours+" : "+minutes+" : "+seconds);
                    }
                    else if (minutes<10){
                        time.setText(hours+" : "+"0"+minutes+" : "+seconds);
                    }
                    else if (seconds<10){
                        time.setText(hours+" : "+minutes+" : "+"0"+seconds);
                    }
                    else
                    {
                        time.setText(hours+" : "+minutes+" : "+seconds);
                    }
                });

            mediaPlayer.setOnReady(() -> seekSlider.setMax(media.getDuration().toSeconds()));
            seekSlider.setOnMouseClicked(event -> mediaPlayer.seek(Duration.seconds(seekSlider.getValue())));
            mediaPlayer.play();
            flag=true;
                    });


        Button deleteButton = new Button("Вилучити файл з історії");
        deleteButton.setOnAction(e -> deleteButtonClicked());

        HBox hBoxH = new HBox();
        hBoxH.setPadding(new Insets(10,10,10,10));
        hBoxH.setSpacing(10);
        hBoxH.getChildren().addAll(deleteButton,openButton);

        VBox vBoxH = new VBox();
        vBoxH.getChildren().addAll(videoTableView, hBoxH);

        Scene sceneH = new Scene(vBoxH);
        window.setScene(sceneH);

        //**********************************************//Плеєр
        primaryStage.setTitle("Video Player");
        final int[] counter = {1};

        final ArrayList<String> fileOpened = new ArrayList<>();

        //Create menu
        Menu fileMenu = new Menu("Файл");
        MenuItem openFile = new MenuItem("Відкрити відео");
        fileMenu.getItems().add(openFile);
        openFile.setOnAction((ActionEvent e) -> {
            if (flag==true){
                mediaPlayer.stop();
                flag = false;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Відкрий мене");
            fileChooser.getExtensionFilters().addAll(          //задаємо розширення файлів
                    new FileChooser.ExtensionFilter("Some video", "*.mp4"),
                    new FileChooser.ExtensionFilter("Всі файли", "*.*")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                System.out.println(file.toURI().toString());
                primaryStage.setTitle(file.getName());
                VideoUri videoUri = new VideoUri();
                videoUri.setNumber(counter[0]);
                videoUri.setUriOfOpenVideo(file.toURI().toString());
                videoUri.setVideoName(file.getName());
//                System.out.println(videoUri.getUri()+"==");
//                System.out.println(videoUri.getVideoName()+"==");
                videoTableView.getItems().add(videoUri);
                fileOpened.add(file.toURI().toString());
                Media media = new Media(fileOpened.get(fileOpened.size()-1));
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                if(primaryStage.isFullScreen()==false){
                    DoubleProperty width = mediaView.fitWidthProperty();
                    DoubleProperty height = mediaView.fitHeightProperty();
                    width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
                    height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
                }
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    seekSlider.setValue(newValue.toSeconds());
                    int hours = (int)newValue.toSeconds()/3600;
                    int minutes = (int)(newValue.toSeconds()%3600)/60;
                    int seconds = (int)(newValue.toSeconds()%3600)%60;
                    if (hours<10 && minutes<10 && seconds<10)
                    {
                        time.setText("0"+hours+" : "+"0"+minutes+" : "+"0"+seconds);
                    }
                    else if (hours<10 && minutes<10)
                    {
                        time.setText("0"+hours+" : "+"0"+minutes+" : "+seconds);
                    }
                    else if (hours<10 && seconds<10)
                    {
                        time.setText("0"+hours+" : "+minutes+" : "+"0"+seconds);
                    }
                    else if (minutes<10 && seconds<10)
                    {
                        time.setText("0"+hours+" : "+"0"+minutes+" : "+seconds);
                    }
                    else if (hours<10){
                        time.setText("0"+hours+" : "+minutes+" : "+seconds);
                    }
                    else if (minutes<10){
                        time.setText(hours+" : "+"0"+minutes+" : "+seconds);
                    }
                    else if (seconds<10){
                        time.setText(hours+" : "+minutes+" : "+"0"+seconds);
                    }
                    else
                    {
                        time.setText(hours+" : "+minutes+" : "+seconds);
                    }
                    //time.setText(hours+" : "+minutes+" : "+"0"+seconds);
                    //String.valueOf(newValue.toSeconds())
                });

                mediaPlayer.setOnReady(() -> seekSlider.setMax(media.getDuration().toSeconds()));
                seekSlider.setOnMouseClicked(event -> mediaPlayer.seek(Duration.seconds(seekSlider.getValue())));
                mediaPlayer.play();
                flag=true;
                counter[0]++;

            }
            System.out.println("open");
        });

        Menu ControlMenu = new Menu("Керування плеєром");

        MenuItem toFullScreen = new MenuItem("На весь екран");
        ControlMenu.getItems().add(toFullScreen);
        toFullScreen.setOnAction((ActionEvent e) -> {
            primaryStage.setFullScreen(true);
        });

        MenuItem toWindow = new MenuItem("Вийти з повноекранного режиму");
        ControlMenu.getItems().add(toWindow);
        toWindow.setOnAction((ActionEvent e) -> {
            primaryStage.setFullScreen(false);
        });

        MenuItem openHistory = new MenuItem("Відкрити історію");
        ControlMenu.getItems().add(openHistory);
        openHistory.setOnAction((ActionEvent e) -> {
            window.show();
        });



        MenuBar menuBar = new MenuBar();



        //Create media
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(BOTTOM_CENTER);
        stackPane.getChildren().addAll(mediaView, seekSlider);

        //Create control
        VBox vBox = new VBox();
        vBox.setAlignment(CENTER);
        HBox controls = new HBox();
        controls.setAlignment(CENTER);
        controls.setPadding(new Insets(10, 10, 10, 10));
        controls.setSpacing(10);



        TextField seekFieldHour = new TextField ();
        seekFieldHour.setPromptText("Години");
        TextField seekFieldMinute = new TextField ();
        seekFieldMinute.setPromptText("Хвилини");
        TextField seekFieldSecond = new TextField ();
        seekFieldSecond.setPromptText("Секунди");
        Button seekButton = new Button("Шукати");
        seekButton.setOnAction(e->{
            double h;
            if (seekFieldHour.getText().equals("")) h=0; else
                h = Double.parseDouble (seekFieldHour.getText())*3600;
            double m;
            if(seekFieldMinute.getText().equals("")) m=0; else
                m = Double.parseDouble (seekFieldMinute.getText())*60;
            double s;
            if (seekFieldSecond.getText().equals("")) s=0; else
                s = Double.parseDouble (seekFieldSecond.getText());
            mediaPlayer.seek(Duration.seconds(h+m+s));
            mediaPlayer.play();
            flag=true;
            seekFieldHour.clear();
            seekFieldMinute.clear();
            seekFieldSecond.clear();
        });

        Button playButton;
        playButton = new Button("Play");
        playButton.setOnAction(e->{
            if(flag==true){
                mediaPlayer.pause();
                flag=false;
                playButton.setText("|>");
            }
            else   {
                mediaPlayer.play();
                flag=true;
                playButton.setText("||");
            }
       });


        Slider volume = new Slider();
        volume.valueProperty().addListener(observable -> mediaPlayer.setVolume(volume.getValue()/100));
        controls.getChildren().addAll(playButton, volume, seekFieldHour,seekFieldMinute,seekFieldSecond,seekButton,time);
        vBox.getChildren().add(controls);

        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setCenter(stackPane); //плеер запхнуть в вбокс вище контролса
        layout.setBottom(vBox); // перемістити в цент
        Scene scene = new Scene(layout, 870, 600);

        scene.getStylesheets().add("style2.css");
        sceneH.getStylesheets().add("style2.css");

        Menu styleMenu = new Menu("Вибір стилю");
        MenuItem changeStyle = new MenuItem("Dark style");
        //styleMenu.getItems().add(changeStyle);
        changeStyle.setOnAction((ActionEvent e) -> {
            scene.getStylesheets().add("style2.css");
            sceneH.getStylesheets().add("style2.css");
        });
        MenuItem changeStyle2 = new MenuItem("Classic style");
        styleMenu.getItems().addAll(changeStyle,changeStyle2);
        changeStyle2.setOnAction((ActionEvent e) -> {
            scene.getStylesheets().clear();
            sceneH.getStylesheets().clear();
        });
        menuBar.getMenus().addAll(fileMenu,ControlMenu,styleMenu);
        scene.setOnMouseClicked(e->{

            if (e.getClickCount() == 2){

                if(primaryStage.isFullScreen()==false){primaryStage.setFullScreen(true); menuBar.setVisible(false);}

                else {primaryStage.setFullScreen(false);menuBar.setVisible(true);}

            }
            if(primaryStage.isFullScreen()==false)
                menuBar.setVisible(true);
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void deleteButtonClicked(){
        ObservableList<VideoUri> productSelected, allProducts;
        allProducts = videoTableView.getItems();
        productSelected = videoTableView.getSelectionModel().getSelectedItems();
        productSelected.forEach(allProducts::remove);
    }

    public static void main(String[] args) {
        launch(args);
    }
}