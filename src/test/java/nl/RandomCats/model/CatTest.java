package nl.RandomCats.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CatTest {

	@Test
	void testEquals() {
		Cat cat1 = new Cat();
		cat1.setUrl("http://testurl.com");
		assertTrue(cat1.equals(cat1));
		Cat cat2 = new Cat();
		cat2.setUrl("http://example.com");
		assertFalse(cat1.equals(cat2));
		assertFalse(cat2.equals(cat1));
		assertFalse(cat1.equals(null));
	}

}
