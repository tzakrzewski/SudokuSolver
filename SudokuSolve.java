/**
* Sudoku's table is checked to see if the table is set up correctly.
* If the table is set up right, then the table is solved.
*
* @author Ted Zakrzewski
* @version 1.0
* @since   2015-09-18
*/
import java.awt.Toolkit;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Stack;

public class SudokuSolve{
	private static final int NUMBER_OF_CLUES = 17;
	private final JTable table;
	private final Set<String> rowNums;
	private final Set<String> colNums;
	private final ArrayList<Set<String>> blockNums;
	private Set<String> solNums;
	private boolean reset;
	private Stack<BackTracking> key;

	public SudokuSolve(JTable table){
		this.table = table;
		this.rowNums = new HashSet<String>();
		this.colNums = new HashSet<String>();
		this.blockNums = new ArrayList<Set<String>>();
		this.key = new Stack<BackTracking>();
		this.reset = false;
		this.solNums = new HashSet<String>();

		blockNums.add(new HashSet<String>());
		blockNums.add(new HashSet<String>());
		blockNums.add(new HashSet<String>());

		checkAndSolve();
	}

	//Checks if Sudoku Table is valid, then solves.
	private void checkAndSolve(){
		if(clueCheck()){
			if(rowNcolumnCheck(true)){
				if(blockCheck(true)){
					solve();
				}
			}
		}
		if(reset){
			originalTable();
		}
	}

	//Checks if enough clues are inputted to table.
	private boolean clueCheck(){
		int clues=0;
		for(int i=0; i<table.getRowCount(); i++){
			for(int j=0; j<table.getColumnCount(); j++){
				if(table.getValueAt(i,j) == null){
					table.setValueAt(new String(""),i,j);
				}
				else{
					if(!table.getValueAt(i,j).equals("")){
						clues++;
					}
				}
			}
		}
		if(clues<NUMBER_OF_CLUES){
			badClues();
			reset = true;
			return false;
		}
		return true;
	}

	//Checks if clues are valid row and column wise.
	private boolean rowNcolumnCheck(boolean warn){
		for(int i=0; i<table.getRowCount(); i++){
			rowNums.clear();
			colNums.clear();
			for(int j=0; j<table.getColumnCount(); j++){
				if(!table.getValueAt(i,j).equals("")){
					if(!rowNums.add((String)table.getValueAt(i,j))){
						table.changeSelection(i, j, false, false);
						table.requestFocus();
						if(warn){badRow();
							reset = true;}
						return false;
					}
				}
				if(!table.getValueAt(j,i).equals("")){
					if(!colNums.add((String)table.getValueAt(j,i))){
						table.setCellSelectionEnabled(true);
						table.changeSelection(j, i, false, false);
						table.requestFocus();
						if(warn){badColumn();
							reset = true;}
						return false;
					}
				}
			}
		}
		return true;
	}

	//Checks if clues are valid block wise.
	private boolean blockCheck(boolean warn){
		int box;
		int boxPerRowOrColumn = 3;
		int clearBlockNums =0;
		for(int i=0; i<table.getRowCount(); i++){
			if(i%boxPerRowOrColumn==clearBlockNums){
				for(Set<String> s : blockNums){
					s.clear();
				}
			}
			for(int j=0; j<table.getColumnCount(); j++){
				box = j/boxPerRowOrColumn;
				if(!table.getValueAt(i,j).equals("")){
					if(!blockNums.get(box).add((String)table.getValueAt(i,j))){
						table.changeSelection(i, j, false, false);
						table.requestFocus();
						if(warn){badBlock();
							reset = true;}
						return false;
					}
				}
				
			}
		}
		return true;
	}

	//Key used for stack to backtrack through Sudoku's solve-brute force algorithm/
	public class BackTracking{
		private final int row, col, val;
		private final Set<String> rowTemp;
		public BackTracking(int row, int col, int val, Set<String> numRow){
			this.row = row;
			this.col = col;
			this.val = val;
			this.rowTemp = new HashSet<String>(numRow);
		}
		public int getRow(){
			return row;
		}
		public int getCol(){
			return col;
		}
		public int getVal(){
			return val;
		}
		public Set<String> getSet(){
			return rowTemp;
		}
	}

	//Where Sudoku is actually solved
	private boolean solve(){
		BackTracking backTemp;
		Set<String> setTemp;
		int num;
		int terminate = 9;
		boolean notFound;

		for(int i=0; i<table.getRowCount(); i++){
			solNums.clear();
			for(int j=0; j<table.getColumnCount(); j++){
				num = 1;
				notFound = true;
				if(!table.getValueAt(i,j).equals("")){
					solNums.add((String)table.getValueAt(i,j));
				}
				else{
					while(notFound){
						if(num<=9){

							if(solNums.add(String.valueOf(num))){
								table.setValueAt(new String(Integer.toString(num)),i,j);
								if(!(rowNcolumnCheck(false) && blockCheck(false))){
									solNums.remove(Integer.toString(num));
									table.setValueAt(new String(""),i,j);
								}
								else{

									notFound = false;
									key.push(new BackTracking(i,j,num,solNums));
								}
							}
						}
						else{
							table.setValueAt(new String(""),i,j);
							if(!key.empty()){
								backTemp = key.pop();
								i = backTemp.getRow();
								j = backTemp.getCol();
								num = backTemp.getVal();
								solNums.remove(Integer.toString(num));
							}
						}
						num++;	
					}
				}
			}
		}
		return true;
	}

	//If any errors were found in table, the original table is given back to User.
	private void originalTable(){
		for(int i=0; i<table.getRowCount(); i++){
			for(int j=0; j<table.getColumnCount(); j++){
				if(table.getValueAt(i,j).equals("")){
					table.setValueAt(null,i,j);
				}
			}
		}
	}

	//Error messages given to the User when something is found invalid.
	private void badClues(){
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(null, "Please Enter More Clues, At Least 17 Clues Are Needed For A Unique Solution!", "Clue Error", JOptionPane.PLAIN_MESSAGE);
	}
	private void badRow(){
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(null, "Highlighted Cell Has Duplicate In Same Row!", "Row Error", JOptionPane.PLAIN_MESSAGE);
	}
	private void badColumn(){
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(null, "Highlighted Cell Has Duplicate In Same Column!", "Column Error", JOptionPane.PLAIN_MESSAGE);
	}
	private void badBlock(){
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(null, "Highlighted Cell Has Duplicate In Same Block!", "Column Error", JOptionPane.PLAIN_MESSAGE);
	}
}
