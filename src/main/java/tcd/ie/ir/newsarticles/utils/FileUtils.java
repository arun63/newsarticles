package tcd.ie.ir.newsarticles.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 
 * @author arun
 *
 */

public class FileUtils {
	
	private static final String CURRENT_USER_DIRECTORY = System.getProperty("user.dir");
    private static final String FBIS_DOCUMENTS_PATH = CURRENT_USER_DIRECTORY + "/data/fbis/";
    private static final String FR94_DOCUMENTS_PATH = CURRENT_USER_DIRECTORY + "/data/fr94/";
    private static final String FT_DOCUMENTS_PATH = CURRENT_USER_DIRECTORY + "/data/ft/";
    private static final String LATIMES_DOCUMENTS_PATH = CURRENT_USER_DIRECTORY + "/data/latimes/";
    private static final String DOC_INDEX_PATH = CURRENT_USER_DIRECTORY + "/index/";
    private static final String RESULTANT_PATH = CURRENT_USER_DIRECTORY + "/test/results";
    private static final String RESULTANT_FILENAME = "/resultant";
    
    
    //Tag
    public static final String START_DOC_TAG = "<DOC>";
    public static final String END_DOC_TAG = "</DOC>";
    
    
    // Pattern Matching
    private static final String FBIS_DOC_PATTERN = "<DOCNO>\\s*(\\S+)\\s*<";
    
    public static void createDirectories(String path) {
		Path p = Paths.get(path);
        if (!Files.exists(p)) {
            try {
            	    Files.createDirectories(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
    
    public static void checkFileExists(String fileName) throws IOException {
    		File f = new File(fileName);
        if(!f.exists() && !f.isDirectory()) { 
        		Files.createFile(Paths.get(fileName));
        }
    }
    
    public static void createAllReqDir() {
		createDirectories(FileUtils.getDocIndexPath());
		createDirectories(FileUtils.getResultantPath());
	}
    
	public static String getResultantPath() {
		return RESULTANT_PATH;
	}
	public static String getDocIndexPath() {
		return DOC_INDEX_PATH;
	}

	public static String getResultantFilename() {
		return RESULTANT_FILENAME;
	}
	
	public static String getFbisDocumentsPath() {
		return FBIS_DOCUMENTS_PATH;
	}

	public static String getFbisDocPattern() {
		return FBIS_DOC_PATTERN;
	}

	public static String getFr94DocumentsPath() {
		return FR94_DOCUMENTS_PATH;
	}

	public static String getFtDocumentsPath() {
		return FT_DOCUMENTS_PATH;
	}

	public static String getLatimesDocumentsPath() {
		return LATIMES_DOCUMENTS_PATH;
	}
}
