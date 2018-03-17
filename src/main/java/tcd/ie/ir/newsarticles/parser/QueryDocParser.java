package tcd.ie.ir.newsarticles.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tcd.ie.ir.newsarticles.entity.QueryObject;
import tcd.ie.ir.newsarticles.utils.FileUtils;

/**
 * 
 * @author arun
 *
 */

public class QueryDocParser {
		
	private BufferedReader bufferReader;
	
	public QueryDocParser(File file) throws FileNotFoundException, UnsupportedEncodingException {
		bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		System.out.println("Query Parsing " + file.getName());
	}

	public Map<Integer, QueryObject> parseQuery() {
		LinkedHashMap<Integer, QueryObject> lHashMap = new LinkedHashMap<Integer, QueryObject>();
		StringBuilder title = new StringBuilder();
		StringBuilder desc = new StringBuilder();
		StringBuilder narr = new StringBuilder();
		try {
			String line;
			boolean descFlag = false;
			boolean narrFlag = false;
			String qNum = null;
			QueryObject queryObj = null;
			while (true) {
				line = bufferReader.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith(FileUtils.TITLE_START_TAG)) {
					line = normPattern(line);
					title.append(new String(line.substring(line.indexOf(FileUtils.TITLE_START_TAG) 
							+ FileUtils.TITLE_START_TAG.length())));
				}
				if (line.startsWith(FileUtils.DESC_START_TAG)) {
					descFlag = true;
					continue;
				}
				if (line.startsWith(FileUtils.NARR_START_TAG)) {
					descFlag = false;
					narrFlag = true;
					continue;
				}
				if (line.startsWith(FileUtils.NUM_START_TAG)) {
					Matcher m = getQueryNumMatcher(line);
					if (m.find()) {
						qNum = m.group(2);
					}
				}
				
				if (line.startsWith(FileUtils.TOP_END_TAG)) {
					//When we identify end top tag, we store it to the object
					narrFlag = false;
					queryObj = new QueryObject();
					queryObj.setNum(qNum);
					queryObj.setTitle(title.toString());
					queryObj.setDesc(desc.toString());
					queryObj.setNarr(narr.toString());
					lHashMap.put(Integer.parseInt(qNum), queryObj);
					//reinitialize
					title = new StringBuilder();
					desc = new StringBuilder();
					narr = new StringBuilder();
				}
				
				if (descFlag) {
					normalize(desc, line);
				}
				if (narrFlag) {
					normalize(narr, line);
				}	
			}
		} catch (IOException e) {
			
		}
		return lHashMap;
	}
	
	private Matcher getQueryNumMatcher(String line) {
		Pattern docNumTag = Pattern.compile(FileUtils.getQueryNumPattern());
		return docNumTag.matcher(line); 
	}

	private void normalize(StringBuilder sb, String line) {
		line = normPattern(line);
		sb.append(line + " ");
	}
	
	private String normPattern(String line) {
		Pattern patt = Pattern.compile(FileUtils.getQueryNormalizePattern());
		Matcher mat = patt.matcher(line);
		return mat.replaceAll(" ");
	}
}
