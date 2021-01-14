package com.cside.ap.Service;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.Gson;

@Service
public class EmotionAnalysisService {
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36";

	private static String KEY = "<replace-with-your-text-analytics-key-here>";
	private static String ENDPOINT = "<replace-with-your-text-analytics-endpoint-here>";

	public String getEmotionAnalysis(String keyword, String fromDate, String toDate) {
		JSONObject jsonObject = new JSONObject();

		String description = "";
		try {
			fromDate = fromDate.replace("-", "").replace(".", "");
			toDate = toDate.replace(".", "");

			String url = "https://some.co.kr/sometrend/analysis/trend/sentiment-transition?sources=13&categories=2046&period=1&endDate="
					+ toDate + "&startDate=" + fromDate + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json")
					.method(Connection.Method.GET).ignoreContentType(true).get();
			JSONParser parser = new JSONParser();
			JSONObject jsonObj = (JSONObject) parser.parse(doc.text());
			JSONObject polarities = new JSONObject();

			if (jsonObj.get("code").equals("200")) {

				JSONObject item = (JSONObject) jsonObj.get("item");
				// System.out.println(item);
				JSONArray jsonArray = (JSONArray) item.get("rows");

				JSONObject keywordMap = new JSONObject();
				Integer negative = 0;
				Integer neutral = 0;
				Integer positive = 0;

				JSONArray data = new JSONArray();

				for (int i = 0; i < jsonArray.size(); i++) {

					JSONObject rows_item = (JSONObject) jsonArray.get(i);
					JSONObject polarities_ = (JSONObject) rows_item.get("polarities");

					negative = negative + Integer.parseInt(polarities_.get("negative").toString());
					neutral = neutral + Integer.parseInt(polarities_.get("neutral").toString());
					positive = positive + Integer.parseInt(polarities_.get("positive").toString());

					JSONArray dataArray = (JSONArray) rows_item.get("data");
					for (int j = 0; j < 10; j++) {
						JSONObject dataArray_ = (JSONObject) dataArray.get(j);
						JSONObject new_dataArray = new JSONObject();
						new_dataArray.put("name", dataArray_.get("name"));
						new_dataArray.put("polarity", dataArray_.get("polarity"));
						new_dataArray.put("frequency", dataArray_.get("frequency"));

						data.add(new_dataArray);
					}
				}
				keywordMap.put("negative", negative);
				keywordMap.put("neutral", neutral);
				keywordMap.put("positive", positive);

				jsonObject.put("keywordMap", keywordMap);
				
				JSONArray arrayList = new JSONArray();
				
				for (int k = 0; k < data.size(); k++) {
					Boolean unique = true;
					JSONObject dataobj_ = (JSONObject) data.get(k);
					
					for (int h = 0; h < arrayList.size(); h++) {

						JSONObject dataobj_2 =(JSONObject) arrayList.get(h);
						
						if ((dataobj_.get("name").equals(dataobj_2.get("name")))) {
							Integer a = Integer.parseInt(dataobj_.get("frequency").toString())+Integer.parseInt(dataobj_2.get("frequency").toString());
						
							dataobj_2.put("frequency",a);
							
							unique = false;
							arrayList.remove(h);
							arrayList.add(dataobj_2);
							h+=1;
							
							break;
						}
					}
					if (unique) {
						arrayList.add(dataobj_);
					}
				}
				
				JSONArray sortedJsonArray = new JSONArray();

			    List<JSONObject> jsonValues = new ArrayList<JSONObject>();
			    for (int i = 0; i < arrayList.size(); i++) {
			        jsonValues.add((JSONObject) arrayList.get(i));
			    }
			    Collections.sort( jsonValues, new Comparator<JSONObject>() {
			        //You can change "Name" with "ID" if you want to sort by ID
			        private static final String KEY_NAME = "frequency";

			        @Override
			        public int compare(JSONObject a, JSONObject b) {
			            String valA = new String();
			            String valB = new String();

			            valA = a.get(KEY_NAME).toString();
						valB = b.get(KEY_NAME).toString();

						//System.out.println(valA + " : "+ valB + " -> "+valA.compareTo(valB));
			            return valA.compareTo(valB);
			        }
			    });

			    for (int i = 0; i < arrayList.size(); i++) {
			        sortedJsonArray.add(jsonValues.get(i));
			    }
			    
			    //System.out.println(sortedJsonArray);
				jsonObject.put("data", arrayList);
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}
}
