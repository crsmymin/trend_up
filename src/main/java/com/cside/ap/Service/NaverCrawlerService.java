package com.cside.ap.Service;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import net.sf.json.JSON;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
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
	// public static final String NAVER_UNIFIED_NEWS_URL =
	// "https://search.naver.com/search.naver?where=news&query=%s&pd=3&ds=%s&de=%s&start=%s&sort=1";
	public static final String NAVER_UNIFIED_NEWS_URL = "https://search.zum.com/search.zum?method=news&cluster=no&option=date&query=%s&rd=1&scp=0&startdate=%s&enddate=%s&datetype=input&period=w&page=%s";

	/**
	 * 네이버 통합검색 카페 URL (거래글제외 일반글만)
	 */
	// public static final String NAVER_UNIFIED_CAFE_URL =
	// "https://search.naver.com/search.naver?where=articleg&query=%s&date_option=6&date_from=%s&date_to=%s&start=%s";
	public static final String NAVER_UNIFIED_CAFE_URL = "https://search.zum.com/search.zum?method=board&option=date&query=%s&rd=1&scp=0&startdate=%s&enddate=%s&datetype=input&period=w&page=%s";

	/**
	 * 네이버 통합검색 블로그 URL
	 */
	// public static final String NAVER_UNIFIED_BLOG_URL =
	// "https://search.naver.com/search.naver?where=post&query=%s&date_option=8&date_from=%s&date_to=%s";
	public static final String NAVER_UNIFIED_BLOG_URL = "https://search.daum.net/search?w=blog&sort=timely&q=%s&DA=STC&sd=%s&ed=%s&page=%s&period=w";

	public String getUnifiedSearchNews(String keyword, String startDate, String endDate, String start) {
		// 뉴스 원문 검색
		JSONObject jsonObject = new JSONObject();
		String cnt = "0";
		try {
			startDate = startDate.replace(".", "");
			endDate = endDate.replace(".", "");

			String url = String.format(NAVER_UNIFIED_NEWS_URL, URLEncoder.encode(keyword, "UTF-8"), startDate, endDate,
					start);
			// System.out.println("getUnifiedSearchNews : " + url);

			Document doc = Jsoup.connect(url).userAgent(USER_AGENT)
					.header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET)
					.ignoreContentType(true).get();

			Elements elements_title = doc.select(".section_head .title_num");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".section_head .title_num").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

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
					// System.out.println("[링크]" +
					// element.getElementsByAttribute("href").attr("href"));
					// System.out.println("[제목]" + element.select("dt a").text());
					// System.out.println("[신문사]" + element.select(".txt_inline
					// ._sp_each_source").text().replaceAll("언론사 선정", ""));
					// System.out.println("[내용]" + element.select("dd").get(1).text());

				}

				List<Map<String, Integer>> page_list = new ArrayList<Map<String, Integer>>();
				Map<String, Integer> page_val = new HashMap<String, Integer>();

				String page_str = "";
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
					int a = page - 1;
					page_str += "<a class=\"news-page-num btn xi-angle-left\" onClick='news_page(" + a + ")'></a>";
				}

				for (int iCount = startPage; iCount <= endPage; iCount++) {
					if (iCount == page) {
						page_str += "<a class='news-page-num on' onClick='news_page(" + iCount + ")'>" + iCount
								+ "</a>";
					} else {
						page_str += "<a class='news-page-num' onClick='news_page(" + iCount + ")'>" + iCount + "</a>";
					}
					page_numbers.add(iCount);
				}

				if (page < totalPage) {
					page_numbers.add((page + 1));
					int a = page + 1;
					page_str += "<a class=\"news-page-num btn xi-angle-right\" onClick='news_page(" + a + ")'></a>";
				}
				int page_[] = { startPage, page, endPage };

				page_val.put("start", startPage);
				page_val.put("page", page);
				page_val.put("endPage", endPage);
				page_list.add(page_val);

				jsonObject.put("naverNews", list);
				jsonObject.put("naverNewsPage", page_str);
			}
			jsonObject.put("naverNewsCnt", cnt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}

	public String getUnifiedSearchCafe(String keyword, String startDate, String endDate, String start) {
		// 카페 원문 검색
		JSONObject jsonObject = new JSONObject();
		String cnt = "0";
		try {
			startDate = startDate.replace(".", "");

			endDate = endDate.replace(".", "");

			String url = String.format(NAVER_UNIFIED_CAFE_URL, URLEncoder.encode(keyword, "UTF-8"), startDate, endDate,
					start);
			// System.out.println("getUnifiedSearchCafe: "+url);

			Document doc = Jsoup.connect(url).userAgent(USER_AGENT)
					.header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET)
					.ignoreContentType(true).get();
			Elements elements_title = doc.select(".section_head .title_num");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".section_head .title_num").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

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
					// System.out.println("[링크]" +
					// element.getElementsByAttribute("href").attr("href"));
					// System.out.println("[제목]" + element.select("dt a").text());
					// System.out.println("[내용]" + element.select("dd").get(1).text());
				}

				String page_str = "";
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
					page_str += "<a onclick=\"cafe_page(" + (page - 1) + ")\" class=\"btn xi-angle-left\"></a>";
				}

				for (int iCount = startPage; iCount <= endPage; iCount++) {
					if (iCount == page) {
						page_str += "<a onclick=\"cafe_page(" + iCount + ")\" class='on'>" + iCount + "</a>";
					} else {
						page_str += "<a onclick=\"cafe_page(" + iCount + ")\">" + iCount + "</a>";
					}
				}

				if (page < totalPage) {
					page_str += "<a onclick=\"cafe_page(" + (page + 1) + ")\" class=\"btn xi-angle-right\"></a>";
				}

				jsonObject.put("naverCafe", list);
				jsonObject.put("naverCafePage", page_str);
			}
			jsonObject.put("naverCafeCnt", cnt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}

	public String getUnifiedSearchBlog(String keyword, String startDate, String endDate, String start) {
		// 블로그 원문 검색
		JSONObject jsonObject = new JSONObject();
		String cnt = "0";
		try {
			startDate = startDate.replace(".", "") + "000000";
			endDate = endDate.replace(".", "") + "235959";

			String url = String.format(NAVER_UNIFIED_BLOG_URL, URLEncoder.encode(keyword, "UTF-8"), startDate, endDate,
					start);
			// System.out.println("getUnifiedSearchBlog: "+url);

			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			Elements elements_title = doc.select(".sub_expander .txt_info");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".sub_expander .txt_info").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");
				cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				System.out.println("[Blog 총 건 수] " + cnt);
				Elements elements = doc.select("#blogColl .coll_cont li");

				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (Element element : elements) {
					Map<String, String> val = new HashMap<String, String>();
					val.put("url", element.select(".f_link_b").attr("href"));
					val.put("title", element.select(".mg_tit a").text());

					val.put("content", element.select(".desc").text());

					val.put("documentDate", element.select(".date").html());
					val.put("writerName", element.select("a.f_nb:eq(1)").html());

					list.add(val);
					// System.out.println("[링크]" + element.select(".f_link_b").attr("href"));
					// System.out.println("[제목]" + element.select(".mg_tit a").text());
					// System.out.println("[내용]" + element.select(".desc").text());
				}

				String page_str = "";
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
					page_str += "<a onclick=\"blog_page(" + (page - 1) + ")\" class=\"btn xi-angle-left\"></a>";
				}

				for (int iCount = startPage; iCount <= endPage; iCount++) {
					if (iCount == page) {
						page_str += "<a onclick=\"blog_page(" + iCount + ")\" class='on'>" + iCount + "</a>";
					} else {
						page_str += "<a onclick=\"blog_page(" + iCount + ")\">" + iCount + "</a>";
					}
				}

				if (page < totalPage) {
					page_str += "<a onclick=\"blog_page(" + (page + 1) + ")\" class=\"btn xi-angle-right\"></a>";
				}

				jsonObject.put("naverBlog", list);
				jsonObject.put("naverBlogPage", page_str);
			}
			jsonObject.put("naverBlogCnt", cnt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);

	}

	public String getUnifiedSearchNewsDesc(String keyword, String startDate, String endDate) {
		// 뉴스 전체페이지 원문조회 (연관어 순위 분석용)
		JSONObject jsonObject = new JSONObject();
		String description = "";
		try {

			startDate = startDate.replace(".", "");
			endDate = endDate.replace(".", "");
			String url = String.format(NAVER_UNIFIED_NEWS_URL, URLEncoder.encode(keyword, "UTF-8"), startDate, endDate,
					"1");

			// System.out.println("getUnifiedSearchNewsDesc: "+url);
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT)
					.header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET)
					.ignoreContentType(true).get();

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
					description += element.select(".txt").text();
				}
				if (totalPage > 1) {
					if (totalPage > 9) {
						totalPage = 8;
					}
					for (int i = 1; i < totalPage + 1; i++) {
						url = String.format(NAVER_UNIFIED_NEWS_URL, URLEncoder.encode(keyword, "UTF-8"), startDate,
								endDate, i);
						doc = Jsoup.connect(url).userAgent(USER_AGENT)
								.header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET)
								.ignoreContentType(true).get();
						doc.select(".news_wrap #newsItemUl li");

						for (Element element : elements) {
							description += element.select(".txt").text();
						}
					}
				}
				// System.out.println("[news] "+totalPage+" "+description.length());
			}
			// System.out.println("description"+description);
			jsonObject.put("description", description);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}

	public String getUnifiedSearchCafeDesc(String keyword, String startDate, String endDate) {
		// 카페 전체페이지 원문조회 (연관어 순위 분석용)
		JSONObject jsonObject = new JSONObject();
		String description = "";
		try {
			startDate = startDate.replace(".", "");

			endDate = endDate.replace(".", "");

			String url = String.format(NAVER_UNIFIED_CAFE_URL, URLEncoder.encode(keyword, "UTF-8"), startDate, endDate,
					"1");

			// System.out.println("getUnifiedSearchCafeDesc: "+url);
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT)
					.header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET)
					.ignoreContentType(true).get();
			Elements elements_title = doc.select(".section_head .title_num");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".section_head .title_num").get(0);
				// [Result] 1-10 / 68,360건
				String[] cntText = ele.text().split("/");

				// System.out.println("[Cafe 총 건 수] " + cntText[1]);

				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				Elements elements = doc.select(".board_list_wrap .report-item-wrap");

				int totalPage = Integer.parseInt(cnt) / 10;
				for (Element element : elements) {
					description += element.select(".txt_wrap").text();
				}
				// System.out.println("CafeBuzz totalPage: "+totalPage);
				if (totalPage > 1) {
					if (totalPage > 9) {
						totalPage = 8;
					}
					for (int i = 1; i < totalPage + 1; i++) {
						url = String.format(NAVER_UNIFIED_CAFE_URL, URLEncoder.encode(keyword, "UTF-8"), startDate,
								endDate, i);
						doc = Jsoup.connect(url).userAgent(USER_AGENT)
								.header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET)
								.ignoreContentType(true).get();

						elements = doc.select(".board_list_wrap .report-item-wrap");
						for (Element element : elements) {
							description += element.select(".txt_wrap").text();
						}
					}
				}
				// System.out.println("[cafe] "+totalPage+" "+description.length());
			}

			jsonObject.put("description", description);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}

	public String getUnifiedSearchBlogDesc(String keyword, String startDate, String endDate) {
		// 블로그 전체페이지 원문조회 (연관어 순위 분석용)
		JSONObject jsonObject = new JSONObject();
		String description = "";
		try {
			startDate = startDate.replace(".", "") + "000000";
			endDate = endDate.replace(".", "") + "235959";

			String url = String.format(NAVER_UNIFIED_BLOG_URL, URLEncoder.encode(keyword, "UTF-8"), startDate, endDate,
					"1");
			// System.out.println("getUnifiedSearchBlogDesc: "+url);

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
					description += element.select(".desc").text();
				}

				// System.out.println("BlogBuzz totalPage: "+totalPage);
				if (totalPage > 1) {
					if (totalPage > 9) {
						totalPage = 8;
					}
					for (int i = 1; i < totalPage + 1; i++) {
						url = String.format(NAVER_UNIFIED_BLOG_URL, URLEncoder.encode(keyword, "UTF-8"), startDate,
								endDate, i + "1");
						doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
						elements = doc.select("#blogColl .coll_cont li");
						for (Element element : elements) {
							description += element.select(".desc").text();
						}
					}
				}
				// System.out.println("[blog] "+totalPage+" "+description.length());
			}

			jsonObject.put("description", description);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}

	// 버즈 분석
	public JSONArray getSearchBuzz(String keyword, String startDate, String endDate, String target) {

		Integer fromDate = Integer.parseInt(endDate.replace(".", "")) + 1;
		startDate = startDate.replace(".", "");

		URL url = null;
		HttpURLConnection connection = null;
		StringBuilder responseBody = new StringBuilder();
		JSONArray buzzContents = new JSONArray();
		try {
			url = new URL("http://svc.saltlux.ai:31781");
			connection = (HttpURLConnection) url.openConnection();
			// Header 정보 지정
			connection.addRequestProperty("Content-Type", "application/json;charset=UTF-8");
			connection.setDoOutput(true);
			connection.setDoInput(true);

			JSONObject jsonBody = new JSONObject();

			// 사용자 키
			jsonBody.put("key", "bca2fa31-efd9-43b4-8e7c-9ba3fd749eea");
			// 서비스 ID
			jsonBody.put("serviceId", "01704663534");

			// 서비스에서 필요로 하는 parameter
			JSONObject argument = new JSONObject();
			argument.put("target", target);
			argument.put("keyword", keyword);
			argument.put("from", startDate);
			argument.put("to", fromDate.toString());
			argument.put("interval", "day");

			jsonBody.put("argument", argument);

			BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

			bos.write(jsonBody.toJSONString().getBytes(StandardCharsets.UTF_8));
			bos.flush();
			bos.close();

			int code = connection.getResponseCode();

			if (code==200) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
				String line = null;
				while ((line = br.readLine()) != null) {
					responseBody.append(line);
				}
				br.close();

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(responseBody.toString());
				JSONObject jsonObj = (JSONObject) obj;
				JSONObject jsonObj2 = (JSONObject) jsonObj.get("return_object");

				// System.out.println(keyword+" "+toDate+"~"+fromDate+" ::: "+jsonObj2);
				JSONObject retrieve = (JSONObject) jsonObj2.get("retrieve");

				String a = retrieve.toString();
				String[] array = a.replace("{", "").replace("}", "").replace("\"", "").split(",");

				for (int i = 0; i < array.length; i++) {
					JSONObject jsonObject = new JSONObject();
					String[] inner_array = array[i].split(":");
					jsonObject.put("date", inner_array[0].replace("-", ".").replace("T00", ""));
					jsonObject.put("count", Integer.parseInt(inner_array[4]));

					buzzContents.add(jsonObject);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buzzContents;
	}

	public String getSearchCafeBuzz(String keyword, String startDate, String endDate)
			throws org.json.simple.parser.ParseException {
		JSONObject jsonObject = new JSONObject();
		String update_date = "[{\"count\":1,";
		String date = "{\"count\":0,";
		try {

			startDate = startDate.replace(".", "");
			endDate = endDate.replace(".", "");
			String url = String.format(NAVER_UNIFIED_CAFE_URL, URLEncoder.encode(keyword, "UTF-8"), startDate, endDate,
					"1");
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT)
					.header("Content-Type", "application/json;charset=UTF-8").method(Connection.Method.GET)
					.ignoreContentType(true).get();
			Elements elements_title = doc.select(".section_head .title_num");

			if (elements_title.size() > 0) {
				// 검색 건수
				Element ele = doc.select(".section_head .title_num").get(0);
				String[] cntText = ele.text().split("/");

				// [Result] 1-10 / 68,360건
				String cnt = cntText[1].replaceAll(",", "").replaceAll("건", "").replaceAll("약", "").replaceAll(" ", "");

				int totalPage = Integer.parseInt(cnt) / 10;
				Elements elements = doc.select(".board_list_wrap .report-item-wrap");

				for (Element element : elements) {
					String str = element.select(".date").text();

					update_date += "\"date\":\"" + str + "\"},{\"count\":1,";
				}
				if (totalPage > 98) {
					totalPage = 99;
				}
				if (totalPage > 1) {
					for (int i = 2; i < totalPage + 1; i++) {
						url = String.format(NAVER_UNIFIED_CAFE_URL, URLEncoder.encode(keyword, "UTF-8"), startDate,
								endDate, i);

						doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
						elements = doc.select(".board_list_wrap .report-item-wrap");

						for (Element element : elements) {
							String str = element.select(".date").text();

							update_date += "\"date\":\"" + str + "\"},{\"count\":1,";
						}
					}
				}

			}
			update_date = update_date.substring(0, update_date.lastIndexOf("{\"count\":1,"));

			// 날짜 포맷 지정
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd"); // 날짜 포맷

			// Date 타입으로 파싱
			Date sDate;
			try {
				sDate = df.parse(startDate.replace(".", ""));

				Date eDate = df.parse(endDate.replace(".", ""));

				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();

				c1.setTime(sDate);
				c2.setTime(eDate);
				update_date += date;

				while (c1.compareTo(c2) != 1) {

					update_date += "\"date\":\"" + sdf.format(c1.getTime()) + "\"},{\"count\":0,";
					c1.add(Calendar.DATE, 1);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!update_date.equals("[{\"count\":0,")) {
				update_date = update_date.substring(0, update_date.lastIndexOf(",{\"count\":0,")) + "]";
				// System.out.println(update_date.replaceAll(" ", ""));
				update_date = getChangeString(update_date.replaceAll(" ", ""));
			}
			jsonObject.put("update_date", getStringtoArray(update_date));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Gson().toJson(jsonObject);
	}

	public String getChangeString(String buzzContents) {
		Calendar c1 = new GregorianCalendar();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd"); // 날짜 포맷
		JSONArray jsonArr = new JSONArray();
		try {
			jsonArr = (JSONArray) new JSONParser().parse(buzzContents);

			// System.out.println(jsonArr.size());
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArr.get(i);
				Date date = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				// System.out.println(jsonObj.get("date"));
				if (jsonObj.get("date").toString().indexOf("일전") != -1) {
					String add_str = jsonObj.get("date").toString().substring(0,
							jsonObj.get("date").toString().indexOf("일전"));

					cal.add(Calendar.DATE, Integer.parseInt("-" + add_str));
					jsonObj.put("date", sdf.format(cal.getTime()));

					jsonArr.set(i, jsonObj);

				} else if (jsonObj.get("date").toString().indexOf("어제") != -1) {
					cal.add(Calendar.DATE, -1);
					jsonObj.put("date", sdf.format(cal.getTime()));

					jsonArr.set(i, jsonObj);
				} else if (jsonObj.get("date").toString().indexOf("시간전") != -1) {
					String add_str = jsonObj.get("date").toString().substring(0,
							jsonObj.get("date").toString().indexOf("시간전"));
					cal.add(Calendar.HOUR, Integer.parseInt("-" + add_str));
					jsonObj.put("date", sdf.format(cal.getTime()));

					jsonArr.set(i, jsonObj);
				} else if (jsonObj.get("date").toString().indexOf("분전") != -1) {
					String add_str = jsonObj.get("date").toString().substring(0,
							jsonObj.get("date").toString().indexOf("분전"));
					cal.add(Calendar.MINUTE, Integer.parseInt("-" + add_str));
					jsonObj.put("date", sdf.format(cal.getTime()));

					jsonArr.set(i, jsonObj);
				} else if (jsonObj.get("date").toString().indexOf("초전") != -1) {
					String add_str = jsonObj.get("date").toString().substring(0,
							jsonObj.get("date").toString().indexOf("초전"));
					cal.add(Calendar.SECOND, Integer.parseInt("-" + add_str));
					jsonObj.put("date", sdf.format(cal.getTime()));

					jsonArr.set(i, jsonObj);
				}
			}
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(jsonArr);

		return jsonArr.toJSONString();

	}

	public String getStringtoArray(String data_str) throws org.json.simple.parser.ParseException {
		JSONArray arrayList = new JSONArray();
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(data_str);
		JSONArray data = (JSONArray) obj;

		for (int k = 0; k < data.size(); k++) {
			Boolean unique = true;
			JSONObject dataobj_ = (JSONObject) data.get(k);

			for (int h = 0; h < arrayList.size(); h++) {

				JSONObject dataobj_2 = (JSONObject) arrayList.get(h);

				if ((dataobj_.get("date").equals(dataobj_2.get("date")))) {
					Integer a = Integer.parseInt(dataobj_.get("count").toString())
							+ Integer.parseInt(dataobj_2.get("count").toString());

					dataobj_2.put("count", a);

					unique = false;
					arrayList.remove(h);
					arrayList.add(dataobj_2);
					h += 1;

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
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject a, JSONObject b) {
				Long valA = new Long(Integer.parseInt(a.get("count").toString()));
				Long valB = new Long(Integer.parseInt(b.get("count").toString()));

				return -valA.compareTo(valB);
			}
		});

		return arrayList.toJSONString();

	}
}
