package asr.proyectoFinal.util;

import org.json.JSONObject;
import org.json.XML;

public class JSONHelper {
    /**
     * Transforms XML Object to JSON Object
     * @param xml
     * @return JSON String
     */
    public static String xmlToJson(String xml) {
        JSONObject xmlJSONObj = XML.toJSONObject(xml);
	    String string = xmlJSONObj.toString();
	    return string;
    }
}