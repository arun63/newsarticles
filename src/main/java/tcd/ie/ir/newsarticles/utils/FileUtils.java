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
    private static final String DATA_FOLDER_PATH = CURRENT_USER_DIRECTORY + "/data/";
    private static final String FBIS_DOCUMENTS_PATH = CURRENT_USER_DIRECTORY + "/data/fbis/";
    private static final String FR94_DOCUMENTS_PATH = CURRENT_USER_DIRECTORY + "/data/fr94/";
    private static final String FT_DOCUMENTS_PATH = CURRENT_USER_DIRECTORY + "/data/ft/";
    private static final String LATIMES_DOCUMENTS_PATH = CURRENT_USER_DIRECTORY + "/data/latimes/";
    private static final String DOC_INDEX_PATH = CURRENT_USER_DIRECTORY + "/index/";
    private static final String QUERY_FILE_PATH = CURRENT_USER_DIRECTORY + "/test/topics.401-450";
    private static final String RESULTANT_PATH = CURRENT_USER_DIRECTORY + "/test/results";
    private static final String RESULTANT_FILENAME = "/resultant";
    private static final String NLP_MODEL_FOLDER = CURRENT_USER_DIRECTORY + "/models/";
    private static final String NLP_MODEL_WSJ = NLP_MODEL_FOLDER + "wsj-0-18-left3words-nodistsim.tagger";
    private static final String NLP_MODLE_WSJ_PROPS = NLP_MODEL_FOLDER + "wsj-0-18-left3words-nodistsim.tagger.props";
    private static final String NLP_MODEL_EN_DIST = NLP_MODEL_FOLDER + "english-left3words-distsim.tagger";
    private static final String NLP_MODEL_EN_DIST_PROPS = NLP_MODEL_FOLDER + "english-left3words-distsim.tagger.props";
    
    
    //Tag
    public static final String START_DOC_TAG = "<DOC>";
    public static final String END_DOC_TAG = "</DOC>";
    public static final String TOP_START_TAG = "<top>";
    public static final String TOP_END_TAG = "</top>";
    public static final String NUM_START_TAG = "<num>";
    public static final String NUM_END_TAG = "</num>";
    public static final String TITLE_START_TAG = "<title>";
    public static final String TITLE_END_TAG = "</title>";
    public static final String DESC_START_TAG = "<desc>";
    public static final String DESC_END_TAG = "</desc>";
    public static final String NARR_START_TAG = "<narr>";
    public static final String NARR_END_TAG = "</narr>";
    
    
    // Pattern Matching
    private static final String DOC_NUM_PATTERN = "<DOCNO>\\s*(\\S+)\\s*<";
    private static final String QUERY_NUM_PATTERN = "<num>\\s*(\\S+)\\s*(\\d+)\\s*";
    private static final String QUERY_NORMALIZE_PATTERN = "[/\\(\\)\\?-]";
    
    
    //Indexer
    private static final String DOCNUM_INDEX = "docNum";
    private static final String CONTENTS_INDEX = "contents";
    
    
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

	public static String getDocNumPattern() {
		return DOC_NUM_PATTERN;
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

	public static String getDataFolderPath() {
		return DATA_FOLDER_PATH;
	}

	public static String getQueryFilePath() {
		return QUERY_FILE_PATH;
	}

	public static String getQueryNumPattern() {
		return QUERY_NUM_PATTERN;
	}

	public static String getQueryNormalizePattern() {
		return QUERY_NORMALIZE_PATTERN;
	}

	public static String getDocnumIndex() {
		return DOCNUM_INDEX;
	}

	public static String getContentsIndex() {
		return CONTENTS_INDEX;
	}

	public static String getNlpModelWsj() {
		return NLP_MODEL_WSJ;
	}

	public static String getNlpModleWsjProps() {
		return NLP_MODLE_WSJ_PROPS;
	}

	public static String getNlpModelEnDistProps() {
		return NLP_MODEL_EN_DIST_PROPS;
	}

	public static String getNlpModelEnDist() {
		return NLP_MODEL_EN_DIST;
	}
	
}
