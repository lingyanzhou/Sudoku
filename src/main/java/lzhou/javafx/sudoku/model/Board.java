package lzhou.javafx.sudoku.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;

@Component
@Scope("prototype")
public class Board {
	
	private SimpleIntegerProperty[][] data;
    private SimpleIntegerProperty[][] initialData;
	private BooleanBinding isBoardComplete;
    
    public Board() {
    	initialData= new SimpleIntegerProperty[9][9];
    	for (int i=0; i<9; ++i) {
    		for (int j=0; j<9; ++j) {
    			initialData[i][j] = new SimpleIntegerProperty(0);
    	  	}
    	}
    	data = new SimpleIntegerProperty[9][9];
    	for (int i=0; i<9; ++i) {
    		for (int j=0; j<9; ++j) {
    			data[i][j] = new SimpleIntegerProperty(0);
    	  	}
    	}
    	isBoardComplete = new IsBoardCompleteBinding(data);
    }
    
    public void resetTo(int[][] initialData) {
    	assert initialData.length==9 && initialData[0].length==9;
    	for (int i=0; i<9; ++i) {
    		for (int j=0; j<9; ++j) {
    			this.initialData[i][j].set(initialData[i][j]);
        	}
    	}
    	reset();
    }
    
    public void reset() {
    	for (int i=0; i<9; ++i) {
    		for (int j=0; j<9; ++j) {
    			data[i][j].set(initialData[i][j].get());
    	  	}
    	}
    }
    
    public SimpleIntegerProperty get(int i, int j) {
    	assert i>=0 && i<9 && j>=0 && j<0;
    	return data[i][j];
    }
    
    public BooleanExpression getIsEditable(int i, int j) {
    	assert i>=0 && i<9 && j>=0 && j<0;
    	return initialData[i][j].greaterThan(0);
    }
    
    public BooleanBinding isBoardCompleteProperty() {
    	return isBoardComplete;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for (int i=0; i<9; ++i) {
    		for (int j=0; j<9; ++j) {
    			sb.append(data[i][j].get());
    			if (j<8) {
    				sb.append(',');
    			}
    		}
    		sb.append('\n');
    	}
    	return sb.toString();
    }
}

class IsBoardCompleteBinding extends BooleanBinding {
	private ObservableIntegerValue[][] board;
	private static int COMPLETE_VAL=1022;
	
	public IsBoardCompleteBinding(ObservableIntegerValue[][] board) {
    	for (int i=0; i<9; ++i) {
    		for (int j=0; j<9; ++j) {
    			this.bind(board[i][j]);
    		}
    	}
    	this.board = board;
	}
	
	@Override
	protected boolean computeValue() {
		int tmp=0;
		for (int i=0; i<9; ++i) {
			tmp=0;
    		for (int j=0; j<9; ++j) {
    			if (board[i][j].get()>0 && board[i][j].get()<10) {
    				tmp|=(1<<board[i][j].get());
    			} else {
    				return false;
    			}
    		}
    		if (tmp!=COMPLETE_VAL) {
    			return false;
    		}
    	}
		for (int i=0; i<9; ++i) {
			tmp=0;
    		for (int j=0; j<9; ++j) {
    			if (board[j][i].get()>0 && board[j][i].get()<10) {
    				tmp|=(1<<board[j][i].get());
    			} else {
    				return false;
    			}
    		}
    		if (tmp!=COMPLETE_VAL) {
    			return false;
    		}
    	}
		for (int ii=0; ii<9; ii+=3) {
    		for (int jj=0; jj<9; jj+=3) {
    			tmp=0;
    			for (int i=0; i<3; i+=1) {
    	    		for (int j=0; j<3; j+=1) {
		    			if (board[i+ii][j+jj].get()>0 && board[i+ii][j+jj].get()<10) {
		    				tmp|=(1<<board[i+ii][j+jj].get());
		    			} else {
		    				return false;
		    			}
    	    		}
    			}
        		if (tmp!=COMPLETE_VAL) {
        			return false;
        		}
    		}
    	}
		return true;
	}
	
}