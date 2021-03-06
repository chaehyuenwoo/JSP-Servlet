package com.join;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.util.DBConn;

public class MemberServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		doPost(req, resp);
	}
	
	protected void forward(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
		
		RequestDispatcher rd = req.getRequestDispatcher(url);
		rd.forward(req, resp);
		
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		
		Connection conn = DBConn.getConnection();
		MemberDAO dao = new MemberDAO(conn);
		
		String cp = req.getContextPath();
		String uri = req.getRequestURI();
		
		//포워딩시킬 데이터 저장할 url
		String url;
		
		
		if(uri.indexOf("created.do")!= -1) {
			
			url = "/member/created.jsp";
			forward(req, resp, url);
			
		}else if(uri.indexOf("created_ok.do")!= -1) {
			
			MemberDTO dto = new MemberDTO();
			
			dto.setUserId(req.getParameter("userId"));
			dto.setUserPwd(req.getParameter("userPwd"));
			dto.setUserName(req.getParameter("userName"));
			dto.setUserBirth(req.getParameter("userBirth"));
			dto.setUserTel(req.getParameter("userTel"));
			
			dao.insertData(dto);
			
			url = cp;
			resp.sendRedirect(url);
			
			
		}else if(uri.indexOf("login.do")!= -1) {
			
			url = "/member/login.jsp";
			forward(req, resp, url);
			
		}else if(uri.indexOf("login_ok.do")!= -1) {
			
			String userId = req.getParameter("userId");
			String userPwd = req.getParameter("userPwd");
			
			MemberDTO dto = dao.getReadData(userId);
			
			if(dto==null || (!dto.getUserPwd().equals(userPwd))) {
				
				req.setAttribute("message", "아이디 또는 패스워드를 정확히 입력하세요!");
				url = "/member/login.jsp";
				
				forward(req, resp, url);
				
				return;
				
			}
			
			//서블릿에서 세션만드는 방법
			HttpSession session = req.getSession();
			
			//customInfo에 담을 것이니 객체 생성
			CustomInfo info = new CustomInfo();
			
			info.setUserId(dto.getUserId());
			info.setUserName(dto.getUserName());
			
			//세션에 로그인 정보 저장
			session.setAttribute("customInfo", info);
			
			//저장했으니 메인화면으로 가기
			url = cp;
			//세션을 변경시키면 리다이렉트 해야 한다,
			resp.sendRedirect(url);
			
			
			//로그아웃은 세션에 있는 데이터를 지운다,
		}else if(uri.indexOf("logout.do")!= -1) {
			
			HttpSession session = req.getSession();
			
			session.removeAttribute("customInfo"); //세션에 있는 변수명 삭제
			session.invalidate();//세션에 있는 데이터 삭제
			
			url = cp;
			resp.sendRedirect(url);
			
			
		}else if(uri.indexOf("searchpw.do")!=-1){
			
			//비밀번호 찾기
			url = "/member/searchpw.jsp";
			forward(req, resp, url);
			
			
		}else if(uri.indexOf("searchpw_ok.do")!=-1) {
			
			//아이디랑 전화번호랑 같은지 비교
			String userId = req.getParameter("userId");
			String userTel = req.getParameter("userTel");
			
			MemberDTO dto = dao.getReadData(userId);//셀렉트로 받을준비하고 

			if(dto==null || (!dto.getUserTel().equals(userTel))) {

				req.setAttribute("message", "회원정보가 존재하지않습니다!!");

				url = "/member/login.jsp";
				forward(req, resp, url);
				return;				

			}else {
				
				String userPwd = dto.getUserPwd();
				
				req.setAttribute("message", "비밀번호는 " + userPwd + " 입니다");

				url = "/member/login.jsp";
				forward(req, resp, url);
				return;				

			}
			
			/*//아이디랑 전화번호 비교
			if(userId.equals(dto.getUserId())  && userTel.equals(dto.getUserTel())) {
			//아이디가 없거나 패스워드가 틀린것
			//dto가 널이거나 || !dto에 있는 userpwd가 일치하지 않으면 


				String UserPwd= dto.getUserPwd();
				
				req.setAttribute("message2", "비밀번호는 " + UserPwd);

				url = "/member/login.jsp";
				forward(req, resp, url);
				
				return;
				
			} else if(dto==null || (!dto.getUserTel().equals(userTel))) {
				
				req.setAttribute("message3", "회원정보가 존재하지 않습니다");

				url = "/member/login.jsp";
				forward(req, resp, url);
				return;
				
				}*/
				
			} else if(uri.indexOf("updated.do")!=-1){
				
				HttpSession session = req.getSession();
				CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
				
				MemberDTO dto = dao.getReadData(info.getUserId());
				req.setAttribute("dto", dto);
				//회원정보수정 포워드 페이지
				url = "/member/updated.jsp";
				forward(req, resp, url);
				
			}else if(uri.indexOf("updated_ok.do")!=-1) {
				
				HttpSession session = req.getSession();
				CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
				
				
				MemberDTO dto = new MemberDTO();
			
				dto.setUserId(info.getUserId());
				dto.setUserPwd(req.getParameter("userPwd"));
				dto.setUserBirth(req.getParameter("userBirth"));
				dto.setUserTel(req.getParameter("userTel"));
		
				dao.updateData(dto);
			
				url = cp ;
				resp.sendRedirect(url);
				
				
				}
			
			
		}
		
	}
