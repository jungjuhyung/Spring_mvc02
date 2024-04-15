package com.ict.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ict.dao.VO;
import com.ict.service.Guestbook2Service;

@Controller
public class Guestbook2Controller {
	// Service인터페이스를 불러와서 변수로 저장
	// ServiceImpl의 자료형으로 사용하기 위해
	@Autowired
	private Guestbook2Service guestbook2Service;
	
	// 암호화는 Spring Security 지원하므로 pom.xml에 추가해야 된다.
	// spring_security.xml(configration data)에 Bean태그를 통해 객체 생성 지정
	// web.xml에서 spring_security.xml을 읽도록 지정해준다.
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("gb2_list.do")
	public ModelAndView getGuestbook2List() {
		ModelAndView mv = new ModelAndView("list");
		List<VO> list = guestbook2Service.getGuestbook2List();
		if (list != null) {
			mv.addObject("list", list);
			return mv;
		}
		return new ModelAndView("error");
	}
	
	@GetMapping("gb2_write.do")
	public ModelAndView getGuestBook2Write() {
		return new ModelAndView("write");
	}
	
	@PostMapping("gb2_write_ok.do")
	public ModelAndView getGuestbook2WriteOK(VO vo, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("redirect:gb2_list.do");
		try {
			String path = request.getSession().getServletContext().getRealPath("/resources/upload");
			// 넘어온 파일의 정보 중 파일의 이름은 f_name에 넣어줘야 DB에 저장 할 수 있다.
			MultipartFile file = vo.getFile();
			if (file.isEmpty()) {
				vo.setF_name("");
			}else {
				// 파라미터로 받은 file을 이용해서 DB에 저장할 f_name을 채워주자.
				// MultipartFile은 같은 이름의 파일은 업로드가 안됨으로 파일 이름을 변경한다.
				// 파일 이름은 UUID(겹치지 않는 랜덤 숫자 생성 객체)를 이용해서 변경 후 DB에 저장하자.
				UUID uuid = UUID.randomUUID();
				String f_name = uuid.toString()+"_"+file.getOriginalFilename();
				vo.setF_name(f_name);
				
				// 이미지 저장
				byte[] in = vo.getFile().getBytes();
				File out = new File(path, f_name);
				FileCopyUtils.copy(in, out);
			}
			
			// 패스워드 암호화
			String pwd = passwordEncoder.encode(vo.getPwd());
			vo.setPwd(pwd);
			
			// DB 저장
			int result = guestbook2Service.getGuestbook2Insert(vo);
			if (result > 0) {
				return mv;
			}else {
				return new ModelAndView("error");
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("하하하");			
		}
		return new ModelAndView("error");
	}
	
	@GetMapping("gb2_detail.do")
	public ModelAndView onelist (String idx) {
		ModelAndView mv = new ModelAndView("onelist");
		VO g2vo = guestbook2Service.getGuestbook2Detail(idx);
		if (g2vo != null) {
			mv.addObject("g2vo", g2vo);
			return mv;
		}
		return new ModelAndView("error");
	}
	
	@GetMapping("guestbook2_down.do")
	public void getguestBook2Down(HttpServletRequest request, HttpServletResponse response) {
		try {
			String f_name = request.getParameter("f_name");
			String path = request.getSession().getServletContext().getRealPath("/resources/upload/"+f_name);
			String r_path = URLEncoder.encode(path, "UTF-8");
			
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=" + r_path);
			
			File file = new File(new String(path.getBytes(), "UTF-8"));
			FileInputStream in = new FileInputStream(file);
			OutputStream out = response.getOutputStream();
			FileCopyUtils.copy(in, out);
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	@PostMapping("gb2_delete.do")
	public ModelAndView getGuestBook2Delete(@ModelAttribute("vo2")VO vo) {
		return new ModelAndView("delete");
	}
	@PostMapping("gb2_delete_ok.do")
	public ModelAndView getGuestBook2DeleteOK(VO vo) {
		ModelAndView mv = new ModelAndView();
		
		// 비밀번호가 맞는지 틀린지 검사하자(DB에 있는 비밀번호가 암호로 되어있다.)
		// jsp에서 입력받음 암호
		String cpwd = vo.getPwd();
		
		VO vo2 = guestbook2Service.getGuestbook2Detail(vo.getIdx());
		
		// DB에서 가지고 온 암호화 된 pwd
		String dpwd = vo2.getPwd();
		
		// passwordEncoder.matches(암호화 되지 않는것, 암호화가 된것)
		// 일치하면 true, 불일치하면 false
		
		if (!passwordEncoder.matches(cpwd,dpwd)) {
			mv.setViewName("delete");
			mv.addObject("pwdcheck", "fail");
			mv.addObject("vo2", vo);
			return mv;
		}else {
			int result = guestbook2Service.getGuestbook2Delete(vo.getIdx());
			if (result > 0) {
				mv.setViewName("redirect:gb2_list.do");
				return mv;
			}
			return new ModelAndView("error");
		}
	}
	
	@PostMapping("gb2_update.do")
	public ModelAndView getGuestBook2Update(String idx) {
		ModelAndView mv = new ModelAndView("update");
		VO vo = guestbook2Service.getGuestbook2Detail(idx);
		if (vo != null) {
			mv.addObject("vo", vo);
			return mv;
		}
		return new ModelAndView("error");
	}
	
	@PostMapping("gb2_update_ok.do")
	public ModelAndView getGuestBook2UpdateOK(VO vo, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		
		// 비밀번호가 맞는지 틀린지 검사하자(DB에 있는 비밀번호가 암호로 되어있다.)
		// jsp에서 입력받음 암호
		String cpwd = vo.getPwd();
		
		VO vo2 = guestbook2Service.getGuestbook2Detail(vo.getIdx());
		
		// DB에서 가지고 온 암호화 된 pwd
		String dpwd = vo2.getPwd();
		
		// passwordEncoder.matches(암호화 되지 않는것, 암호화가 된것)
		// 일치하면 true, 불일치하면 false
		
		if (!passwordEncoder.matches(cpwd,dpwd)) {
			mv.setViewName("update");
			mv.addObject("pwdcheck", "fail");
			mv.addObject("vo", vo2);
			return mv;
		}else {
			try {
				String path = request.getSession().getServletContext().getRealPath("/resources/upload");
				MultipartFile file = vo.getFile();
				String old_f_name = vo.getOld_f_name();
				if (file.isEmpty()) {
					vo.setF_name(old_f_name);
				}else {
					UUID uuid = UUID.randomUUID();
					String f_name = uuid.toString()+"_"+file.getOriginalFilename();
					vo.setF_name(f_name);
					
					// 이미지 복사 붙이기
					byte[] in = file.getBytes();
					File out = new File(path, f_name);
					FileCopyUtils.copy(in, out);
					
				}
				int result = guestbook2Service.getGuestbook2Update(vo);
				if (result > 0) {
					mv.setViewName("redirect:gb2_detail.do?idx="+vo.getIdx());
					return mv;
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			
		}
		return new ModelAndView("error");
	}
}
