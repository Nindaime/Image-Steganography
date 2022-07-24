/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.HashMap;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.util.Duration;
/**
 *
 * @author PETER
 */
public class ScreenController extends StackPane{
    
    public static HashMap<String, Pane> screens = new HashMap<>();
    
    public ScreenController(){
        super();
    }
    
    public void addScreen(String name, Pane screen){
        screens.put(name, screen);
    }
    
    public Pane getScreen(String name){
        return screens.get(name);
    }
    
    public boolean loadScreen(String name, String resource){
        try{
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Pane loadScreen = (Pane) myLoader.load();
            ControlledScreen myScreenController = (ControlledScreen) myLoader.getController();
            myScreenController.setScreenParent(this);
            addScreen(name, loadScreen);
            return true;
        }catch(IOException io){
            io.printStackTrace();
            return false;
        }
    }
    
    public boolean setScreen(final String name){
        if(screens.get(name) != null){
            Pane pane = screens.get(name);
            
            if(!getChildren().isEmpty()){
                Pane currentPage = (Pane) getChildren().get(0);
                Timeline fadeOutCurrentScreen = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(currentPage.opacityProperty(), 1)), 
                        new KeyFrame(Duration.millis(400), (ActionEvent t) -> {
                            getChildren().remove(0); 
                            getChildren().add(0, pane);}
                                ,new KeyValue(currentPage.opacityProperty(), 0, Interpolator.EASE_IN)));
                fadeOutCurrentScreen.play();
                
                Timeline fadeInSecondScreen = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(pane.opacityProperty(), 0)),
                        new KeyFrame(Duration.millis(400), new KeyValue(pane.opacityProperty(), 1, Interpolator.EASE_OUT)));
                fadeInSecondScreen.setDelay(Duration.millis(400));
                fadeInSecondScreen.play();
                System.out.println("Second animation playing");
            }
            else{
                final DoubleProperty opacity = opacityProperty();
                getChildren().add(pane);
                Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0)),
                        new KeyFrame(Duration.millis(600), new KeyValue(opacity, 1.0)));
                fadeIn.play();
                System.out.println("First animation playing");
            }
            return true;
        }
        else{
                System.out.println("Screen hasn't been loaded!!! \n");
                return false;
        }
        
    }
}
