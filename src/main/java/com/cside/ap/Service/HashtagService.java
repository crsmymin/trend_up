package com.cside.ap.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class HashtagService {

	public JSONObject getKorHashtagService() {
		JSONObject jsonObject = new JSONObject();
		
		try {
			Document doc = Jsoup.connect("http://cocotag.kr")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();
			
			Elements elements = doc.select(".table100-body").select("tr");

			if( elements.size() > 0 ) {
				List<Object> list = new ArrayList<Object>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("tag",element.select("a").text());
					val.put("cnt",element.select(".column3").text());
					list.add(val);
				}
				jsonObject.put("KorHashtagRank", list);
			}
		} catch (Exception e) {
			System.out.println("[cocotag 에러] " +e);
		}
		return jsonObject;
	}
	
	public JSONObject getEngHashtagService() {
		JSONObject jsonObject = new JSONObject();
		
		try {
			Document doc = Jsoup.connect("https://top-hashtags.com/instagram/")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();
			
			Elements elements = doc.select(".i-group").select("li");

			if( elements.size() > 0 ) {
				List<Object> list = new ArrayList<Object>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("tag",element.select("a").text());
					val.put("cnt",element.select(".i-total").text());
					list.add(val);
				}
				jsonObject.put("EngHashtagRank", list);
			}
		} catch (Exception e) {
			System.out.println("[top-hashtags 에러] " +e);
		}
		return jsonObject;
	}

	public JSONObject getTiktokHashtagService() {
		JSONObject jsonObject = new JSONObject();
		
		try {
			Document doc = Jsoup.connect("https://top-hashtags.com/tiktok/")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();
			
			Elements elements = doc.select(".i-group").select("li");

			if( elements.size() > 0 ) {
				List<Object> list = new ArrayList<Object>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("tag",element.select("strong").text());
					val.put("cnt",element.select(".i-total").text());
					list.add(val);
				}
				jsonObject.put("TiktokHashtagRank", list);
			}
		} catch (Exception e) {
			System.out.println("[top-hashtags tiktok 에러] " +e);
		}
		return jsonObject;
	}
	
	public JSONObject getRelatedHashtags(String searchValue) {
		JSONObject jsonObject = new JSONObject();
		
		try {
			List<Object> list = new ArrayList<Object>();
			
			Document doc_similar = Jsoup.connect("https://www.tagsfinder.com/en-kr/similar/"+searchValue)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();
			
			String element_similar = doc_similar.select("#hashtagy").text();
			
			if( !element_similar.equals("") ) {
				list.add(element_similar);
			}
			
			Document doc_related = Jsoup.connect("https://www.tagsfinder.com/en-kr/related/"+searchValue)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();
			
			String element_related = doc_related.select("#hashtagy").text();
			
			if( !element_related.equals("") ) {
				list.add(element_related);
			}
			
			Document doc_top = Jsoup.connect("https://top-hashtags.com/hashtag/"+searchValue)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();
			
			Elements elements_top = doc_top.select(".entry-content").select(".i-tags");
			
			if( elements_top.size() > 0 ) {
				for (Element element : elements_top) {
					list.add(element.select(".tht-tags").text());
				}
			}
			
			jsonObject.put("Hashtags", list);
			
			
		} catch (Exception e) {
			System.out.println("[getRelatedHashtags 에러] " +e);
		}
		return jsonObject;
	}
}
