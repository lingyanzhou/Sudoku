package lzhou.javafx.sudoku.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lzhou.javafx.misc.FxmlLoaderProducer;

@Component 
@Scope("singleton")
public class AboutView implements View {
	@Autowired
	private FxmlLoaderProducer loaderProducer;
	
	Parent cached;
	
	public AboutView() {
		cached = null;
	}

	@Override
	public Parent getView() {
		if (cached!=null) {
			return cached;
		}
		try {
			FXMLLoader loader = loaderProducer.createLoader();
			loader.setLocation(getClass().getResource("/fxml/About.fxml"));
			cached = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return cached;
	}

}
