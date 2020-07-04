package nl.RandomCats.model;

import java.awt.image.BufferedImage;

public class Cat {
	
	private String name;
	private String url;
	private BufferedImage image;
	
	public Cat() {
		
	}
	
	public Cat(String url, BufferedImage image) {
		this.url = url;
		this.image = image;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Cat)) {
			return false;
		}
		
		Cat otherCat = (Cat) obj;
		return (this.url == null ? otherCat.url == null : this.url.equals(otherCat.url));
	}
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

}
