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
	@RequestMapping(value="/dashboard")
	public void main() throws GeneralSecurityException{
		try {
			Properties properties = PropertiesLoader.fromResource("sample.properties");
		
			String baseUrl = properties.getProperty("BASE_URL");
			String apiKey = properties.getProperty("API_KEY");
			String secretKey = properties.getProperty("SECRET_KEY");
			String timestamp = String.valueOf(System.currentTimeMillis());
			//System.out.println(timestamp);
			//System.out.println(Signatures.of(timestamp, "GET", "/keywordstool", secretKey));
			
			
			//https://api.naver.com/keywordstool?hintKeywords=애플
			//X-API-KEY 010000000027c98570374508b9b8fd8e949aba0885e3d0cd37c555a6015cf5462e62aadd4b
			//X-Signature
			//X-Customer 2061401
			//X-Timestamp
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/")
	public String dashboard(){
		return "dashboard";
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
