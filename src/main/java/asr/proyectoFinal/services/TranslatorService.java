package asr.proyectoFinal.services;

// import asr.proyectoFinal.models.YahooNew;
import asr.proyectoFinal.util.VCAPHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

public class TranslatorService {

	private static String apiKey = null;
	private static String apiUrl = null;
	private static LanguageTranslator languageTranslator = null;
	
	public static String translate(String content, String targetLanguage)	{
		// Check credentials
		if(apiKey == null || apiUrl == null)
			TranslatorService.setCredentials();

		// Create client if not existing
		if(languageTranslator == null)
			TranslatorService.createClient();
		
		TranslateOptions translateOptions = new TranslateOptions.Builder()
			.addText(content)
			.target(targetLanguage)
			.build();
		
		TranslationResult translationResult = languageTranslator.translate(translateOptions).execute().getResult();
		
		System.out.println(translationResult);
		
		String jsonTranslation = translationResult.toString();
		JsonObject rootObj = JsonParser.parseString(jsonTranslation).getAsJsonObject();
		JsonArray translations = rootObj.getAsJsonArray("translations");
		String firstTranslation = content;
		
		if(translations.size() > 0)
			firstTranslation = translations.get(0).getAsJsonObject().get("translation").getAsString();
		
		return firstTranslation;
	}

	private static void setCredentials() {
		if (System.getenv("VCAP_SERVICES") != null) {
            // When running in Bluemix, the VCAP_SERVICES env var will have the credentials
            // for all bound/connected services
            // Parse the VCAP JSON structure looking for language_translator.
            JsonObject translatorCredentials = VCAPHelper.getCloudCredentials("language_translator");
            if (translatorCredentials == null) {
				System.out.println("No Language Translator service bound to this application");
				return;
            }
			apiKey = translatorCredentials.get("apikey").getAsString();
			apiUrl = translatorCredentials.get("url").getAsString();
        } else {
            System.out.println("Running locally. Looking for credentials in translator.properties");
			apiKey = VCAPHelper.getLocalProperties("translator.properties").getProperty("translator_api");
			apiUrl = VCAPHelper.getLocalProperties("translator.properties").getProperty("translator_url");
            if (apiKey == null || apiKey.length() == 0 || apiUrl == null || apiUrl.length() == 0) {
				System.out.println("To use Language Translator, set the Language Translator credentials (translator_api and translator_url) in src/main/resources/translator.properties");
				return;
            }
		}
	}

	private static void createClient() {
		Authenticator authenticator = new IamAuthenticator(apiKey);
		
		languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
		
		languageTranslator.setServiceUrl(apiUrl);
	}
}
