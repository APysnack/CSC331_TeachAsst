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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
				JPanel asgnStgPnl = bldAsgnStgPnl();
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

	public JPanel bldAsgnStgPnl() {

		JPanel asgnStg = new JPanel();
		// foo. note to self: return to editing here //
		try {
			ImageIO.read(new File("images\\chair_rsz.png"));
			BufferedImage image = ImageIO.read(new File("images\\chair_rsz.png"));
			ImageIcon icon = new ImageIcon(image);
			JLabel label = new JLabel(icon);

			asgnStg.add(label);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JButton backBtn = new JButton("Back");
		asgnStg.add(backBtn);
		System.out.println("sanity");
		backBtn.addActionListener(e -> cl.show(scrnMgr, "Attendance"));
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

}