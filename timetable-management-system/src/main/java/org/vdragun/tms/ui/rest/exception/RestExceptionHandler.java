package org.vdragun.tms.ui.rest.exception;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Translator;

/**
 * Responsible for handling application exceptions in RESTful resources by
 * providing appropriate response entity to the client
 * 
 * @author Vitaliy Dragun
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(basePackages = "org.vdragun.tms.ui.rest.resource")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Autowired
    private Translator translator;

    @Autowired
    private MessageSource messageSource;

    /**
     * Handles {@link MissingServletRequestParameterException}. Triggered when a
     * 'required' request parameter is missing.
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        LOG.warn("Handling missing request parameter exception for parameter: [{}]", ex.getParameterName());

        String errorMsg = translator.getLocalizedMessage(Message.MISSING_REQUIRED_PARAMETER, ex.getParameterName());
        return buildResponseEntity(new ApiError(BAD_REQUEST, errorMsg, ex));
    }

    /**
     * Handles {@link HttpMediaTypeNotSupportedException}. This one triggered when
     * JSON is invalid.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        LOG.warn("Handling not supported HTTP media type exception for type: [{}]", ex.getContentType());
        
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }

        String supportedTypes = mediaTypes.stream()
                .map(MediaType::toString)
                .collect(joining(", "));
        String errorMsg = translator.getLocalizedMessage(Message.UNSUPPORTED_MEDIA_TYPE, supportedTypes);

        return buildResponseEntity(new ApiError(UNSUPPORTED_MEDIA_TYPE, errorMsg, ex), headers);
    }
    
    /**
     * Handles {@link MethodArgumentNotValidException}. Triggered when an object
     * fails {@link Valid} validation.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        LOG.warn("Handling method argument not valid exception", ex);

        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(translator.getLocalizedMessage(Message.VALIDATION_ERROR));
        apiError.addFieldValidationErrors(ex.getBindingResult().getFieldErrors(), messageSource);
        apiError.addGlobalValidationErrors(ex.getBindingResult().getGlobalErrors(), messageSource);
        return buildResponseEntity(apiError);
    }

    /**
     * Handles {@link ConstraintViolationException}. Triggered when
     * {@link Validated} fails.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        LOG.warn("Handling constraint violation exception", ex);

        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(translator.getLocalizedMessage(Message.VALIDATION_ERROR));
        apiError.addValidationErrors(ex.getConstraintViolations());
        return buildResponseEntity(apiError);
    }

    /**
     * Handles {@link ResourceNotFoundException}. Triggered when requested resource
     * can not be found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourseNotFoundExcepion(ResourceNotFoundException ex) {
        LOG.warn("Handling resource not found exception", ex);

        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(translator.getLocalizedMessage(Message.RESOURCE_NOT_FOUND));
        return buildResponseEntity(apiError);
    }

    /**
     * Handles {@link HttpMessageNotReadableException}. Triggered when request JSON
     * is malformed.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ServletWebRequest webRequest = (ServletWebRequest) request;
        LOG.warn("Handling HTTP message not readable exception: {} to {}",
                webRequest.getHttpMethod(), webRequest.getRequest().getServletPath(), ex);

        String errorMsg = translator.getLocalizedMessage(Message.MALFORMED_JSON_REQUEST);
        return buildResponseEntity(new ApiError(BAD_REQUEST, errorMsg, ex));
    }

    /**
     * Handles {@link HttpMessageNotWritableException}. Triggered when response JSON is malformed
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        LOG.error("Handling HTTP message not writable exception", ex);

        String errorMsg = translator.getLocalizedMessage(Message.MALFORMED_JSON_RESPONSE);
        return buildResponseEntity(new ApiError(INTERNAL_SERVER_ERROR, errorMsg, ex));
    }

    /**
     * Handles {@link NoHandlerFoundException}. Triggered when no handler can be
     * found for given request
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        LOG.warn("Handling no handler found exception: method: [{}], URL: [{}]",
                ex.getHttpMethod(), ex.getRequestURL(), ex);

        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(translator.getLocalizedMessage(Message.NO_HANDLER_FOUND,
                ex.getHttpMethod(), ex.getRequestURL()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    /**
     * Handles {@link DataIntegrityViolationException}. Triggered when some kind of
     * DB integrity constraint is violated.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {
        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            LOG.warn("Handling DB Constraint violation exception", ex.getCause());
            
            String errorMsg = translator.getLocalizedMessage(Message.DB_ERROR);
            return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, errorMsg, ex.getCause()));
        }
        LOG.error("Handling data integrity violation exception", ex);

        String errorMsg = translator.getLocalizedMessage(Message.INTERNAL_SERVER_ERROR);
        return buildResponseEntity(new ApiError(INTERNAL_SERVER_ERROR, errorMsg, ex));
    }

    /**
     * Handles {@link MethodArgumentTypeMismatchException}. Triggered when fail to
     * resolve controller's method argument because of type miss match.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handlemethodArgumentTypeMissmatch(
            MethodArgumentTypeMismatchException ex) {
        LOG.warn("Handling method argument type missmatch exception", ex);

        String argumentName = ex.getName();
        Object offendingValue = ex.getValue();
        Class<?> requiredType = ex.getRequiredType();
        
        String errorMsg = translator.getLocalizedMessage(Message.ARGUMENT_TYPE_MISSMATCH,
                argumentName, offendingValue, requiredType);
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(errorMsg);
        apiError.setDebugMessage(ex.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles other exceptions.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleApplicationException(Exception ex) {
        LOG.error("Handling application exception", ex);

        String errorMsg = translator.getLocalizedMessage(Message.INTERNAL_SERVER_ERROR);
        return buildResponseEntity(new ApiError(INTERNAL_SERVER_ERROR, errorMsg, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return buildResponseEntity(apiError, null);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError, HttpHeaders headers) {
        return new ResponseEntity<>(apiError, headers, apiError.getStatus());
    }

}
