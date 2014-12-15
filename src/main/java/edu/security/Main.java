package edu.security;

import java.io.FileNotFoundException;

import edu.security.ui.UserInterface;

public class Main {
	public static void main(String[] args) {
		String propFile = "properties.prop";
		UserInterface ui = null;
		
		try {
			ui = new UserInterface(propFile, System.in, System.out);
			ui.printMainUI();
		} catch (FileNotFoundException e) {
			System.out.println("Did not find the properties file: " + propFile);
		} finally {
			ui.close();
		}
	}
}
