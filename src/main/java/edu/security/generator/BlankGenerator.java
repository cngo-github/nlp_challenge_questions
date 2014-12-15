package edu.security.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.security.file.FileOps;
import edu.security.nlp.NLPProcessor;
import edu.security.nlp.factory.NLPFactory;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class BlankGenerator implements ChallengeGenerator {
	private NLPProcessor nlp = null;
	private List<String> storyFiles = new ArrayList<String>();
	private List<String> expectedAnswers = new ArrayList<String>();
	
	public BlankGenerator(String annotators, List<String> stories) {
		this.nlp = NLPFactory.createEngine(annotators);
		this.storyFiles = stories;
	}
	
	public String getChallenge() {
		int rand = (new Random()).nextInt(this.storyFiles.size());
		this.expectedAnswers.clear();
		Set<String> ansSet = new HashSet<String>();
		
		String story = FileOps.readFile(this.storyFiles.get(rand));
		Annotation doc = this.nlp.process(story);
		
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		String ret = "Enter the missing words seperated by spaces:\n\n";
		
		for(CoreMap sentence: sentences) {
			for(CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String type = token.getString(NamedEntityTagAnnotation.class);
				
				if(type.equals("PERSON") || type.equals("LOCATION")) {
					ret += " [blank]";
					ansSet.add(token.getString(TextAnnotation.class).toLowerCase());
				} else {
					ret += " " + token.getString(TextAnnotation.class);
				}
			}
		}
		
		this.expectedAnswers.addAll(ansSet);
		
		return ret;
	}
	
	public boolean authenticate(String answers) {
		if(this.expectedAnswers.isEmpty()) {
			return false;
		}
		
		List<String> arr = Arrays.asList(answers.toLowerCase().split("\\W"));
		
		for(String elem: arr) {
			if(!this.expectedAnswers.contains(elem)) {
				return false;
			}
		}
		
		for(String elem: this.expectedAnswers) {
			if(!arr.contains(elem)) {
				return false;
			}
		}
		
		return true;
	}
}
