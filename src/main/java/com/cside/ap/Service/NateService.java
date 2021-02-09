package com.cside.ap.Service;

import org.springframework.stereotype.Service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Service
public class NateService {
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36";

	public JSONObject getNateRank(String searchValue) {
		JSONObject jsonObject_result = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			String url = "https://news.nate.com/today/keywordList?service_dtm="+searchValue;
			
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json")
					.method(Connection.Method.GET).ignoreContentType(true).get();
			JSONParser parser = new JSONParser();
			JSONObject jsonObj = (JSONObject) parser.parse(doc.text());
			
			if (jsonObj.get("result").toString().equals("200")) {
				JSONObject data = (JSONObject) jsonObj.get("data");
				
				for (int i = 0; i < data.size(); i++) {
					JSONObject jsonObject = (JSONObject) data.get(""+i);
					
					jsonArray.add(jsonObject.get("keyword_service"));
				}
				
			}	
			jsonObject_result.put("nateRank",jsonArray);
		} catch (Exception e) {
			System.out.println("네이트 실시간 순위 에러] "+ e);
		}
		return jsonObject_result;
	}
}
