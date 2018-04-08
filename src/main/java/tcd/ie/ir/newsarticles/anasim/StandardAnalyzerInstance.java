package tcd.ie.ir.newsarticles.anasim;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * 
 * @author arun
 *
 */

public class StandardAnalyzerInstance extends StopwordAnalyzerBase{
	
	public StandardAnalyzerInstance(CharArraySet stopwords) {
		super(stopwords);
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new StandardFilter(tokenizer);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, stopwords);
        //tokenStream = new KStemFilter(tokenStream);
        //tokenStream = new PorterStemFilter(tokenStream);
        //tokenStream = new PartOfSpeechTaggingFilter(tokenStream);
        tokenStream = new NGramTokenFilter(tokenStream, 3, 5);
        TokenStreamComponents tokenStreamComponents = new TokenStreamComponents(tokenizer, tokenStream);
        return tokenStreamComponents;
	}

	
}
