package com.cside.ap.Service;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Service
public class NaverCrawlerService {
	/**
	 * USER_AGENT
	 */
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36";
	
	/**
	 * 네이버 통합검색 뉴스 URL
	 */
	//public static final String NAVER_UNIFIED_NEWS_URL = "https://search.naver.com/search.naver?where=news&query=%s&pd=3&ds=%s&de=%s&start=%s&sort=1";
	public static final String NAVER_UNIFIED_NEWS_URL = "https://search.daum.net/search?w=news&sort=recency&q=%s&cluster=n&DA=STC&dc=STC&pg=1&r=1&rc=1&at=more&ed=%s&sd=%s&period=w&p=%s";
	
	/**
	 * 네이버 통합검색 카페 URL (거래글제외 일반글만)
	 */
	//public static final String NAVER_UNIFIED_CAFE_URL = "https://search.naver.com/search.naver?where=articleg&query=%s&date_option=6&date_from=%s&date_to=%s&start=%s";
	public static final String NAVER_UNIFIED_CAFE_URL = "https://search.daum.net/search?w=cafe&sort=recency&q=%s&cluster=n&DA=STC&dc=STC&pg=1&r=1&rc=1&at=more&ed=%s&sd=%s&period=w&p=%s";

	/**
	 * 네이버 통합검색 블로그 URL
	 */
	//public static final String NAVER_UNIFIED_BLOG_URL = "https://search.naver.com/search.naver?where=post&query=%s&date_option=8&date_from=%s&date_to=%s";
	public static final String NAVER_UNIFIED_BLOG_URL = "https://search.daum.net/search?w=blog&sort=timely&q=%s&DA=STC&ed=%s&sd=%s&page=%s&period=w";



	public String getUnifiedSearchNews(String keyword, String fromDate, String toDate, String start) {
		// 뉴스 원문 검색
		JSONObject jsonObject = new JSONObject();
		try {
			fromDate=fromDate.replace("-", "").replace(".", "")+"235959";
			toDate=toDate.replace(".", "")+"000000";
			
		System.out.println(keyword);

			String url = String.format(NAVER_UNIFIED_NEWS_URL,keyword, fromDate, toDate,start);
			
			
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			Elements elements_title = doc.select(".sub_expander .txt_info");
			
			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".sub_expander .txt_info").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				System.out.println("[News 총 건 수] " + cnt);
				
				Elements elements = doc.select("#newsColl .coll_cont li");

				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("link", element.getElementsByAttribute("href").attr("href"));
					val.put("title", element.select(".mg_tit a").text());
					
					val.put("description", element.select(".f_eb").text());

					String str = element.select(".date").html();
					String[] array = str.split("<span class=\"txt_bar\">|</span>");
					
					val.put("date", array[0]);
					val.put("medium", array[2]);
					
					
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
			fromDate=fromDate.replace("-", "").replace(".", "")+"235959";
			toDate=toDate.replace(".", "")+"000000";

			String url = String.format(NAVER_UNIFIED_CAFE_URL,keyword, fromDate, toDate,start);

			//System.out.println("[cafe URL] "+start +"p;   "+url);
			
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			Elements elements_title = doc.select(".sub_expander .txt_info");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".sub_expander .txt_info").get(0);
				
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");
				System.out.println("[Cafe 총 건 수] " + cnt);
				Elements elements = doc.select("#cafeColl .coll_cont li");
				
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("link", element.getElementsByAttribute("href").attr("href"));
					val.put("title", element.select(".mg_tit a").text());
					val.put("description", element.select(".f_eb").text());
					val.put("date", element.select(".date").text());

					val.put("medium", element.select("a.f_nb:eq(1)").html());

					list.add(val);
					//System.out.println("[링크]" + element.getElementsByAttribute("href").attr("href"));
					//System.out.println("[제목]" + element.select("dt a").text());
					//System.out.println("[내용]" + element.select("dd").get(1).text());
					//System.out.println("[출처]" + element.select(".txt_block .inline .txt84").get(0).text());
					//System.out.println("[날짜]" +  element.select(".txt_inline").text());
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
			fromDate=fromDate.replace("-", "").replace(".", "")+"235959";

			toDate=toDate.replace(".", "")+"000000";

			String url = String.format(NAVER_UNIFIED_BLOG_URL,keyword, fromDate, toDate ,start);

			//System.out.println("[blog URL] "+start +"p;   "+url);
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			Elements elements_title = doc.select(".sub_expander .txt_info");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".sub_expander .txt_info").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				System.out.println("[Blog 총 건 수] " + cnt);
				Elements elements = doc.select("#blogColl .coll_cont li");

				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("link", element.getElementsByAttribute("href").attr("href"));
					val.put("title", element.select(".mg_tit a").text());

					val.put("description", element.select(".f_eb").text());
					
					val.put("date", element.select(".date").html());
					val.put("medium", element.select("a.f_nb:eq(1)").html());
					
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
					page_str+="<a onclick=\"blog_page(" + (page - 1) + ")\" class=\"btn xi-angle-left\"></a>";
				}

				for (int iCount = startPage; iCount <= endPage; iCount++) {
					if (iCount == page) {
						page_str+="<a onclick=\"blog_page(" +iCount+ ")\" class='on'>"+iCount+"</a>";
					} else {
						page_str+="<a onclick=\"blog_page(" +iCount+ ")\">"+iCount+"</a>";
					}
				}

				if (page < totalPage) {
					page_str+="<a onclick=\"blog_page(" +(page + 1) + ")\" class=\"btn xi-angle-right\"></a>";
				}
				
				jsonObject.put("naverBlogCnt", cnt);
				jsonObject.put("naverBlog", list);
				jsonObject.put("naverBlogPage", page_str);
			}
		} catch (IOException e) {
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
			fromDate=fromDate.replace(".", "")+"235959";
			toDate=toDate.replace(".", "")+"000000";
			
			String url = String.format(NAVER_UNIFIED_NEWS_URL, keyword, fromDate, toDate,"1");
			
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			//System.out.println(doc);
			Elements elements_title = doc.select(".sub_expander .txt_info");
			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".sub_expander .txt_info").get(0);

				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");
				int totalPage = Integer.parseInt(cnt) / 10;
				Elements elements = doc.select("#newsColl .coll_cont li");
				
				for (Element element : elements) {
					description+= element.select(".f_eb").text();
				}
				if(totalPage>1) {
					if(totalPage>9) {
						totalPage=8;
					}
					for(int i=1;i<totalPage+1;i++) {
						url = String.format(NAVER_UNIFIED_NEWS_URL, keyword, fromDate, toDate,i);
						doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
						elements = doc.select("#newsColl .coll_cont li");
						
						for (Element element : elements) {
							description+= element.select(".f_eb").text();
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
			fromDate=fromDate.replace(".", "")+"235959";
			toDate=toDate.replace(".", "")+"000000";
			
			String url = String.format(NAVER_UNIFIED_CAFE_URL,keyword, fromDate, toDate,"1");

			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			Elements elements_title = doc.select(".sub_expander .txt_info");
			
			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".sub_expander .txt_info").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");

				//System.out.println("[Cafe 총 건 수] " + cntText[1]);
				
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				Elements elements = doc.select("#cafeColl .coll_cont li");
				int totalPage = Integer.parseInt(cnt) / 10;
				for (Element element : elements) {
					description+= element.select(".f_eb").text();
				}
				//System.out.println("CafeBuzz totalPage: "+totalPage);
				if(totalPage>1) { 
					if(totalPage>9) {
						totalPage=8;
					}
					for(int i=1;i<totalPage+1;i++) {
						url = String.format(NAVER_UNIFIED_CAFE_URL, keyword, fromDate, toDate,i);
						doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
						elements = doc.select("#cafeColl .coll_cont li");
						for (Element element : elements) {
							description+= element.select(".f_eb").text();
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
			fromDate=fromDate.replace(".", "")+"235959";
			toDate=toDate.replace(".", "")+"000000";
			
			String url = String.format(NAVER_UNIFIED_BLOG_URL, keyword, fromDate, toDate ,"1");

			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			Elements elements_title = doc.select(".sub_expander .txt_info");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".sub_expander .txt_info").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");

				
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				Elements elements = doc.select("#blogColl .coll_cont li");
				int totalPage = Integer.parseInt(cnt) / 10;
				for (Element element : elements) {
					description+= element.select(".f_eb").text();
				}

				//System.out.println("BlogBuzz totalPage: "+totalPage);
				if(totalPage>1) {
					if(totalPage>9) {
						totalPage=8;
					}
					for(int i=1;i<totalPage+1;i++) {
						url = String.format(NAVER_UNIFIED_BLOG_URL,keyword, fromDate, toDate,i+"1");
						doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
						elements = doc.select("#blogColl .coll_cont li");
						for (Element element : elements) {
							description+= element.select(".f_eb").text();
						}
					}
				}
				//System.out.println("[blog] "+totalPage+"   "+description.length());
			}

			jsonObject.put("description", description);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}
	
	// 버즈 분석 
		public JSONArray  getSearchBuzz(String keyword, String fromDate, String toDate,String target) {
			
			fromDate=fromDate.replace(".", "");
			toDate=toDate.replace(".", "");
			
			System.out.println(keyword);
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
	            argument.put("to",fromDate);
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

				System.out.println(jsonObj2);
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
