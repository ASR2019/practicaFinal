package asr.proyectoFinal.services;

import asr.proyectoFinal.models.YahooNew;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

public class Translator {
	
	public static YahooNew translateToSpanish(YahooNew noticia)	{
		YahooNew aux = noticia;
		String description = noticia.getDescription();
		String translatedDescription = Translator.translate(description, "en", "es", true);
		aux.setTranslatedDescription(translatedDescription);
				
		return aux;	
	}
	
	public static String translate(String palabra, String sourceModel, String destModel, boolean conversational)	{
			String model;
			if(sourceModel.equals("en") || sourceModel.equals("es") ||
			destModel.equals("en") || destModel.equals("es"))
			{
			model=sourceModel+"-"+destModel;
			if(conversational)
			model+="-conversational";
			}
			else
			model="en-es";
			Authenticator authenticator = new IamAuthenticator("sGBqIGkLecdsa4RdsA3imHp_lvb7MMlZNzdasq-PgkmCXdsf59P0");
			LanguageTranslator languageTranslator = new LanguageTranslator("2018-05-01",
			authenticator);
			languageTranslator.setServiceUrl("https://gatewaylon.watsonplatform.net/language-translator/api");
			TranslateOptions translateOptions = new TranslateOptions.Builder()
			 .addText(palabra)
			 .modelId(model)
			 .build();
			TranslationResult translationResult = languageTranslator.translate(translateOptions).execute().getResult();
			System.out.println(translationResult);
			String traduccionJSON = translationResult.toString();
			JsonObject rootObj = JsonParser.parseString(traduccionJSON).getAsJsonObject();
			JsonArray traducciones = rootObj.getAsJsonArray("translations");
			String traduccionPrimera = palabra;
			if(traducciones.size()>0)
			traduccionPrimera =
			traducciones.get(0).getAsJsonObject().get("translation").getAsString();
			return traduccionPrimera;
			}
}
