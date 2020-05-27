package org.vdragun.tms.ui.rest.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * Class representing any API error. Contains relevant information about errors
 * that happen during REST calls.
 * 
 * @author Vitaliy Dragun
 *
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.CUSTOM, property = "error", visible = true)
@JsonTypeIdResolver(LowerCaseClassNameResolver.class)
public class ApiError {

    private HttpStatus status;
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private List<ApiSubError> subErrors;

    public ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }

    public void addFieldValidationErrors(List<FieldError> fieldErrors, MessageSource messageSource) {
        fieldErrors.forEach(err -> addValidationError(err, messageSource));
    }

    public void addGlobalValidationErrors(List<ObjectError> globalErrors, MessageSource messageSource) {
        globalErrors.forEach(err -> addValidationError(err, messageSource));
    }

    public void addValidationErrors(Set<ConstraintViolation<?>> violations, MessageSource messageSource) {
        violations.forEach(err -> addValidationError(err, messageSource));
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public List<ApiSubError> getSubErrors() {
        return subErrors;
    }

    public void setSubErrors(List<ApiSubError> subErrors) {
        this.subErrors = subErrors;
    }

    public void addSubError(ApiSubError subError) {
        if (subErrors == null) {
            subErrors = new ArrayList<>();
        }
        subErrors.add(subError);
    }

    @Override
    public int hashCode() {
        return Objects.hash(debugMessage, message, status, subErrors, timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApiError other = (ApiError) obj;
        return Objects.equals(debugMessage, other.debugMessage) && Objects.equals(message, other.message)
                && status == other.status && Objects.equals(subErrors, other.subErrors)
                && Objects.equals(timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        return "ApiError [status=" + status + ", timestamp=" + timestamp + ", message=" + message + ", debugMessage="
                + debugMessage + ", subErrors=" + subErrors + "]";
    }

    private void addValidationError(String object, String field, Object rejectedValue, String message) {
        addSubError(new ApiValidationError(object, field, rejectedValue, message));
    }

    private void addValidationError(String object, String message) {
        addSubError(new ApiValidationError(object, message));
    }

    private void addValidationError(FieldError fieldError, MessageSource messageSource) {
        addValidationError(
                fieldError.getObjectName(),
                fieldError.getField(),
                fieldError.getRejectedValue(),
                getMessage(fieldError, messageSource));
    }


    private void addValidationError(ObjectError objectError, MessageSource messageSource) {
        addValidationError(
                objectError.getObjectName(),
                getMessage(objectError, messageSource));
    }

    /**
     * Convenient method for adding error of :{@link ConstraintViolation}. Usually
     * when a {@link Validated} validation fails.
     */
    private void addValidationError(ConstraintViolation<?> violation, MessageSource messageSource) {
        addValidationError(
                null,
                ((PathImpl) violation.getPropertyPath()).getLeafNode().asString(),
                violation.getInvalidValue(),
                messageSource.getMessage(violation.getMessage(), null, getLocale()));
    }

    private String getMessage(MessageSourceResolvable resolvable, MessageSource messageSource) {
        return messageSource.getMessage(resolvable, getLocale());
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
