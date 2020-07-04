package nl.RandomCats.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import nl.RandomCats.exceptions.SavedImageException;
import nl.RandomCats.model.Cat;
import nl.RandomCats.utils.AppProperties;
import nl.RandomCats.view.View;

public class ImageLoadingTask extends SwingWorker<Cat, Void> {
	
	private static Logger log = Logger.getLogger(ImageLoadingTask.class);

	private View view;
	private Cat cat;
	private String catApiUrl;

	public ImageLoadingTask(View view, Cat cat) {
		this.view = view;
		this.cat = cat;
		this.catApiUrl = AppProperties.getInstance().get("cat-api-url");
		SwingUtilities.invokeLater(() -> {
			view.getProgressBar().setVisible(true);
			view.setButtonsEnabled(false);
		});
	}

	@Override
	protected Cat doInBackground() throws Exception {
		BufferedImage image = null;
		
		if (cat.getUrl() == null) {
			cat.setUrl(getRandomCatUrl());
			URL url = new URL(cat.getUrl());
			image = ImageIO.read(url);
		} else {
			File file = new File(cat.getUrl());
			try {
				image = ImageIO.read(file);
			} catch (IOException e) {
				view.setCurrentCat(cat);
				throw new SavedImageException(e);
			}
		}

		cat.setImage(image);
		return cat;
	}

	protected void done() {
		Cat cat;
		try {
			cat = get();
			view.setCurrentCat(cat);
			view.getImagePanel().repaint();
		} catch (Exception e) {
			view.getProgressBar().setVisible(false);
			
			Throwable cause = e.getCause();
			String message;
			if(cause instanceof SavedImageException) {
				message = "Loading image failed. Deleting it from list.";
				this.firePropertyChange("loadingSavedFailed", false, true);
			} else {
				message = "Loading image failed";
			}
			JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
			log.error("Error loading image: " + e);
		}
		view.getProgressBar().setVisible(false);
		view.setButtonsEnabled(true);
	}

	public String getRandomCatUrl() throws Exception {
		URL url = new URL(catApiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setReadTimeout(10000);

		int code = connection.getResponseCode();
		String response = connection.getResponseMessage();

		String imgUrl = null;
		if (code == 200) {
			try (BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				JsonArray jsonArray = new JsonParser().parse(input).getAsJsonArray();
				imgUrl = jsonArray.get(0).getAsJsonObject().get("url").getAsString();
			}
		} else {
			throw new Exception("Couldn't connect to cat API, code: " + code + ", message: " + response);
		}

		return imgUrl;
	}

}
