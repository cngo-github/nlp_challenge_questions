package edu.security.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import edu.security.file.FileOps;
import edu.security.generator.BlankGenerator;
import edu.security.generator.ChallengeGenerator;
import edu.security.generator.DocRetGenerator;

public class UserInterface {
	private Properties properties = null;
	private Scanner in = null;
	private PrintStream out = null;
	private List<String> files = new ArrayList<String>();
	private ChallengeGenerator challenger = null;
	
	public UserInterface(String properties, InputStream in, OutputStream out)
			throws FileNotFoundException {
		this.properties  = FileOps.readProperties(properties);
		this.in = new Scanner(in);
		this.out = new PrintStream(out);
		this.getStoredStories();
	}
	
	public void printMainUI() {
		String mainUI = "Select a technique.\n\n\tI) Input a story\n\t" +
				"F) Fill in the blanks.\n" +
				"\tD) Document recovery.\n\tQ) Quit\n\n" +
				"Selection: ";
		
		boolean run = true;
		
		while(run) {
			this.out.append(mainUI);
			String input = this.in.nextLine().toLowerCase().substring(0, 1);
			
			switch (input) {
				case "f":
					if(this.files.isEmpty()) {
						this.addStory();
					}
					
					this.challenger = new BlankGenerator(
							this.properties.getProperty("annotators"), this.files);
					this.challenge();
					
					break;
				case "d":
					if(this.files.isEmpty()) {
						this.addStory();
					}
					
					this.challenger = new DocRetGenerator(
							this.properties.getProperty("annotators"), this.files,
							this.properties.getProperty("question"), 0.5);
					this.challenge();
					
					break;
				case "q":
					this.out.append("Exiting program...\n");
					run = false;
					break;
				case "i":
					this.addStory();
					break;
				default:
					this.out.append("Invalid value.  Try again.\n");
					break;
			}
		}
	}
	
	public void close() {
		this.in.close();
		this.out.close();
	}
	
	private void addStory() {
		boolean check = true;
		
		while(check) {
			this.out.append(this.properties.getProperty("question") + "\n\n");
			String input = this.in.nextLine();
			
			this.out.append("Save this story [Y/N]? ");
			String confirm = this.in.nextLine().toLowerCase().substring(0, 1);
			
			if(confirm.equals("y")) {
				String fileName = "answer_" + (new Date()).getTime() + 
						(new Random()).nextInt() + ".txt";
				
				try {
					FileOps.saveToFile(input, fileName);
					this.files.add(fileName);
					check = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void challenge() {
		this.out.append(this.challenger.getChallenge() + "\n");
		this.out.append("Answer: ");
		
		String answer = this.in.nextLine();
		
		if(this.challenger.authenticate(answer)) {
			this.out.append("Authenticated.\n");
		} else {
			this.out.append("Not authenticated.\n");
		}
	}
	
	private void getStoredStories() {
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("answer_") && name.endsWith(".txt");
			}
		};
		
		this.files = FileOps.scanDirectory(".", filter);
	}
}
