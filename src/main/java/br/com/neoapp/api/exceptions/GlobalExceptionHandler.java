package br.com.neoapp.api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Manipulador de exceções global para a aplicação.
 * <p>
 * Anotada com {@code @RestControllerAdvice}, esta classe centraliza o tratamento de exceções
 * para todos os controllers da API. Ela captura exceções específicas e as converte em
 * respostas HTTP padronizadas e significativas para o cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manipula a exceção {@link EmailExistsException}, lançada quando se tenta criar um
     * cliente com um e-mail que já existe.
     *
     * @param e       A exceção {@code EmailExistsException} capturada.
     * @param request O objeto da requisição HTTP que causou o erro.
     * @return um {@link ResponseEntity} com status 409 (Conflict) e um corpo de erro padronizado.
     */
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<StandardError> emailExists(EmailExistsException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(EmailExistsException.ERROR)
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    /**
     * Manipula a exceção {@link CpfExistsException}, lançada quando se tenta criar um
     * cliente com um CPF que já existe.
     *
     * @param e       A exceção {@code CpfExistsException} capturada.
     * @param request O objeto da requisição HTTP que causou o erro.
     * @return um {@link ResponseEntity} com status 409 (Conflict) e um corpo de erro padronizado.
     */
    @ExceptionHandler(CpfExistsException.class)
    public ResponseEntity<StandardError> cpfExists(CpfExistsException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(CpfExistsException.ERROR)
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    /**
     * Manipula a exceção {@link MethodArgumentNotValidException}, que ocorre quando a validação
     * de um DTO anotado com {@code @Valid} falha.
     *
     * @param e       A exceção {@code MethodArgumentNotValidException} capturada, contendo os detalhes dos erros.
     * @param request O objeto da requisição HTTP que causou o erro.
     * @return um {@link ResponseEntity} com status 422 (Unprocessable Entity) e um corpo de erro
     * detalhado com a lista de campos inválidos e suas respectivas mensagens.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationErrors(MethodArgumentNotValidException e, HttpServletRequest request){
        int status = HttpStatus.UNPROCESSABLE_ENTITY.value();

        ValidationError err = (ValidationError) ValidationError.builder()
                .timestamp(Instant.now())
                .status(status)
                .error("Validation Error")
                .message("Dados inválidos. Verifique os erros de cada campo.")
                .path(request.getRequestURI())
                .build();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            err.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }

    /**
     * Manipula a exceção {@link ClientNotFound}, que ocorre quando uma operação
     * tenta acessar um cliente por um ID que não existe.
     *
     * @param e       A exceção {@code ClientNotFound} capturada.
     * @param request O objeto da requisição HTTP que causou o erro.
     * @return um {@link ResponseEntity} com status 404 (Not Found) e um corpo de erro padronizado.
     */
    @ExceptionHandler(ClientNotFound.class)
    public ResponseEntity<StandardError> handleClientNotFound(ClientNotFound e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(ClientNotFound.ERROR)
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    /**
     * Manipula a exceção {@link EmailOrPassworInvalid}, que ocorre durante a tentativa de login
     * com credenciais inválidas.
     *
     * @param e       A exceção {@code EmailOrPassworInvalid} capturada.
     * @param request O objeto da requisição HTTP que causou o erro.
     * @return um {@link ResponseEntity} com status 401 (Unauthorized) e um corpo de erro padronizado.
     */
    @ExceptionHandler(EmailOrPassworInvalid.class)
    public ResponseEntity<StandardError> handleEmailOrPassInvalid(EmailOrPassworInvalid e, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(EmailOrPassworInvalid.ERROR)
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    /**
     * Manipulador genérico para qualquer {@link RuntimeException} não tratada por outros handlers.
     * <p>
     * Atua como uma "rede de segurança" para capturar erros inesperados no servidor.
     *
     * @param e       A exceção {@code RuntimeException} genérica capturada.
     * @param request O objeto da requisição HTTP que causou o erro.
     * @return um {@link ResponseEntity} com status 500 (Internal Server Error) e uma mensagem genérica de erro.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardError> handleRuntimeException(RuntimeException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error("INTERNAL_ERROR")
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    /**
     * Manipula a exceção {@link AuthorizationDeniedException}, que ocorre quando um usuário autenticado
     * tenta acessar um recurso para o qual não tem permissão.
     *
     * @param e       A exceção {@code AuthorizationDeniedException} capturada.
     * @param request O objeto da requisição HTTP que causou o erro.
     * @return um {@link ResponseEntity} com status 403 (Forbidden) e um corpo de erro padronizado.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<StandardError> handleAuthorizationDeniedException(AuthorizationDeniedException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error("Não autorizado.")
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }
}
