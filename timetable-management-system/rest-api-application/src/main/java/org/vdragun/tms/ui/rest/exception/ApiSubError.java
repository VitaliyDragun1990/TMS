package org.vdragun.tms.ui.rest.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Marker interface for all API sub errors
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO containing additional information about particular API error's sub-error")
public interface ApiSubError {

}
