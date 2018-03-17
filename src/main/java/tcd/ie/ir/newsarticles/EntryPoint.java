package tcd.ie.ir.newsarticles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

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
	}
	
	
	public static void printOutput(Map<Integer, QueryObject> queryObj) {
		for(Map.Entry<Integer, QueryObject> entry: queryObj.entrySet()) {
			System.out.println(entry.getValue().toString());
		}
	}
}
