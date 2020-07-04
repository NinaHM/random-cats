package nl.RandomCats.datasource;

import java.util.List;

import nl.RandomCats.exceptions.SavedImageException;
import nl.RandomCats.model.Cat;

public interface Datasource<T> {
	
	public boolean open() throws SavedImageException;
	public boolean close() throws SavedImageException;
	public boolean save(T t) throws SavedImageException;
	public List<T> findAll() throws SavedImageException;
	public boolean delete(T t) throws SavedImageException;
	public Cat findByName(String name) throws SavedImageException;
}
