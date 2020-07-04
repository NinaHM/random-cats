package nl.RandomCats.controller;

import nl.RandomCats.exceptions.SavedImageException;

public interface ThrowingFunction<R, T> {

	R apply(T t) throws SavedImageException;
}
