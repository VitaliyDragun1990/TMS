package org.vdragun.tms.config;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.Arrays;
import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.vdragun.tms.core.application.exception.ConfigurationException;
import org.vdragun.tms.ui.rest.exception.ApiError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String INTERNAL_ERROR = "Internal Server Error";

    private static final String ACCESS_DENIED = "Access is denied";

    private static final String AUTHENTICATION_IS_REQUIRED = "Full authentication is required to access this resource";

    private static final String APPLICATION_JSON = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

    @Autowired
    private ObjectMapper mapper;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER).name("Authorization")))
                .info(new Info()
                        .title("TMS API")
                        .description("Timetable Management Application")
                        .version("v1.0")
                        .license(new License().name("Apache 2.0").url("http://google.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write")));

    }

    @Bean
    public OpenApiCustomiser customGlobalresponseStatusOpenApiCustomizer() {
        return openApi -> {
            List<Operation> operations = openApi.getPaths()
                    .values()
                    .stream().flatMap(pathItem -> pathItem.readOperations().stream())
                    .collect(toList());
            
            addDefaultResponsesToEachOperation(operations);
            addAuthResponsesToProtectedOperations(operations);
        };
    }

    private void addAuthResponsesToProtectedOperations(List<Operation> operations) {
        operations.stream()
                .filter(operation -> !operation.getTags().contains("user"))
                .forEach(operation -> {
          ApiResponse unauthorized = buildUnauthorizedResponse();
          ApiResponse forbidden = buildForbiddenResponse();
            
          operation.getResponses().addApiResponse("401", unauthorized);
          operation.getResponses().addApiResponse("403", forbidden);
        });
    }

    private void addDefaultResponsesToEachOperation(List<Operation> operations) {
        operations.forEach(operation -> {
            ApiResponse internalServerError = buildInternalServerErrorResponse();
            operation.getResponses().addApiResponse("500", internalServerError);
        });
    }
    
    private ApiResponse buildInternalServerErrorResponse() {
        MediaType mediaType = new MediaType();
        mediaType.example(buildExample(INTERNAL_SERVER_ERROR, INTERNAL_ERROR));

        return new ApiResponse()
                .description(INTERNAL_ERROR)
                .content(new Content().addMediaType(APPLICATION_JSON, mediaType));
    }

    private ApiResponse buildForbiddenResponse() {
        MediaType mediaType = new MediaType();
        mediaType.example(buildExample(FORBIDDEN, ACCESS_DENIED));

        return new ApiResponse()
                .description(ACCESS_DENIED)
                .content(new Content().addMediaType(APPLICATION_JSON, mediaType));
    }

    private ApiResponse buildUnauthorizedResponse() {
        MediaType mediaType = new MediaType();
        mediaType.example(buildExample(UNAUTHORIZED, AUTHENTICATION_IS_REQUIRED));

        return new ApiResponse()
                .description("Authorization required")
                .content(new Content().addMediaType(APPLICATION_JSON, mediaType));
    }
    
    private Object buildExample(HttpStatus status, String msg) {
        ApiError error = new ApiError(status);
        error.setMessage(msg);

        return toJson(error);
    }

    private String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ConfigurationException("Error adding global API responses to resources", e);
        }

    }
}
