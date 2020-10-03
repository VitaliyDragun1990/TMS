package org.vdragun.tms.ui.rest.exception;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents API error produced by some kind of validation failure.
 * 
 * @author Vitaliy Dragun
 *
 */
@JsonInclude(Include.NON_NULL)
public class ApiValidationError implements ApiSubError {

    private String object;

    private String field;

    private Object rejectedValue;

    private String message;

    public ApiValidationError(String object, String field, Object rejectedValue, String message) {
        this.object = object;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public ApiValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, message, object, rejectedValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        ApiValidationError other = (ApiValidationError) obj;

        return Objects.equals(field, other.field) && Objects.equals(message, other.message)
                && Objects.equals(object, other.object) && Objects.equals(rejectedValue, other.rejectedValue);
    }

    @Override
    public String toString() {
        return "ApiValidationError [object=" + object + ", field=" + field + ", rejectedValue=" + rejectedValue
                + ", message=" + message + "]";
    }

}
