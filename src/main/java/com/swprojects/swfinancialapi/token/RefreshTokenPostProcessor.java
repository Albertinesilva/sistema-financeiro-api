package com.swprojects.swfinancialapi.token;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return Optional.ofNullable(returnType.getMethod())
        .map(method -> method.getName().equals("postAccessToken"))
        .orElse(false);
  }

  @Override
  public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
    HttpServletResponse res = ((ServletServerHttpResponse) response).getServletResponse();

    DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;

    String refreshToken = body.getRefreshToken().getValue();
    adicionarRefreshTokenNoCookie(refreshToken, req, res);
    removerRefreshTokenDoBody(token);
    return body;
  }

  private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
    // Remove o refresh token do corpo da resposta
    token.setRefreshToken(null);
  }

  private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse res) {
    // Adiciona o refresh token no cookie
    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false); // TUDO: Mudar para true em produção quando o HTTPS estiver habilitado
    refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token"); // Define o caminho do cookie
    refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 30 dias de expiração
    res.addCookie(refreshTokenCookie);
  }

}
