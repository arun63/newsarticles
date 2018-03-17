package tcd.ie.ir.newsarticles.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import tcd.ie.ir.newsarticles.parser.DocumentParser;

/**
 * 
 * @author arun
 *
 */

public class SearchIndexer {
	
	private IndexWriter indexWriter;
    private FSDirectory indexFSDir;
    
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
					if (doc != null && doc.getField("contents") != null)
						indexWriter.addDocument(doc);
				}
			}
		}
	}
    
    public void close() throws IOException {
        indexWriter.close();
    }

}
