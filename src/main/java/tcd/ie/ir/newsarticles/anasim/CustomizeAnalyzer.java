package tcd.ie.ir.newsarticles.anasim;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CustomizeAnalyzer extends Analyzer{

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		final Set<String> posTags = new HashSet<>();
		Tokenizer source = new StandardTokenizer(); 
		//PartOfSpeechAttribute posAtt = source.addAttribute(PartOfSpeechAttribute.class);
	    TokenStream filter = new LowerCaseFilter(source);                
	    filter = new StopFilter(filter, StandardAnalyzer.ENGLISH_STOP_WORDS_SET);  
	    filter = new PartOfSpeechTaggingFilter(filter);
	    filter = new NGramTokenFilter(filter, 3, 5);
	    //filter.addAttribute(PartOfSpeechAttribute.class);
	    
        TokenStreamComponents tokenStreamComponents = new TokenStreamComponents(source, filter);
        return tokenStreamComponents;
	}

}
