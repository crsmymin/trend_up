package com.cside.ap.Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cside.ap.Service.MorphemeAnalysisSevice;
import com.cside.ap.Service.NaverCrawlerService;
import com.cside.ap.Service.NaverService;
import com.cside.ap.Service.TwitterService;
import com.cside.ap.Service.ZumService;
import com.cside.ap.Service.AdminService;
import com.cside.ap.Service.EmotionAnalysisService;
import com.cside.ap.VO.SearchModel;

@RestController
public class SearchRestController {

	@Autowired
	AdminService adminService;
	
	@Autowired
	NaverService naverService;

	@Autowired
	ZumService zumService;

	@Autowired
	NaverCrawlerService naverCrawlerService;

	@Autowired
	TwitterService twitterService;

	@Autowired
	MorphemeAnalysisSevice morphemeAnalysisSevice;

	@Autowired
	EmotionAnalysisService emotionAnalysisService;
	
	@RequestMapping(value = "/searchRank")
	public void searchRank(SearchModel searchModel, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Content-Type", "text/html;charset=utf-8");
		JSONObject jsonObject = new JSONObject();

		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");

		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchRank");

		JSONObject jsonObject_naver = new JSONObject();
		jsonObject_naver = naverService.getNaverRank(searchModel.getSearchValue().replace(".", "-"));
		jsonObject.put("naver", jsonObject_naver);

		JSONObject jsonObject_zum = new JSONObject();
		jsonObject_zum = zumService.getZumRank(searchModel.getSearchValue().replace(".", ""));
		jsonObject.put("zum", jsonObject_zum);
		
		String twitterSearchValue = searchModel.getSearchValue().replace("T", "/");
		twitterSearchValue = twitterSearchValue.replace(":00:00", "").replace(".", "-");

		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("HH");
		Date date = new Date();

		if (twitterSearchValue.indexOf(format1.format(date)) > -1) {

			String result = twitterSearchValue.substring(twitterSearchValue.lastIndexOf("/") + 1);
			if (result == format2.format(date)) {
				twitterSearchValue = "";
			} else {
				twitterSearchValue = Integer
						.toString(Integer.parseInt(format2.format(date)) - Integer.parseInt(result));
			}
		}

		JSONObject jsonObject_twitter = new JSONObject();
		jsonObject_twitter = twitterService.getTwitterRank(twitterSearchValue);
		jsonObject.put("twitter", jsonObject_twitter);
		response.getWriter().print(jsonObject);
	}
	@RequestMapping(value = "/searchRankAuto")
	public void searchRankAuto(SearchModel searchModel, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Content-Type", "text/html;charset=utf-8");
		JSONObject jsonObject = new JSONObject();

		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");

		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchRankAuto");

		JSONObject jsonObject_naver = new JSONObject();
		jsonObject_naver = naverService.getNaverRank(searchModel.getSearchValue().replace(".", "-"));
		jsonObject.put("naver", jsonObject_naver);

		response.getWriter().print(jsonObject);
	}

	@RequestMapping(value = "/searchNaverNews")
	public ResponseEntity<SearchModel> searchNaverNews(SearchModel searchModel, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");

		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchNaverNews");

		String searchValue = adminService.encodingString(searchModel.getSearchValue());
		searchModel.setSearchValue(searchValue);
		searchModel.setNaverNews(naverService.getNaverNews(searchModel.getSearchValue()));

		// Twitter API
		// searchModel.setTwitter(
		// twitterService.getTwitterSearch(searchModel.getSearchValue()) );

		searchModel.setNaverCrawlerNews(naverCrawlerService.getUnifiedSearchNews(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate(), searchModel.getStart()));
		searchModel.setNaverCrawlerCafe(naverCrawlerService.getUnifiedSearchCafe(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate(), searchModel.getStart()));
		searchModel.setNaverCrawlerBlog(naverCrawlerService.getUnifiedSearchBlog(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate(), searchModel.getStart()));

		return new ResponseEntity<>(searchModel, httpStatus);
	}

	@RequestMapping(value = "/drawWordCloud")
	public ResponseEntity<SearchModel> drawWordCloud(SearchModel searchModel, HttpServletRequest request)
			throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;
		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");
		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "drawWordCloud");

		String searchValue = adminService.encodingString(searchModel.getSearchValue());
		searchModel.setSearchValue(searchValue);

		String naverContents = this.naverCrawlerService.getUnifiedSearchNewsDesc(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate());
		
		String naverContents_news = "";

		if (!naverContents.equals("") && naverContents != null && !naverContents.equals("{\"description\":\"\"}")) {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(naverContents);
			JSONObject jsonObj = (JSONObject) obj;
			naverContents = (String) jsonObj.get("description");
			if (naverContents.length() > 10000) {
				naverContents = naverContents.substring(10000);
			}
			naverContents_news = this.morphemeAnalysisSevice.getMorpheme(naverContents);
		}

		naverContents = this.naverCrawlerService.getUnifiedSearchCafeDesc(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate());
		String naverContents_cafe = "";

		if (!naverContents.equals("") && naverContents != null && !naverContents.equals("{\"description\":\"\"}")) {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(naverContents);
			JSONObject jsonObj = (JSONObject) obj;
			naverContents = (String) jsonObj.get("description");
			if (naverContents.length() > 10000) {
				naverContents = naverContents.substring(0, 10000);
			}

			naverContents_cafe = this.morphemeAnalysisSevice.getMorpheme(naverContents);
		}

		naverContents = this.naverCrawlerService.getUnifiedSearchBlogDesc(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate());

		String naverContents_blog = "";

		if (!naverContents.equals("") && naverContents != null && !naverContents.equals("{\"description\":\"\"}")) {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(naverContents);
			JSONObject jsonObj = (JSONObject) obj;
			naverContents = (String) jsonObj.get("description");
			if (naverContents.length() > 10000) {
				naverContents = naverContents.substring(0, 10000);
			}

			naverContents_blog = this.morphemeAnalysisSevice.getMorpheme(naverContents);

		}
		String naverContents_total = naverContents_news + naverContents_cafe + naverContents_blog;

		naverContents_total = naverContents_total.replace("[", "");
		naverContents_total = naverContents_total.replace("]", ",");

		if (!naverContents_total.equals("")) {
			naverContents_total = "[" + naverContents_total.substring(0, naverContents_total.length() - 1) + "]";
			
		}
		searchModel.setMorpheme(getStringtoArray(naverContents_total,"word"));
		
		return new ResponseEntity<>(searchModel, httpStatus);

	}

	@RequestMapping(value = "/drawBuzzChart")
	public ResponseEntity<SearchModel> drawBuzzChart(SearchModel searchModel, HttpServletRequest request)
			throws Exception {
		String searchValue = adminService.encodingString(searchModel.getSearchValue());
		searchModel.setSearchValue(searchValue);
		
		HttpStatus httpStatus = HttpStatus.OK;
		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");
		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "drawBuzzChart");
		
		String naverContents_cafe = this.naverCrawlerService.getSearchCafeBuzz(searchModel.getSearchValue(), searchModel.getStartDate(), searchModel.getEndDate());
		
		if (!naverContents_cafe.equals("")) {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(naverContents_cafe);
		JSONObject jsonObj = (JSONObject)obj;
		naverContents_cafe = (String)jsonObj.get("update_date");
		}
		
		searchModel.setUploadDateCafe(getStringtoArray(naverContents_cafe,"date"));

		JSONArray naverContents_news = new JSONArray();
		naverContents_news = this.naverCrawlerService.getSearchBuzz(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate(), "news");

		searchModel.setUploadDateNews(naverContents_news.toJSONString());
		//System.out.println("naverContents_news  "+naverContents_news.toJSONString());
		
		JSONArray naverContents_blog = new JSONArray();
		naverContents_blog = this.naverCrawlerService.getSearchBuzz(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate(), "blog");
		searchModel.setUploadDateBlog(naverContents_blog.toJSONString());
		//System.out.println("naverContents_blog  "+naverContents_blog.toJSONString());

		return new ResponseEntity<>(searchModel, httpStatus);
	}

	@RequestMapping(value = "/searchCrawlerNews")
	public ResponseEntity<SearchModel> searchCrawlerNews(SearchModel searchModel, HttpServletRequest request)
			throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");

		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchNaverNews");
		String searchValue = adminService.encodingString(searchModel.getSearchValue());
		searchModel.setSearchValue(searchValue);

		
		searchModel.setNaverCrawlerNews(naverCrawlerService.getUnifiedSearchNews(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate(), searchModel.getStart()));

		return new ResponseEntity<>(searchModel, httpStatus);
	}

	@RequestMapping(value = "/searchCrawlerCafe")
	public ResponseEntity<SearchModel> searchCrawlerCafe(SearchModel searchModel, HttpServletRequest request)
			throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");

		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchNaverNews");
		String searchValue = adminService.encodingString(searchModel.getSearchValue());
		searchModel.setSearchValue(searchValue);
		searchModel.setNaverCrawlerCafe(naverCrawlerService.getUnifiedSearchCafe(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate(), searchModel.getStart()));

		return new ResponseEntity<>(searchModel, httpStatus);
	}

	@RequestMapping(value = "/searchCrawlerBlog")
	public ResponseEntity<SearchModel> searchCrawlerBlog(SearchModel searchModel, HttpServletRequest request)
			throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");

		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchNaverNews");
		String searchValue = adminService.encodingString(searchModel.getSearchValue());
		searchModel.setSearchValue(searchValue);
		searchModel.setNaverCrawlerBlog(naverCrawlerService.getUnifiedSearchBlog(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate(), searchModel.getStart()));

		return new ResponseEntity<>(searchModel, httpStatus);
	}

	@RequestMapping(value = "/emotionAnalysis")
	public ResponseEntity<SearchModel> emotionAnalysis(SearchModel searchModel, HttpServletRequest request)
			throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginID");

		Map<String, String> map = new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "emotionAnalysis");

		String searchValue = adminService.encodingString(searchModel.getSearchValue());
		searchModel.setSearchValue(searchValue);

		searchModel.setEmotionAnalysis(emotionAnalysisService.getEmotionAnalysis(searchModel.getSearchValue(),
				searchModel.getStartDate(), searchModel.getEndDate()));

		return new ResponseEntity<>(searchModel, httpStatus);
	}
	
	public String getStringtoArray(String data_str,String data_get) throws org.json.simple.parser.ParseException {
		JSONArray arrayList = new JSONArray();
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(data_str);
		JSONArray data = (JSONArray) obj;

		for (int k = 0; k < data.size(); k++) {
			Boolean unique = true;
			JSONObject dataobj_ = (JSONObject) data.get(k);

			for (int h = 0; h < arrayList.size(); h++) {

				JSONObject dataobj_2 = (JSONObject) arrayList.get(h);

				if ((dataobj_.get(data_get).equals(dataobj_2.get(data_get)))) {
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
		for (int i = 0; i < arrayList.size(); i++) {
	        sortedJsonArray.add(jsonValues.get(i));
	        if (i==29) break;
	    }
	    
		return sortedJsonArray.toJSONString();

	}
}
