package com.cside.ap.Service;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class NaverService {
	
	private static final String CLIENT_ID = "rsIfqMXzEMcYTGeA2ZMn";
	private static final String CLIENT_SECRET = "Zi0w9eUsaE";

	@SuppressWarnings({ "unchecked", "deprecation" })
	public JSONObject getNaverRank(String searchValue) {
		JSONObject jsonObject = new JSONObject();
		
		//searchValue="2017-03-06T21:00:00";
		//searchValue="2018-03-06T21:00:00";
		//searchValue="2019-03-06T21:00:00";
		//searchValue="2020-01-06T21:00:00";
		//searchValue="2020-10-01T00:00:00";
		
		try {
			Document doc = Jsoup.connect("https://datalab.naver.com/keyword/realtimeList.naver?datetime="+searchValue+"&where=main")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
					.get();

			Elements elements = doc.select(".ranking_item").select(".item_title");

			String date_str=searchValue.replace("-", "").replace("T", "").replace(":", "");
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
			Date date1=dateFormat.parse("20181010060000");
			Date date2=dateFormat.parse("20191128060000");
			Date date=dateFormat.parse(date_str);

			if(date1.after(date)) {
				elements = doc.select(".keyword_rank.select_date").select(".title");
			}else if(date2.after(date)){
				elements = doc.select("div[data-age='all']").select(".title");
			}
			
			if( elements.size() > 0 ) {
				List<Object> list = new ArrayList<Object>();
				for (Element element : elements) {
					list.add( element.text() );
				}
				jsonObject.put("naverRank", list);
			}else {
				List<Object> list = new ArrayList<Object>();
				
				list.add( "데이터가 없습니다.");	
				jsonObject.put("naverRank", list);
			}
		} catch (Exception e) {
			System.out.println("[네이버 실시간 순위 에러] " +e);
		}
		//System.out.println("네이버 실시간 순위 : {} "+ jsonObject);
		return jsonObject;
	}
	
	public String getNaverNews(String searchValue) {
		String result = "";
		JSONObject jsonObject = new JSONObject();
		try {
			URI apiURL = new URI("https://openapi.naver.com/v1/search/news.json");
			apiURL = new URIBuilder(apiURL)
					.addParameter("query", searchValue)
					.addParameter("display", "100")
					.addParameter("start", "1")
					.addParameter("sort", "sim")
					.build();

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(apiURL);
			get.addHeader("X-Naver-Client-Id", CLIENT_ID);
			get.addHeader("X-Naver-Client-Secret", CLIENT_SECRET);
			HttpResponse response = httpClient.execute(get);

			int responseCode = response.getStatusLine().getStatusCode();
			if(200 == responseCode) {
				ResponseHandler<String> handler = new BasicResponseHandler();
				result = handler.handleResponse(response);
				JSONParser parser = new JSONParser();
				Object obj = parser.parse( result );
				jsonObject = (JSONObject) obj;
				
				URI apiURL2 = new URI("http://surffing.net/getSearchAPI.do");
				apiURL2 = new URIBuilder(apiURL2)
						.addParameter("keyword", searchValue)
						.build();

				HttpClient httpClient2 = HttpClientBuilder.create().build();
				HttpGet get2 = new HttpGet(apiURL2);
				HttpResponse response2 = httpClient2.execute(get2);

				int responseCode2 = response2.getStatusLine().getStatusCode();
				if(200 == responseCode2) {
					ResponseHandler<String> handler2 = new BasicResponseHandler();
					String result2 = handler2.handleResponse(response2);
					Document doc = Jsoup.parse(result2);
					Element ele = doc.select(".center").get(1);
					Element ele2 = doc.select(".center").get(2);
						
					String search_pc=ele.toString().replaceAll("<td class=\"center\"> ", "").replaceAll(" </td>", "").replaceAll(",", "");
					String search_mobile=ele2.toString().replaceAll("<td class=\"center\"> ", "").replaceAll(" </td>", "").replaceAll(",", "");
					
					jsonObject.put("searchPc",Integer.parseInt(search_pc));
					jsonObject.put("searchMobile",Integer.parseInt(search_mobile));
					jsonObject.put("searchTotal",Integer.parseInt(search_pc)+ Integer.parseInt(search_mobile));
							
					//System.out.println(jsonObject);
				} else {
					throw new Exception( "Response Status Error : ErrorCode = " + responseCode2);
				}
				
			} else {
				throw new Exception( "Response Status Error : ErrorCode = " + responseCode);
			}
		} catch (Exception e) {
			System.out.println("[네이버 뉴스 검색 에러] "+ e);
		}
		//System.out.println("네이버 뉴스 검색 : {} "+result);
		return jsonObject.toString();
	}
}
