package lzhou.javafx.sudoku;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javafx.application.Application;
import javafx.stage.Stage;
import lzhou.javafx.sudoku.controller.StagesController;

public class MainApp extends Application {
	private ApplicationContext applicationContext= null;
	
	public MainApp() {
		applicationContext= null;
	}
	
    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
	}

	@Override
    public void start(Stage stage) throws Exception {
		
		applicationContext.getBean(StagesController.class).getAppStage(stage).show();
    }

    @Override
	public void init() throws Exception {
		super.init();
		applicationContext = new ClassPathXmlApplicationContext("beans.xml");
	}

	/**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    
    //public static void main(String[] args) {
        //Application.launch(MainApp.class, args);
    	//applicationContext = new ClassPathXmlApplicationContext("classpath*:beans.xml");
    //}
    

}
