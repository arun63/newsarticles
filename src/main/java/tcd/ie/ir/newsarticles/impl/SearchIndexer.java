package tcd.ie.ir.newsarticles.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import tcd.ie.ir.newsarticles.entity.QueryObject;
import tcd.ie.ir.newsarticles.entity.ResultantObject;
import tcd.ie.ir.newsarticles.parser.DocumentParser;
import tcd.ie.ir.newsarticles.utils.FileUtils;

/**
 * 
 * @author arun
 *
 */

public class SearchIndexer {
	
	private IndexWriter indexWriter;
    private static FSDirectory indexFSDir;
    
    public SearchIndexer(String indexPath) throws IOException {
        indexFSDir = FSDirectory.open(Paths.get(indexPath));
    }
    
    public void configuration() throws IOException {
        Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);   
        iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        //iwConfig.setSimilarity(new ClassicSimilarity());
        iwConfig.setRAMBufferSizeMB(256.0);
        indexWriter = new IndexWriter(indexFSDir, iwConfig);
    }
    
    public void invokeDocsIndex(File docsPath) throws IOException {
		if (docsPath.canRead()) {
			if (docsPath.isDirectory()) {
				String[] files = docsPath.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						invokeDocsIndex(new File(docsPath, files[i]));
					}
				}
			} else {
				DocumentParser allDocs = new DocumentParser(docsPath);
				Document doc;
				while (allDocs.hasNext()) {
					doc = allDocs.next();
					if (doc != null && doc.getField(FileUtils.getContentsIndex()) != null)
						indexWriter.addDocument(doc);
				}
			}
		}
	}
    
    public static ArrayList<ResultantObject> invokeSearch(QueryObject searchQuery) throws IOException, ParseException {
    	
		DirectoryReader indexDirReader = DirectoryReader.open(indexFSDir);
	    IndexSearcher indexSearcher = new IndexSearcher(indexDirReader);
	    //indexSearcher.setSimilarity(new ClassicSimilarity());
	    String fieldsArr[] = {"contents"}; 
	    Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());               
	    QueryParser queryParser = new MultiFieldQueryParser(fieldsArr, analyzer);
	    Query query = multipleFieldQuery(searchQuery, queryParser);  
	    TopDocs topDocs = indexSearcher.search(query, 1000);
	    ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	    ArrayList<ResultantObject> resultDocs = new ArrayList<>();
	    int i = 1;
	    for (ScoreDoc scoreDoc: scoreDocs) {
	    		//System.out.println(scoreDoc.doc);
	        String currentID = indexSearcher.doc(scoreDoc.doc).get(FileUtils.getDocnumIndex());
	        ResultantObject currentDoc = new ResultantObject(currentID, scoreDoc.score, i++);
	        resultDocs.add(currentDoc);
	    }
	    return resultDocs;     
	}
    
    public static BooleanQuery multipleFieldQuery(QueryObject queryObj,QueryParser queryParser) throws ParseException {
		
    		ArrayList<String> nStrings = new ArrayList<>();
    		String title = queryObj.getTitle();
		String desc = queryObj.getDesc();
		String narr = queryObj.getNarr();
		
		String nTitle = normalize(title);
		String nDesc = normalize(desc);
		String nNarr = normalize(narr);
		
		nStrings.add(nTitle);
		nStrings.add(nDesc);
		nStrings.add(nNarr);
		
		BoostQuery qTitle = new BoostQuery(queryParser.parse(nTitle),  (float) 0.5);		
		BoostQuery qDesc = new BoostQuery(queryParser.parse(nDesc), (float) 1.2);
		BoostQuery qNarr = new BoostQuery(queryParser.parse(nNarr), (float) 2);
		
		/*List<String> tagStrings = getTaggerString(nStrings);
		//System.out.println("Tags: " + tagStrings.toString());
		BoostQuery qTitle = new BoostQuery(queryParser.parse(tagStrings.get(0)),  (float) 0.5);		
		BoostQuery qDesc = new BoostQuery(queryParser.parse(tagStrings.get(1)), (float) 1.2);
		BoostQuery qNarr = new BoostQuery(queryParser.parse(tagStrings.get(2)), (float) 2); */
		
		BooleanQuery booleanQuery = new BooleanQuery.Builder()
				.add(qTitle, BooleanClause.Occur.SHOULD)
				.add(qDesc, BooleanClause.Occur.SHOULD)
				.add(qNarr, BooleanClause.Occur.SHOULD)
				.build();	
		
		return booleanQuery;
	}
    
    public static List<String> getTaggerString(List<String> toTags) {
    		
    		ArrayList<String> taggedString = new ArrayList<>();
    		MaxentTagger maxTagger = loadNLPLibrary();
    		for(String toTag : toTags) {
    			toTag = tokenizer(toTag);
    			System.out.println(toTag);
    			taggedString.add(maxTagger.tagString(toTag));
    		}
    		
    		return taggedString;
    }

	public static MaxentTagger loadNLPLibrary() {
    		MaxentTagger maxTagger = null;
    		try {
    				maxTagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
    				//maxTagger = new MaxentTagger(FileUtils.getNlpModelEnDist(), 
				//		loadNLPProperties(FileUtils.getNlpModelEnDistProps()));
			} catch (Exception e) {
				e.printStackTrace();
			}
    		return maxTagger;
    }
	
	public static String tokenizer(String toToken) {
		StringTokenizer sTokenizer = new StringTokenizer(toToken);
		StringBuilder sb = new StringBuilder();
		while (sTokenizer.hasMoreTokens()) {
			String token = sTokenizer.nextToken();
			if (!token.contains("$") && !token.contains("\'") 
					&& !token.contains("(") && !token.contains(")")
					&& !token.contains(",") && !token.contains(".") 
					&& !token.contains(":") && !token.contains("`") 
					&& !token.contains(";")) {
				sb.append(token + " ");
			}
		}
		return sb.toString();
	}
    
    public static Properties loadNLPProperties(String fileName) throws IOException {
    		Properties props = new Properties();
		FileInputStream inStream = null;
		inStream = new FileInputStream(fileName);
		props.load(inStream);
		inStream.close();
		return props;
    }
    
    
    private static String normalize(String query) {
    		return QueryParser.escape(query);	
	}
    
    public void close() throws IOException {
        indexWriter.close();
    }

}
