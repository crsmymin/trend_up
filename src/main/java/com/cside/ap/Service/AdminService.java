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
	
}
