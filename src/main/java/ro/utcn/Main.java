package ro.utcn;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ro.utcn.config.AppConfig;

public class Main extends Application {

    private Logger LOGGER = LogManager.getLogger(Main.class);

    static ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.info("Application Started");
        Manager manager = ctx.getBean(Manager.class);
        LOGGER.debug("Bean loaded");
        manager.setStage(primaryStage);
        manager.loadMainPage();
    }
}
