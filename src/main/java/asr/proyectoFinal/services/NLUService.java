package asr.proyectoFinal.services;

import java.util.ArrayList;
import java.util.List;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.natural_language_understanding.v1.model.Features;
import com.ibm.watson.natural_language_understanding.v1.model.SentimentOptions;

import asr.proyectoFinal.util.VCAPHelper;

public class NLUService {
	
	private static String nluUrl = VCAPHelper.getLocalProperties("nlu.properties").getProperty("nlu_url");
	private static String apiKey = VCAPHelper.getLocalProperties("nlu.properties").getProperty("nlu_api");
	
	public static void main(String args[])	{
		
		String text = "Microsoft stock collapses and godzilla attacks. Pedro is great";
		
		List<String> targets = new ArrayList<>();
		targets.add("godzilla");
		targets.add("Pedro");
		
		AnalysisResults response = NLUService.analisisSentimiento(text, targets);
		System.out.println(response);
		
	}
	
	public static AnalysisResults analisisSentimiento(String text, List<String> targets)	{
	//Sobre el texto dado se devuelve un análisis de sentimiento en torno a cada target asignando una puntuación de -1(negativo) a +1(positivo).
		IamAuthenticator authenticator = new IamAuthenticator(apiKey);
		NaturalLanguageUnderstanding naturalLanguageUnderstanding = new NaturalLanguageUnderstanding("2019-07-12", authenticator);
		naturalLanguageUnderstanding.setServiceUrl(nluUrl);

		//String url = "www.wsj.com/news/markets";

		

		SentimentOptions sentiment = new SentimentOptions.Builder()
		  .targets(targets)
		  .build();

		Features features = new Features.Builder()
		  .sentiment(sentiment)
		  //.categories(null)
		  .build();

		AnalyzeOptions parameters = new AnalyzeOptions.Builder()
		  //.url(url)
		  .text(text)
		  .features(features)
		  .build();

		AnalysisResults response = naturalLanguageUnderstanding
		  .analyze(parameters)
		  .execute()
		  .getResult();
		//System.out.println(parameters);
		//System.out.println(response);
		return response;
	}
}
