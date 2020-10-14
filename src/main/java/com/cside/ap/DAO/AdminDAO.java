package com.cside.ap.DAO;


import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cside.ap.VO.AdminVO;


@Repository
public class AdminDAO {

	@Autowired
	private SqlSession sqlSession;
	
	
	public Map<String, Integer> getList(AdminVO vo){
		Map<String, Integer> login_ok = sqlSession.selectOne("admin.getList", vo);
		return login_ok;
	}
}
