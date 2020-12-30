package com.cside.ap.Controller;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cside.ap.Service.AdminService;
import com.cside.ap.Service.PropertiesLoader;
import com.cside.ap.Service.Signatures;
import com.cside.ap.VO.AdminVO;

import net.sf.json.JSONObject;



@Controller
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@RequestMapping(value="/login")
	public String admin(){
		return "admin_login";
	}
	@RequestMapping(value="/")
	public String dashboard(){
		return "dashboard";
	}
	@RequestMapping(value="/trend")
	public String trend(){
		return "dashboard";
	}
	@RequestMapping(value="/keyword")
	public String keyword(){
		return "keyword_analyze";
	}
	
	@RequestMapping(value="/login_action" , method = RequestMethod.POST)
	public @ResponseBody Object login_action(@RequestBody AdminVO vo,HttpServletRequest request){
		JSONObject json = new JSONObject();
		
		Map<String, Integer> result = adminService.getList(vo);
		HttpSession session = request.getSession();
		if(result!=null) {
			String id = String.valueOf(result.get("id"));
			
			json.put("id",id);
			session.setAttribute("loginID", id); 
			session.setMaxInactiveInterval(150*60);
		}
		
		return json;
	}
	
}
