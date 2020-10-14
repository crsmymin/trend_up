package com.cside.ap.Interceptor;

import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CommonInterceptor extends HandlerInterceptorAdapter{

	 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        //System.out.println("===================       START       ===================");
        //System.out.println(" Request URI \t:  " + request.getRequestURI());
        HttpSession session = request.getSession();
 		if( session.getAttribute("loginID") == null) {
 			// 로그인 화면으로 이동
 			response.sendRedirect(request.getContextPath() + "/login");
 			return false;
 		}
 		
        
        return super.preHandle(request, response, handler);
    }
 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
       
        //System.out.println("===================        END        ===================\n");
    }
}