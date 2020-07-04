package nl.RandomCats.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import nl.RandomCats.exceptions.SavedImageException;
import nl.RandomCats.model.Cat;
import nl.RandomCats.utils.AppProperties;

public class CatDatasource implements Datasource<Cat> {
	
	private String connectionString;
	private static final String INSERT = "INSERT INTO cats (name, url) VALUES(?, ?)";
	private static final String DELETE = "DELETE FROM cats WHERE url = ?"; 
	private static final String FIND_BY_NAME = "SELECT * FROM cats WHERE name = ?"; 

	private PreparedStatement insert;
	private PreparedStatement delete;
	private PreparedStatement findByName;
	private Connection conn;
	
	private static final CatDatasource INSTANCE = new CatDatasource();
	
	private CatDatasource() {
		connectionString = AppProperties.getInstance().get("db-url");
	}
	
	public static CatDatasource getInstance() {
		return INSTANCE;
	}

	public boolean open() throws SavedImageException {
		try {
			conn = DriverManager.getConnection(connectionString);
			insert = conn.prepareStatement(INSERT);
			delete = conn.prepareStatement(DELETE);
			findByName = conn.prepareStatement(FIND_BY_NAME);
			return true;
		} catch (SQLException e) {
			throw new SavedImageException("Failed to open database connection.", e, true);
		}
	}

	public boolean close() throws SavedImageException {
		try {
			if (insert != null) {
				insert.close();
			}
			
			if (delete != null) {
				delete.close();
			}
			
			if (findByName != null) {
				findByName.close();
			}

			if (conn != null) {
				conn.close();
			}
			return true;
		} catch (SQLException e) {
			throw new SavedImageException("Failed to close connection.", e, true); 
		}
	}

	public synchronized boolean save(Cat cat) throws SavedImageException {
		try {
			insert.setString(1, cat.getName());
			insert.setString(2, cat.getUrl());
			insert.execute();
			return true;
		} catch (SQLException e) {
			throw new SavedImageException("Failed to save image to database.", e);
		}
	}

	public synchronized List<Cat> findAll() throws SavedImageException {
		String sql = "SELECT * FROM cats";
		try (Statement statement = conn.createStatement(); 
			ResultSet results = statement.executeQuery(sql)) {

			List<Cat> oldCats = new ArrayList<>();
			while (results.next()) {
				Cat cat = new Cat();
				cat.setName(results.getString("name"));
				cat.setUrl(results.getString("url"));
				oldCats.add(cat);
			}
			return oldCats;
		} catch (SQLException e) {
			throw new SavedImageException("Failed to load images from database.", e);
		} 
	}
	
	public synchronized Cat findByName(String name) throws SavedImageException {
		try {
			findByName.setString(1,  name);
			ResultSet result = findByName.executeQuery();
			Cat cat = null;
			if(result.next()) {
				cat = new Cat();
				cat.setName(result.getString("name"));
				cat.setUrl(result.getString("url"));
			}
			return cat;
		} catch (SQLException e) {
			throw new SavedImageException("Error retrieving image from database.", e);
		}	
	}

	public synchronized boolean delete(Cat cat) throws SavedImageException {
		try {
			delete.setString(1, cat.getUrl());
			delete.execute();
			return true;
		} catch (SQLException e) {
			throw new SavedImageException("Failed to delete image from database.", e);
		} 
	}
}
