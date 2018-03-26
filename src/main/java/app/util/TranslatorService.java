package app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Gives translations for questions and keywords using Translate Shell. 
 * For using this class, Translate Shell must be installed. 
 * https://github.com/soimort/translate-shell#installation
 **/

public class TranslatorService {
	
	private String executeCommand(String command, String lang, String text) {
		
		//Translate Shell's abbr. of hindi - hi. 
		if(lang.equals("hi_IN")) {
			lang = "hi"; 
		}
		
		String finalCommand = command + lang + " '" + text + "'";
		//System.out.println(finalCommand);
		
		StringBuffer output = new StringBuffer();
		
		Process p;
		try {
			p = Runtime.getRuntime().exec(finalCommand);
			p.waitFor();
			BufferedReader reader = 
                           new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + " ");
			}
			//System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
	}
	
	/**
	 * Accepts question in English and returns a JSONObject having its translation in target languages
	 * @param question
	 * @return
	 * @throws FileNotFoundException
	 */
	public JSONObject translateNewQuestion(String question) throws FileNotFoundException {
		JSONObject result = new JSONObject();
        Vector<String> targetLanguages = getTargetLanguages();
        for (String lang : targetLanguages) {
        	if (lang.equals("en")) {
        		result.put("en", question);
        	}
        	else {
        		result.put(lang, executeCommand("trans -b :", lang, question));
        	}
        }
        return result;
	}
	
	/**
	 * Accepts keywordList in English and returns a JSONObject having its translation in target languages
	 * @param keywordList
	 * @return
	 * @throws FileNotFoundException 
	 */
	public JSONObject translateNewKeywords(List<String> keywordList) throws FileNotFoundException {
		JSONArray newKeywordList = new JSONArray();
		newKeywordList.addAll(keywordList);
		JSONObject result = new JSONObject();
		Vector<String> targetLanguages = getTargetLanguages();
		
        for (String lang : targetLanguages) {
        	if (lang.equals("en")) {
        		result.put("en", keywordList);
        	}
        	else {
        		result.put(lang, translateKeywords(newKeywordList, lang, "trans -b :"));
        	}
        }
        return result;
	}
	
	private JSONArray translateKeywords(JSONArray keywordList, String lang, String command) {
		JSONArray translatedKeywordList = new JSONArray();
		
		for (int i = 0; i < keywordList.size(); i++) {
			translatedKeywordList.add(executeCommand(command, lang, (String) keywordList.get(i)));
		}
		return translatedKeywordList;
	}
	
	private void translateQuestions() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONArray a = (JSONArray) parser.parse(new FileReader("src/resources/List_question_with_attributes.json"));
		Vector<String> targetLanguages = getTargetLanguages();

        //Example command : trans -b :de "Why is the sky blue?"
        String command = "trans -b :";
        
        JSONArray writeAll = new JSONArray();
        JSONArray writeAdditional = new JSONArray();
        int all_id = 0, add_id = 0;
        
        // loop over all JSONObjects to get translation for questions and keywords
		for(Object o : a) {
			JSONObject jsonObject = (JSONObject) o;
			JSONObject ltq = (JSONObject) jsonObject.get("languageToQuestion");
			JSONObject ltk = (JSONObject) jsonObject.get("languageToKeyword");
			String question = (String) ltq.get("en");
			Boolean keywordPresent = true;
			
			if(ltk.isEmpty())
				keywordPresent = false;
			
			JSONArray keywordList = new JSONArray();
			if (keywordPresent == true)
				if (ltk.containsKey("en"))
					keywordList = (JSONArray) ltk.get("en");
				else if (ltk.containsKey(""))
					keywordList = (JSONArray) ltk.get("");
			
			int addedLangs = 0;
			for (String lang : targetLanguages) {
				if (!ltq.containsKey(lang) || ltq.containsValue(null)) {
					ltq.put(lang, executeCommand(command, lang, question));
					if (keywordPresent == true)
						ltk.put(lang, translateKeywords(keywordList, lang, command));
					addedLangs++;	
				}
			}
			
			JSONObject result = new JSONObject();
			result.put("languageToQuestion", ltq);
			result.put("languageToKeyword", ltk);
			
			// translation for all langs added
			if (addedLangs == targetLanguages.size()-1) {
				result.put("id", ++all_id);
				writeAll.add(result);
			}
			
			// translation for only remaining langs added
			else {
				result.put("id", ++add_id);
				writeAdditional.add(result);
			}
			
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		writer.writeValue(new File("src/resources/allTranslations.json"), writeAll);
		writer.writeValue(new File("src/resources/addedTranslations.json"), writeAdditional);
		
	}

	/**
	 * Returns a vector of target languages as given in the file
	 * @return
	 * @throws FileNotFoundException
	 */
	private Vector<String> getTargetLanguages() throws FileNotFoundException {
		File file = new File("src/resources/List_target_language.txt");
        Scanner sc = new Scanner(file);
        Vector<String> targetLanguages = new Vector<>();
        
        // create a vector of target langs
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            int colon = line.indexOf(":");
            String lang = line.substring(0, colon);
            targetLanguages.add(lang.trim());
        }
		return targetLanguages;
	}
	

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		TranslatorService obj = new TranslatorService();
		obj.translateQuestions();
//		System.out.println(obj.translateNewQuestion("Why is the sky blue?"));
//		List<String> list = new ArrayList<String>();
//		list.add("sky");
//		list.add("blue");
//		System.out.println(obj.translateNewKeywords(list));
	}
}
