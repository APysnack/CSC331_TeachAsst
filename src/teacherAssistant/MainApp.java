package teacherAssistant;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class MainApp {

	public static void main(String[] args) {
		LoginWindow login = new LoginWindow();
		login.setSize(900, 550);
		login.setVisible(true);
	}

}
