package edu.security.property.test;

import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.function.BiConsumer;

import edu.security.file.FileOps;

public class PropertyTest {
	public static void main(String[] args) {
		try {
			Properties prop = FileOps.readProperties("properties.prop");
			
			prop.forEach(new BiConsumer<Object, Object>() {
				@Override
				public void accept(Object t, Object u) {
					System.out.println("Key: " + t.toString() + " Value: " + u.toString());
				}
			});
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
	}
}
