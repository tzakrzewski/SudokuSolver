/**
* The cell's in Sudoku are validated from the Users input.
*
* @author Ted Zakrzewski
* @version 1.0
* @since   2015-09-18
*/
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;


import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SudokuValidIn extends DefaultCellEditor{
	private final JTable table;
	private final JScrollPane scrollPane;
	private JFormattedTextField form;
	private final Dimension screenSize;
	private int fontSize;

	public SudokuValidIn(JTable table, JScrollPane scrollPane){
		super(new JFormattedTextField());

		this.table = table;
		this.scrollPane = scrollPane;
		this.form = (JFormattedTextField)getComponent();
		this.fontSize = 12;
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		NumberFormatter numForm = new NumberFormatter(NumberFormat.getIntegerInstance());
		numForm.setFormat(NumberFormat.getIntegerInstance());
		numForm.setMinimum(new Integer(1));
		numForm.setMaximum(new Integer(9));

		form.setHorizontalAlignment(SwingConstants.CENTER);
		form.setFormatterFactory(new DefaultFormatterFactory(numForm));
		form.setFocusLostBehavior(JFormattedTextField.PERSIST);
	}

	//Overridden to change font's style and size, also allows for edits to be highlighted isntead of clicking on a cell first. 
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		JFormattedTextField textForm = (JFormattedTextField)super.getTableCellEditorComponent(table,value,isSelected,row,column);
		fontSize = 12;
        if(scrollPane.getHeight() > screenSize.getHeight()/2){
            fontSize = (int)(.075 * scrollPane.getHeight())-20;
        }
        form.setFont(new Font("Arial", Font.PLAIN, fontSize));
		textForm.selectAll();
		return textForm;
	}

	//Overridden to ensure values entered by User are acceptable, i.e. 1-9.
	@Override
	public boolean stopCellEditing(){
		form = (JFormattedTextField)getComponent();
		String s = ((String)getCellEditorValue());

		if(form.isEditValid() && !(s.contains(" ")) && !(s.contains("0"))){
			try{
				form.commitEdit();
			} catch (ParseException exc){
			}
		}
		else{
			if(s.equals("")){
				form.setValue(null);
			}
			else if(!notValidIn() || s.contains(" ")){
				return false;
			}
		}
		return super.stopCellEditing();
	}

	//Error message for faulty input.
	private boolean notValidIn(){
		Toolkit.getDefaultToolkit().beep();
		form.selectAll();
		JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(form), "Please Enter A Number Between 1-9 \n Please Don't Add Spaces", "Oops", JOptionPane.ERROR_MESSAGE);
		return false;
	}

}
