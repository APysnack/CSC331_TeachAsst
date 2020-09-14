package teacherAssistant;

import java.awt.BorderLayout;
//allows you to store height and width, nothing else
import java.awt.Dimension;
import java.awt.event.*;
//allows you to ask different questions of the OS
import java.awt.Toolkit;

import javax.swing.*;

public class ErrorPage extends JFrame implements ActionListener {
	
	JLabel errorLbl;
	
	
	public ErrorPage() {
		this.setSize(900, 550);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (this.getWidth() / 2);
		int yPos = (dim.height / 2) - (this.getHeight() / 2);
		this.setLocation(xPos, yPos);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Error");
		JPanel main_window = new JPanel();
		JLabel errorLbl = new JLabel("There was an error with your Login");
		JButton backBtn = new JButton("Back");
		backBtn.addActionListener(this);
		
		main_window.add(errorLbl);
		main_window.add(backBtn);
		
		this.add(main_window);
		this.setVisible(true);
		
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
				LoginWindow login = new LoginWindow();
				login.setSize(900, 550);
				login.setVisible(true);
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
