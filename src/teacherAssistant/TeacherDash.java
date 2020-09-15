package teacherAssistant;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class TeacherDash extends JFrame {

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

	TeacherDash(dbConnection conn) {

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
		this.setTitle("User Login");

		cl = new CardLayout();
		scrnMgr = new JPanel(cl);

		JPanel tchrDash = buildTchrDash();

		JPanel clsAtndPnl = bldClsAtndPnl();
		JPanel clsAsgnmtPnl = bldClsAsgnmtPnl();
		JPanel clsGrdPnl = bldClsGrdPnl();
		JPanel clsBhvrPnl = bldClsBhvrPnl();

		scrnMgr.add(tchrDash, "Teacher Dashboard");
		scrnMgr.add(clsAtndPnl, "Attendance");
		scrnMgr.add(clsAsgnmtPnl, "Assignments");
		scrnMgr.add(clsGrdPnl, "Grades");
		scrnMgr.add(clsBhvrPnl, "Behavior");

		this.add(scrnMgr);
		this.pack();
	}

	// ------------------------------------------------------------------------ //
	// Tier 1: Teacher Dashboard Page
	// ------------------------------------------------------------------------ //

	public JPanel buildTchrDash() {
		JPanel tchrDash = new JPanel();

		JButton clsAtndBtn = new JButton("Attendance");
		JButton clsAsgnmtBtn = new JButton("Assignments");
		JButton clsGrdBtn = new JButton("Grades");
		JButton clsBhvrBtn = new JButton("Behavior");
		JButton lgOutBtn = new JButton("Log Out");

		tchrDash.add(clsAtndBtn);
		tchrDash.add(clsAsgnmtBtn);
		tchrDash.add(clsGrdBtn);
		tchrDash.add(clsBhvrBtn);
		tchrDash.add(lgOutBtn);

		clsAtndBtn.addActionListener(e -> cl.show(scrnMgr, "Attendance"));
		clsAsgnmtBtn.addActionListener(e -> cl.show(scrnMgr, "Assignments"));
		clsGrdBtn.addActionListener(e -> cl.show(scrnMgr, "Grades"));
		clsBhvrBtn.addActionListener(e -> cl.show(scrnMgr, "Behavior"));

		lgOutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				LoginWindow login = new LoginWindow();
				login.setVisible(true);
				login.setSize(900, 550);
			}
		});

		return tchrDash;
	}

	// ------------------------------------------------------------------------ //
	// Tier 2: Class Attendance Panel
	// ------------------------------------------------------------------------ //

	public JPanel bldClsAtndPnl() {
		JPanel clsAtndPnl = new JPanel();
		JButton rcrdAtndBtn = new JButton("Record Attendance");
		JButton vwAtndBtn = new JButton("View Attendance");
		JButton asgnStgBtn = new JButton("Assign Seating Charts");
		JButton backBtn = new JButton("Back");

		clsAtndPnl.add(asgnStgBtn);
		clsAtndPnl.add(rcrdAtndBtn);
		clsAtndPnl.add(vwAtndBtn);
		clsAtndPnl.add(backBtn);

		rcrdAtndBtn.addActionListener(e -> cl.show(scrnMgr, "Record Attendance"));
		vwAtndBtn.addActionListener(e -> cl.show(scrnMgr, "View Attendance"));

		asgnStgBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel asgnStgPnl = bldAsgnStgPnl(1);
				scrnMgr.add(asgnStgPnl, "Assign Seating");
				cl.show(scrnMgr, "Assign Seating");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Teacher Dashboard"));

		return clsAtndPnl;
	}

	// ------------------------------------------------------------------------ //
	// Tier 2: Class Assignments Panel
	// ------------------------------------------------------------------------ //

	public JPanel bldClsAsgnmtPnl() {
		JPanel clsAsgnmtPnl = new JPanel();
		JButton crtAsgnmtBtn = new JButton("Create New Assignment");
		JButton delAsgnmtBtn = new JButton("Delete Assignment");
		JButton vwAsgnmtBtn = new JButton("View Assignments");

		JButton backBtn = new JButton("Back");

		clsAsgnmtPnl.add(crtAsgnmtBtn);
		clsAsgnmtPnl.add(delAsgnmtBtn);
		clsAsgnmtPnl.add(vwAsgnmtBtn);
		clsAsgnmtPnl.add(backBtn);

		crtAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel crtAsgnmtPnl = bldCrtAsgnmtPnl();
				scrnMgr.add(crtAsgnmtPnl, "Create Assignment");
				cl.show(scrnMgr, "Create Assignment");
			}
		});

		delAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel delAsgnmtPnl = bldDelAsgnmtPnl();
				scrnMgr.add(delAsgnmtPnl, "Delete Assignment");
				cl.show(scrnMgr, "Delete Assignment");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Teacher Dashboard"));

		vwAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel vwAsgnmtPnl = bldVwAsgnmtPnl();
				scrnMgr.add(vwAsgnmtPnl, "View Assignment");
				cl.show(scrnMgr, "View Assignment");
			}
		});

		return clsAsgnmtPnl;
	}

	public JPanel bldClsGrdPnl() {
		JPanel clsGrdPnl = new JPanel();
		JButton backBtn = new JButton("Back");
		clsGrdPnl.add(backBtn);
		backBtn.addActionListener(e -> cl.show(scrnMgr, "Teacher Dashboard"));
		return clsGrdPnl;
	}

	public JPanel bldClsBhvrPnl() {
		JPanel clsBhvrPnl = new JPanel();
		JButton backBtn = new JButton("Back");
		clsBhvrPnl.add(backBtn);
		backBtn.addActionListener(e -> cl.show(scrnMgr, "Teacher Dashboard"));
		return clsBhvrPnl;
	}

	public JPanel bldAsgnStgPnl(int selectTbl) {
		JPanel asgnStg = new JPanel();

		// will store all table data (tblname1, tblcapacity1, tblname2, tblcapcity2,
		// etc.)
		List<Integer> tblSizeData = new ArrayList<>();

		// will contain a string-formatted list of above table data (e.g. table 1: 5
		// seats)
		List<String> tblDataList = new ArrayList<>();

		// Gets the table names and sizes from the database
		tblSizeData = conn.getClassTblSizes();

		// placeholder string to store each iteration of string format into tblDataList
		String temp_string = "";

		// k will increment in steps of 1, l will increment in 2's
		int l = 0;
		int k = 1;

		// while k < the number of tables in the array (not the number of indexes)
		while (k <= (tblSizeData.size() / 2)) {

			// i'th element is table name, i+1'th element is table's capacity
			temp_string = "Table " + tblSizeData.get(l).toString() + ": " + tblSizeData.get(l + 1).toString()
					+ " Seats";
			tblDataList.add(temp_string);

			// increments k for the number of elements in the array
			k++;
			l = l + 2;
		}

		// gets the max size of the individual table selected
		int tblMaxSize = conn.getClassTblSize(selectTbl);

		// sets focus on the menu with the list of tables
		JComboBox tblMenu = new JComboBox(tblDataList.toArray());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				tblMenu.requestFocus();
			}
		});

		// sets the scurrently elected table to the one selected in the function
		// argument
		tblMenu.setSelectedItem("Table " + selectTbl + ": " + tblMaxSize + " Seats");
		asgnStg.add(tblMenu);

		// gets list of all students and all students at the selected table
		ArrayList allStdnts = conn.getAllStdnts();
		ArrayList tblStdnts = conn.getTblStdnts(selectTbl);
		ArrayList unassignedStdnts = conn.getTblStdnts(0);

		// creates an array of labels to store n images
		JLabel[] labelList = new JLabel[tblMaxSize];
		JComboBox[] menuList = new JComboBox[tblMaxSize];

		for (int i = 0; i < tblMaxSize; i++) {

			// for each seat at the table, creates a chair icon and dropdown box
			labelList[i] = makeChair();
			JComboBox stdntMenu = new JComboBox(allStdnts.toArray());

			// sets the first field of the dropdown box to be empty
			stdntMenu.insertItemAt("", 0);

			// adds chair and dropdown box to the panel
			asgnStg.add(labelList[i]);
			asgnStg.add(stdntMenu);

			// for tables with more seats than students, sets those seats to empty
			if (tblStdnts.size() > i) {
				stdntMenu.setSelectedItem(tblStdnts.get(i));
			} else {
				stdntMenu.setSelectedIndex(0);
			}

			menuList[i] = stdntMenu;
		}

		JLabel unLabel = new JLabel("Currently Unassigned: ");
		JComboBox unassigned = new JComboBox(unassignedStdnts.toArray());
		asgnStg.add(unLabel);
		asgnStg.add(unassigned);

		// responds when user changes an option in the table menu
		tblMenu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				// gets the currently selected table from the drop down menu
				String str = tblMenu.getSelectedItem().toString();
				str = str.substring(6, 8);

				// replaces all non-digits with blanks
				// Selected table is the currently selected table name
				int selectedTbl = Integer.parseInt(str.replaceAll("[\\D]", ""));

				JPanel asgnStg = bldAsgnStgPnl(selectedTbl);
				scrnMgr.add(asgnStg, "Assign Seating");
				cl.show(scrnMgr, "Assign Seating");

			}
		});

		JButton backBtn = new JButton("Back");
		JButton submit = new JButton("Submit Changes");
		asgnStg.add(submit);
		asgnStg.add(backBtn);
		backBtn.addActionListener(e -> cl.show(scrnMgr, "Attendance"));

		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				List<String> seatList = new ArrayList();

				for (int i = 0; i < tblMaxSize; i++) {
					if (menuList[i].getSelectedItem().toString() == "") {

					} else {
						seatList.add(menuList[i].getSelectedItem().toString());
					}
				}

				conn.updateTableSeats(selectTbl, seatList);
				
				JPanel asgnStg = bldAsgnStgPnl(selectTbl);
				scrnMgr.add(asgnStg, "Assign Seating");
				cl.show(scrnMgr, "Assign Seating");

			}

		});

		return asgnStg;

	}

	public JPanel bldRcrdAtndPnl() {
		JPanel rcrdAtndPnl = new JPanel();
		return rcrdAtndPnl;
	}

	public JPanel bldVwAtndPnl() {
		JPanel vwAtndPnl = new JPanel();
		return vwAtndPnl;
	}

	public JPanel bldCrtAsgnmtPnl() {
		JPanel crtAsgnmtPnl = new JPanel();

		JLabel asgnmtIDLbl = new JLabel("Assignment ID");
		JTextField asgnmtIDFld = new JTextField("", 10);
		JButton crtAsgnmtBtn = new JButton("Create new assignment");
		JLabel asgnmtTtlLbl = new JLabel("Title");
		JTextField asgnmtTtlFld = new JTextField("", 10);
		JLabel asgnmtDtlLbl = new JLabel("Assignment Details");
		JLabel asgnmtPtLbl = new JLabel("Assignment Points");
		JTextField asgnmtPtFld = new JTextField("", 5);
		JLabel asgnmtDateLbl = new JLabel("Due Date (YYYY-MM-DD)");
		JTextField asgnmtDateFld = new JTextField("", 10);
		JButton backBtn = new JButton("Back");

		JTextArea asgnmtDtlArea = new JTextArea(5, 20);
		asgnmtDtlArea.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(asgnmtDtlArea);

		crtAsgnmtPnl.add(scrollPane);
		crtAsgnmtPnl.add(asgnmtIDLbl);
		crtAsgnmtPnl.add(asgnmtIDFld);
		crtAsgnmtPnl.add(asgnmtTtlLbl);
		crtAsgnmtPnl.add(asgnmtTtlFld);
		crtAsgnmtPnl.add(asgnmtDtlLbl);
		crtAsgnmtPnl.add(asgnmtDtlArea);
		crtAsgnmtPnl.add(asgnmtPtLbl);
		crtAsgnmtPnl.add(asgnmtPtFld);
		crtAsgnmtPnl.add(asgnmtDateLbl);
		crtAsgnmtPnl.add(asgnmtDateFld);
		crtAsgnmtPnl.add(crtAsgnmtBtn);
		crtAsgnmtPnl.add(backBtn);

		if (error_flag == 0) {
			JLabel success = new JLabel("Assignment added successfully");
			crtAsgnmtPnl.add(success);
			error_flag = -1;
		}

		else if (error_flag == 2) {
			JLabel duplicate = new JLabel("An assignment with this ID exists");
			crtAsgnmtPnl.add(duplicate);
			error_flag = -1;
		}

		else if (error_flag == 3) {
			JLabel error = new JLabel("There was an unknown error with your request");
			crtAsgnmtPnl.add(error);
			error_flag = -1;
		}

		else if (error_flag == 4) {
			JLabel frmt_err = new JLabel("An error was detected in the format of your entry.");
			JLabel frmt_err2 = new JLabel("Please double check your date format. Point/ID values must also be numeric");
			crtAsgnmtPnl.add(frmt_err);
			crtAsgnmtPnl.add(frmt_err2);
			error_flag = -1;
		}

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Assignments"));

		crtAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String id = asgnmtIDFld.getText();
				String title = asgnmtTtlFld.getText();
				String details = asgnmtDtlArea.getText();
				String points = asgnmtPtFld.getText();
				String date = asgnmtDateFld.getText();

				error_flag = conn.addAssignment(id, title, details, points, date);

				JPanel crtAsgnmtPnl = bldCrtAsgnmtPnl();
				scrnMgr.add(crtAsgnmtPnl, "Create Assignment");
				cl.show(scrnMgr, "Create Assignment");
			}
		});

		return crtAsgnmtPnl;
	}

	public JPanel bldDelAsgnmtPnl() {

		JPanel delAsgnmtPnl = new JPanel();

		JLabel delAsgnmtLbl = new JLabel("Select Assignment to Delete (cannot be undone)");
		JTextField delAsgnmtFld = new JTextField("", 10);
		JButton delAsgnmtBtn = new JButton("Delete Assignment");
		JButton backBtn = new JButton("Back");

		delAsgnmtPnl.add(delAsgnmtLbl);
		delAsgnmtPnl.add(delAsgnmtFld);
		delAsgnmtPnl.add(delAsgnmtBtn);
		delAsgnmtPnl.add(backBtn);

		if (error_flag == 0) {
			JLabel success = new JLabel("Assignment Deleted Successfully");
			delAsgnmtPnl.add(success);
		} else if (error_flag == 1) {
			JLabel duplicate = new JLabel("Assignment ID Not found");
			delAsgnmtPnl.add(duplicate);
		} else if (error_flag == 3) {
			JLabel error = new JLabel("There was an unknown error with your request");
			delAsgnmtPnl.add(error);
		}

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Assignments"));

		delAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				error_flag = conn.removeRow("assignments", delAsgnmtFld.getText());

				JPanel delAsgnmtPnl = bldDelAsgnmtPnl();
				scrnMgr.add(delAsgnmtPnl, "Delete Assignment");
				cl.show(scrnMgr, "Delete Assignment");
			}
		});

		return delAsgnmtPnl;
	}

	public JPanel bldVwAsgnmtPnl() {
		JPanel vwAsgnmtPnl = new JPanel(new GridLayout(3, 1, 2, 2));

		JButton backBtn = new JButton("Back");

		JTable table = conn.getJTable("assignments");
		JScrollPane scrollPane = new JScrollPane(table);

		JPanel pad = new JPanel();
		pad.setBorder(new EmptyBorder(10, 10, 10, 10));

		vwAsgnmtPnl.add(scrollPane);
		vwAsgnmtPnl.add(pad);
		vwAsgnmtPnl.add(backBtn);

		vwAsgnmtPnl.repaint();
		vwAsgnmtPnl.revalidate();

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Assignments"));
		return vwAsgnmtPnl;
	}

	public JLabel makeChair() {
		JLabel lbl = new JLabel();

		try {
			ImageIO.read(new File("images\\chair_rsz.png"));
			BufferedImage image = ImageIO.read(new File("images\\chair_rsz.png"));
			ImageIcon icon = new ImageIcon(image);
			lbl.setIcon(icon);
			return lbl;
		} catch (IOException e1) {
			lbl.setText("Err: Image Not Found");
			e1.printStackTrace();
			return lbl;
		}
	}

}