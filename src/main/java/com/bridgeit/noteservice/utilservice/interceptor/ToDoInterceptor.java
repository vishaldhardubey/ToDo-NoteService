/*package com.bridgeit.noteservice.utilservice.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bridgeit.noteservice.securityservice.security.TokenGenerator;
import com.bridgeit.noteservice.repository.IRedisRepository;

*//**
 * @author : Vishal Dhar Dubey
 * @version: 1.0
 *           <p>
 *           <b>ToDoInterceptor is to control the funcationality of all the
 *           API's from view to the controller and vice-versa.</b>
 *           </p>
 * @since : 30-07-2018
 *//*
@Component
public class ToDoInterceptor implements HandlerInterceptor {

	static final Logger LOGGER = LoggerFactory.getLogger(ToDoInterceptor.class);

	@Autowired
	TokenGenerator tokenGenerator;

	@Autowired
	IRedisRepository iRedisRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		LOGGER.info("Inside the prehandle interceptor : "+request.getRequestURI());
		String token = request.getHeader("token");
		String userId = tokenGenerator.parseJWT(token);
		LOGGER.info(token);
		String tokenn = iRedisRepository.getToken(userId);
		if (tokenn == null) {
			LOGGER.info("Ending the prehandle interceptor by returning :"+false);
			return false;
		}
		request.setAttribute("userId", userId);
		LOGGER.info(tokenn);
		LOGGER.info("Ending the prehandle interceptor : "+request.getRequestURI());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);

	}
}
*/