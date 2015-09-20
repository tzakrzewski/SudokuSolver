/**
* This application is ment to be used to solve any level of Sudoku,
* as long as it is given atleast 17 clues for a unique solution. 
* The User Interface and Sudoku Table is initialized and created here.
* 
* @author Ted Zakrzewski
* @version 1.0
* @since   2015-09-18
*/
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.ScrollPaneConstants;

public class SudokuGui{
	private final JFrame frame;
	private final JScrollPane scrollPane;
	private final JTable table;
	private final JButton solve;
	private final JButton clear;
	private final JPanel panel;
	private final Dimension screenSize;
	private int fontSize;

	public SudokuGui(){
		this.frame = new JFrame("Sudoku Solver");
		this.table = new JTable(9,9);
		this.scrollPane = new JScrollPane(table);
		this.solve = new JButton("Solve");
		this.clear = new JButton("Clear");
		this.panel = new JPanel();
		this.fontSize = 12;
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	}

	//Initializes Sudoku Table and User Interface.
	public void init(){
		table.setTableHeader(null);
		table.setRowSelectionAllowed(false);
		table.setDefaultEditor(Object.class, new SudokuValidIn(table, scrollPane)); //Checks if values entered are valid.
		table.setCellSelectionEnabled(true);
		
		for(int i=0; i<table.getColumnCount(); i++){
			table.getColumnModel().getColumn(i).setCellRenderer(new CellEdits()); //Edits for table cell: color, size, font and resizing.
		}

		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.BLACK);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		panel.add(solve);
		panel.add(clear);
		panel.setLayout(new GridLayout(1,2));

		GridBagConstraints bag = new GridBagConstraints();
		Container c = frame.getContentPane();
		c.setLayout(new GridBagLayout());
		bag.fill = GridBagConstraints.BOTH;
		bag.anchor = GridBagConstraints.FIRST_LINE_START;
		bag.weightx = 1.0;
		bag.weighty = 0.9;
		bag.gridx = 0 ;
		bag.gridy = 0;
		c.add(scrollPane, bag);
		bag.weightx = 1.0;
		bag.weighty = 0.1;
		bag.gridx = 0;
		bag.gridy = 1;
		c.add(panel, bag);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500,500);
		frame.setVisible(true);

		listeners();
	}

	//All edits for the Sudoku Table such as font, color, alignment, highlights and resizing.
	public class CellEdits extends DefaultTableCellRenderer{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
			Component cell = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setBackground(Color.WHITE);
			if(((row==0 || row==1 || row==2) && (column==3 || column==4 || column==5)) ||
				((row==3 || row==4 || row==5) && (column==0 || column==1 || column==2 || column==6 || column==7 || column==8)) ||
				((row==6 || row==7 || row==8) && (column==3 || column==4 || column==5))){
                cell.setBackground(Color.LIGHT_GRAY);
            }

            table.setRowHeight(((int)scrollPane.getHeight())/table.getRowCount()+1);

            setHorizontalAlignment(SwingConstants.CENTER);
            fontSize = 12;
            if(scrollPane.getHeight() > screenSize.getHeight()/2){
            	fontSize = (int)(.075 * scrollPane.getHeight())-20;
            }
            cell.setFont(new Font("Arial", Font.PLAIN, fontSize));

            
            if(table.isCellSelected(row, column)){
            	setBackground(new Color(178,230,250));
            }

            return cell;
		}
	}

	//Listeners for both Solve and Clear buttons, as well as numerical numbers on keypad and default.
	//This allows the selection of a cell to move to the next cell towards the right when a number is pressed.
	public void listeners(){
		solve.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(table.isEditing()){
					table.getCellEditor().stopCellEditing();
				}
				new SudokuSolve(table);
			}
		});
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(table.isEditing()){
					table.getCellEditor().cancelCellEditing();
				}
				for(int i=0; i<table.getRowCount(); i++){
					for(int j=0; j<table.getColumnCount(); j++){
						table.setValueAt(null,i,j);
					}
				}
			}
		});
		table.addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent e){ 
            }
            public void keyReleased(KeyEvent e){
                int row,col; 
                if(e.getKeyCode()==KeyEvent.VK_1 || e.getKeyCode()==KeyEvent.VK_2 || e.getKeyCode()==KeyEvent.VK_3 || e.getKeyCode()==KeyEvent.VK_4 ||
                        e.getKeyCode()==KeyEvent.VK_5 || e.getKeyCode()==KeyEvent.VK_6 || e.getKeyCode()==KeyEvent.VK_7 || e.getKeyCode()==KeyEvent.VK_8 ||
                            e.getKeyCode()==KeyEvent.VK_9 || e.getKeyCode()==KeyEvent.VK_NUMPAD1 || e.getKeyCode()==KeyEvent.VK_NUMPAD2 || e.getKeyCode()==KeyEvent.VK_NUMPAD3 || 
                                e.getKeyCode()==KeyEvent.VK_NUMPAD4 || e.getKeyCode()==KeyEvent.VK_NUMPAD5 || e.getKeyCode()==KeyEvent.VK_NUMPAD6 || e.getKeyCode()==KeyEvent.VK_NUMPAD7 || 
                                    e.getKeyCode()==KeyEvent.VK_NUMPAD8 || e.getKeyCode()==KeyEvent.VK_NUMPAD9){
                    row = table.getEditingRow();
                    col = table.getEditingColumn();
                    if(table.getEditingColumn()==table.getColumnCount()-1){
                        row++;
                        col=-1;
                        if(table.getEditingRow()==table.getRowCount()-1){
                            row=0;
                            col=-1;
                        }
                    }
                    table.editCellAt(row, col+1);
                    table.changeSelection(table.getEditingRow(),table.getEditingColumn(),false,false);
					table.removeEditor();
                }
            }
            public void keyTyped(KeyEvent e){  
            }
        });
	}
	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				SudokuGui gui = new SudokuGui();
				gui.init();
			}
		});
	}
}
