package tcd.ie.ir.newsarticles.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import tcd.ie.ir.newsarticles.utils.FileUtils;

/**
 * 
 * @author arun
 *
 */

public class DocumentParser implements Iterator<Document>{

	private BufferedReader bufferReader;
	private boolean hasNext = false;
	
	public DocumentParser(File file) throws FileNotFoundException {
		bufferReader = new BufferedReader(new FileReader(file));
		System.out.println("Parsing " + file.getName());
	}
	
	public boolean hasNext() {
		return !hasNext;
	}

	public Document next() {
		Document document = new Document();
		StringBuilder stringBuilder = new StringBuilder();
		
		String line;
		boolean docFlag = false;
		try {			
			while (true) {
				line = bufferReader.readLine();
				if (line == null) {
					hasNext = true;
					break;
				}
				if (!docFlag) {
					if (line.startsWith(FileUtils.START_DOC_TAG)) {
						docFlag = true;
					}	
					else {
						continue;
					}			
				}
				if (line.startsWith(FileUtils.END_DOC_TAG)) {
					docFlag = false;
					stringBuilder.append(line);
					break;
				}

				Matcher matcher = getDocNumMatcher(line);
				if (matcher.find()) {
					String docNum = matcher.group(1);
					document.add(new StringField("docNum", docNum, Field.Store.YES));
				}
				
				stringBuilder.append(line);
			}
			if (stringBuilder.length() > 0)
				document.add(new TextField("contents", stringBuilder.toString(), Field.Store.NO));
			
		} catch (IOException e) {
			document = null;
		}
		return document;
	}
	
	private Matcher getDocNumMatcher(String line) {
		Pattern docNumTag = Pattern.compile(FileUtils.getFbisDocPattern());
		return docNumTag.matcher(line); 
	}
}
