package com.swprojects.swfinancialapi.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    List<ErroResponse> erros = Arrays.asList(new ErroResponse(mensagemUsuario, mensagemDesenvolvedor));
    return handleExceptionInternal(ex, erros, headers,
        HttpStatus.BAD_REQUEST, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status, @NonNull WebRequest request) {

    List<ErroResponse> erros = criarListaDeErros(ex.getBindingResult());
    return handleExceptionInternal(ex, erros, headers,
        HttpStatus.BAD_REQUEST, request);
  }

  private List<ErroResponse> criarListaDeErros(BindingResult bindingResult) {
    List<ErroResponse> erros = new ArrayList<>();
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
      String mensagemDesenvolvedor = fieldError.toString();
      erros.add(new ErroResponse(mensagemUsuario, mensagemDesenvolvedor));
    }
    return erros;
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
