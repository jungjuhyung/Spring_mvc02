package com.ict.service;

import java.util.List;

import com.ict.dao.VO;

public interface Guestbook2Service {
	
	// 전체보기
	List<VO> getGuestbook2List();
	
	// 상세보기
	VO getGuestbook2Detail(String idx);
	
	// 삽입
	int getGuestbook2Insert(VO vo);
	
	// 삭제
	int getGuestbook2Delete(String idx);
	
	// 수정
	int getGuestbook2Update(VO vo);
	
}
