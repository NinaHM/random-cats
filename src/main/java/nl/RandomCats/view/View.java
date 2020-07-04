package nl.RandomCats.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import nl.RandomCats.model.Cat;

public class View extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final Color GREEN = new Color(32, 178, 170);
	private static final Color YELLOW = new Color(240, 230, 140);
	private static final Color ORANGE = new Color(255, 127, 80);
	
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel progressBarPanel;
	private JPanel imagePanel;
	private JPanel buttonPanel;
	private JButton newCatButton;
	private JButton saveCatButton;
	private JButton oldCatButton;
	private JButton deleteCatButton;
	private JProgressBar progressBar;
	private Cat currentCat;
	
	public View() {
		createFrame();
		addTopPanel();
		addImagePanel();
		addBottomPanel();
		addProgressBar();
		addButtons();
		setVisible(true);
	}
	
	private void createFrame() {
		getContentPane().setBackground(Color.WHITE);
		setBounds(100, 100, 1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(20, 10));
        setLocationRelativeTo(null);
	}
	
	private void addTopPanel() {
		topPanel = new JPanel();
		topPanel.setBackground(YELLOW);
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));
		
		JLabel label = new JLabel("Random Cats");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Tahoma", Font.BOLD, 26));
		topPanel.add(label);

		add(topPanel, BorderLayout.NORTH);
	}
	
	private void addImagePanel() {
		imagePanel = new ImageJPanel();
		imagePanel.setBackground(Color.WHITE);
		
		add(imagePanel);
	}
	
	private void addBottomPanel() {
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void addButtons() {
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));
		buttonPanel.setBackground(YELLOW);
		
		newCatButton = new CustomButton("New Cat", GREEN);		
		oldCatButton = new CustomButton("Old Cat", ORANGE);		
		saveCatButton = new CustomButton("Save", GREEN);
		deleteCatButton = new CustomButton("Delete", ORANGE);

		buttonPanel.add(newCatButton);
		buttonPanel.add(oldCatButton);
		buttonPanel.add(saveCatButton);
		buttonPanel.add(deleteCatButton);
		
		bottomPanel.add(buttonPanel);
	}
	
	private void addProgressBar() {
		progressBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		progressBarPanel.setBackground(Color.WHITE);
		
		progressBar = new JProgressBar();
		progressBar.setForeground(GREEN);
		progressBar.setString("");
		progressBar.setStringPainted(false);
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		progressBar.setPreferredSize(new Dimension(400, 15));
		
		progressBarPanel.add(progressBar);
		bottomPanel.add(progressBarPanel);
	}
	
	public void setButtonsEnabled(boolean enabled) {
		newCatButton.setEnabled(enabled);
		saveCatButton.setEnabled(enabled);
		oldCatButton.setEnabled(enabled);
		deleteCatButton.setEnabled(enabled);
	}
	
	private class ImageJPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			if(currentCat != null && currentCat.getImage() != null) {
				super.paintComponent(g);
				
				int imgWidth = currentCat.getImage().getWidth(null);
				int imgHeight = currentCat.getImage().getHeight(null);
				
				double imgAspect = (double) imgHeight / imgWidth;
				
		        int panelWidth = this.getWidth();
		        int panelHeight = this.getHeight();
		         
		        double panelAspect = (double) panelHeight / panelWidth;
		 
		        int x1 = 0; // top left X position
		        int y1 = 0; // top left Y position
		        int x2 = 0; // bottom right X position
		        int y2 = 0; // bottom right Y position
		         
		        if (imgWidth < panelWidth && imgHeight < panelHeight) {

		            x1 = (panelWidth - imgWidth)  / 2;
		            y1 = (panelHeight - imgHeight) / 2;
		            x2 = imgWidth + x1;
		            y2 = imgHeight + y1;
		             
		        } else {
		            if (panelAspect > imgAspect) {
		                y1 = panelHeight;
		                panelHeight = (int) (panelWidth * imgAspect);
		                y1 = (y1 - panelHeight) / 2;
		            } else {
		                x1 = panelWidth;
		                panelWidth = (int) (panelHeight / imgAspect);
		                x1 = (x1 - panelWidth) / 2;
		            }
		            x2 = panelWidth + x1;
		            y2 = panelHeight + y1;
		        }

		        g.drawImage(currentCat.getImage(), x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
			} else {
				super.paintComponent(g);
			}
		} 
	}

	public void setCurrentCat(Cat currentCat) {
		this.currentCat = currentCat;
	}
	
	public Cat getCurrentCat() {
		return this.currentCat;
	}
	
	public JButton getNewCatButton() {
		return newCatButton;
	}
	
	public JButton getSaveCatButton() {
		return saveCatButton;
	}
	
	public JButton getOldCatButton() {
		return oldCatButton;
	}

	public JButton getDeleteCatButton() {
		return deleteCatButton;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JPanel getImagePanel() {
		return imagePanel;
	}
	
}
