package edu.security.nlp.engine;

import java.util.Properties;

import edu.security.nlp.NLPProcessor;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordProcessor implements NLPProcessor {
	private StanfordCoreNLP pipeline = null;
	
	public void createPipeline(String annotators) {
		Properties props = new Properties();
		props.setProperty("annotators", annotators);
		this.pipeline = new StanfordCoreNLP(props);
	}

	public Annotation process(String text) {
		return this.pipeline.process(text);
	}
}
