/**
 * 
 */
package com.lirong.gascard.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author： pengl
 * @Date：2016年6月28日 下午10:59:46 
 * @Description：cookie工具类
 */
@Component("cookieUtil")
public class CookieUtil {
	/**
	 * 设置Cooke（单位秒）
	 * @param request
	 * @param response
	 * @param cookieKey
	 * @param cookieValue
	 * @param timeOut
	 */
	public void setCookie(HttpServletRequest request,
                          HttpServletResponse response, String cookieKey, String cookieValue,
                          int timeOut) {
		Cookie cookies[] = request.getCookies();
		Cookie cookie = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (!cookies[i].getName().equalsIgnoreCase(cookieKey))
					continue;
				cookie = cookies[i];
				break;
			}
		}
		if (cookie == null) {
			cookie = new Cookie(cookieKey, cookieValue);
			cookie.setPath("/");
			cookie.setMaxAge(timeOut);
		} else {
			cookie.setValue(cookieValue);
			cookie.setPath("/");
			cookie.setMaxAge(timeOut);
		}
		response.addCookie(cookie);
	}
	/**
	 * 设置Cooke（默认会话级cookie）
	 * @param request
	 * @param response
	 * @param cookieKey
	 * @param cookieValue
	 */
	public void setCookie(HttpServletRequest request,
                          HttpServletResponse response, String cookieKey, String cookieValue) {
		Cookie cookies[] = request.getCookies();
		Cookie cookie = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (!cookies[i].getName().equalsIgnoreCase(cookieKey))
					continue;
				cookie = cookies[i];
				break;
			}
		}
		if (cookie == null) {
			cookie = new Cookie(cookieKey, cookieValue);
			cookie.setPath("/");
		} else {
			cookie.setValue(cookieValue);
			cookie.setPath("/");

		}

		response.addCookie(cookie);

	}
	
	public void removeCookie(HttpServletResponse response, String cookieKey){
		
		Cookie cookie = new Cookie(cookieKey , null);
		
		cookie.setMaxAge(0);
		
		cookie.setPath("/");
		
		response.addCookie(cookie); 
		
	}
	
	public String getCookie(HttpServletRequest request, String cookieKey) {

		Cookie cookies[] = request.getCookies();

		Cookie cookie = null;

		String cookieValue = "";

		if (cookies != null) {

			for (int i = 0; i < cookies.length; i++) {

				if (!cookies[i].getName().equalsIgnoreCase(cookieKey))

					continue;

				cookie = cookies[i];

				break;

			}

		}

		if (cookie != null)

			cookieValue = cookie.getValue();

		return cookieValue;

	}
}
