package com.cside.ap.Controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cside.ap.Service.MorphemeAnalysisSevice;
import com.cside.ap.Service.NaverCrawlerService;
import com.cside.ap.Service.NaverService;
import com.cside.ap.Service.TwitterService;
import com.cside.ap.Service.EmotionAnalysisService;
import com.cside.ap.VO.SearchModel;

@RestController
public class SearchRestController {
	
	@Autowired
	NaverService naverService;

	@Autowired
	NaverCrawlerService naverCrawlerService;
	
	@Autowired
	TwitterService twitterService;

	@Autowired
	MorphemeAnalysisSevice morphemeAnalysisSevice;

	@Autowired
	EmotionAnalysisService emotionAnalysisService;

	@RequestMapping( value="/searchRank" )
	public void searchRank(SearchModel searchModel,HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/html;charset=utf-8");
		JSONObject jsonObject = new JSONObject(); 
		
		HttpSession session = request.getSession();
		String loginID =(String) session.getAttribute("loginID");
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchRank");
		
		JSONObject jsonObject_naver = new JSONObject(); 
		jsonObject_naver = naverService.getNaverRank(searchModel.getSearchValue().replace(".", "-"));
		jsonObject.put("naver", jsonObject_naver);
		
		String twitterSearchValue=searchModel.getSearchValue().replace("T", "/");
		twitterSearchValue=twitterSearchValue.replace(":00:00", "").replace(".", "-");
		
		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat ( "HH");
		Date date = new Date();
		
		if(twitterSearchValue.indexOf(format1.format(date)) > -1) {
			
			String result = twitterSearchValue.substring(twitterSearchValue.lastIndexOf("/")+1);
			if(result==format2.format(date)) {
				twitterSearchValue="";
			}else {
				twitterSearchValue= Integer.toString(Integer.parseInt(format2.format(date))-Integer.parseInt(result));
			}
		}
		
		JSONObject jsonObject_twitter = new JSONObject(); 
		jsonObject_twitter = twitterService.getTwitterRank(twitterSearchValue);
		jsonObject.put("twitter", jsonObject_twitter);
		response.getWriter().print(jsonObject);
	}
	
	@RequestMapping( value="/searchNaverNews")
	public ResponseEntity<SearchModel> searchNaverNews(SearchModel searchModel,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		
		HttpStatus httpStatus =HttpStatus.OK;
		
		HttpSession session = request.getSession();
		String loginID =(String) session.getAttribute("loginID");
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchNaverNews");
		

		String searchValue = new String(searchModel.getSearchValue().getBytes("iso-8859-1"), "utf-8");
		searchModel.setSearchValue(searchValue);
		searchModel.setNaverNews( naverService.getNaverNews(searchModel.getSearchValue()) );

		//System.out.println("1"+searchModel.getSearchValue());
		//Twitter API
		//searchModel.setTwitter( twitterService.getTwitterSearch(searchModel.getSearchValue()) );
		
		searchModel.setNaverCrawlerNews( naverCrawlerService.getUnifiedSearchNews(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate(), searchModel.getStart()) );
		searchModel.setNaverCrawlerCafe( naverCrawlerService.getUnifiedSearchCafe(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate(), searchModel.getStart()) );
		searchModel.setNaverCrawlerBlog( naverCrawlerService.getUnifiedSearchBlog(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate(), searchModel.getStart()) );
		
		
		return new ResponseEntity<>(searchModel, httpStatus);
	}
	@RequestMapping( value="/drawWordCloud")
	public ResponseEntity<SearchModel> drawWordCloud(SearchModel searchModel, HttpServletRequest request) throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;
	      HttpSession session = request.getSession();
	      String loginID = (String)session.getAttribute("loginID");
	      Map<String, String> map = new HashMap();
	      map.put("login_id", loginID);
	      map.put("action", "drawWordCloud");
		  
	      String searchValue = new String(searchModel.getSearchValue().getBytes("iso-8859-1"), "utf-8");
			searchModel.setSearchValue(searchValue);
			
			//System.out.println("2"+searchModel.getSearchValue());
	      String naverContents = this.naverCrawlerService.getUnifiedSearchNewsDesc(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate());

		 String naverContents_news = "";
		 
		 //System.out.println(naverContents);
       if (!naverContents.equals("") && naverContents != null && !naverContents.equals("{\"description\":\"\"}")) {
          JSONParser parser = new JSONParser();
          Object obj = parser.parse(naverContents);
          JSONObject jsonObj = (JSONObject)obj;
          naverContents = (String)jsonObj.get("description");
          if (naverContents.length() > 10000) {
             naverContents = naverContents.substring(10000);
          }
          //System.out.println(naverContents);
          naverContents_news = this.morphemeAnalysisSevice.getMorpheme(naverContents);
       }

       naverContents = this.naverCrawlerService.getUnifiedSearchCafeDesc(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate());
       //System.out.println("naverContents_news"+naverContents);
       String naverContents_cafe = "";
       
     
       if (!naverContents.equals("") && naverContents != null && !naverContents.equals("{\"description\":\"\"}")) {
          JSONParser parser = new JSONParser();
          Object obj = parser.parse(naverContents);
          JSONObject jsonObj = (JSONObject)obj;
          naverContents = (String)jsonObj.get("description");
          if (naverContents.length() > 10000) {
             naverContents = naverContents.substring(0, 10000);
          }

          naverContents_cafe = this.morphemeAnalysisSevice.getMorpheme(naverContents);
       }

       naverContents = this.naverCrawlerService.getUnifiedSearchBlogDesc(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate());
      
       String naverContents_blog = "";
       
       if (!naverContents.equals("") && naverContents != null && !naverContents.equals("{\"description\":\"\"}")) {
          JSONParser parser = new JSONParser();
          Object obj = parser.parse(naverContents);
          JSONObject jsonObj = (JSONObject)obj;
          naverContents = (String)jsonObj.get("description");
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
          JSONParser parser2 = new JSONParser();
          Object obj2 = parser2.parse(naverContents_total);
          JSONArray jsonObj2 = (JSONArray)obj2;
          searchModel.setMorpheme(jsonObj2.toString());
       }
	      
	      return new ResponseEntity(searchModel, httpStatus);
	      
	   }
	@RequestMapping( value="/drawBuzzChart")
	public ResponseEntity<SearchModel> drawBuzzChart(SearchModel searchModel, HttpServletRequest request) throws Exception {
		
		
	      HttpStatus httpStatus = HttpStatus.OK;
	      HttpSession session = request.getSession();
	      String loginID = (String)session.getAttribute("loginID");
	      Map<String, String> map = new HashMap();
	      map.put("login_id", loginID);
	      map.put("action", "drawBuzzChart");
			
	      String searchValue = new String(searchModel.getSearchValue().getBytes("iso-8859-1"), "utf-8");
			searchModel.setSearchValue(searchValue);
	      JSONArray naverContents_news = new JSONArray(); 
			naverContents_news = this.naverCrawlerService.getSearchBuzz(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate(), "news");
			searchModel.setUploadDateNews(naverContents_news.toJSONString());
			
			JSONArray naverContents_blog = new JSONArray(); 
			naverContents_blog = this.naverCrawlerService.getSearchBuzz(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate(),"blog");
			searchModel.setUploadDateBlog(naverContents_blog.toJSONString());
			
			//JSONArray naverContents_cafe = new JSONArray(); 
			//naverContents_cafe = this.naverCrawlerService.getSearchBuzz(searchModel.getSearchValue(), searchModel.getFromDate(), searchModel.getToDate(),"twitter");
			//searchModel.setUploadDateCafe(naverContents_cafe.toJSONString());
			

			 //System.out.println(naverContents_news);
			 //System.out.println(naverContents_blog);
			 //System.out.println(naverContents_cafe);
	      return new ResponseEntity(searchModel, httpStatus);
	   }
	
	@RequestMapping( value="/searchCrawlerNews")
	public ResponseEntity<SearchModel> searchCrawlerNews(SearchModel searchModel,HttpServletRequest request) throws Exception {
		
		HttpStatus httpStatus =HttpStatus.OK;
		
		HttpSession session = request.getSession();
		String loginID =(String) session.getAttribute("loginID");
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchNaverNews");
		String searchValue = new String(searchModel.getSearchValue().getBytes("iso-8859-1"), "utf-8");
		searchModel.setSearchValue(searchValue);
		searchModel.setNaverCrawlerNews( naverCrawlerService.getUnifiedSearchNews(searchModel.getSearchValue(), searchModel.getToDate(), searchModel.getFromDate(), searchModel.getStart()) );
		
		
		return new ResponseEntity<>(searchModel, httpStatus);
	}
	
	@RequestMapping( value="/searchCrawlerCafe" )
	public ResponseEntity<SearchModel> searchCrawlerCafe(SearchModel searchModel,HttpServletRequest request) throws Exception {
		
		HttpStatus httpStatus =HttpStatus.OK;
		
		HttpSession session = request.getSession();
		String loginID =(String) session.getAttribute("loginID");
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchNaverNews");
		String searchValue = new String(searchModel.getSearchValue().getBytes("iso-8859-1"), "utf-8");
		searchModel.setSearchValue(searchValue);
		searchModel.setNaverCrawlerCafe( naverCrawlerService.getUnifiedSearchCafe(searchModel.getSearchValue(), searchModel.getToDate(), searchModel.getFromDate(), searchModel.getStart()) );
		
		
		return new ResponseEntity<>(searchModel, httpStatus);
	}
	

	@RequestMapping( value="/searchCrawlerBlog" )
	public ResponseEntity<SearchModel> searchCrawlerBlog(SearchModel searchModel,HttpServletRequest request) throws Exception {
		
		HttpStatus httpStatus =HttpStatus.OK;
		
		HttpSession session = request.getSession();
		String loginID =(String) session.getAttribute("loginID");
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "searchNaverNews");
		String searchValue = new String(searchModel.getSearchValue().getBytes("iso-8859-1"), "utf-8");
		searchModel.setSearchValue(searchValue);
		searchModel.setNaverCrawlerBlog( naverCrawlerService.getUnifiedSearchBlog(searchModel.getSearchValue(), searchModel.getToDate(),searchModel.getFromDate(),  searchModel.getStart()) );
		
		
		return new ResponseEntity<>(searchModel, httpStatus);
	}
	
	@RequestMapping( value="/emotionAnalysis" )
	public ResponseEntity<SearchModel> emotionAnalysis(SearchModel searchModel,HttpServletRequest request) throws Exception {
		
		HttpStatus httpStatus =HttpStatus.OK;
		
		HttpSession session = request.getSession();
		String loginID =(String) session.getAttribute("loginID");
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("login_id", loginID);
		map.put("action", "emotionAnalysis");
		
		String searchValue = new String(searchModel.getSearchValue().getBytes("iso-8859-1"), "utf-8");
		searchModel.setSearchValue(searchValue);
		
		searchModel.setEmotionAnalysis( emotionAnalysisService.getEmotionAnalysis(searchModel.getSearchValue(), searchModel.getToDate(),searchModel.getFromDate()) );
		
		return new ResponseEntity<>(searchModel, httpStatus);
	}
}
