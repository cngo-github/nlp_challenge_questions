package edu.security.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import ro.cmit.lucene.EnglishLemmaAnalyzer;
import edu.security.file.FileOps;
import edu.security.nlp.NLPProcessor;
import edu.security.nlp.factory.NLPFactory;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class LuceneGenerator implements ChallengeGenerator {
	private String question = "";
	private double threshold = 0.5;
	private NLPProcessor nlp = null;
	private List<String> files = new ArrayList<String>();
	private Directory store = new RAMDirectory();
	private Analyzer analyzer = null;
	
	public LuceneGenerator(String annotators, List<String> files,
			String question, String posModel, double threshold) throws Exception {
		this.nlp = NLPFactory.createEngine(annotators);
		this.files = files;
		this.question = question;
		this.threshold = threshold;
		this.analyzer = new EnglishLemmaAnalyzer(posModel);
		
		this.populateDirectory();
	}

	@Override
	public String getChallenge() {
		return question;
	}

	@Override
	public boolean authenticate(String answers) {
		List<String> query = this.extractKeywords(answers);
		try {
			this.searchDirectory(this.buildQuery(query));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void populateDirectory() throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_CURRENT,
				this.analyzer);
		IndexWriter iw = new IndexWriter(this.store, config);
		
		for(String file: this.files) {
			Document doc = new Document();
			
			String text = FileOps.readFile(file);
			doc.add(new Field("text", text, TextField.TYPE_STORED));
			iw.addDocument(doc);
		}
		
		iw.close();
	}
	
	private void searchDirectory(String queryStr) throws IOException, ParseException {
		DirectoryReader reader = DirectoryReader.open(this.store);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "text",
				this.analyzer);
		Query query = parser.parse(queryStr);
		
		ScoreDoc[] hits = searcher.search(query, null, 1000).scoreDocs;
		List<ScoreDoc> hitList = Arrays.asList(hits);
		
		System.out.println(hitList.size());
		for(ScoreDoc hit: hitList) {
			System.out.println(hit.score);
			System.out.println(searcher.doc(hit.doc).getField("text"));
		}
		
		// Now search the index:// Parse a simple query that searches for "text":
//	    QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "fieldname", analyzer);
//	    Query query = parser.parse("text");
//	    ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
//	    assertEquals(1, hits.length);
	    // Iterate through the results:
//	    for (int i = 0; i < hits.length; i++) {
//	      Document hitDoc = isearcher.doc(hits[i].doc);
//	      assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
//	    }
//	    ireader.close();
//	    directory.close();
	}
	
	private List<String> extractKeywords(String text) {
		List<String> ret = new ArrayList<String>();
		Annotation doc = this.nlp.process(text);
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		
		for(CoreMap sentence: sentences) {
			for(CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String type = token.getString(NamedEntityTagAnnotation.class);
				
				if(type.equals("PERSON") || type.equals("LOCATION")) {
					ret.add(token.getString(TextAnnotation.class).toLowerCase());
				}
			}
		}
		
		return ret;
	}
	
	private String buildQuery(List<String> keywords) {
		String ret = "";
		
		for(String keyword: keywords) {
			if(ret != "") {
				ret += " AND ";
			}
			
			ret += keyword;
		}
		
		return ret;
	}
}
