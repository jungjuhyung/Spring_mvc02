package com.ict.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


// DAO에 사용하는 annotation
@Repository
public class DAO {
	// 오류 확인해보기 위한 sysout 대신 사용
	// logger.info("문자",[옵션]); 로 오류 출력
	// logger.debug("문자",[옵션]); 로 오류 출력
	// logger.error("문자",[옵션]); 로 오류 출력
	// logger.warn("문자",[옵션]); 로 오류 출력
	private static final Logger logger = LoggerFactory.getLogger(DAO.class);
	
	// DB처리할 SqlSessionTemplate(configration으로 이미 만들어져있음)와 연결 
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	public List<VO> getGuestbook2List(){
		try {
			return sqlSessionTemplate.selectList("guestbook2.list");
		} catch (Exception e) {
			System.out.println("하이");
			logger.info("list", e);
		}
		return null;
	}
	
	public int getGuestbook2Insert(VO vo) {
		try {
			return sqlSessionTemplate.insert("guestbook2.insert", vo);
		} catch (Exception e) {
			System.out.println("하이");
			logger.info("Insert", e);
		}
		return -1;
	}
	public VO getGuestbook2Detail(String idx){
		try {
			return sqlSessionTemplate.selectOne("guestbook2.selectone", idx);
		} catch (Exception e) {
			System.out.println("하이");
			logger.info("Detail", e);
		}
		return null;
	}
	public int getGuestbook2Delete(String idx){
		try {
			return sqlSessionTemplate.delete("guestbook2.delete", idx);
		} catch (Exception e) {
			System.out.println("하이");
			logger.info("Delete", e);
		}
		return -1;
	}
	
	public int getGuestbook2Update(VO vo) {
		try {
			return sqlSessionTemplate.update("guestbook2.update", vo);
		} catch (Exception e) {
			System.out.println("하이");
			logger.info("Update", e);
		}
		return -1;
	}
}
