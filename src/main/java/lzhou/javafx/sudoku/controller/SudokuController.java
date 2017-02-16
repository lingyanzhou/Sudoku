package lzhou.javafx.sudoku.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import lombok.Setter;
import lzhou.javafx.recentfile.entity.RecentFile;
import lzhou.javafx.recentfile.service.RecentFileService;
import lzhou.javafx.sudoku.model.Board;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

@Component 
@Scope("singleton")
public class SudokuController implements Initializable {
	@Autowired
	private StagesController stagesController;
	
	@Autowired
	private RecentFileService recentFileService;
	
	@Autowired
	private Board board;
	
	private TextField[][] textFields;
	
    @FXML
    private GridPane gridPane;

    @FXML
    private Menu recentFileMenu;
    
    private InvalidationListener boardInvalidationListener;
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	ObservableList<MenuItem> menuList = recentFileMenu.getItems();
    	menuList.clear();
    	Iterable<RecentFile> rfs = recentFileService.findAllOrderByIdDesc();
    	for (RecentFile f: rfs) {
			MenuItem menuItem = new MenuItem();
			menuItem.setUserData(f);
			menuItem.setText(f.getFilename());
			menuItem.setOnAction(
				e->{
					loadBoard(new File(((RecentFile)((MenuItem)e.getSource()).getUserData()).getFilename()));
			});
			menuList.add(menuItem);
		}
    	textFields = new TextField[9][9];
    	for (int i=0; i<9; ++i) {
    		for (int j=0; j<9; ++j) {
    			TextField tf = new TextField();
    			textFields[i][j]=tf;
    			tf.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
    			tf.getStyleClass().add(getCellClass(i, j));
    			tf.textProperty().bindBidirectional(board.get(i, j), new SudokuCellConvertor());
    			tf.textProperty().addListener((ObservableValue<? extends String> arg0, String arg1, String arg2)-> {
    				try {
    					int tmp=Integer.parseInt(arg2);
    					if (tmp<1 || tmp>9) {
    						 Platform.runLater(() -> {
    							 tf.clear();
    				         });
    					}
    				} catch (Exception e) {
						 Platform.runLater(() -> {
							 tf.clear();
				         });
    				}
    			});
    			tf.disableProperty().bind(board.getIsEditable(i, j));
    			gridPane.add(tf, i, j);
    		}
    	}
    	
    	boardInvalidationListener = new BoardInvalidationListener(board);
    	board.isBoardCompleteProperty().addListener(boardInvalidationListener);
    	
    }
    
    @FXML
    private void onQuitClicked(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Quit Dialog");
    	alert.setHeaderText("Quit?");
    	alert.setContentText("You are about to quit. Are your sure?");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		Platform.exit();
    	}
    }
    
    @FXML
    private void onOpenClicked(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Resource File");
    	fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Text Files", "*.txt"));
    	File selectedFile = fileChooser.showOpenDialog(null);
    	if (selectedFile != null && loadBoard(selectedFile)) {    		
    		addNewRecentFile(selectedFile);
    	}
    }
    
    private void addNewRecentFile(File recent) {
    	RecentFile newFile = recentFileService.save(new RecentFile(recent.getAbsolutePath()));
		ObservableList<MenuItem> menuList = recentFileMenu.getItems();
		
		MenuItem menuItem = new MenuItem();
		menuItem.setUserData(newFile);
		menuItem.setText(newFile.getFilename());
		menuItem.setOnAction(
			e->{
				loadBoard(new File(((RecentFile)((MenuItem)e.getSource()).getUserData()).getFilename()));
		});
		menuList.add(0, menuItem);
		
		if (menuList.size()>5) {
			MenuItem old = menuList.remove(menuList.size()-1);
			recentFileService.delete((RecentFile)old.getUserData());
		}
    }
    
    private boolean loadBoard(File csv) {
    	BufferedReader reader = null;
    	try {
    		reader = new BufferedReader(new FileReader(csv));
    		int[][] newBoard = new int[9][9];
    		for (int i=0; i<9; ++i) {
    			String line = reader.readLine();
    			if (line==null) {
    				throw new RuntimeException("Wrong file format.");
    			}
    			String[] tokens = line.split(",");
    			if (tokens.length!=9) {
    				throw new RuntimeException("Wrong file format.");
    			}
    			for (int j=0; j<9; ++j) {
    				tokens[j] = tokens[j].trim();
    				newBoard[i][j]= Integer.parseInt(tokens[j]);
    			}
    		}
    		board.isBoardCompleteProperty().removeListener(boardInvalidationListener);
    		board.resetTo(newBoard);
    		board.isBoardCompleteProperty().addListener(boardInvalidationListener);
    		return true;	
    	} catch (Exception e) {
    		showExceptionAlert("Exception occured while reading CSV!", e);
    	} finally {
    		if (reader!=null) {
    			try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
		return false;
    }
    
    @FXML
    private void onHintClicked(ActionEvent event) {
    	
    }
    
    @FXML
    private void onAboutClicked(ActionEvent event) {
    	System.err.println(stagesController);
    	Stage stage = stagesController.getStage(StagesController.StageId.About);
    	stage.showAndWait();
    }
    
    @FXML
    private void onResetClicked(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Reset Dialog");
    	alert.setHeaderText("Reset?");
    	alert.setContentText("You are about to reset the board. Are your sure?");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		board.reset();
    	}
    }
    
    private static void showExceptionAlert(String title, Exception e) {
    	Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Appliction Error");
		alert.setHeaderText(title);
		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.setResizable(true);
		alert.showAndWait();
    }
    
	
	private static String getCellClass(int i, int j) {
		if (i/3==1) {
			if (j/3==1) {
				return "cellDark";
			} else {
				return "cellLight";
			}
		} else {
			if (j/3==1) {
				return "cellLight";
			} else {
				return "cellDark";
			}
		}
	}
}

class BoardInvalidationListener implements InvalidationListener {
	private Board board;

	public BoardInvalidationListener(Board board) {
		this.board = board;
	}
	
	@Override
	public void invalidated(Observable observable) {
		if (board.isBoardCompleteProperty().get()) {
			Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setTitle("Congratulations!");
	    	alert.setHeaderText("You solved it!");

	    	alert.showAndWait();
		}
	}

}

class SudokuCellConvertor extends StringConverter<Number> {

	@Override
	public Number fromString(String arg0) {
		try {
			int i=Integer.parseInt(arg0);
			if (i>0 && i<10) {
				return i;
			}
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public String toString(Number arg0) {
		if (arg0.intValue()<10 && arg0.intValue()>0) {
			return arg0.toString();
		}
		return "";
	}
}


