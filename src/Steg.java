import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Peter
 */
public class Steg extends Application{

    public static final String coverImagePath = System.getProperty("user.home")+File.separator+"Desktop"+File.separator+"steganography"+File.separator+"Cover-Image"+File.separator;
    public static final String hiddenDataPath = System.getProperty("user.home")+File.separator+"Desktop"+File.separator+"steganography"+File.separator+"Hidden-Data"+File.separator;
    public static final String stegoImagePath = System.getProperty("user.home")+File.separator+"Desktop"+File.separator+"steganography"+File.separator+"Output"+File.separator;
    public static final FileChooser fileChooser = new FileChooser();
    public static String MainPage = "MainPage";
    public static String MainFile = "MainPage.fxml";


    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static Stage mainStage;

    @Override
    public void start(Stage stage)throws FileNotFoundException, IOException, URISyntaxException{
        ScreenController mainController = new ScreenController();
        mainStage = stage;
        mainController.loadScreen(MainPage, MainFile);

        mainController.setScreen(MainPage);

        Scene scene = new Scene(mainController);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

    }

    public String selectImage() throws FileNotFoundException, IOException, URISyntaxException {
        configureFileChooser(fileChooser);
        return fileChooser.showOpenDialog(mainStage).getPath();
    }
    
    public static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Select Images");
        fileChooser.setInitialDirectory(new File(coverImagePath).getParentFile());
        if (fileChooser.getExtensionFilters().isEmpty()) {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Bitmap Images", "*.bmp"));
        }
    }
    

}
