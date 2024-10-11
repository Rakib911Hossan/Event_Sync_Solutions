package com.Corporate.Event_Sync.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.util.ResourceBundle;

@Component
public class Controller implements Initializable {

    @FXML

    public Label example;

    public void comment(){
        example.setText("NAME");
    }

    @Bean
    String title(){
        return "CRUD11113";
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        comment();
    }
}
