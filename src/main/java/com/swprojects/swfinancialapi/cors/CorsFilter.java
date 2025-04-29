package com.swprojects.swfinancialapi.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.swprojects.swfinancialapi.config.property.SwFinancialApiProperty;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

  @Autowired
  private SwFinancialApiProperty swFinancialApiProperty;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    httpResponse.setHeader("Access-Control-Allow-Origin", swFinancialApiProperty.getOriginPermitida());
    httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

    if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())
        && swFinancialApiProperty.getOriginPermitida().equals(httpRequest.getHeader("Origin"))) {
      httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
      httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
      httpResponse.setHeader("Access-Control-Max-Age", "3600");
      httpResponse.setStatus(HttpServletResponse.SC_OK);
    } else {
      httpResponse.setHeader("Access-Control-Allow-Origin", "*");
      httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
      httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
      chain.doFilter(request, response);
    }
  }

}
