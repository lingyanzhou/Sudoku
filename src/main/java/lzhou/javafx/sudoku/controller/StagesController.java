package lzhou.javafx.sudoku.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lzhou.javafx.sudoku.view.AboutView;
import lzhou.javafx.sudoku.view.AppView;

@Controller
@Scope("singleton")
public class StagesController {
	@Autowired
	AppView appView;
	
	@Autowired
	AboutView aboutView;
	
	static public enum StageId {
		App,
		About,
	}
	private Map<StageId, Stage> map;
	
	public StagesController(){
		map = new HashMap<StageId, Stage>();
	}
	
	public Stage getAppStage(Stage mainStage) {
		if (map.containsKey(StageId.App)) {
			return map.get(StageId.App);
		}		
		Parent root = appView.getView();
		Scene scene = new Scene(root);
		mainStage.setScene(scene);
		map.put(StageId.App, mainStage);
		return mainStage;
	}
	
	public Stage getStage(StageId id) {
		if (map.containsKey(id)) {
			return map.get(id);
		}
		Stage stage = new Stage();
		Parent root = null;
		if (id==StageId.About) {
			root= aboutView.getView();
		}
		Scene scene = new Scene(root);
    	stage.setScene(scene);
		map.put(id, stage);
		return stage;
    	
	}
}
