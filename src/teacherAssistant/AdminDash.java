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
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AdminDash extends JFrame {

	String[] usrTypes = { "Student", "Teacher", "Admin" };
	String[] subjectTypes = { "Alchemy", "Apparition", "Astronomy", "Care of Magical Creatures", "Charms",
			"Defense Against the Dark Arts", "Divination", "Flying", "General Studies", "Herbology", "History of Magic",
			"Muggle Studies", "Potions", "Transfiguration" };
	
	String userName;
	int error_flag = -1;
	JLabel lbl;
	CardLayout cl;
	JPanel scrnMgr;
	dbConnection conn;

	// ------------------------------------------------------------------------ //
	// Main Window
	// ------------------------------------------------------------------------ //

	AdminDash(dbConnection conn, String userName) {
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
		this.setTitle("Administrator Dashboard");

		cl = new CardLayout();
		scrnMgr = new JPanel(cl);

		JPanel adminDash = bldAdminDash();

		JPanel addUsrPnl = bldAddUsrPnl("Student");
		JPanel editUsrPnl = bldEditUsrPnl("Student");
		JPanel delUsrPnl = bldDelUsrPnl();
		JPanel vwUsrPnl = bldVwUsrPnl();

		scrnMgr.add(adminDash, "Admin Dashboard");
		scrnMgr.add(addUsrPnl, "Add User");
		scrnMgr.add(editUsrPnl, "Edit User");
		scrnMgr.add(delUsrPnl, "Delete User");
		scrnMgr.add(vwUsrPnl, "View User");

		this.add(scrnMgr);
		this.pack();
	}

	public JPanel bldAdminDash() {
		JPanel adminDash = new JPanel();

		JButton addUsrBtn = new JButton("Add User");
		JButton editUsrBtn = new JButton("Edit User");
		JButton delUsrBtn = new JButton("Delete User");
		JButton vwUsrBtn = new JButton("View User");
		JButton lgOutBtn = new JButton("Log Out");

		adminDash.add(addUsrBtn);
		adminDash.add(editUsrBtn);
		adminDash.add(delUsrBtn);
		adminDash.add(vwUsrBtn);
		adminDash.add(lgOutBtn);

		addUsrBtn.addActionListener(e -> cl.show(scrnMgr, "Add User"));
		editUsrBtn.addActionListener(e -> cl.show(scrnMgr, "Edit User"));
		delUsrBtn.addActionListener(e -> cl.show(scrnMgr, "Delete User"));

		lgOutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				LoginWindow login = new LoginWindow();
				login.setVisible(true);
				login.setSize(900, 550);
			}
		});

		vwUsrBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel vwUsrPnl = bldVwUsrPnl();
				scrnMgr.add(vwUsrPnl, "View User");
				cl.show(scrnMgr, "View User");
			}
		});

		return adminDash;
	}

	public JPanel bldAddUsrPnl(String selectedUser) {
		JPanel addUsrPnl = new JPanel();
		ArrayList tchrArray = conn.getAllNames("teachers");
		JComboBox tchrComboBox = new JComboBox(tchrArray.toArray());
		JComboBox sbjctComboBox = new JComboBox(subjectTypes);
		JTextField subjectFld = new JTextField("", 15);
		JLabel vrblLbl = new JLabel("");
		JLabel lbl = new JLabel();

		JComboBox typesMenu = new JComboBox(usrTypes);
		typesMenu.setSelectedItem(selectedUser);

		JLabel usrLbl = new JLabel("User Name");
		JLabel pwLbl = new JLabel("User Password");
		JLabel typeLbl = new JLabel("User Type");

		JButton crtUsrBtn = new JButton("Register New User");
		JButton backBtn = new JButton("Back");

		JTextField usrNmTxtFld = new JTextField("", 15);
		JTextField usrPwTxtFld = new JTextField("", 15);

		addUsrPnl.add(typeLbl);
		addUsrPnl.add(typesMenu);
		addUsrPnl.add(usrLbl);
		addUsrPnl.add(usrNmTxtFld);
		addUsrPnl.add(pwLbl);
		addUsrPnl.add(usrPwTxtFld);

		if (selectedUser == "Student") {
			vrblLbl = new JLabel("Choose a Teacher for this Student");
			addUsrPnl.add(vrblLbl);
			addUsrPnl.add(tchrComboBox);
		}

		if (selectedUser == "Teacher") {
			vrblLbl = new JLabel("Subject");
			addUsrPnl.add(vrblLbl);
			addUsrPnl.add(sbjctComboBox);
		}

		addUsrPnl.add(crtUsrBtn);
		addUsrPnl.add(backBtn);

		lbl = check_errors("User");

		addUsrPnl.add(lbl);

		typesMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedType = typesMenu.getSelectedItem().toString();
				JPanel addUsrPnl = bldAddUsrPnl(selectedType);
				scrnMgr.add(addUsrPnl, "Add User");
				cl.show(scrnMgr, "Add User");
			}
		});

		crtUsrBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int privilege = 0;
				String arg0 = "";
				String usrName = usrNmTxtFld.getText();
				String usrPW = usrPwTxtFld.getText();

				if (typesMenu.getSelectedItem().equals("Student")) {
					privilege = 3;
					arg0 = tchrComboBox.getSelectedItem().toString();

				} else if (typesMenu.getSelectedItem().equals("Teacher")) {
					privilege = 2;
					arg0 = sbjctComboBox.getSelectedItem().toString();
				} else {
					privilege = 1;
				}

				if (usrName.length() < 5 || usrPW.length() < 5) {
					error_flag = 3;
					JPanel addUsrPnl = bldAddUsrPnl(selectedUser);
					scrnMgr.add(addUsrPnl, "Add User");
					cl.show(scrnMgr, "Add User");
					return;
				}

				error_flag = conn.createUser(usrName, usrPW, privilege, arg0);

				JPanel addUsrPnl = bldAddUsrPnl(selectedUser);
				scrnMgr.add(addUsrPnl, "Add User");
				cl.show(scrnMgr, "Add User");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Admin Dashboard"));

		return addUsrPnl;
	}

	public JPanel bldEditUsrPnl(String selectedUser) {
		JPanel editUsrPnl = new JPanel();
		JLabel edtLbl = new JLabel();
		JLabel lbl = new JLabel();
		ArrayList tchrArray = conn.getAllNames("teachers");
		JComboBox tchrComboBoxEdt = new JComboBox(tchrArray.toArray());
		JComboBox edtSbjctComboBox = new JComboBox(subjectTypes);

		JLabel oldIdLbl = new JLabel("Enter a User ID to Edit");
		JTextField oldIdTxtBox = new JTextField("", 15);
		JLabel newIdLbl = new JLabel("Enter a new User ID");
		JTextField newIdTxtBox = new JTextField("", 15);
		JLabel newPWLbl = new JLabel("Enter a new Password");
		JTextField newPWTxtBox = new JTextField("", 15);

		JLabel menuLbl = new JLabel("Set user's privilege");
		JButton editSbmtBtn = new JButton("Submit");
		JButton backBtn = new JButton("Back");

		JComboBox usrTypesEdt = new JComboBox(usrTypes);
		usrTypesEdt.setSelectedItem(selectedUser);

		editUsrPnl.add(oldIdLbl);
		editUsrPnl.add(oldIdTxtBox);
		editUsrPnl.add(newIdLbl);
		editUsrPnl.add(newIdTxtBox);
		editUsrPnl.add(newPWLbl);
		editUsrPnl.add(newPWTxtBox);
		editUsrPnl.add(menuLbl);
		editUsrPnl.add(usrTypesEdt);

		if (selectedUser == "Student") {
			edtLbl = new JLabel("Choose a Teacher for this Student");
			editUsrPnl.add(edtLbl);
			editUsrPnl.add(tchrComboBoxEdt);
		}

		if (selectedUser == "Teacher") {
			edtLbl = new JLabel("Subject");
			editUsrPnl.add(edtLbl);
			editUsrPnl.add(edtSbjctComboBox);
		}

		editUsrPnl.add(editSbmtBtn);
		editUsrPnl.add(backBtn);

		lbl = check_errors("User");
		
		editUsrPnl.add(lbl);

		usrTypesEdt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedUser = usrTypesEdt.getSelectedItem().toString();
				JPanel editUsrPnl = bldEditUsrPnl(selectedUser);
				scrnMgr.add(editUsrPnl, "Edit User");
				cl.show(scrnMgr, "Edit User");
			}
		});

		editSbmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String arg0 = "";
				int privilege = 0;

				String selectedUser = usrTypesEdt.getSelectedItem().toString();

				if (selectedUser.equals("Student")) {
					privilege = 3;
					arg0 = tchrComboBoxEdt.getSelectedItem().toString();
				} else if (selectedUser.equals("Teacher")) {
					privilege = 2;
					arg0 = edtSbjctComboBox.getSelectedItem().toString();

				} else {
					privilege = 1;
				}
				
				if (newIdTxtBox.getText().length() < 5 || newPWTxtBox.getText().length() < 5) {
					error_flag = 3;
					JPanel addUsrPnl = bldAddUsrPnl(selectedUser);
					scrnMgr.add(addUsrPnl, "Edit User");
					cl.show(scrnMgr, "Edit User");
					return;
				}
				
				error_flag = conn.editUser(oldIdTxtBox.getText(), newIdTxtBox.getText(), newPWTxtBox.getText(),
						privilege, arg0);

				JPanel editUsrPnl = bldEditUsrPnl(selectedUser);
				scrnMgr.add(editUsrPnl, "Edit User");
				cl.show(scrnMgr, "Edit User");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Admin Dashboard"));

		return editUsrPnl;
	}

	public JPanel bldVwUsrPnl() {
		JPanel vwUsrPnl = new JPanel(new GridLayout(3, 1, 2, 2));

		JButton backBtn = new JButton("Back");

		JTable table = conn.getJTable("users");
		JScrollPane scrollPane = new JScrollPane(table);

		JPanel pad = new JPanel();
		pad.setBorder(new EmptyBorder(10, 10, 10, 10));

		vwUsrPnl.add(scrollPane);
		vwUsrPnl.add(pad);
		vwUsrPnl.add(backBtn);

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Admin Dashboard"));
		return vwUsrPnl;
	}

	public JPanel bldDelUsrPnl() {

		JPanel delUsrPnl = new JPanel();
		JLabel delUsrLbl = new JLabel("Warning: Deleting a user cannot be undone");
		JLabel delUsrId = new JLabel("Enter User ID");
		JTextField delUsrTxtBox = new JTextField("", 15);
		JButton sbmtDelUsrBtn = new JButton("Delete User");
		JButton backBtn = new JButton("Back");
		JLabel lbl = new JLabel();

		delUsrPnl.add(delUsrLbl);
		delUsrPnl.add(delUsrId);
		delUsrPnl.add(delUsrTxtBox);
		delUsrPnl.add(sbmtDelUsrBtn);
		delUsrPnl.add(backBtn);

		lbl = check_errors("User");
		
		delUsrPnl.add(lbl);

		sbmtDelUsrBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				error_flag = conn.removeRow("Users", delUsrTxtBox.getText());
				JPanel delUsrPnl = bldDelUsrPnl();
				scrnMgr.add(delUsrPnl, "Delete User");
				cl.show(scrnMgr, "Delete User");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Admin Dashboard"));

		return delUsrPnl;
	} // end

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
			msg.setText("ERROR: Usernames/Passwords must be at least 5 chars");
		}

		else if (error_flag == 4) {
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