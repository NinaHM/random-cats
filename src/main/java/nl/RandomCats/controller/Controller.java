package nl.RandomCats.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import nl.RandomCats.datasource.CatDatasource;
import nl.RandomCats.datasource.Datasource;
import nl.RandomCats.exceptions.SavedImageException;
import nl.RandomCats.model.Cat;
import nl.RandomCats.view.View;

public class Controller implements PropertyChangeListener {

	private static final Logger LOGGER = Logger.getLogger(Controller.class);
	
	private View view;
	private List<Cat> oldCats;
	private Datasource<Cat> datasource;
	private ListIterator<Cat> iterator;
	private ExecutorService executorService;

	public Controller() {
		try {
			SwingUtilities.invokeAndWait(() -> this.view = new View());
		} catch (InvocationTargetException | InterruptedException e) {
			LOGGER.fatal("Starting UI failed", e);
		}
		this.datasource = CatDatasource.getInstance();	
		process((e) -> datasource.open(), null);
		executorService = Executors.newFixedThreadPool(3);
		setNewCat();
		getOldCats();
		addActionListeners();
	}

	private void addActionListeners() {
		view.getNewCatButton().addActionListener(e -> setNewCat());
		view.getSaveCatButton().addActionListener(e -> saveCat());
		view.getOldCatButton().addActionListener(e -> setOldCat());
		view.getDeleteCatButton().addActionListener(e -> deleteCat());
		view.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				close();
			}
		});
	}

	private void setNewCat() {
		setNewCat(new Cat());
	}
	
	private void setNewCat(Cat cat) {
		ImageLoadingTask task = new ImageLoadingTask(view, cat);
		task.addPropertyChangeListener(this);
		task.execute();
	}

	private void setOldCat() {		
		if(oldCats == null) {
			return;
		}
		
		if(iterator == null) {
			iterator = oldCats.listIterator();
		}
		
		if(oldCats.size() > 0) {
			Cat cat = null;
			
			if(iterator.hasNext()) {
				cat = iterator.next();
			} else if((iterator = oldCats.listIterator()).hasNext()) {
				cat = iterator.next();
			}

			if(cat.getImage() == null) {
				setNewCat(cat);
			} else {
				view.setCurrentCat(cat);
				view.getImagePanel().repaint();
			}
		} else {
			JOptionPane.showMessageDialog(view, "There are no cat pictures saved.");
		}
	}

	private void saveCat() {
		if(oldCats == null) {
			return;
		}
		
		if(iterator == null) {
			iterator = oldCats.listIterator();
		}
		
		Cat currentCat = view.getCurrentCat();
		boolean alreadySaved = oldCats.stream().anyMatch(oldCat -> oldCat.equals(currentCat));

		if (alreadySaved) {
			JOptionPane.showMessageDialog(view, "This picture is already saved.");
			return;
		}
		
		getValidName().ifPresent(name -> {
					currentCat.setName(name);
					ImageSavingTask task = new ImageSavingTask(view, currentCat);
					task.execute();
					iterator.add(currentCat);
					iterator.previous();
				});
	}
	
	private Optional<String> getValidName() {
		String name = JOptionPane.showInputDialog("Please give your cat a name");
	
		while (name == null || name.trim().isEmpty() || process((e) -> datasource.findByName(e), name) != null) {		
			if (name == null) {
				break;
			}
			
			if(name.trim().isEmpty()) {
				name = JOptionPane.showInputDialog("Please give your cat a name");
			}
			
			if(process((e) -> datasource.findByName(e), name) != null) {
				name = JOptionPane.showInputDialog("This name already exists. Please try a different one.");
			}
		}
		
		return Optional.ofNullable(name);
	}

	private void getOldCats() {
		executorService.submit(() -> process((e) -> oldCats = datasource.findAll(), null));
	}
	
	private <R, T> R process(ThrowingFunction<R, T> function, T t) {
		try {
			return function.apply(t);
		} catch (SavedImageException e) {
			if(e.isFatal()) {
				LOGGER.fatal(e.getMessage(), e);
				showErrorMessage(e.getMessage() + " Closing application.");
				System.exit(0);
			} else {
				LOGGER.error(e.getMessage(), e);
				showErrorMessage(e.getMessage());
			}
			return null;
		}
	}

	private void deleteCat() {
		Cat cat = view.getCurrentCat();
		
		if(!oldCats.contains(cat)) {
			return;
		}

		int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this picture?", 
				"Deleting", JOptionPane.YES_NO_OPTION);
		if(answer == JOptionPane.YES_OPTION) {
			removePicture(cat);
			setOldCat();
		}
		
	}

	private void close() {
		executorService.shutdown();
		try {
		    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
		        executorService.shutdownNow();
		    } 
		} catch (InterruptedException e) {
		    executorService.shutdownNow();
		}
		
		process((e) -> datasource.close(), null);
		
		System.exit(0);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if("loadingSavedFailed".equals(propertyName)) {
			removePicture(view.getCurrentCat());
		}
	}
	
	private void removePicture(Cat cat) {
		Path imageToDelete = Paths.get(cat.getUrl());
		try {
			Files.deleteIfExists(imageToDelete);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		
		executorService.submit(() -> process((e) -> datasource.delete(e), view.getCurrentCat()));
		iterator.remove();
	}
	
	private void showErrorMessage(String message) {
		try {
			SwingUtilities.invokeAndWait(() -> JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE));
		} catch (InvocationTargetException | InterruptedException e) {
			LOGGER.error("Failed to show error message: " + message, e);
		}
	}
}
