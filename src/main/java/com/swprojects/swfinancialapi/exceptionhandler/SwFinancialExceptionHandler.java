package com.swprojects.swfinancialapi.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Classe responsável por capturar e tratar exceções lançadas pelos controllers
 * da API, retornando mensagens apropriadas ao usuário e desenvolvedor.
 */
@RestControllerAdvice
public class SwFinancialExceptionHandler extends ResponseEntityExceptionHandler {

  @Autowired
  private MessageSource messageSource;

  /**
   * Trata exceções lançadas quando o corpo da requisição está mal formatado
   * (ex: JSON inválido).
   *
   * @param ex      Exceção lançada
   * @param headers Cabeçalhos HTTP
   * @param status  Código de status HTTP
   * @param request Requisição atual
   * @return ResponseEntity com mensagens para o usuário e desenvolvedor
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status, @NonNull WebRequest request) {

    String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
    String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
    List<ErroResponse> erros = Arrays.asList(new ErroResponse(mensagemUsuario, mensagemDesenvolvedor));
    return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Trata exceções de validação de argumentos em métodos (ex: validações de
   * 
   * @Valid).
   *
   *          @param ex Exceção lançada
   * @param headers Cabeçalhos HTTP
   * @param status  Código de status HTTP
   * @param request Requisição atual
   * @return ResponseEntity com lista de erros de validação
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status, @NonNull WebRequest request) {

    List<ErroResponse> erros = criarListaDeErros(ex.getBindingResult());
    return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Trata exceções genéricas do tipo RuntimeException e retorna status 404.
   * Útil para lidar com recursos não encontrados de forma mais genérica.
   *
   * @param ex      Exceção lançada
   * @param request Requisição atual
   * @return ResponseEntity com mensagens de erro
   */
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
    String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
    String mensagemDesenvolvedor = ex.getCause() != null ? ex.toString() : ex.toString();
    List<ErroResponse> erros = Arrays.asList(new ErroResponse(mensagemUsuario, mensagemDesenvolvedor));
    return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Trata a exceção lançada ao tentar deletar um recurso inexistente no banco.
   *
   * @param ex      Exceção lançada
   * @param request Requisição atual
   * @return ResponseEntity com mensagens de erro
   */
  @ExceptionHandler(EmptyResultDataAccessException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
      WebRequest request) {
    String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
    String mensagemDesenvolvedor = ex.getCause() != null ? ex.toString() : ex.toString();
    List<ErroResponse> erros = Arrays.asList(new ErroResponse(mensagemUsuario, mensagemDesenvolvedor));
    return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Constrói uma lista de erros de validação a partir do resultado de
   * validações do Spring.
   *
   * @param bindingResult Resultado da validação
   * @return Lista de mensagens de erro para usuário e desenvolvedor
   */
  private List<ErroResponse> criarListaDeErros(BindingResult bindingResult) {
    List<ErroResponse> erros = new ArrayList<>();
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
      String mensagemDesenvolvedor = fieldError.toString();
      erros.add(new ErroResponse(mensagemUsuario, mensagemDesenvolvedor));
    }
    return erros;
  }

  /**
   * Classe de resposta padrão para erros tratados, contendo mensagens separadas
   * para o usuário e para o desenvolvedor.
   */
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
