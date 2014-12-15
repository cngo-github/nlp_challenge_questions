package edu.security.nlp.test;

import java.util.List;

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

public class NLPTest {
	public static void main(String[] args) {
		String annotators = "tokenize, ssplit, pos, lemma, ner, parse, dcoref";
		String message = "My uncle Bob is the best uncle in the world.  He was" +
				" born in 1970 and loves to go fishing.  He taught me how to " +
				"fish in the summer of 1990 in Minnesota.";
		NLPProcessor engine = NLPFactory.createEngine(annotators);

		Annotation doc = engine.process(message);
		
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for(CoreMap sentence : sentences) {
			for(CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Token text
				System.out.println(token.getString(TextAnnotation.class));
				// POS tag
				System.out.println(token.get(PartOfSpeechAnnotation.class));
				// NER Label
				System.out.println(token.get(NamedEntityTagAnnotation.class));
				// LEMA Label
				System.out.println(token.get(LemmaAnnotation.class));
				System.out.println("=====");
			}
		}
	}
}
