package teacherAssistant;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class StudentDash extends JFrame {

	String userName;
	CardLayout cl;
	JPanel scrnMgr;
	dbConnection conn;
	int error_flag = -1;

	// ------------------------------------------------------------------------ //
	// Main Window
	// ------------------------------------------------------------------------ //

	StudentDash(dbConnection conn, String userName) {
		this.conn = conn;
		this.userName = userName;

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

		JPanel stdntDash = bldStdntDash();

		JPanel vwAsgnmtPnl = bldVwAsgnmtPnl();
		JPanel vwGrdPnl = bldVwGrdPnl();
		JPanel vwMiscPnl = bldVwMiscPnl();

		scrnMgr.add(stdntDash, "Student Dashboard");
		scrnMgr.add(vwAsgnmtPnl, "Assignments");
		scrnMgr.add(vwGrdPnl, "Grades");
		scrnMgr.add(vwMiscPnl, "Miscellaneous");

		this.add(scrnMgr);

		this.pack();
	}

	public JPanel bldStdntDash() {
		JPanel dshbrd = new JPanel();

		JButton asgnmtBtn = new JButton("Assignments");
		JButton grdBtn = new JButton("Grades");
		JButton miscBtn = new JButton("Other");
		JButton lgOutBtn = new JButton("Log Out");

		dshbrd.add(asgnmtBtn);
		dshbrd.add(grdBtn);
		dshbrd.add(miscBtn);
		dshbrd.add(lgOutBtn);

		asgnmtBtn.addActionListener(e -> cl.show(scrnMgr, "Assignments"));
		grdBtn.addActionListener(e -> cl.show(scrnMgr, "Grades"));
		miscBtn.addActionListener(e -> cl.show(scrnMgr, "Miscellaneous"));

		lgOutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				LoginWindow login = new LoginWindow();
				login.setVisible(true);
				login.setSize(900, 550);
			}
		});

		return dshbrd;
	}

	public JPanel bldVwGrdPnl() {
		JPanel grdPnl = new JPanel();
		JButton backBtn = new JButton("Back");
		String override_query = "!select * from grades where studentID = '" + userName + "';";

		JTable grdTbl = conn.getJTable(override_query);
		
		if (grdTbl.getRowCount() == 0) {
			JTextArea noGrades = new JTextArea(20, 40);
			Border border = BorderFactory.createLineBorder(Color.gray, 1);
			noGrades.setBorder(border);
			noGrades.setEditable(false);
			noGrades.setLineWrap(true);
			noGrades.setWrapStyleWord(true);
			
			noGrades.setText("As of now, this user has no grades to display. This is most likely because "
					+ "no grades have been submitted for this user.\n\nPlease check back at another time");
			grdPnl.add(noGrades);

		} else {
			JScrollPane grdScrollPane = new JScrollPane(grdTbl);
			grdPnl.add(grdScrollPane);
		}

		grdPnl.add(backBtn);

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Student Dashboard"));
		

		return grdPnl;
	}

	public JPanel bldVwMiscPnl() {
		JPanel miscPnl = new JPanel();
		JButton backBtn = new JButton("Back");
		JLabel lbl = new JLabel();
		
		String override_query = "!select * from behavior where stdntID='" + userName + "';";
		
		JTable bhvrTbl = conn.getJTable(override_query);

		int absences = conn.getAbsences(userName);
		String absString = "You currently have " + absences + " absences.";
		lbl.setText(absString);
		
		miscPnl.add(lbl);
		
		if (bhvrTbl.getRowCount() == 0) {
			JTextArea noBhvr = new JTextArea(20, 40);
			Border border = BorderFactory.createLineBorder(Color.gray, 1);
			noBhvr.setBorder(border);
			noBhvr.setEditable(false);
			noBhvr.setLineWrap(true);
			noBhvr.setWrapStyleWord(true);
			
			noBhvr.setText("As of now, this user has no grades to display. This is most likely because "
					+ "no grades have been submitted for this user.\n\nPlease check back at another time");
			miscPnl.add(noBhvr);

		} else {
			JScrollPane bhvrScrollPane = new JScrollPane(bhvrTbl);
			miscPnl.add(bhvrScrollPane);
		}
		

		miscPnl.add(backBtn);

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Student Dashboard"));

		return miscPnl;
	}

	public JPanel bldVwAsgnmtPnl() {
		JPanel asgnmtPnl = new JPanel(new BorderLayout());

		JPanel mainPnl = new JPanel(new GridLayout(2, 2, 5, 5));

		JButton backBtn = new JButton("Back");
		JTable asgnmtTbl = conn.getJTable("Assignments");
		JScrollPane asgnmtSP = new JScrollPane(asgnmtTbl);

		JTextArea dtlArea = new JTextArea(28, 30);
		Border border = BorderFactory.createLineBorder(Color.gray, 1);
		dtlArea.setBorder(border);
		dtlArea.setText("Select an assignment to view full details");
		dtlArea.setLineWrap(true);
		dtlArea.setEditable(false);

		JPanel pad = new JPanel();
		pad.setBorder(new EmptyBorder(10, 10, 10, 10));
		JPanel pad2 = new JPanel();
		pad2.setBorder(new EmptyBorder(10, 10, 10, 10));
		JPanel pad3 = new JPanel();
		pad2.setBorder(new EmptyBorder(10, 10, 10, 10));
		JPanel pad4 = new JPanel();
		pad2.setBorder(new EmptyBorder(20, 20, 20, 20));
		JPanel pad5 = new JPanel();
		pad2.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel backBtnPnl = new JPanel(new GridLayout(3, 1, 0, 0));
		backBtnPnl.add(backBtn);
		backBtnPnl.add(pad3);
		backBtnPnl.add(pad4);

		mainPnl.add(asgnmtSP);
		mainPnl.add(dtlArea);
		mainPnl.add(pad5);
		mainPnl.add(backBtnPnl);

		asgnmtPnl.add(pad, BorderLayout.NORTH);
		asgnmtPnl.add(mainPnl, BorderLayout.CENTER);
		asgnmtPnl.add(pad2, BorderLayout.SOUTH);

		asgnmtTbl.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {

				asgnmtTbl.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						int row = asgnmtTbl.getSelectedRow();
						String selectedID = asgnmtTbl.getValueAt(row, 0).toString();

						String details = conn.getAssgnmtDtl(selectedID);
						dtlArea.setText(details);
					}
				});

				dtlArea.setVisible(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Student Dashboard"));

		return asgnmtPnl;
	}

}