package tcd.ie.ir.newsarticles.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

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
	    String fieldsArr[] = {"contents"}; 
	    Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());               
	    QueryParser queryParser = new MultiFieldQueryParser(fieldsArr, analyzer);
	    BooleanQuery query = multipleFieldQuery(searchQuery, queryParser);  
	    TopDocs topDocs = indexSearcher.search(query, 5);
	    ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	    ArrayList<ResultantObject> resultDocs = new ArrayList<>();
	    int i = 1;
	    for (ScoreDoc scoreDoc: scoreDocs) {
	        String currentID = indexSearcher.doc(scoreDoc.doc).get(FileUtils.getDocnumIndex());
	        ResultantObject currentDoc = new ResultantObject(currentID, scoreDoc.score, i++);
	        resultDocs.add(currentDoc);
	    }
	    
	    return resultDocs;     
	}
    
    public static BooleanQuery multipleFieldQuery(QueryObject queryObj,QueryParser queryParser) throws ParseException {
		
    		String title = queryObj.getTitle();
		String desc = queryObj.getDesc();
		String narr = queryObj.getNarr();
		
		String nTitle = normalize(title);
		String nDesc = normalize(desc);
		String nNarr = normalize(narr);
		
		BoostQuery qTitle = new BoostQuery(queryParser.parse(nTitle), 2);		
		BoostQuery qDesc = new BoostQuery(queryParser.parse(nDesc), 2);
		BoostQuery qNarr = new BoostQuery(queryParser.parse(nNarr), 2);
		
		BooleanQuery booleanQuery = new BooleanQuery.Builder()
				.add(qTitle, BooleanClause.Occur.MUST)
				.add(qDesc, BooleanClause.Occur.MUST)
				.add(qNarr, BooleanClause.Occur.MUST)
				.build();	
		
		return booleanQuery;
	}
    
    
    private static String normalize(String query) {
    		return QueryParser.escape(query);	
	}
    
    public void close() throws IOException {
        indexWriter.close();
    }

}
