package tcd.ie.ir.newsarticles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;

import tcd.ie.ir.newsarticles.entity.QueryObject;
import tcd.ie.ir.newsarticles.entity.ResultantObject;
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
		
		FileUtils.createAllReqDir(true);

		final File docsDir = new File(FileUtils.getDataFolderPath());

		long sTime = System.currentTimeMillis();		
		SearchIndexer searchIndex;
		try {
			System.out.println("Parsing and indexing the documents");
			searchIndex = new SearchIndexer(FileUtils.getDocIndexPath());
			searchIndex.configuration();
			searchIndex.invokeDocsIndex(docsDir);
			searchIndex.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Indexing the document took: " + (System.currentTimeMillis() - sTime)/1000 + " total sec");
		
		
		final File queryFile = new File(FileUtils.getQueryFilePath());
		QueryDocParser queryDocParser = null;
		Map<Integer, QueryObject> queryObj = new LinkedHashMap<Integer, QueryObject>();
		try {
			queryDocParser = new QueryDocParser(queryFile);
			queryObj = queryDocParser.parseQuery();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			queryDocParser.closeReader(queryFile);
		}

		// Print the query output
		//printOutput(queryObj);	
		
		try {
			invokeQuerySearch(queryObj);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void invokeQuerySearch(Map<Integer, QueryObject> queryObj) throws IOException, ParseException {
		String fileName = "standard_def";
		String fileToWrite = FileUtils.getResultantPath() + "/" + fileName;
        FileUtils.checkFileExists(fileToWrite);
        FileWriter resultSet = new FileWriter(fileToWrite);
        System.out.println("Processing the queries");
        
        for(Map.Entry<Integer, QueryObject> queryMap : queryObj.entrySet()) {
        		
        		QueryObject query = queryMap.getValue();
        		String result = "";
                ArrayList<ResultantObject> resultList = SearchIndexer.invokeSearch(query);
                for (ResultantObject currentResult: resultList) {
                    resultSet.append(query.getNum()).append(" Q0 ").append(currentResult.getrId());
                    resultSet.append(" ").append(String.valueOf(currentResult.getRank())).append(" ");
                    resultSet.append(String.valueOf(currentResult.getScore())).append(" STANDARD\n");
                }
                resultSet.write(result);
        	
        }
        System.out.println("Finished processing the queries - resultant path: " + fileToWrite);
        resultSet.close();  
	}
	
	public static void printOutput(Map<Integer, QueryObject> queryObj) {
		for(Map.Entry<Integer, QueryObject> entry: queryObj.entrySet()) {
			System.out.println(entry.getValue().toString());
		}
	}
	
}
