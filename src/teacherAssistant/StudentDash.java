package teacherAssistant;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class StudentDash extends JFrame {

	JButton lgnBtn, getPwBtn;
	JLabel lgnLbl;
	JTextField usrField, pwField;
	JPanel currPanel;
	CardLayout cl;
	JPanel scrnMgr;
	JPanel lgnPanel;
	dbConnection conn;
	int error_flag = -1;

	// ------------------------------------------------------------------------ //
	// Main Window
	// ------------------------------------------------------------------------ //

	StudentDash(dbConnection conn) {
		this.conn = conn;

		this.setSize(900, 550);

		// allows us to ask questions of the OS
		Toolkit tk = Toolkit.getDefaultToolkit();

		// holds width and height for screen to display window on
		Dimension dim = tk.getScreenSize();

		// dim.width returns width of screen.
		// gets relative size of screen and subtract size of window
		int xPos = (dim.width / 2) - (this.getWidth() / 2);
		int yPos = (dim.height / 2) - (this.getHeight() / 2);

		// sets the location of the window to xpos and ypos as defined above
		this.setLocation(xPos, yPos);

		// sets window to terminate process when user hits X
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// sets title of window
		this.setTitle("Student Dashboard");

		cl = new CardLayout();
		scrnMgr = new JPanel(cl);

		// code from the other dashboard to replicate
//		JPanel tchrDash = buildTchrDash();
//		
//		JPanel clsAtndPnl = bldClsAtndPnl();
//		JPanel clsAsgnmtPnl = bldClsAsgnmtPnl();
//		JPanel clsGrdPnl = bldClsGrdPnl();
//		JPanel clsBhvrPnl = bldClsBhvrPnl();
//		
//		scrnMgr.add(tchrDash, "Teacher Dashboard");
//		scrnMgr.add(clsAtndPnl, "Attendance");
//		scrnMgr.add(clsAsgnmtPnl, "Assignments");
//		scrnMgr.add(clsGrdPnl, "Grades");
//		scrnMgr.add(clsBhvrPnl, "Behavior");

		this.add(scrnMgr);

		this.pack();
	}
}