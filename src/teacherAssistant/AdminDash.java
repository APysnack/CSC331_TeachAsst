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

	JButton lgnBtn, getPwBtn;
	JLabel lgnLbl;
	JTextField usrField, pwField;
	JPanel currPanel;
	CardLayout cl;
	JPanel scrnMgr;
	JPanel lgnPanel;
	dbConnection conn;
	int error_flag = -1;
	String[] usrTypes;

	// ------------------------------------------------------------------------ //
	// Main Window
	// ------------------------------------------------------------------------ //

	AdminDash(dbConnection conn) {
		String[] usrTypes = { "Student", "Teacher", "Admin" };
		this.usrTypes = usrTypes;
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

		vwUsrBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel vwUsrPnl = bldVwUsrPnl();
				scrnMgr.add(vwUsrPnl, "View User");
				cl.show(scrnMgr, "View User");
			}
		});

		lgOutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				LoginWindow login = new LoginWindow();
				login.setVisible(true);
				login.setSize(900, 550);
			}
		});
		return adminDash;
	}

	public JPanel bldAddUsrPnl(String selectedUser) {
		JPanel addUsrPnl = new JPanel();
		ArrayList tchrArray = conn.getAllNames("teachers");
		JComboBox tempComboBox = new JComboBox(tchrArray.toArray());
		JTextField subjectFld = new JTextField("", 15);
		JLabel lbl;

		JComboBox usrMenu = new JComboBox(usrTypes);
		usrMenu.setSelectedItem(selectedUser);

		JLabel usrLbl = new JLabel("User Name");
		JLabel pwLbl = new JLabel("User Password");
		JLabel typeLbl = new JLabel("User Type");

		JButton crtUsrBtn = new JButton("Register New User");
		JButton backBtn = new JButton("Back");

		JTextField usrTextFld = new JTextField("", 15);
		JTextField pwTextFld = new JTextField("", 15);

		addUsrPnl.add(typeLbl);
		addUsrPnl.add(usrMenu);
		addUsrPnl.add(usrLbl);
		addUsrPnl.add(usrTextFld);
		addUsrPnl.add(pwLbl);
		addUsrPnl.add(pwTextFld);

		if (selectedUser == "Student") {
			lbl = new JLabel("Choose a Teacher for this Student");
			addUsrPnl.add(lbl);
			addUsrPnl.add(tempComboBox);
		}

		if (selectedUser == "Teacher") {
			lbl = new JLabel("Subject");
			addUsrPnl.add(lbl);
			addUsrPnl.add(subjectFld);
		}

		addUsrPnl.add(crtUsrBtn);
		addUsrPnl.add(backBtn);

		if (error_flag == 3) {
			JLabel tooShort = new JLabel("Usernames and Passwords must be at least 5 Characters Long");
			addUsrPnl.add(tooShort);
			error_flag = -1;
		}

		if (error_flag == 0) {
			JLabel success = new JLabel("User successfully added");
			addUsrPnl.add(success);
			error_flag = -1;
		}

		if (error_flag == 2) {
			JLabel nameTaken = new JLabel("ERROR: \n" + "That name is already registered to another user \n"
					+ "Please try a different Username");
			addUsrPnl.add(nameTaken);
			error_flag = -1;

		}

		usrMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String selectedUser = usrMenu.getSelectedItem().toString();
				JPanel addUsrPnl = bldAddUsrPnl(selectedUser);
				scrnMgr.add(addUsrPnl, "Add User");
				cl.show(scrnMgr, "Add User");

			}
		});

		crtUsrBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int privilege = 0;
				String arg0 = "";

				if (usrMenu.getSelectedItem().equals("Student")) {
					privilege = 3;
					arg0 = tempComboBox.getSelectedItem().toString();

				} else if (usrMenu.getSelectedItem().equals("Teacher")) {
					privilege = 2;
					arg0 = subjectFld.getSelectedText();
				} else {
					privilege = 1;
				}

				if (usrTextFld.getText().length() < 5 || pwTextFld.getText().length() < 5) {
					error_flag = 3;
					JPanel newUsrPnl = bldAddUsrPnl(selectedUser);
					scrnMgr.add(newUsrPnl, "Add User");
					cl.show(scrnMgr, "Add User");
					return;
				}

				error_flag = conn.createUser(usrTextFld.getText(), pwTextFld.getText(), privilege, arg0);

				JPanel newUsrPnl = bldAddUsrPnl(selectedUser);
				scrnMgr.add(newUsrPnl, "Add User");
				cl.show(scrnMgr, "Add User");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Admin Dashboard"));

		return addUsrPnl;
	}

	public JPanel bldEditUsrPnl(String selectedUser) {
		JPanel editUsrPnl = new JPanel();
		ArrayList tchrArray = conn.getAllNames("teachers");
		JComboBox tempComboBox = new JComboBox(tchrArray.toArray());
		JTextField subjectFld = new JTextField("", 15);
		JLabel lbl;

		JLabel oldIdLbl = new JLabel("Enter a User ID to Edit");
		JTextField oldIdTxtBox = new JTextField("", 15);
		JLabel newIdLbl = new JLabel("Enter a new User ID");
		JTextField newIdTxtBox = new JTextField("", 15);
		JLabel newPWLbl = new JLabel("Enter a new Password");
		JTextField newPWTxtBox = new JTextField("", 15);

		JLabel menuLbl = new JLabel("Set user's privilege");
		JButton editSbmtBtn = new JButton("Submit");
		JButton backBtn = new JButton("Back");

		JComboBox usrMenu = new JComboBox(usrTypes);
		usrMenu.setSelectedItem(selectedUser);

		editUsrPnl.add(oldIdLbl);
		editUsrPnl.add(oldIdTxtBox);
		editUsrPnl.add(newIdLbl);
		editUsrPnl.add(newIdTxtBox);
		editUsrPnl.add(newPWLbl);
		editUsrPnl.add(newPWTxtBox);
		editUsrPnl.add(menuLbl);
		editUsrPnl.add(usrMenu);

		if (selectedUser == "Student") {
			lbl = new JLabel("Choose a Teacher for this Student");
			editUsrPnl.add(lbl);
			editUsrPnl.add(tempComboBox);
		}

		if (selectedUser == "Teacher") {
			lbl = new JLabel("Subject");
			editUsrPnl.add(lbl);
			editUsrPnl.add(subjectFld);
		}

		editUsrPnl.add(editSbmtBtn);
		editUsrPnl.add(backBtn);

		if (error_flag == 0) {
			JLabel success = new JLabel("User successfully edited");
			editUsrPnl.add(success);
		}

		if (error_flag == 1) {
			JLabel usrDNE = new JLabel("This user does not exist");
			editUsrPnl.add(usrDNE);
		}

		if (error_flag == 2) {
			JLabel nameTaken = new JLabel("ERROR: \n" + "That name is already registered to another user \n"
					+ "Please try a different Username");
			editUsrPnl.add(nameTaken);

		}

		if (error_flag == 3) {
			JLabel tooShort = new JLabel("ERROR: \n" + "Please ensure your name and password are a minimum of 5 chars");
			editUsrPnl.add(tooShort);
		}

		usrMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedUser = usrMenu.getSelectedItem().toString();
				JPanel editUsrPnl = bldEditUsrPnl(selectedUser);
				scrnMgr.add(editUsrPnl, "Add User");
				cl.show(scrnMgr, "Add User");
			}
		});

		editSbmtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String arg0 = "";
				int privilege = 0;

				String selectedUser = usrMenu.getSelectedItem().toString();

				if (selectedUser.equals("Student")) {
					privilege = 3;
					arg0 = tempComboBox.getSelectedItem().toString();
				} else if (selectedUser.equals("Teacher")) {
					privilege = 2;
					arg0 = subjectFld.getText();

				} else {
					privilege = 1;
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

		vwUsrPnl.repaint();
		vwUsrPnl.revalidate();

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Admin Dashboard"));
		return vwUsrPnl;
	}

	public JPanel bldDelUsrPnl() {

		JPanel delUsrPnl = new JPanel();
		JLabel delUsrHdr = new JLabel("Warning: Deleting a user cannot be undone");
		JLabel delUsrId = new JLabel("Enter User ID");
		JTextField usrIdTxtBox = new JTextField("", 15);
		JButton delUsrBtn = new JButton("Delete User");

		JButton backBtn = new JButton("Back");

		delUsrPnl.add(delUsrHdr);
		delUsrPnl.add(delUsrId);
		delUsrPnl.add(usrIdTxtBox);
		delUsrPnl.add(delUsrBtn);
		delUsrPnl.add(backBtn);

		if (error_flag == 0) {
			JLabel success = new JLabel("User successfully removed");
			delUsrPnl.add(success);
		}

		if (error_flag == 1) {
			JLabel usrDNE = new JLabel("This user does not exist");
			delUsrPnl.add(usrDNE);
		}

		delUsrBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				error_flag = conn.removeRow("Users", usrIdTxtBox.getText());
				JPanel delUsrPnl = bldDelUsrPnl();
				scrnMgr.add(delUsrPnl, "Delete User");
				cl.show(scrnMgr, "Delete User");
			}
		});

		backBtn.addActionListener(e -> cl.show(scrnMgr, "Admin Dashboard"));

		return delUsrPnl;
	}

}