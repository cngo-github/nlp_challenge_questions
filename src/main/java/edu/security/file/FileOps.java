package edu.security.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class FileOps {
	public static Properties readProperties(String file) throws FileNotFoundException {
		File fin = new File(file);
		
		Scanner scan = new Scanner(fin);
		Properties prop = new Properties();
			
		while(scan.hasNext()) {
			String[] arr = scan.nextLine().split("=");
			prop.setProperty(arr[0], arr[1]);
		}
		
		scan.close();
		
		return prop;
	}
	
	public static void saveToFile(String doc, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		
		writer.append(doc);
		writer.close();
	}
	
	public static String readFile(String fileName) {
		Scanner scan = null;
		String ret = "";
		
		try {
			scan = new Scanner(new File(fileName));
			
			while(scan.hasNext()) {
				ret = ret + scan.nextLine();
			}
		} catch (FileNotFoundException e) {
			ret = "";
		} finally {
			scan.close();
		}
		
		return ret;
	}
	
	public static List<String> scanDirectory(String directory, FilenameFilter filter) {
		File dir = new File(directory);
		List<String> ret = new ArrayList<String>();
		
		File[] found = dir.listFiles(filter);
		
		List<File> foundList = Arrays.asList(found);
		
		for(File file: foundList) {
			System.out.println(file.getName());
			ret.add(file.getName());
		}
		
		return ret;
	}
}
