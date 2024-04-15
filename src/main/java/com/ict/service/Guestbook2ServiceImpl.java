package com.ict.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ict.dao.DAO;
import com.ict.dao.VO;

@Service
public class Guestbook2ServiceImpl implements Guestbook2Service{
	
	// SqlSessionTemplate을 가지고 있는 DAO(configration에 의해 이미 만들어져 있음)와 연결
	@Autowired
	private DAO dao;

	// 오류 로그 찍기 위해 사용하는 객체
	private static final Logger logger = LoggerFactory.getLogger(Guestbook2ServiceImpl.class);
	
	@Override
	public List<VO> getGuestbook2List() {
		return dao.getGuestbook2List();
	}

	@Override
	public VO getGuestbook2Detail(String idx) {
		return dao.getGuestbook2Detail(idx);
	}

	@Override
	public int getGuestbook2Insert(VO vo) {
		return dao.getGuestbook2Insert(vo);
	}

	@Override
	public int getGuestbook2Delete(String idx) {
		return dao.getGuestbook2Delete(idx);
	}

	@Override
	public int getGuestbook2Update(VO vo) {
		return dao.getGuestbook2Update(vo);
	}
	
}
