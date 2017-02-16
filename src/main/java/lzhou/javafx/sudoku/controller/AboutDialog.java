package lzhou.javafx.sudoku.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;

@Component 
@Scope("singleton")
public class AboutDialog {
	@Autowired
	StagesController stagesController;
	
	@FXML
	private void onOkClicked(ActionEvent event) {
		stagesController.getStage(StagesController.StageId.About).close();
	}
	
}
