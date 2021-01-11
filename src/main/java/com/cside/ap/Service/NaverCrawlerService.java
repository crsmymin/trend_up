package com.cside.ap.Service;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Service
public class NaverCrawlerService {
	/**
	 * USER_AGENT
	 */
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36";
	
	/**
	 * 네이버 통합검색 뉴스 URL
	 */
	//public static final String NAVER_UNIFIED_NEWS_URL = "https://search.naver.com/search.naver?where=news&query=%s&pd=3&ds=%s&de=%s&start=%s&sort=1";
	public static final String NAVER_UNIFIED_NEWS_URL = "https://search.zum.com/search.zum?method=news&cluster=no&option=date&query=%s&rd=1&scp=0&enddate=%s&startdate=%s&datetype=input&period=w&page=%s";
	
	/**
	 * 네이버 통합검색 카페 URL (거래글제외 일반글만)
	 */
	//public static final String NAVER_UNIFIED_CAFE_URL = "https://search.naver.com/search.naver?where=articleg&query=%s&date_option=6&date_from=%s&date_to=%s&start=%s";
	public static final String NAVER_UNIFIED_CAFE_URL = "https://search.zum.com/search.zum?method=board&option=date&query=%s&rd=1&scp=0&enddate=%s&startdate=%s&datetype=input&period=w&page=%s";
	
	/**
	 * 네이버 통합검색 블로그 URL
	 */
	//public static final String NAVER_UNIFIED_BLOG_URL = "https://search.naver.com/search.naver?where=post&query=%s&date_option=8&date_from=%s&date_to=%s";
	public static final String NAVER_UNIFIED_BLOG_URL ="https://search.daum.net/search?w=blog&sort=timely&q=%s&DA=STC&ed=%s&sd=%s&page=%s&period=w";



	public String getUnifiedSearchNews(String keyword, String fromDate, String toDate, String start) {
		// 뉴스 원문 검색
		JSONObject jsonObject = new JSONObject();
		try {
			fromDate=fromDate.replace("-", "").replace(".", "");
			toDate=toDate.replace(".", "");

			String url = String.format(NAVER_UNIFIED_NEWS_URL,URLEncoder.encode(keyword, "UTF-8"), fromDate, toDate,start);
		 
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET) .ignoreContentType(true).get();

			Elements elements_title = doc.select(".section_head .title_num");
			
			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".section_head .title_num").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				System.out.println("[News 총 건 수] " + cnt);
				
				Elements elements = doc.select(".news_wrap #newsItemUl li");

				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("link", element.select(".report-link").attr("href"));
					val.put("title", element.select(".report-link").text());
					
					val.put("description", element.select(".txt").text());

					String str = element.select(".txt_block").text();
					String[] array = str.split(" ");
					val.put("medium", array[0]);
					
					String date_str = element.select(".txt_block span").text();
					val.put("date", date_str);
					
					
					list.add(val);
					//System.out.println("[링크]" + element.getElementsByAttribute("href").attr("href"));
					//System.out.println("[제목]" + element.select("dt a").text());
					//System.out.println("[신문사]" + element.select(".txt_inline ._sp_each_source").text().replaceAll("언론사 선정", ""));
					//System.out.println("[내용]" + element.select("dd").get(1).text());

				}

				List<Map<String, Integer>> page_list = new ArrayList<Map<String, Integer>>();
				Map<String, Integer> page_val = new HashMap<String, Integer>();
				
				String page_str="";
				int page = Integer.parseInt(start);
				int countList = 10;
				int countPage = 10;
				int totalCount = Integer.parseInt(cnt);
				int totalPage = totalCount / countList;
				if (totalCount % countList > 0) {
					totalPage++;
				}

				if (totalPage < page) {
					page = totalPage;
				}

				int startPage = ((page - 1) / 10) * 10 + 1;
				int endPage = startPage + countPage - 1;
				if (endPage > totalPage) {
					endPage = totalPage;
				}
				ArrayList page_numbers = new ArrayList();
				if (page > 1) {
					page_numbers.add((page - 1));
					int a=page-1;
					page_str+="<a class=\"news-page-num btn xi-angle-left\" onClick='news_page("+a+")'></a>";
				}

				for (int iCount = startPage; iCount <= endPage; iCount++) {
					if (iCount == page) {
						page_str+="<a class='news-page-num on' onClick='news_page("+iCount+")'>"+iCount+"</a>";
					} else {
						page_str+="<a class='news-page-num' onClick='news_page("+iCount+")'>"+iCount+"</a>";
					}
					page_numbers.add(iCount);
				}

				if (page < totalPage) {
					page_numbers.add((page + 1));
					int a=page+1;
					page_str+="<a class=\"news-page-num btn xi-angle-right\" onClick='news_page("+a+")'></a>";
				}
				int page_[]= {startPage,page,endPage};

				page_val.put("start", startPage);
				page_val.put("page", page);
				page_val.put("endPage", endPage);
				page_list.add(page_val);
				
				
				jsonObject.put("naverNewsCnt", cnt);
				jsonObject.put("naverNews", list);
				jsonObject.put("naverNewsPage", page_str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}
	public String getUnifiedSearchCafe(String keyword, String fromDate, String toDate, String start) {
		// 카페 원문 검색
		JSONObject jsonObject = new JSONObject();
		try {
			fromDate=fromDate.replace("-", "").replace(".", "");

			toDate=toDate.replace(".", "");

			String url = String.format(NAVER_UNIFIED_CAFE_URL,URLEncoder.encode(keyword, "UTF-8"), fromDate, toDate ,start);

			//System.out.println("[Cafe URL] "+start +"p;   "+url);
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET) .ignoreContentType(true).get();
			Elements elements_title = doc.select(".section_head .title_num");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".section_head .title_num").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				System.out.println("[Cafe 총 건 수] " + cnt);
				Elements elements = doc.select(".board_list_wrap .report-item-wrap");

				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("link", element.select(".tit").attr("href"));
					val.put("title", element.select(".tit").text());

					val.put("description", element.select(".txt_wrap").text());
					
					String str = element.select(".origin").text();
					String[] array = str.split(" ");
					val.put("medium", str);
					
					String date_str = element.select(".date").text();
					val.put("date", date_str);
					
					list.add(val);
					//System.out.println("[링크]" + element.getElementsByAttribute("href").attr("href"));
					//System.out.println("[제목]" + element.select("dt a").text());
					//System.out.println("[내용]" + element.select("dd").get(1).text());
				}
				
				String page_str="";
				int page = Integer.parseInt(start);
				int countList = 10;
				int countPage = 10;
				int totalCount = Integer.parseInt(cnt);
				int totalPage = totalCount / countList;
				if (totalCount % countList > 0) {
					totalPage++;
				}

				if (totalPage < page) {
					page = totalPage;
				}

				int startPage = ((page - 1) / 10) * 10 + 1;
				int endPage = startPage + countPage - 1;
				if (endPage > totalPage) {
					endPage = totalPage;
				}

				if (page > 1) {
					page_str+="<a onclick=\"cafe_page(" + (page - 1) + ")\" class=\"btn xi-angle-left\"></a>";
				}

				for (int iCount = startPage; iCount <= endPage; iCount++) {
					if (iCount == page) {
						page_str+="<a onclick=\"cafe_page(" +iCount+ ")\" class='on'>"+iCount+"</a>";
					} else {
						page_str+="<a onclick=\"cafe_page(" +iCount+ ")\">"+iCount+"</a>";
					}
				}

				if (page < totalPage) {
					page_str+="<a onclick=\"cafe_page(" +(page + 1) + ")\" class=\"btn xi-angle-right\"></a>";
				}
				
				
				
				jsonObject.put("naverCafeCnt", cnt);
				jsonObject.put("naverCafe", list);
				jsonObject.put("naverCafePage", page_str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}
	
	public String getUnifiedSearchBlog(String keyword, String fromDate, String toDate,String start) {
		// 블로그 원문 검색
		JSONObject jsonObject = new JSONObject();
		
		try {
			fromDate=fromDate.replace("-", "").replace(".", "");
			toDate=toDate.replace(".", "");
			String url = "https://some.co.kr/sometrend/analysis/trend/document?sources=13&categories=2046&endDate="+fromDate+"&startDate="+toDate+"&keyword="+URLEncoder.encode(keyword, "UTF-8")+"&source=blog";
			
			System.out.println(url);
			
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json;charset=UTF-8") .method(Connection.Method.GET) .ignoreContentType(true).get();
			Elements elements = doc.select("body");
			String body_data = elements.toString().replaceAll("<body>", "").replaceAll("</body>", "");
			
			
			JSONParser parser = new JSONParser();
			Object obj = parser.parse( body_data );
			JSONObject jsonObj = (JSONObject) obj;
			JSONObject jsonObj_item = (JSONObject) jsonObj.get("item");
			
			
			Long totalCnt = (Long) jsonObj_item.get("totalCnt");
			JSONArray data = (JSONArray) jsonObj_item.get("documentList");
			
			jsonObject.put("naverBlog", data);

			jsonObject.put("naverBlogCnt", totalCnt);
			//jsonObject.put("naverBlog", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);

	}
	public String getUnifiedSearchNewsDesc(String keyword, String fromDate, String toDate) {
		//뉴스 전체페이지 원문조회 (연관어 순위 분석용)
		JSONObject jsonObject = new JSONObject();
		String description="";
		try {
			fromDate=fromDate.replace("-", "").replace(".", "");
			toDate=toDate.replace(".", "");
			String url = String.format(NAVER_UNIFIED_NEWS_URL,URLEncoder.encode(keyword, "UTF-8"), fromDate, toDate,"1");
			 
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET) .ignoreContentType(true).get();

			Elements elements_title = doc.select(".section_head .title_num");
			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".section_head .title_num").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				int totalPage = Integer.parseInt(cnt) / 10;
				Elements elements = doc.select(".news_wrap #newsItemUl li");
				
				for (Element element : elements) {
					description+= element.select(".txt").text();
				}
				if(totalPage>1) {
					if(totalPage>9) {
						totalPage=8;
					}
					for(int i=1;i<totalPage+1;i++) {
						url = String.format(NAVER_UNIFIED_NEWS_URL, URLEncoder.encode(keyword, "UTF-8"), fromDate, toDate,i);
						doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET) .ignoreContentType(true).get();
						doc.select(".news_wrap #newsItemUl li");
						
						for (Element element : elements) {
							description+= element.select(".txt").text();
						}
					}
				}
				//System.out.println("[news] "+totalPage+"   "+description.length());
			}
			//System.out.println("description"+description);
			jsonObject.put("description", description);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}
	public String getUnifiedSearchCafeDesc(String keyword, String fromDate, String toDate) {
		//카페 전체페이지 원문조회 (연관어 순위 분석용)
		JSONObject jsonObject = new JSONObject();
		String description="";
		try {
			fromDate=fromDate.replace("-", "").replace(".", "");

			toDate=toDate.replace(".", "");
			
			String url = String.format(NAVER_UNIFIED_CAFE_URL,URLEncoder.encode(keyword, "UTF-8"), fromDate, toDate ,"1");

			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json;charset=UTF-8") .method(Connection.Method.GET) .ignoreContentType(true).get();
			Elements elements_title = doc.select(".section_head .title_num");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".section_head .title_num").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");

				//System.out.println("[Cafe 총 건 수] " + cntText[1]);
				
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				Elements elements = doc.select(".board_list_wrap .report-item-wrap");

				int totalPage = Integer.parseInt(cnt) / 10;
				for (Element element : elements) {
					description+= element.select(".txt_wrap").text();
				}
				//System.out.println("CafeBuzz totalPage: "+totalPage);
				if(totalPage>1) { 
					if(totalPage>9) {
						totalPage=8;
					}
					for(int i=1;i<totalPage+1;i++) {
						url = String.format(NAVER_UNIFIED_CAFE_URL, URLEncoder.encode(keyword, "UTF-8"), fromDate, toDate,i);
						doc =Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json;charset=UTF-8") .method(Connection.Method.GET) .ignoreContentType(true).get();
						
						elements = doc.select(".board_list_wrap .report-item-wrap");
						for (Element element : elements) {
							description+= element.select(".txt_wrap").text();
						}
					}
				}
				//System.out.println("[cafe] "+totalPage+"   "+description.length());
			}
			

			jsonObject.put("description", description);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}
	public String getUnifiedSearchBlogDesc(String keyword, String fromDate, String toDate) {
		//블로그 전체페이지 원문조회 (연관어 순위 분석용)
		JSONObject jsonObject = new JSONObject();
		String description="";
		try {
			fromDate=fromDate.replace("-", "").replace(".", "");
			toDate=toDate.replace(".", "");
			String url = "https://some.co.kr/sometrend/analysis/trend/document?sources=13&categories=2046&endDate="+fromDate+"&startDate="+toDate+"&keyword="+URLEncoder.encode(keyword, "UTF-8")+"&source=blog";
			
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).header("Content-Type", "application/json;charset=UTF-8") .method(Connection.Method.GET) .ignoreContentType(true).get();
			Elements elements = doc.select("body");
			String body_data = elements.toString().replaceAll("<body>", "").replaceAll("</body>", "");
			
			
			JSONParser parser = new JSONParser();
			Object obj = parser.parse( body_data );
			JSONObject jsonObj = (JSONObject) obj;
			
			JSONObject jsonObj_item = (JSONObject) jsonObj.get("item");
			
			JSONArray jsonArray = (JSONArray)jsonObj_item.get("documentList");
			
			System.out.println(jsonArray.size());
			
			for (int i=0;i<jsonArray.size();i++){ 
				
				JSONObject item = (JSONObject) jsonArray.get(i);
				description+=item.get("title");
				description+=item.get("content");
			   } 
			
			jsonObject.put("description", description);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}
	
	// 버즈 분석 
		public JSONArray  getSearchBuzz(String keyword, String fromDate_str, String toDate,String target) {
			
			Integer fromDate=Integer.parseInt(fromDate_str.replace(".", ""))+1;
			toDate=toDate.replace(".", "");
			
			URL url = null;
	        URLConnection connection = null;
	        StringBuilder responseBody = new StringBuilder();
	        JSONArray buzzContents = new JSONArray(); 
			try {
				url = new URL("http://svc.saltlux.ai:31781");
	            connection = url.openConnection();
	            // Header 정보 지정
	            connection.addRequestProperty("Content-Type", "application/json");
	            connection.setDoOutput(true);
	            connection.setDoInput(true);
	 
	            JSONObject jsonBody = new JSONObject();
	            // 사용자 키
	            jsonBody.put("key",  "bca2fa31-efd9-43b4-8e7c-9ba3fd749eea");
	            // 서비스 ID
	            jsonBody.put("serviceId", "01704663534");
	 
	            // 서비스에서 필요로 하는 parameter
	            JSONObject argument = new JSONObject();
	            argument.put("target",target);
	            argument.put("keyword",keyword);
	            argument.put("from",toDate);
	            argument.put("to",fromDate.toString());
	            argument.put("interval","day");
	 
	            jsonBody.put("argument", argument);

	            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
	            
	            bos.write(jsonBody.toJSONString().getBytes(StandardCharsets.UTF_8));
	            bos.flush();
	            bos.close();
	 
	            BufferedReader br = new BufferedReader(
	                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
	            String line = null;
	            while ((line = br.readLine()) != null) {
	                responseBody.append(line);
	            }
	            br.close();
				
	            
				JSONParser parser = new JSONParser();
				Object obj = parser.parse( responseBody.toString() );
				JSONObject jsonObj = (JSONObject) obj;
				JSONObject jsonObj2 = (JSONObject) jsonObj.get("return_object");

				//System.out.println(keyword+"  "+toDate+"~"+fromDate+"  :::  "+jsonObj2);
				JSONObject retrieve = (JSONObject) jsonObj2.get("retrieve");
				
				String a=retrieve.toString();
				String[] array =a.replace("{", "").replace("}", "").replace("\"", "").split(",");
				
				for(int i=0;i<array.length;i++) {
					JSONObject jsonObject = new JSONObject();
					String[] inner_array =array[i].split(":");
					jsonObject.put("date", inner_array[0].replace("-", ".").replace("T00", ""));
					jsonObject.put("count",  Integer.parseInt(inner_array[4]));
					
					buzzContents.add (jsonObject);

				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return buzzContents;
		}
		
}
