package nl.RandomCats.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class AppProperties {
	
	private static Logger log = Logger.getLogger(AppProperties.class);
	private static final AppProperties INSTANCE = new AppProperties();
	private Properties properties;	
	
	private AppProperties() {
		String appConfigPath = Thread.currentThread().getContextClassLoader().getResource("application.properties").getPath();
		 
		this.properties = new Properties();
		try {
			this.properties.load(new FileInputStream(appConfigPath));
		} catch (FileNotFoundException e) {
			log.fatal("Couldn't find propertiesfile: " + e);
		} catch (IOException e) {
			log.fatal("Problem loading properties file: " + e);
		}
	}
	
	public static AppProperties getInstance() {
		return INSTANCE;
	}
	
	public String get(String name) {
		return this.properties.getProperty(name);
	}

}
