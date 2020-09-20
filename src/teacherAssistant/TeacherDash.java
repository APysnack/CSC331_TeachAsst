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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import org.jdesktop.swingx.JXDatePicker;

public class TeacherDash extends JFrame {

	String usrName;
	CardLayout cl;
	JPanel scrnMgr;
	dbConnection conn;
	int error_flag = -1;
	int targetInt = -1;
	int tblMaxSize = 0;

	// ------------------------------------------------------------------------ //
	// Main Window
	// ------------------------------------------------------------------------ //

	TeacherDash(dbConnection conn, String userName) {

		this.conn = conn;
		this.usrName = userName;

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
		this.setTitle("Teacher Dashboard");

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
		JButton mdfyTblBtn = new JButton("Modify Table");
		JButton backBtn = new JButton("Back");

		clsAtndPnl.add(asgnStgBtn);
		clsAtndPnl.add(mdfyTblBtn);
		clsAtndPnl.add(rcrdAtndBtn);
		clsAtndPnl.add(vwAtndBtn);
		clsAtndPnl.add(backBtn);

		rcrdAtndBtn.addActionListener(e -> cl.show(scrnMgr, "Record Attendance"));
		vwAtndBtn.addActionListener(e -> cl.show(scrnMgr, "View Attendance"));

		asgnStgBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel asgnStgPnl = bldAsgnStgPnl();
				scrnMgr.add(asgnStgPnl, "Assign Seating");
				cl.show(scrnMgr, "Assign Seating");
			}
		});

		mdfyTblBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel mdfyTblPnl = bldMdfyTblPnl();
				scrnMgr.add(mdfyTblPnl, "Modify Table");
				cl.show(scrnMgr, "Modify Table");
			}
		});

		rcrdAtndBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel rcrdAtndPnl = bldRcrdAtndPnl();
				scrnMgr.add(rcrdAtndPnl, "Record Attendance");
				cl.show(scrnMgr, "Record Attendance");
			}
		});

		vwAtndBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel vwAtndPnl = bldVwAtndPnl();
				scrnMgr.add(vwAtndPnl, "View Attendance");
				cl.show(scrnMgr, "View Attendance");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Teacher Dashboard"));

		return clsAtndPnl;
	}

	public JPanel bldRcrdAtndPnl() {
		JPanel rcrdAtndPnl = new JPanel();
		JButton sbmtBtn = new JButton("Submit");
		JButton backBtn = new JButton("Back");
		JLabel lbl = new JLabel("Is the student present? Check if yes.");
		JXDatePicker picker = new JXDatePicker();
		picker.setDate(Calendar.getInstance().getTime());
		picker.setFormats(new SimpleDateFormat("MM.dd.YYYY"));
		picker.getUI();

		ArrayList allStdnts = conn.getAllNames("Students");
		JPanel stndtPnl = new JPanel();
		List<JCheckBox> chkBoxList = new ArrayList<>();
		ButtonGroup grp = new ButtonGroup();

		rcrdAtndPnl.add(picker);

		for (int i = 0; i < allStdnts.size(); i++) {
			JCheckBox chkBox = new JCheckBox(allStdnts.get(i).toString());
			chkBoxList.add(chkBox);
			rcrdAtndPnl.add(chkBoxList.get(i));
		}

		rcrdAtndPnl.add(sbmtBtn);
		rcrdAtndPnl.add(backBtn);

		sbmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String selectedDate = dateFormat.format(picker.getDate());

				String name = "";
				boolean is_present = false;

				for (int i = 0; i < chkBoxList.size(); i++) {
					name = chkBoxList.get(i).getText();
					is_present = chkBoxList.get(i).isSelected();
					conn.recordAttendance(name, selectedDate, is_present);
					error_flag = -1;
				}

				repaint();
				revalidate();
				JPanel rcrdAtndPnl = bldRcrdAtndPnl();
				scrnMgr.add(rcrdAtndPnl, "Record Attendance");
				cl.show(scrnMgr, "Record Attendance");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Attendance"));

		return rcrdAtndPnl;
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
		clsAsgnmtPnl.add(backBtn);

		crtAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel crtAsgnmtPnl = bldCrtAsgnmtPnl();
				scrnMgr.add(crtAsgnmtPnl, "Create Assignment");
				cl.show(scrnMgr, "Create Assignment");
			}
		});

		delAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel delAsgnmtPnl = bldDelAsgnmtPnl();
				scrnMgr.add(delAsgnmtPnl, "Delete Assignment");
				cl.show(scrnMgr, "Delete Assignment");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Teacher Dashboard"));

		return clsAsgnmtPnl;
	}

	public JPanel bldClsGrdPnl() {
		JPanel clsGrdPnl = new JPanel();
		JButton addGrdBtn = new JButton("Add Grade");
		JButton edtGrdBtn = new JButton("Edit Grade");
		JButton stdntGrdBtn = new JButton("Student Grades");
		JButton backBtn = new JButton("Back");
		clsGrdPnl.add(addGrdBtn);
		clsGrdPnl.add(edtGrdBtn);
		clsGrdPnl.add(stdntGrdBtn);
		clsGrdPnl.add(backBtn);

		addGrdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel addGrdPnl = bldAddGrdPnl("");
				scrnMgr.add(addGrdPnl, "Add Grade");
				cl.show(scrnMgr, "Add Grade");
			}
		});

		edtGrdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel edtGrdPnl = bldEdtGrdPnl();
				scrnMgr.add(edtGrdPnl, "Edit Grade");
				cl.show(scrnMgr, "Edit Grade");
			}
		});

		stdntGrdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
				revalidate();
				JPanel stdntGrdPnl = bldStdntGrdPnl();
				scrnMgr.add(stdntGrdPnl, "Student Grades");
				cl.show(scrnMgr, "Student Grades");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Teacher Dashboard"));
		return clsGrdPnl;
	}

	public JPanel bldAddGrdPnl(String selAsgnmt) {
		JPanel addGrdPnl = new JPanel();
		JButton sbmtGrdBtn = new JButton("Submit Grade");
		JButton backBtn = new JButton("Back");
		JLabel grdLbl = new JLabel("Grade For This Assignment");
		JTextField grdTxtFld = new JTextField("", 10);

		ArrayList asgnmtLst = conn.getAllAsgnmts();
		List<String> asgnmtDataLst = new ArrayList<>();

		ArrayList allStdnts = conn.getAllNames("students");
		JComboBox stdntMenu = new JComboBox(allStdnts.toArray());

		JLabel lbl = new JLabel();

		if (error_flag == 4) {
			lbl.setText("ERROR: Grades may only contain numbers");
			error_flag = -1;
		} else if (error_flag == -2) {
			lbl.setText("Grades should have a maximum of 6 digits and no more than 2 decimal places");
			error_flag = -1;
		} else {
			lbl = check_errors("Grade");
		}

		String temp_string = "";

		int l = 0;
		int k = 1;

		while (k <= (asgnmtLst.size() / 2)) {

			temp_string = "Assignment Number " + asgnmtLst.get(l).toString() + ": " + asgnmtLst.get(l + 1).toString();
			asgnmtDataLst.add(temp_string);

			// increments k for the number of elements in the array
			k++;
			l = l + 2;
		}

		JComboBox asgnmtMenu = new JComboBox(asgnmtDataLst.toArray());

		if (selAsgnmt == "") {
			asgnmtMenu.setSelectedIndex(0);
		} else {
			asgnmtMenu.setSelectedItem(selAsgnmt);
		}

		String override_query = "!select grades.assignmentID, grades.assignmentTitle, grades.studentID, grades.grade, "
				+ "assignments.points from grades inner join assignments on assignments.id = grades.assignmentID where"
				+ " assignments.teacherID='" + usrName + "';";

		JTable table = conn.getJTable(override_query);
		JScrollPane scrollPane = new JScrollPane(table);

		JPanel pad = new JPanel();
		pad.setBorder(new EmptyBorder(10, 10, 10, 10));

		addGrdPnl.add(asgnmtMenu);
		addGrdPnl.add(stdntMenu);
		addGrdPnl.add(grdLbl);
		addGrdPnl.add(grdTxtFld);
		addGrdPnl.add(sbmtGrdBtn);
		addGrdPnl.add(lbl);
		addGrdPnl.add(scrollPane);
		addGrdPnl.add(backBtn);

		sbmtGrdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tempString = asgnmtMenu.getSelectedItem().toString();

				int i = 0;
				int assignmentID = 0;
				String num = "";
				String assignmentTitle = "";

				// extracts assignment title and number from tempString (formatted as Assignment
				// Number X: AsgnmtTitle)
				while (i < tempString.length()) {

					// extracts the title following the colon
					if (tempString.charAt(i) == ':') {
						assignmentTitle = tempString.substring((i + 2), tempString.length());
						break;
					}

					// gets the id number
					if (Character.isDigit(tempString.charAt(i))) {
						while (Character.isDigit(tempString.charAt(i))) {
							num = num + tempString.charAt(i);
							i++;
						}
						continue;
					}

					i++;
				}

				assignmentID = Integer.parseInt(num);

				String stdntName = stdntMenu.getSelectedItem().toString();

				if (grdTxtFld.getText().matches("^[0-9]*\\.?[0-9]+$") && grdTxtFld.getText().length() > 0) {
					double stdntGrade = Double.parseDouble(grdTxtFld.getText());

					error_flag = conn.addGrades(assignmentID, assignmentTitle, stdntName, stdntGrade, "Add");

				} else {
					error_flag = 4;

				}

				String itemSelect = asgnmtMenu.getSelectedItem().toString();

				repaint();
				revalidate();
				JPanel addGrdPnl = bldAddGrdPnl(itemSelect);
				scrnMgr.add(addGrdPnl, "Add Grade");
				cl.show(scrnMgr, "Add Grade");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Grades"));
		return addGrdPnl;
	}

	public JPanel bldEdtGrdPnl() {
		JPanel edtGrdPnl = new JPanel();
		JButton sbmtGrdBtn = new JButton("Submit Grade");
		JButton backBtn = new JButton("Back");
		JLabel grdLbl = new JLabel("Grade For This Assignment");
		JTextField grdTxtFld = new JTextField("", 10);

		ArrayList asgnmtLst = conn.getAllAsgnmts();
		List<String> asgnmtDataLst = new ArrayList<>();

		ArrayList allStdnts = conn.getAllNames("Students");
		JComboBox stdntMenu = new JComboBox(allStdnts.toArray());

		JLabel lbl = new JLabel();

		if (error_flag == 4) {
			lbl.setText("ERROR: Grades may only contain numbers");
			error_flag = -1;
		} else {
			lbl = check_errors("Grade");
		}

		String temp_string = "";

		int l = 0;
		int k = 1;

		while (k <= (asgnmtLst.size() / 2)) {

			temp_string = "Assignment Number " + asgnmtLst.get(l).toString() + ": " + asgnmtLst.get(l + 1).toString();
			asgnmtDataLst.add(temp_string);

			// increments k for the number of elements in the array
			k++;
			l = l + 2;
		}

		JComboBox asgnmtMenu = new JComboBox(asgnmtDataLst.toArray());

		String override_query = "!select grades.assignmentID, grades.assignmentTitle, grades.studentID, grades.grade, "
				+ "assignments.points from grades inner join assignments on assignments.id = grades.assignmentID "
				+ "where grades.teacherID='" + usrName + "';";

		JTable table = conn.getJTable(override_query);
		JScrollPane scrollPane = new JScrollPane(table);

		JPanel pad = new JPanel();
		pad.setBorder(new EmptyBorder(10, 10, 10, 10));

		edtGrdPnl.add(asgnmtMenu);
		edtGrdPnl.add(stdntMenu);
		edtGrdPnl.add(grdLbl);
		edtGrdPnl.add(grdTxtFld);
		edtGrdPnl.add(sbmtGrdBtn);
		edtGrdPnl.add(lbl);
		edtGrdPnl.add(scrollPane);
		edtGrdPnl.add(backBtn);

		sbmtGrdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String tempString = asgnmtMenu.getSelectedItem().toString();

				int i = 0;
				int assignmentID = 0;
				String num = "";
				String assignmentTitle = "";

				while (i < tempString.length()) {
					if (tempString.charAt(i) == ':') {
						assignmentTitle = tempString.substring((i + 2), tempString.length());
						break;
					}

					if (Character.isDigit(tempString.charAt(i))) {
						while (Character.isDigit(tempString.charAt(i))) {
							num = num + tempString.charAt(i);
							i++;
						}
						continue;
					}

					i++;
				}

				assignmentID = Integer.parseInt(num);

				String stdntName = stdntMenu.getSelectedItem().toString();

				if (grdTxtFld.getText().matches("^[0-9]*\\.?[0-9]+$") && grdTxtFld.getText().length() > 0) {
					double stdntGrade = Double.parseDouble(grdTxtFld.getText());
					error_flag = conn.addGrades(assignmentID, assignmentTitle, stdntName, stdntGrade, "edit");
				} else {
					error_flag = 4;

				}

				repaint();
				revalidate();
				JPanel edtGrdPnl = bldEdtGrdPnl();
				scrnMgr.add(edtGrdPnl, "Edit Grade");
				cl.show(scrnMgr, "Edit Grade");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Grades"));
		return edtGrdPnl;
	}

	public JPanel bldStdntGrdPnl() {
		JPanel stdntGrdPnl = new JPanel();

		String override_query = "!select ID, tableID, grade from students where teacherID='" + usrName + "';";

		JTable table = conn.getJTable(override_query);
		JScrollPane dataScrollPane = new JScrollPane(table);

		stdntGrdPnl.add(dataScrollPane);

		JButton backBtn = new JButton("Back");
		stdntGrdPnl.add(backBtn);
		backBtn.addActionListener(e -> cl.show(scrnMgr, "Grades"));
		return stdntGrdPnl;
	}

	public JPanel bldClsBhvrPnl() {
		JPanel clsBhvrPnl = new JPanel();

		ArrayList allStdnts = conn.getAllNames("Students");
		ArrayList bhvrEnums = conn.getEnumFields("behavior", "bhvrtype");

		JComboBox stdntMenu = new JComboBox(allStdnts.toArray());
		JComboBox bhvrTypes = new JComboBox(bhvrEnums.toArray());

		JButton sbmtBtn = new JButton("Submit");
		JButton backBtn = new JButton("Back");

		JTextArea comments = new JTextArea(5, 30);
		comments.setEditable(true);
		comments.setLineWrap(true);
		comments.setWrapStyleWord(true);
		JScrollPane commentPane = new JScrollPane(comments);

		JXDatePicker picker = new JXDatePicker();
		picker.setDate(Calendar.getInstance().getTime());
		picker.setFormats(new SimpleDateFormat("MM.dd.yyyy"));
		picker.getUI();

		JTable table = conn.getJTable("Behavior");
		table.setVisible(true);
		JScrollPane dataScrollPane = new JScrollPane(table);

		clsBhvrPnl.add(picker);
		clsBhvrPnl.add(stdntMenu);
		clsBhvrPnl.add(bhvrTypes);
		clsBhvrPnl.add(commentPane);
		clsBhvrPnl.add(sbmtBtn);
		clsBhvrPnl.add(dataScrollPane);
		clsBhvrPnl.add(backBtn);

		sbmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String selectedDate = dateFormat.format(picker.getDate());
				String bhvrType = bhvrTypes.getSelectedItem().toString();
				String stdntName = stdntMenu.getSelectedItem().toString();
				String feedback = comments.getText();

				comments.setText("");

				error_flag = conn.logBehavior(stdntName, selectedDate, bhvrType, feedback);

				repaint();
				revalidate();
				JPanel clsBhvrPnl = bldClsBhvrPnl();
				scrnMgr.add(clsBhvrPnl, "Behavior");
				cl.show(scrnMgr, "Behavior");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Teacher Dashboard"));
		return clsBhvrPnl;
	}

	public JPanel bldAsgnStgPnl() {
		JPanel asgnStg = new JPanel();

		tblMaxSize = 0;

		// will store all table data (tblname1, tblcapacity1, tblname2, tblcapacity2,
		// etc.)
		List<Integer> tblSizeData = new ArrayList<>();

		// Gets the table names and sizes relevant to the current teacher from the
		// database
		tblSizeData = conn.getClassTblSizes();

		if (targetInt == -1) {
			if (tblSizeData.isEmpty()) {
				tblMaxSize = 0;
			}
			else {
				targetInt = tblSizeData.get(0);
				tblMaxSize = conn.getClassTblSize(targetInt);
			}
		} else {
			tblMaxSize = conn.getClassTblSize(targetInt);
		}
		// foo 
		// initializes table data into string format Table X: Y Seats
		List<String> tblDataList = initializeTblSel(tblSizeData);

		// sets focus on the menu with the list of tables
		JComboBox tblMenu = new JComboBox(tblDataList.toArray());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				tblMenu.requestFocus();
			}
		});

		// sets the currently selected table to the one selected in the function
		// argument
		tblMenu.setSelectedItem("Table " + targetInt + ": " + tblMaxSize + " Seats");
		asgnStg.add(tblMenu);

		// gets list of all students and all students at the selected table
		ArrayList allStdnts = conn.getAllNames("Students");
		ArrayList tblStdnts = conn.getTblStdnts(targetInt);
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
		JButton randomize = new JButton("Randomize Seats");
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
				targetInt = Integer.parseInt(str.replaceAll("[\\D]", ""));

				repaint();
				revalidate();
				JPanel asgnStg = bldAsgnStgPnl();
				scrnMgr.add(asgnStg, "Assign Seating");
				cl.show(scrnMgr, "Assign Seating");

			}
		});

		JButton backBtn = new JButton("Back");
		JButton submit = new JButton("Submit Changes");
		asgnStg.add(submit);
		asgnStg.add(randomize);
		asgnStg.add(backBtn);

		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				targetInt = -1;
				repaint();
				revalidate();
				cl.show(scrnMgr, "Attendance");
			}
		});

		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				List<String> seatList = new ArrayList();

				for (int i = 0; i < tblMaxSize; i++) {
					if (menuList[i].getSelectedItem().toString() == "") {

					} else {
						seatList.add(menuList[i].getSelectedItem().toString());
					}
				}

				conn.updateTableSeats(targetInt, seatList);

				targetInt = -1;
				repaint();
				revalidate();
				JPanel asgnStg = bldAsgnStgPnl();
				scrnMgr.add(asgnStg, "Assign Seating");
				cl.show(scrnMgr, "Assign Seating");

			}

		});

		randomize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				conn.randomizeTables(usrName);

				repaint();
				revalidate();
				JPanel asgnStg = bldAsgnStgPnl();
				scrnMgr.add(asgnStg, "Assign Seating");
				cl.show(scrnMgr, "Assign Seating");
			}
		});

		return asgnStg;

	}

	public JPanel bldMdfyTblPnl() {
		JPanel mdfyTblPnl = new JPanel();

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

		JComboBox tblMenu = new JComboBox(tblDataList.toArray());

		JLabel addTblLbl = new JLabel("Add New Table");
		JLabel tblNameLbl = new JLabel("New Table ID");
		JTextField tblNameTxtFld = new JTextField("", 15);
		JLabel tblCapacityLbl = new JLabel("Table Max Capacity");
		JTextField tblCapacityTxtFld = new JTextField("", 15);
		JButton addTblBtn = new JButton("Add Table");
		JLabel delTblLbl = new JLabel("Delete Table (Cannot be Undone)");
		JButton delTblBtn = new JButton("Delete Table");
		JButton backBtn = new JButton("Back");

		mdfyTblPnl.add(addTblLbl);
		mdfyTblPnl.add(tblNameLbl);
		mdfyTblPnl.add(tblNameTxtFld);
		mdfyTblPnl.add(tblCapacityLbl);
		mdfyTblPnl.add(tblCapacityTxtFld);
		mdfyTblPnl.add(addTblBtn);
		mdfyTblPnl.add(delTblLbl);
		mdfyTblPnl.add(tblMenu);
		mdfyTblPnl.add(delTblBtn);
		mdfyTblPnl.add(backBtn);

		JLabel lbl = new JLabel();

		if (error_flag == 4) {
			lbl.setText("ERROR: Table Names and Capacities can only use integers");
			error_flag = -1;
		} else {
			lbl = check_errors("table");
		}

		mdfyTblPnl.add(lbl);

		addTblBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String tblID = tblNameTxtFld.getText();
				String tblCapacity = tblCapacityTxtFld.getText();

				if (tblID.matches("[0-9]+") && tblID.length() > 0 && tblCapacity.matches("[0-9]+")
						&& tblCapacity.length() > 0) {
					int tblIDNum = Integer.parseInt(tblID);
					int tblCapacityNum = Integer.parseInt(tblCapacity);
					error_flag = conn.addClsTbl(tblIDNum, tblCapacityNum);

				} else {
					error_flag = 4;
				}

				repaint();
				revalidate();
				JPanel mdfyTblPnl = bldMdfyTblPnl();
				scrnMgr.add(mdfyTblPnl, "Modify Table");
				cl.show(scrnMgr, "Modify Table");

			}
		});

		delTblBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String str = tblMenu.getSelectedItem().toString();
				str = str.substring(6, 8);

				// replaces all non-digits with blanks
				// Selected table is the currently selected table name
				int selectedTbl = Integer.parseInt(str.replaceAll("[\\D]", ""));

				error_flag = conn.delClsTbl(selectedTbl);

				if (error_flag == 0) {
					error_flag = 5;
				}

				repaint();
				revalidate();
				JPanel mdfyTblPnl = bldMdfyTblPnl();
				scrnMgr.add(mdfyTblPnl, "Modify Table");
				cl.show(scrnMgr, "Modify Table");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Attendance"));

		return mdfyTblPnl;
	}

	public JPanel bldVwAtndPnl() {
		JPanel vwAtndPnl = new JPanel();
		JButton backBtn = new JButton("Back");

		String override_query = "!select students.id, attendance.clsDate, attendance.isPresent, "
				+ "students.absences from students inner join attendance on attendance.stdntID = students.id where"
				+ " attendance.teacherID = '" + usrName + "';";

		JTable table = conn.getJTable(override_query);
		JScrollPane scrollPane = new JScrollPane(table);

		vwAtndPnl.add(scrollPane);
		vwAtndPnl.add(backBtn);

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Attendance"));

		return vwAtndPnl;
	}

	public JPanel bldCrtAsgnmtPnl() {
		JPanel crtAsgnmtPnl = new JPanel(new BorderLayout());

		JLabel asgnmtIDLbl = new JLabel("Assignment ID");
		JTextField asgnmtIDFld = new JTextField("", 2);
		JButton crtAsgnmtBtn = new JButton("Create Assignment");
		JLabel asgnmtTtlLbl = new JLabel("Title");
		JTextField asgnmtTtlFld = new JTextField("", 7);
		JLabel asgnmtPtLbl = new JLabel("Assignment Points");
		JTextField asgnmtPtFld = new JTextField("", 3);

		JXDatePicker picker = new JXDatePicker();
		picker.setDate(Calendar.getInstance().getTime());
		picker.setFormats(new SimpleDateFormat("MM.dd.yyyy"));
		picker.getUI();

		JButton backBtn = new JButton("Back");

		JPanel dtlPanl = new JPanel(new GridLayout(2, 1, 0, 0));

		JLabel asgnmtDtlLbl = new JLabel("Assignment Details");
		JTextArea asgnmtDtlArea = new JTextArea(15, 20);
		asgnmtDtlArea.setEditable(true);
		asgnmtDtlArea.setLineWrap(true);
		asgnmtDtlArea.setWrapStyleWord(true);
		JScrollPane dtlScrollPane = new JScrollPane(asgnmtDtlArea);

		JPanel pad = new JPanel();
		pad.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel pad2 = new JPanel();
		pad.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel pad3 = new JPanel();
		pad.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel pad4 = new JPanel();
		pad.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel pad5 = new JPanel();
		pad.setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel backBtnComposite = new JPanel(new GridLayout(1, 5, 0, 0));
		backBtnComposite.add(pad);
		backBtnComposite.add(pad2);
		backBtnComposite.add(backBtn);
		backBtnComposite.add(pad3);
		backBtnComposite.add(pad4);

		JPanel txtAreaComposite = new JPanel(new BorderLayout(0, 7));

		txtAreaComposite.add(asgnmtDtlLbl, BorderLayout.NORTH);
		txtAreaComposite.add(dtlScrollPane, BorderLayout.CENTER);
		txtAreaComposite.add(backBtnComposite, BorderLayout.SOUTH);

		dtlPanl.add(txtAreaComposite);
		dtlPanl.add(pad5);

		JPanel middleComposite = new JPanel(new GridLayout(1, 2, 5, 5));

		JTable table = conn.getJTable("Assignments");
		JScrollPane dataScrollPane = new JScrollPane(table);
		middleComposite.add(dataScrollPane);
		middleComposite.add(dtlPanl);

		JPanel compositePnl = new JPanel();

		compositePnl.add(asgnmtIDLbl);
		compositePnl.add(asgnmtIDFld);
		compositePnl.add(asgnmtTtlLbl);
		compositePnl.add(asgnmtTtlFld);
		compositePnl.add(asgnmtPtLbl);
		compositePnl.add(asgnmtPtFld);
		compositePnl.add(picker);
		compositePnl.add(crtAsgnmtBtn);

		crtAsgnmtPnl.add(compositePnl, BorderLayout.NORTH);
		crtAsgnmtPnl.add(middleComposite, BorderLayout.CENTER);

		JLabel lbl = new JLabel();

		if (error_flag == 4) {
			JLabel frmt_err = new JLabel("ERROR: An error was detected in the format of your entry.");
			JLabel frmt_err2 = new JLabel("Please double check your date format. Point/ID values must also be numeric");
			crtAsgnmtPnl.add(frmt_err);
			crtAsgnmtPnl.add(frmt_err2);
			error_flag = -1;
		} else {
			lbl = check_errors("Assignment");
		}

		compositePnl.add(lbl);

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Assignments"));

		crtAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String id = asgnmtIDFld.getText();
				String title = asgnmtTtlFld.getText();
				String details = asgnmtDtlArea.getText();
				String points = asgnmtPtFld.getText();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String selectedDate = dateFormat.format(picker.getDate());

				error_flag = conn.addAssignment(id, title, details, points, selectedDate);

				repaint();
				revalidate();
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

		JTable table = conn.getJTable("Assignments");
		JScrollPane scrollPane = new JScrollPane(table);

		delAsgnmtPnl.add(delAsgnmtLbl);
		delAsgnmtPnl.add(delAsgnmtFld);
		delAsgnmtPnl.add(delAsgnmtBtn);
		delAsgnmtPnl.add(backBtn);
		delAsgnmtPnl.add(scrollPane);

		if (error_flag == 4) {
			JLabel frmt_err = new JLabel("ERROR: An error was detected in the format of your entry.");
			JLabel frmt_err2 = new JLabel("ID Must be a Positive Integer");
			delAsgnmtPnl.add(frmt_err);
			delAsgnmtPnl.add(frmt_err2);
			error_flag = -1;
		} else {
			JLabel lbl = check_errors("Assignment");
			delAsgnmtPnl.add(lbl);
		}

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Assignments"));

		delAsgnmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				String asgnmtID = delAsgnmtFld.getText();

				if (asgnmtID.matches("[0-9]+") && asgnmtID.length() > 0) {
					error_flag = conn.removeRow("assignments", asgnmtID);
				} else {
					error_flag = 4;
				}

				repaint();
				revalidate();
				JPanel delAsgnmtPnl = bldDelAsgnmtPnl();
				scrnMgr.add(delAsgnmtPnl, "Delete Assignment");
				cl.show(scrnMgr, "Delete Assignment");
			}
		});

		return delAsgnmtPnl;
	}

	public List<String> initializeTblSel(List<Integer> tblSizeData) {
		// will contain a string-formatted list of above table data (e.g. table 1: 5
		// seats)
		List<String> tblDataList = new ArrayList<>();

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

		return tblDataList;
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

	public JLabel check_errors(String vrbl) {
		JLabel msg = new JLabel();

		if (error_flag == 0) {
			msg.setText(vrbl + " added successfully");
		}

		else if (error_flag == 1) {
			msg.setText("ERROR: " + vrbl + " ID not found");
		}

		else if (error_flag == 2) {
			msg.setText("ERROR: " + vrbl + " is a duplicate of another " + vrbl);
		}

		else if (error_flag == 3) {
			msg.setText("ERROR: " + "There was an unknown error with your request");
		}

		else if (error_flag == 5) {
			msg.setText(vrbl + " deleted successfully");
		}

		else if (error_flag == 6) {
			msg.setText(vrbl + " updated successfully");
		}

		else {
			msg.setText("");
		}

		error_flag = -1;

		return msg;
	}

}