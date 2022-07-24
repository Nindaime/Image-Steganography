import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainPageController implements Initializable, ControlledScreen {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Steg stegUtilObject = new Steg();

    @FXML
    private Label lblWindow1Notification;

    @FXML
    private Label lblWindow2Notification;

    @FXML
    private ImageView imgV_Window1;

    @FXML
    private ImageView imgV_Window2;

    @FXML
    private BorderPane ctrlPanel1;

    @FXML
    private BorderPane ctrlPanel2;

    @FXML
    private BorderPane ctrlPanel3;

    @FXML
    private BorderPane ctrlPanel4;

    @FXML
    private BorderPane ctrlPanel5;

    String coverImage = "";
    String hiddenImage = "";
    String password = "";
    ImageSteganography st;
    private static String subWindow = "PasswordPage.fxml";
    int random;

    @FXML
    public void selectImage(ActionEvent e){
        try{
            String id = ((Button)e.getSource()).getId();
            String ImageAddress = stegUtilObject.selectImage();
            System.out.println("Button clicked is: "+id);
            if(((Button)e.getSource()).getId().matches("btnCoverImage")){
                coverImage = ImageAddress;
                lblWindow1Notification.setText("Cover Image Uploaded");
//                imgV_Window1.setImage(new Image(new FileInputStream(ImageAddress)));
            }else if(((Button)e.getSource()).getId().matches("btnHiddenImage")){
                hiddenImage = ImageAddress;
                lblWindow2Notification.setText("Hidden Image Uploaded");

//                myController.

                Stage stage = new Stage();
                stage.initOwner(Steg.mainStage);
                stage.initStyle(StageStyle.TRANSPARENT);
                FXMLLoader myLoader = new FXMLLoader(getClass().getResource(subWindow));
                passwordWindow = (AnchorPane) myLoader.load();
                stage.setScene(new Scene(passwordWindow));
                stage.showAndWait();


//                System.out.println("Type your stego-key(4 Characters): ");
//                Scanner scanner = new Scanner(System.in);
//                password = scanner.next();

                st = new ImageSteganography();
                st.encode(coverImage, password, hiddenImage);

                random = (int) (Math.random() * 2);
                switch (random){
                    case 0:
                        lblWindow1Notification.setOpacity(0);
                        lblWindow2Notification.setOpacity(0);
                        imgV_Window1.setImage(new Image(new FileInputStream(new File(coverImage))));
                        imgV_Window2.setImage(new Image(new FileInputStream(st.getOutputFile())));
                        System.out.println("Steganographic image in right window");
                        break;
                    case 1:
                        lblWindow1Notification.setOpacity(0);
                        lblWindow2Notification.setOpacity(0);
                        imgV_Window1.setImage(new Image(new FileInputStream(st.getOutputFile())));
                        imgV_Window2.setImage(new Image(new FileInputStream(new File(coverImage))));
                        System.out.println("Steganographic image in left window");
                        break;
                }

            }



        }catch(Exception er){
            er.printStackTrace();
        }
    }

    @FXML
    private void extraMessage(ActionEvent e){
        imgV_Window1.setImage(null);
        imgV_Window2.setImage(null);

        ((Stage) (passwordWindow.getScene().getWindow())).showAndWait();

        st.decode(st.getOutputFile().getPath(), password);

        System.out.println("Message file name: "+st.getMessageFile().getPath());

        try{
            if(random == 1)
                imgV_Window1.setImage(new Image(new FileInputStream(st.getMessageFile())));
            else
                imgV_Window2.setImage(new Image(new FileInputStream(st.getMessageFile())));

        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }

    }

    ScreenController myController;

    @Override
    public void setScreenParent(ScreenController screenPage) {
        myController = screenPage;
    }

    @FXML
    private MenuItem menuBtnClose;

    @FXML
    private AnchorPane passwordWindow;

    @FXML
    private void mainCloseButtonAction(ActionEvent event) {
            ((Stage) container.getScene().getWindow()).close();
    }

    @FXML
    private void psCloseButtonAction(ActionEvent e){
        ((Stage) passwordWindow.getScene().getWindow()).close();
    }

    @FXML
    private void submitPassword(ActionEvent e){
        TextField pass = (TextField) passwordWindow.lookup("#tfPassword");
        password = pass.getText();
        psCloseButtonAction(e);
        pass.clear();
    }

    @FXML
    private VBox container;

    @FXML
    private AnchorPane aPanelWindow1;

    @FXML
    private AnchorPane aPanelWindow2;

    @FXML
    private void requestfocus(MouseEvent event) {
        ((Node)event.getSource()).requestFocus();
    }

    double yOffset = 0;
    double xOffset = 0;

    @FXML
    private void determine(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        xOffset = stage.getX() - event.getScreenX();
        yOffset = stage.getY() - event.getScreenY();
    }

    @FXML
    private void pick(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        ((Node) event.getSource()).setCursor(Cursor.CLOSED_HAND);
        stage.setX(event.getScreenX() + xOffset);
        stage.setY(event.getScreenY() + yOffset);
    }

    @FXML
    private void drop(MouseEvent event) {
        ((Node) event.getSource()).setCursor(Cursor.OPEN_HAND);
    }

}
