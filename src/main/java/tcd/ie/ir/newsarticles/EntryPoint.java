package tcd.ie.ir.newsarticles;

import java.io.File;
import java.io.IOException;

import tcd.ie.ir.newsarticles.impl.SearchIndexer;
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

		final File fbisDocDir = new File(FileUtils.getFbisDocumentsPath());
		
		//final File latimesDocDir = new File(FileUtils.getLatimesDocumentsPath());

		long sTime = System.currentTimeMillis();		
		SearchIndexer searchIndex;
		try {
			searchIndex = new SearchIndexer(FileUtils.getDocIndexPath());
			searchIndex.configuration();
			searchIndex.invokeDocsIndex(fbisDocDir);
			searchIndex.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println((System.currentTimeMillis() - sTime)/1000 + " total sec");
	}
}
