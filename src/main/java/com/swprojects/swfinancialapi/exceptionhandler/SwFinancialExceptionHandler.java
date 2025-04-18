package com.swprojects.swfinancialapi.exceptionhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SwFinancialExceptionHandler extends ResponseEntityExceptionHandler {

  @Autowired
  private MessageSource messageSource;

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status, @NonNull WebRequest request) {

    String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
    String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
    return handleExceptionInternal(ex, new ErroResponse(mensagemUsuario, mensagemDesenvolvedor), headers,
        HttpStatus.BAD_REQUEST, request);
  }

  public static class ErroResponse {
    private String mensagemUsuario;
    private String mensagemDesenvolvedor;

    public ErroResponse(String mensagemUsuario, String mensagemDesenvolvedor) {
      this.mensagemUsuario = mensagemUsuario;
      this.mensagemDesenvolvedor = mensagemDesenvolvedor;
    }

    public String getMensagemUsuario() {
      return mensagemUsuario;
    }

    public String getMensagemDesenvolvedor() {
      return mensagemDesenvolvedor;
    }
  }

}
