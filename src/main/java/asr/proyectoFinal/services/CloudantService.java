package asr.proyectoFinal.services;
/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/ 

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// import com.cloudant.client.api.Changes;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
// import com.cloudant.client.api.model.ChangesResult;
import com.google.gson.JsonObject;

import asr.proyectoFinal.models.Symbol;
// import asr.proyectoFinal.models.Word;
import asr.proyectoFinal.util.VCAPHelper;

public class CloudantService {
	private Database db = null;
	private static String databaseName = "prediction";
	
	public CloudantService() {
		this(databaseName);
	}
	
	public CloudantService(String name){
		// Save database name to class
		if(!databaseName.equals(name))
			CloudantService.databaseName = name;
		
		// Connect to Cloudant database
		CloudantClient cloudant = createClient();
		if(cloudant != null)
			db = cloudant.database(databaseName, true);
	}
	
	public Database getDB(){
		return db;
	}

	/**
	 * https://static.javadoc.io/com.cloudant/cloudant-client/2.18.0/com/cloudant/client/api/Changes.html
	 */
	// public void test() {
	// 	// feed type continuous
 	// 	Changes changes = db.changes()
 	// 		.includeDocs(true)
	// 		.heartBeat(30000)
 	// 		.continuousChanges();

	// 		while (changes.hasNext()) {
 	// 			ChangesResult.Row feed = changes.next();
	// 			String docId = feed.getId();
	// 			JsonObject doc = feed.getDoc();
	// 		}

	// 			//while loop blocks; stop from another thread
	// 		changes.stop(); // stop continuous feed
	// }
	// public <T> Collection<T> getAll(Class<T> classOfT){
	// 	List<T> docs;
	// 	try {
	// 		docs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(classOfT);
	// 	} catch (IOException e) {
	// 		return null;
	// 	}
	// 	return docs;
	// }

	public Collection<Symbol> getAll(){
        List<Symbol> docs;
		try {
			docs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Symbol.class);
		} catch (IOException e) {
			return null;
		}
        return docs;
	}
	
	public Map<String,String> getReferences(){
        Map<String,String> refs;
		
		refs = this.getAll().stream().collect(Collectors.toMap(Symbol::getSymbolId,Symbol::get_id));
		
        return refs;
	}
	
	public Symbol get(String id) {
		return db.find(Symbol.class, id);
	}
	
	
	public Symbol persist(Symbol td) {
		String id = db.save(td).getId();
		return db.find(Symbol.class, id);
	}

	public Symbol update(String id, Symbol newSymbol) {
		Symbol visitor = db.find(Symbol.class, id);

		visitor.setStock(newSymbol.getStock());
		visitor.setNews(newSymbol.getNews());
		visitor.setSymbolId(newSymbol.getSymbolId());
		visitor.setNews(newSymbol.getNews());
				
		db.update(visitor);

		return db.find(Symbol.class, id);
	}
	
	public void delete(String id) {
		Symbol visitor = db.find(Symbol.class, id);
		db.remove(id, visitor.getSymbolId());
		
	}

	public int count() throws Exception {
		return getAll().size();
	}
	
	private static CloudantClient createClient() {
		
		String url;
	
		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for cloudant.
			JsonObject cloudantCredentials = VCAPHelper.getCloudCredentials("cloudant");
			if(cloudantCredentials == null){
				System.out.println("No cloudant database service bound to this application");
				return null;
			}
			url = cloudantCredentials.get("url").getAsString();
		} else {
			System.out.println("Running locally. Looking for credentials in cloudant.properties");
			url = VCAPHelper.getLocalProperties("cloudant.properties").getProperty("cloudant_url");
			if(url == null || url.length()==0){
				System.out.println("To use a database, set the Cloudant url in src/main/resources/cloudant.properties");
				return null;
			}
		}
	
		try {
			System.out.println("Connecting to Cloudant");
			CloudantClient client = ClientBuilder.url(new URL(url)).build();
			System.out.println("Connected!");
			return client;
		} catch (Exception e) {
			System.out.println("Unable to connect to database");
			//e.printStackTrace();
			return null;
		}
	}
}
