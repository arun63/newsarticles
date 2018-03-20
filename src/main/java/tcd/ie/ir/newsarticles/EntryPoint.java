package tcd.ie.ir.newsarticles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import tcd.ie.ir.newsarticles.entity.QueryObject;
import tcd.ie.ir.newsarticles.impl.SearchIndexer;
import tcd.ie.ir.newsarticles.parser.QueryDocParser;
import tcd.ie.ir.newsarticles.utils.FileUtils;

/**
 * 
 * @author arun
 *
 */

public class EntryPoint 
{
	public static void main(String[] args) { 
		
		FileUtils.createAllReqDir();

		final File docsDir = new File(FileUtils.getDataFolderPath());

		long sTime = System.currentTimeMillis();		
		SearchIndexer searchIndex;
		try {
			searchIndex = new SearchIndexer(FileUtils.getDocIndexPath());
			searchIndex.configuration();
			searchIndex.invokeDocsIndex(docsDir);
			searchIndex.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println((System.currentTimeMillis() - sTime)/1000 + " total sec");
		
		
		final File queryFile = new File(FileUtils.getQueryFilePath());
		QueryDocParser queryDocParser;
		Map<Integer, QueryObject> queryObj = new LinkedHashMap<Integer, QueryObject>();
		try {
			queryDocParser = new QueryDocParser(queryFile);
			queryObj = queryDocParser.parseQuery();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// Print the query output
		printOutput(queryObj);
		try {
			fireQuery(401,queryObj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void printOutput(Map<Integer, QueryObject> queryObj) {
		for(Map.Entry<Integer, QueryObject> entry: queryObj.entrySet()) {
			System.out.println(entry.getValue().toString());
		}
	}
	
	public static void fireQuery(int index, Map<Integer, QueryObject> queryObj) throws Exception {
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(FileUtils.getDocIndexPath())));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());;
	    String field = "contents";
	    for(Map.Entry<Integer, QueryObject> entry: queryObj.entrySet()) {
	    	int querynumber = entry.getKey();
	    	String query = entry.getValue().getTitle();
	    	QueryParser queryParser = new QueryParser(field,analyzer);
	    	TopDocs results = searcher.search(queryParser.parse(query), 100);
	        ScoreDoc[] hits = results.scoreDocs;
	        File file = new File(FileUtils.getResultantPath()+"/results");
	        PrintWriter pw = new PrintWriter(file);
	        for(int i=0;i<hits.length;i++) {
	        	int s = hits[i].doc;
	        	Document d = searcher.doc(s);
	        	int j = i+1;
	        	pw.println(querynumber + " Q0 " +  d.get("ID") + " " + j + " " + hits[i].score + " STANDARD");
	        	pw.flush();
	        }
	        int numTotalHits = Math.toIntExact(results.totalHits);
	        System.out.println(numTotalHits + " total matching documents");
	    }
	    
	}
}
