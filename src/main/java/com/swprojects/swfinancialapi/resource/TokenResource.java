package com.swprojects.swfinancialapi.resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swprojects.swfinancialapi.config.property.SwFinancialApiProperty;

@RestController
@RequestMapping("tokens")
public class TokenResource {

  @Autowired
  private SwFinancialApiProperty swFinancialApiProperty;

  @DeleteMapping("/revoke")
  public void revokeToken(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = new Cookie("refreshToken", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(swFinancialApiProperty.getSeguranca().isEnableHttps());
    cookie.setPath(request.getContextPath() + "/oauth/token");
    cookie.setMaxAge(0);

    response.addCookie(cookie);
    response.setStatus(HttpStatus.NO_CONTENT.value());

  }
}
