package edu.security.nlp.factory;

import edu.security.nlp.NLPProcessor;
import edu.security.nlp.engine.StanfordProcessor;

public class NLPFactory {
	public static NLPProcessor createEngine(String annotators) {
		NLPProcessor engine = new StanfordProcessor();
		
		engine.createPipeline(annotators);
		return engine;
	}
}
