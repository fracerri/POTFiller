package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JTextArea;

public class Utils {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy-HH.mm.ss");
	public static final  String NEWLINE = "\n";
	public static final  String EQ = "=";
	public static final  String DOT = ".";
	public static final String EXT_FOLDER = "ext";
	public static final String OUTPUT_FOLDER_FILLED_PO = "output"+ File.separator+ "filled_po";
	public static final String OUTPUT_FOLDER_LOGS= "output"+ File.separator+ "logs";
	public static final String OUTPUT_FOLDER_MANUAL_TRANS = "output"+ File.separator+ "manual_translations";
	public static final String PO_EXTENSION = ".po";
	public static final String SUFFIX_LOG_FILE = "_log_file.txt";
	public static final String SUFFIX_MANUAL_TRANS = "_manualTranslation.txt";
	public static final String MSGID = "msgid";
	private static final String TMP = "_TMP";
	public static HashMap<String, String> propertiesMapEN = new HashMap<>();
	public static HashMap<String, String> propertiesMapIT = new HashMap<>();
	public static HashMap<String, String> propertiesMapFR = new HashMap<>();
	public static HashMap<String, String> propertiesMapDE = new HashMap<>();
	public static HashMap<String, String> propertiesMapES = new HashMap<>();

	private Utils() {}
	/**
	 * 
	 * @param className
	 * @param path
	 * @return
	 */
	public static ImageIcon createImageIcon(Class className, String path) {
        java.net.URL imgURL = className.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	
	/**
	 * 
	 * @return
	 */
	public static String getTimestamp() {
		return sdf.format(new Date());
	}
	
	/**
	 * Load properties in map
	 * @param dirProperties
	 * @param log 
	 * @throws IOException
	 */
	public static void loadProperties(File dirProperties, JTextArea log) throws IOException {
		//Clear maps
		propertiesMapEN.clear();
		propertiesMapIT.clear();
		propertiesMapDE.clear();
		propertiesMapES.clear();
		propertiesMapFR.clear();
		
		if(dirProperties.listFiles().length > 0) {
			for (final File prop : dirProperties.listFiles()) {
				if (prop.getName().lastIndexOf(".properties") > -1) {
					fillPropertiesMaps(log, prop);
				}
			}
			
		}
		
		//load ext properties (projects ones)
		File dirExt = new File(dirProperties+File.separator+EXT_FOLDER);
		if(dirExt.listFiles().length > 0) {
			for (final File extProp : dirExt.listFiles()) {
				fillPropertiesMaps(log, extProp);
			}
		}
	}
	
	/**
	 * 
	 * @param log
	 * @param prop
	 * @throws IOException
	 */
	private static void fillPropertiesMaps(JTextArea log, final File prop) throws IOException {
		String name = prop.getName().substring(0, prop.getName().lastIndexOf(DOT));
		String lang = prop.getName().substring(name.length() - 2, name.length());
		switch (lang) {
		case "en":
			fillMapEN(propertiesMapEN, prop, log);
			break;
		case "it":
			fillMap(propertiesMapIT, prop, log);
			break;
		case "fr":
			fillMap(propertiesMapFR, prop, log);
			break;
		case "de":
			fillMap(propertiesMapDE, prop, log);
			break;
		case "es":
			fillMap(propertiesMapES, prop, log);
			break;
		default:
		}
	}
	
	/**
	 * Fill map --> map id : message Id and map value : message code
	 * @param map
	 * @param prop
	 * @param log
	 * @throws IOException
	 */
	private static void fillMapEN(HashMap<String, String> map, File prop, JTextArea log) throws IOException {
		FileReader fr = new FileReader(prop.getPath());
		BufferedReader br = new BufferedReader(fr);

		try {
			String line = br.readLine();

			while (line != null) {
				if (line.indexOf(EQ) > -1) {
					String code = line.substring(0, line.indexOf(EQ));
					String messageId = line.substring(line.indexOf(EQ) + 1, line.length());
					map.put(messageId, code);
				}
				
				line = br.readLine();
			}
		} finally {
			log.append(Utils.getTimestamp() + " Properties file " + prop.getName() + " loaded." + NEWLINE);
			br.close();
			fr.close();
		}
	}
	
	/**
	 * Fill map --> map id : code and map value : message translated
	 * @param map
	 * @param prop
	 * @param log
	 * @throws IOException
	 */
	private static void fillMap(HashMap<String, String> map, File prop, JTextArea log) throws IOException {
		FileReader fr = new FileReader(prop.getPath());
		BufferedReader br = new BufferedReader(fr);
		try {
			String line = br.readLine();
			while (line != null) {
				if (line.indexOf(EQ) > -1) {
					String code = line.substring(0, line.indexOf(EQ));
					String message = line.substring(line.indexOf(EQ) + 1, line.length());
					map.put(code, message);
				}

				line = br.readLine();
			}
			
		} finally {
			log.append(Utils.getTimestamp() + " Properties file " + prop.getName() + " loaded." + NEWLINE);
			br.close();
			fr.close();
		}
	}

	
	/**
	 * 
	 * @param filePO
	 * @param propertiesFiles
	 * @throws IOException 
	 */
	public static void fillPO(File filePO) throws IOException {
			String name = filePO.getName().substring(0, filePO.getName().lastIndexOf(DOT));
			switch (name) {
			case "it":
				updateFilePO(filePO, propertiesMapIT);
				break;
			case "fr":
				updateFilePO(filePO, propertiesMapFR);
				break;
			case "de":
				updateFilePO(filePO, propertiesMapDE);
				break;
			case "es":
				updateFilePO(filePO, propertiesMapES);
				break;
			default:
			}
	}
	
	
	private static void updateFilePO(File filePO, HashMap<String, String> propertiesMap)
			throws IOException {
		int found = 0;
		int already = 0;
		int notfound = 0;
		
		String name = filePO.getName().substring(0, filePO.getName().lastIndexOf(DOT));
		//RESULT
		File dirResult = new File(filePO.getParent() + File.separator + OUTPUT_FOLDER_FILLED_PO);
		if(!dirResult.exists()) {
			dirResult.mkdirs();
		}
		File result = new File(dirResult.getPath() + File.separator + name + PO_EXTENSION);
		if(!result.exists()) {
			result.createNewFile();
		}
		//END RESULT
	
		//LOG FILE
		File dirLog = new File(filePO.getParent() + File.separator + OUTPUT_FOLDER_LOGS);
		if(!dirLog.exists()) {
			dirLog.mkdirs();
		}
		File logFile = new File(dirLog.getPath() + File.separator + name + SUFFIX_LOG_FILE);
		if(!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile);
		BufferedWriter logFilewr = new BufferedWriter(fw);
		//END LOG FILE
		
		//MANUAL TRANSLATION
		File manTransDir = new File(filePO.getParent() + File.separator + OUTPUT_FOLDER_MANUAL_TRANS);
		if(!manTransDir.exists()) {
			manTransDir.mkdirs();
		}
		File manual = new File(manTransDir.getPath() + File.separator + name + SUFFIX_MANUAL_TRANS);
			if(!manual.exists()) {
				manual.createNewFile();
			}
		FileWriter fm = new FileWriter(manual);
		BufferedWriter wm = new BufferedWriter(fm);
		//END MANUAL TRANSLATION
				
		
		BufferedWriter wr = new BufferedWriter( new FileWriter(result));
		BufferedReader br = new BufferedReader(new FileReader(filePO.getPath()));
			logFilewr.write("----" + filePO + " UPDATED ");
			logFilewr.newLine();
			String line = br.readLine();
			while (line != null) {
				wr.write(line);
				wr.newLine();
				if (line.startsWith(MSGID)) {
					String msgstr = br.readLine();
					String str = msgstr.substring(msgstr.indexOf("\"") + 1, msgstr.length() - 1);
					if (str.isEmpty()) {
						String id = line.substring(line.indexOf("\"") + 1, line.length() - 1);
						String code = propertiesMapEN.get(id);
						if (code != null) {
							String langMessage = propertiesMap.get(code);
							if (langMessage != null) {
								wr.write("msgstr \"" + langMessage + "\"");
								wr.newLine();
								logFilewr.write("id: " + id + " - " + "code: " + code);
								logFilewr.newLine();
								found++;
							}
						}else {
							wr.write(msgstr);
							wr.newLine();
							logFilewr.write("NOT FOUND MESSAGE ID: " + id);
							logFilewr.newLine();
							if(!id.equals("")) {
								notfound++;
								wm.write(id);
								wm.newLine();
							}
						}
					}else {
						wr.write(msgstr);
						wr.newLine();
						already++;
					}
				}

				line = br.readLine();
			}
		
	int tot = found+notfound+already;
	logFilewr.write("------RESULT --> TOT ITEMS: " + tot + " , FOUND: " + found + " , NOT FOUND: " + notfound + " , ALREADY PRESENT: " + already);
	logFilewr.newLine();
	wr.close();
	br.close();
	logFilewr.close();
	wm.close();
	}
	
	
	public static void loadManualTranslations(File dirManual, File dirPO, JTextArea log ) {
		
		for(File manualFile : dirManual.listFiles()) {
			HashMap<String,String> manualMap = getManualTranslationsMap(manualFile,log);
			String lang = manualFile.getName().substring(0, 2);
			File dirResult = new File(dirPO + File.separator + OUTPUT_FOLDER_FILLED_PO);

			File filePO = getFileInDir(dirResult,lang+PO_EXTENSION);
			FileReader fr = null;
			try {
				//RESULT
				String name = filePO.getName().substring(0, filePO.getName().lastIndexOf(DOT));
				File result = new File(dirResult.getPath() + File.separator + name +TMP+ PO_EXTENSION);
				if(!result.exists()) {
					result.createNewFile();
				}
				//END RESULT
				
				BufferedWriter wr = new BufferedWriter( new FileWriter(result));
				
				fr = new FileReader(filePO);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					wr.write(line);
					wr.newLine();
					if(line.startsWith(MSGID)) {
						String msgstr = br.readLine();
						String str = msgstr.substring(msgstr.indexOf("\"") + 1, msgstr.length() - 1);
						if (str.isEmpty()) {
							String id = line.substring(line.indexOf("\"") + 1, line.length() - 1);
							String translation = manualMap.get(id);
							if (translation != null) {
								wr.write("msgstr \"" + translation + "\"");
								wr.newLine();
							}else {
								wr.write(msgstr);
								wr.newLine();
							}
						}else {
							wr.write(msgstr);
							wr.newLine();
						}
					}
					line = br.readLine();
				}
				
				fr.close();
				wr.close();
				Files.move(Paths.get(result.getPath()), Paths.get(filePO.getPath()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				log.append(getTimestamp() + " " + e.getMessage());
			}
		}
		
	}
	
	/**
	 * 
	 * @param manualFile
	 * @param log 
	 * @return
	 */
	private static HashMap<String, String> getManualTranslationsMap(File manualFile, JTextArea log) {
		HashMap<String, String> map = new HashMap<>();
		FileReader fr = null;
		try {
			fr = new FileReader(manualFile);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String enMessageid = line.substring(0, line.indexOf("="));
				String translation = line.substring(line.indexOf("=") + 1, line.length());
				map.put(enMessageid, translation);
				line = br.readLine();
			}
			fr.close();
		} catch (IOException e) {
			log.append(getTimestamp() + " " + e.getMessage());
		}
		return map;
	}
	
	public static File getFileInDir(File dir, String fileName) {
		for(File file : dir.listFiles()) {
			if(file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}
	
}
