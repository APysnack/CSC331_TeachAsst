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
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginWindow extends JFrame {

	JButton lgnBtn, getPwBtn;
	JLabel lgnLbl;
	JTextField usrField, pwField;
	JPanel currPanel;
	CardLayout cl;
	JPanel scrnMgr;
	JPanel lgnPanel;
	dbConnection conn;

	// ------------------------------------------------------------------------ //
	// Main Window
	// ------------------------------------------------------------------------ //

	LoginWindow() {

		dbConnection connect = new dbConnection();
		conn = connect;

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

		JPanel mainPnl = new JPanel(new BorderLayout());

		JPanel pad = new JPanel();
		pad.setBorder(new EmptyBorder(140, 140, 140, 140));
		JPanel pad2 = new JPanel();
		pad2.setBorder(new EmptyBorder(140, 140, 140, 140));
		JPanel pad3 = new JPanel();
		pad3.setBorder(new EmptyBorder(95, 95, 95, 95));
		JPanel pad4 = new JPanel();
		pad4.setBorder(new EmptyBorder(95, 95, 95, 95));

		JPanel midPanel = new JPanel(new BorderLayout());

		JPanel lgnWndw = new JPanel();
		JPanel lgnPnl = new JPanel(new GridLayout(4, 1, 2, 2));

		JButton lgnBtn = new JButton("Log In");
		lgnPnl.add(lgnBtn);

		lgnLbl = new JLabel("Please Enter your Login Information");

		usrField = new JTextField("Enter Username", 15);

		// Needs to handle so that the user can enter text without clearing
		usrField.addFocusListener(new FocusListener() {
			// if user clicks on UserName field, it sets the text to empty for use to input
			@Override
			public void focusGained(FocusEvent e) {

				if (usrField.getText().equals("Enter Username")) {
					usrField.setText("");
				}
			}

			// if user unclicks userName field it sets text to empty only if user hasnt
			// typed
			@Override
			public void focusLost(FocusEvent e) {
				if (usrField.getText().isEmpty()) {
					usrField.setText("Enter Username");
				}
			}
		});
		pwField = new JTextField("Enter Password", 15);

		pwField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (pwField.getText().equals("Enter Password")) {
					pwField.setText("");
				}
			}

			public void focusLost(FocusEvent e) {
				if (pwField.getText().isEmpty()) {
					pwField.setText("Enter Password");
				}
			}
		});

		JPanel ctrLblPnl = new JPanel();
		ctrLblPnl.add(lgnLbl);
		lgnPnl.add(ctrLblPnl);
		lgnPnl.add(usrField);
		lgnPnl.add(pwField);
		lgnPnl.add(lgnBtn);

		midPanel.add(lgnPnl, BorderLayout.CENTER);
		midPanel.add(pad, BorderLayout.WEST);
		midPanel.add(pad2, BorderLayout.EAST);
		midPanel.add(pad3, BorderLayout.NORTH);
		midPanel.add(pad4, BorderLayout.SOUTH);

		mainPnl.add(midPanel, BorderLayout.CENTER);

		lgnBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String userName = usrField.getText();
				String password = pwField.getText();
				
				int privilege = conn.connectUser(userName, password);

				if (privilege == 1) {
					AdminDash adminDash = new AdminDash(conn, userName);
					adminDash.setVisible(true);
					adminDash.setSize(900, 550);
					dispose();
				}

				else if (privilege == 2) {
					TeacherDash tchrDash = new TeacherDash(conn, userName);
					tchrDash.setVisible(true);
					tchrDash.setSize(900, 550);
					dispose();
				}

				else if (privilege == 3) {
					dispose();
					StudentDash stdntDash = new StudentDash(conn, userName);
					stdntDash.setVisible(true);
					stdntDash.setSize(900, 550);
					dispose();
				}

				else {
					dispose();
					ErrorPage err = new ErrorPage();
				}
			}
		});

		this.add(mainPnl);

		this.pack();
	}
}