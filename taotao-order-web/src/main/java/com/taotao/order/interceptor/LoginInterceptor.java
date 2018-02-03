package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserLoginService;

/**
 * 判断用户身份的拦截器
 * <p>Title: LoginInterceptor</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Autowired
	private UserLoginService userLoginService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	
	/*
	 *  1 从cookie中取token
		2 没有token，需要跳转到登录页面
		3 有token，则调用sso系统的服务，根据token查询用户信息
		4 如果查不到用户信息，则意味着用户登录已经过期，需要跳转到登录页面
		5 查询到用户信息，则放行
	 * */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//从cookie中取token
		String token = CookieUtils.getCookieValue(request, COOKIE_TOKEN_KEY);
		//如果没有token，直接跳转到sso系统的登录页面
		if (StringUtils.isBlank(token)) {
			//http://localhost:8088/showLogin/login
			String url = SSO_URL + "/page/showLogin?redirect=" + request.getRequestURL().toString() ;
			response.sendRedirect(url);
			//拦截
			return false;
		}
		//如果有token，需要调用sso系统的服务，根据token查询用户信息。
		TaotaoResult result = userLoginService.getUserByToken(token);
		TbUser user = null;
		if (result != null && result.getStatus() == 200) {
			user = (TbUser) result.getData();
		}
		//如果查询不到用户，跳转到sso系统的登录页面
		else {
			String url = SSO_URL + "/page/showLogin?redirect=" + request.getRequestURL().toString() ;
			response.sendRedirect(url);
			//拦截
			return false;
		}
		//把user对象放到request中
		request.setAttribute("user", user);
		//如果查询到用户，放行
		return true;
	}

}
