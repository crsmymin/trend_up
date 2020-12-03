package com.cside.ap.Service;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TwitterService {
	
	private static final String CUSTOMER_KEY = "Yl6XbO2cEnONIenOHbdzgrDeq";
	private static final String CUSTOMER_SECRET = "PY64FQQEOnbehRH0kV2nSAFIAYHhPGL4HSOd0mF5zMxBoIsKkM";
	private static final String ACCESS_TOKEN = "1230384928841330690-w4SewVEdoIiIcbFQeY0dpBGM3Ax4r8";
	private static final String ACCESS_TOKEN_SECRET = "t2RGoOQdbcASPg3U7q76tGm8pmbsneswklwTmyusbxVN4";
	
	
	@SuppressWarnings("unchecked")
	public JSONObject getTwitterRank(String searchValue) {
		JSONObject jsonObject_result = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			searchValue=searchValue.replace(".", "-");
			Document doc = Jsoup.connect("https://getdaytrends.com/ko/korea/"+searchValue)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();
			Elements elements = doc.select("#trends").select(".ranking").select( "tr").select( ".main");
			List<Object> list = new ArrayList<Object>();
			List<Object> list2 = new ArrayList<Object>();
			
			if( elements.size() > 0 ) {
				for (Element element : elements) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("rank", element.select( "a").text());
					jsonObject.put("rank2", element.select("span").text().replace(" 트윗", ""));
					jsonArray.add(jsonObject);
				}
				
			}
			jsonObject_result.put("twitterRank",jsonArray);
			
		} catch (Exception e) {
			System.out.println("[트위터 실시간 순위 에러] "+ e);
		}
		return jsonObject_result;
	}
	
	@SuppressWarnings("unchecked")
	public String getTwitterSearch(String searchValue) {
		JSONObject jsonObject = new JSONObject();
		try {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
				.setOAuthConsumerKey(CUSTOMER_KEY)
				.setOAuthConsumerSecret(CUSTOMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);

			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();

			Query query = new Query(searchValue);
			QueryResult result = twitter.search(query);
			if( result.getTweets().size() > 0 ) {
				jsonObject.put("twitter", result.getTweets());
			}
		} catch (Exception e) {
			System.out.println("[트위터 검색 에러] "+ e);
		}
		System.out.println("트위터 검색 순위 : {} "+jsonObject);
		return new Gson().toJson(jsonObject);
	}
}
