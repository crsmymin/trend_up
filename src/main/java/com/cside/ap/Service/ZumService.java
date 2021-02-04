package com.cside.ap.Service;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ZumService {
	
	private static final String CLIENT_ID = "rsIfqMXzEMcYTGeA2ZMn";
	private static final String CLIENT_SECRET = "Zi0w9eUsaE";

	@SuppressWarnings({ "unchecked", "deprecation" })
	public JSONObject getZumRank(String searchValue) {
		JSONObject jsonObject = new JSONObject();
		
		String pattern = "yyyyMMdd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String today = simpleDateFormat.format(new Date());
		
		String[] cntText = searchValue.split("T");
		
		String searchURL = "";
		if (!today.equals(cntText[0])) searchURL = "daily/"+cntText[0];
		
		try {
			
			Document doc = Jsoup.connect("http://issue.zum.com/"+searchURL)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();
			System.out.println("getZumRank :  "  + "http://issue.zum.com/"+searchURL);
			if (searchURL.equals("")){
				
				Elements elements = doc.select("#issueKeywordOpenList").select("li");
				List<Object> list = new ArrayList<Object>();
				for (Element element : elements) {
					list.add( element.select(".word").text() );
				}
				jsonObject.put("zumRank", list);
			}else {
				Elements elements = doc.select(".ranking_list").select("li");
				List<Object> list = new ArrayList<Object>();
				for (Element element : elements) {
					list.add( element.select(".word").text() );
				}
				jsonObject.put("zumRank", list);
			}
			
		} catch (Exception e) {
			System.out.println("[줌 실시간 순위 에러] " +e);
		}
		//System.out.println("네이버 실시간 순위 : {} "+ jsonObject);
		return jsonObject;
	}
	
}
