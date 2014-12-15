package edu.security.generator;

import java.util.ArrayList;
import java.util.List;

import edu.security.file.FileOps;
import edu.security.nlp.NLPProcessor;
import edu.security.nlp.factory.NLPFactory;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class DocRetGenerator implements ChallengeGenerator {
	private NLPProcessor nlp = null;
	private String question = "";
	private List<String> storyFiles = new ArrayList<String>();
	private double threshold = 0.5;
	
	public DocRetGenerator(String annotators, List<String> stories,
			String question, double threshold) {
		this.nlp = NLPFactory.createEngine(annotators);
		this.storyFiles = stories;
		this.question = question;
		this.threshold = threshold;
	}
	
	@Override
	public String getChallenge() {
		return question;
	}

	@Override
	public boolean authenticate(String answers) {
		List<String> ansList = this.extractKeywords(answers);
		double ratio = 0;
		
		for(String file: this.storyFiles) {
			String story = FileOps.readFile(file);
			List<String> match = this.extractKeywords(story);
			
			double count = 0;
			
			for(String elem: ansList) {
				if(match.contains(elem)) {
					count++;
				}
			}
			
			double temp = count / match.size();
			
			if(temp > ratio) {
				ratio = temp;
			}
		}
		
		return ratio >= threshold;
	}
	
	private List<String> extractKeywords(String text) {
		Annotation doc = this.nlp.process(text);
		List<String> ret = new ArrayList<String>();
		
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		
		for(CoreMap sentence: sentences) {
			for(CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String type = token.getString(NamedEntityTagAnnotation.class);
				String typePOS = token.getString(PartOfSpeechAnnotation.class);
				
				if(type.equals("PERSON") || type.equals("LOCATION")) {
					ret.add(token.getString(TextAnnotation.class).toLowerCase());
				} else if(typePOS.contains("VB")) {
					ret.add(token.get(LemmaAnnotation.class).toLowerCase());
				}
			}
		}
		
		return ret;
	}
}
