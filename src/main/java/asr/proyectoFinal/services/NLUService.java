package asr.proyectoFinal.services;

import java.util.ArrayList;
import java.util.List;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.natural_language_understanding.v1.model.Features;
import com.ibm.watson.natural_language_understanding.v1.model.SentimentOptions;

import asr.proyectoFinal.util.VCAPHelper;

public class NLUService {
	
	private static String nluUrl = VCAPHelper.getLocalProperties("nlu.properties").getProperty("nlu_url");
	private static String apiKey = VCAPHelper.getLocalProperties("nlu.properties").getProperty("nlu_api");
	
	public static void main(String args[])	{
		
		String text = "googl";
		
		List<String> targets = new ArrayList<>();
		targets.add("a");
		//targets.add("Pedro");
		
		AnalysisResults response = NLUService.analisisSentimiento(text);
		System.out.println(response);
		
	}
	
	public static AnalysisResults analisisSentimiento(String stockSymbol)	{
	//Sobre el texto dado se devuelve un análisis de sentimiento en torno a cada target asignando una puntuación de -1(negativo) a +1(positivo).
		IamAuthenticator authenticator = new IamAuthenticator(apiKey);
		NaturalLanguageUnderstanding naturalLanguageUnderstanding = new NaturalLanguageUnderstanding("2019-07-12", authenticator);
		naturalLanguageUnderstanding.setServiceUrl(nluUrl);

		String url = "https://feeds.finance.yahoo.com/rss/2.0/headline?s="+stockSymbol;

		

		SentimentOptions sentiment = new SentimentOptions.Builder()
		  //.targets(targets)
		  .build();
		
		ConceptsOptions concepts = new ConceptsOptions.Builder()
				.build();

		Features features = new Features.Builder()
		  .sentiment(sentiment)
		  .concepts(concepts)
		  //.categories(null)
		  .build();

		AnalyzeOptions parameters = new AnalyzeOptions.Builder()
		  .url(url)
		  //.text(text)
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
