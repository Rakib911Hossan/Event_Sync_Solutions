package com.Corporate.Event_Sync;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class EventSyncApplication extends Application {
	public static ConfigurableApplicationContext context;
	public static void main(String[] args) {

		Application.launch(EventSyncApplication.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		context=SpringApplication.run(EventSyncApplication.class);
		FXMLLoader fxml=new FXMLLoader(getClass().getResource
				("/com.Corporate.Event_Sync/main.fxml"));
		fxml.setControllerFactory(context::getBean);
		Scene scene=new Scene(fxml.load());
		String title=context.getBean("title",String.class);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();

	}

}
