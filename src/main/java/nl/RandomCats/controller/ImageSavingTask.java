package nl.RandomCats.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import nl.RandomCats.datasource.CatDatasource;
import nl.RandomCats.exceptions.SavedImageException;
import nl.RandomCats.model.Cat;
import nl.RandomCats.view.View;

public class ImageSavingTask extends SwingWorker<Cat, Void> {

	private static Logger log = Logger.getLogger(ImageSavingTask.class);
	
	private View view;
	private Cat cat;

	public ImageSavingTask(View view, Cat cat) {
		this.view = view;
		this.cat = cat;
		SwingUtilities.invokeLater(() -> {
			view.getProgressBar().setVisible(true);
			view.setButtonsEnabled(false);
		});
	}

	@Override
	protected Cat doInBackground() throws Exception {
		URL url = new URL(cat.getUrl());
		BufferedImage image = ImageIO.read(url);
				
		Path directory = FileSystems.getDefault().getPath("SavedPictures");
		if(Files.notExists(directory)) {
			log.info("Creating new SavedPictures directory");
			Files.createDirectory(directory);
		}

		Path imagePath = Paths.get(directory.toString(), cat.getName() + ".jpg");
		cat.setUrl(imagePath.toString());
		
		ImageIO.write(image, "jpg", imagePath.toFile());
		
		CatDatasource.getInstance().save(cat);

		return cat;
	}

	protected void done() {
		Cat cat;
		try {
			cat = get();
		} catch (Exception e) {
			view.getProgressBar().setVisible(false);
			String message = "Saving image failed";
			log.error(message + ": " + e.getMessage());
			JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		view.getProgressBar().setVisible(false);
		view.setButtonsEnabled(true);
	}
}
