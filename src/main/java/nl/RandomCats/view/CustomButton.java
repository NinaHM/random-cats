package nl.RandomCats.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

public class CustomButton extends JButton {
	
	private static final long serialVersionUID = 1L;

	public CustomButton(String text, Color color) {
		super(text);
		setForeground(Color.WHITE);
		setFont(new Font("Tahoma", Font.BOLD, 16));
		setBorderPainted(false);
		setBackground(color);
	}
}
