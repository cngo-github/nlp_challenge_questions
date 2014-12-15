package edu.security.nlp;

import edu.stanford.nlp.pipeline.Annotation;

/**
 * 
 * @author Chuong Ngo
 *
 * Notes
 * ======
 * 1) Remove the exposure of the Stanford Annotation object to generalize the
 * interface.
 */
public interface NLPProcessor {
	public void createPipeline(String annotators);
	public Annotation process(String text);
}
