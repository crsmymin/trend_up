package com.cside.ap.Controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cside.ap.Service.AdminService;
import com.cside.ap.Service.HashtagService;
import com.cside.ap.VO.SearchModel;

@Controller
public class HashtagController {

	@Autowired
	AdminService adminService;
	
	@Autowired
	private HashtagService hashtagService;
	
	@RequestMapping(value="/hashtag")
	public String admin(){
		return "hashtag";
	}

	@RequestMapping(value="/hashtagRank", produces = "application/json; charset=utf8")
	public void hashtagRank (HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/html;charset=utf-8");
		JSONObject jsonObject = new JSONObject();
		
		JSONObject jsonObject_kor = new JSONObject();
		jsonObject_kor = hashtagService.getKorHashtagService();
		jsonObject.put("korRank", jsonObject_kor);
		

		JSONObject jsonObject_eng = new JSONObject();
		jsonObject_eng = hashtagService.getEngHashtagService();
		jsonObject.put("engRank", jsonObject_eng);


		JSONObject jsonObject_tiktok = new JSONObject();
		jsonObject_tiktok = hashtagService.getTiktokHashtagService();
		jsonObject.put("tiktokRank", jsonObject_tiktok);

		response.getWriter().print(jsonObject);
	}
	
	@RequestMapping(value = "/relatedHashtag")
	public void searchRelatedHashtags(SearchModel searchModel, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Content-Type", "text/html;charset=utf-8");
		
		String searchValue = adminService.encodingString(searchModel.getSearchValue());
		searchModel.setSearchValue(searchValue);

		JSONObject jsonObject = new JSONObject();
		jsonObject = hashtagService.getRelatedHashtags(searchModel.getSearchValue());
	
		response.getWriter().print(jsonObject);
	}

}
