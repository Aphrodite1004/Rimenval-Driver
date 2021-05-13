package driver.transporterimenval.com.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class PlaceJSONParser {
	
	/** Receives a JSONObject and returns a list */
	public List<HashMap<String,String>> parse(JSONObject jObject){		
		
		JSONArray jPlaces = null;
		try {
			jPlaces = jObject.getJSONArray("predictions");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getPlaces(jPlaces);
	}
	
	
	private List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
		int placesCount = jPlaces.length();
		List<HashMap<String, String>> placesList = new ArrayList<>();
		HashMap<String, String> place = null;

		for(int i=0; i<placesCount;i++){
			try {
				place = getPlace((JSONObject)jPlaces.get(i));
				placesList.add(place);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return placesList;
	}
	
	/** Parsing the Place JSON object */
	private HashMap<String, String> getPlace(JSONObject jPlace){

		HashMap<String, String> place = new HashMap<>();
		
		String id="";
		String reference="";
		String description="";		
		
		try {
			
			description = jPlace.getString("description");			
			id = jPlace.getString("id");
			reference = jPlace.getString("reference");
			
			place.put("description", description);
			place.put("_id",id);
			place.put("reference",reference);			
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}		
		return place;
	}
}
