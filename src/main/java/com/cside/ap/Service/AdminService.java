package com.cside.ap.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cside.ap.DAO.AdminDAO;
import com.cside.ap.VO.AdminVO;

@Service
public class AdminService {

	@Autowired
	private AdminDAO dataDAO;
	
	public Map<String, Integer> getList(AdminVO vo) {
		
		dataDAO.getList(vo);
		
		return dataDAO.getList(vo);
	}
	public String encodingString(String searchValue){

		try {
			searchValue = new String(searchValue.getBytes("iso-8859-1"), "utf-8");
			//searchValue =searchValue; // 운영서버에 반영할 때!
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return searchValue;
	}
}
