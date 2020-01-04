package asr.proyectoFinal.services;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.natural_language_understanding.v1.model.Features;
import com.ibm.watson.natural_language_understanding.v1.model.SentimentOptions;

import asr.proyectoFinal.models.YahooNew;
import asr.proyectoFinal.util.VCAPHelper;

public class NLUService {
	
	private static String nluUrl = null;
	private static String nluApi = null;
	
	// Sobre el texto dado se devuelve un análisis de sentimiento en torno a cada target asignando una puntuación de -1(negativo) a +1(positivo).
	public static AnalysisResults sentimentAnalysis(YahooNew stockNew)	{
		if(nluUrl == null || nluApi == null)
			NLUService.setCredentials();
		
		// Validation to use the service
		IamAuthenticator authenticator = new IamAuthenticator(nluApi);
		NaturalLanguageUnderstanding naturalLanguageUnderstanding = new NaturalLanguageUnderstanding("2019-07-12", authenticator);
		naturalLanguageUnderstanding.setServiceUrl(nluUrl);

		SentimentOptions sentiment = new SentimentOptions.Builder().build();
		
		Features features = new Features.Builder()
		.sentiment(sentiment)
		.build();
		
		AnalyzeOptions parameters = new AnalyzeOptions.Builder()
		  .url(stockNew.getLink())
		  .features(features)
		  .build();

		AnalysisResults response = naturalLanguageUnderstanding
		  .analyze(parameters)
		  .execute()
		  .getResult();
				
		return response;
	}

	public static Map<YahooNew, AnalysisResults> sentimentAnalysis(ArrayList<YahooNew> stockNews) {
		return stockNews
			.stream()
			.collect(Collectors.toMap(Function.identity(), NLUService::sentimentAnalysis));
	}
	
	private static void setCredentials() {
		if (System.getenv("VCAP_SERVICES") != null) {
            // When running in Bluemix, the VCAP_SERVICES env var will have the credentials
            // for all bound/connected services
            // Parse the VCAP JSON structure looking for cloudant.
            JsonObject nluCredentials = VCAPHelper.getCloudCredentials("natural-language-understanding");
            if (nluCredentials == null) {
				System.out.println("No Natural Language Understanding service bound to this application");
				return;
            }
			nluUrl = nluCredentials.get("url").getAsString();
			nluApi = nluCredentials.get("apikey").getAsString();
        } else {
            System.out.println("Running locally. Looking for credentials in nlu.properties");
			nluApi = VCAPHelper.getLocalProperties("nlu.properties").getProperty("nlu_api");
			nluUrl = VCAPHelper.getLocalProperties("nlu.properties").getProperty("nlu_url");
            if (nluApi == null || nluApi.length() == 0 || nluUrl == null || nluUrl.length() == 0) {
				System.out.println("To use Natural Language Understanding, set the Natural Language Understanding credentials (nlu_api and nlu_url) in src/main/resources/nlu.properties");
				return;
            }
        }
	}
}
