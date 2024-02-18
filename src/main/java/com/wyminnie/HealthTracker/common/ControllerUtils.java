package com.wyminnie.healthtracker.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

public class ControllerUtils {
    public static final String ERR_PASSWORD_MISMATCHED = "ERR_PASSWORD_MISMATCHED";
    public static final String ERR_PERMISSION_DENIED = "ERR_PERMISSION_DENIED";
    public static final String ERR_NOT_AUTHORIZED = "ERR_NOT_AUTHORIZED";
    public static final String ERR_NOT_FOUND = "ERR_NOT_FOUND";
    public static final String ERR_SERVER_ERROR = "ERR_SERVER_ERROR";
    public static final String ERR_DUPLICATED_USERNAME = "ERR_DUPLICATED_USERNAME";
    public static final String ERR_USER_VALID_FAILED = "ERR_USER_VALID_FAILED";

    public static Object ok(Object data) {
        return ok(data, null);
    }

    public static Object wrapJsonResponse(Object data, String error, Object metadata) {
        Map<String, Object> wrapper = new HashMap<>();
        if (error != null) {
            wrapper.put("error", error);
        } else {
            wrapper.put("data", data);
        }
        wrapper.put("metadata", metadata);

        return wrapper;
    }

    public static Object ok(Object data, Object metadata) {
        if (data instanceof Page page) {
            Map<String, Object> pageDto = new HashMap<>();
            pageDto.put("content", page.getContent());
            if (page.getPageable().isUnpaged()) {
                pageDto.put("currentPage", 0);
                pageDto.put("totalPages", 1);
                pageDto.put("totalElements", page.getTotalElements());
            } else {
                pageDto.put("currentPage", page.getPageable().getPageNumber());
                pageDto.put("totalPages", page.getTotalPages());
                pageDto.put("totalElements", page.getTotalElements());
            }

            return wrapJsonResponse(pageDto, null, metadata);
        } else {
            return wrapJsonResponse(data, null, metadata);
        }
    }

    public static Object fail(String error) {
        return wrapJsonResponse(null, error, null);
    }

    public static Object fail(String error, Object metadata) {
        return wrapJsonResponse(null, error, metadata);
    }

    public static Object notFound() {
        return fail(ERR_NOT_FOUND);
    }

    public static Object notAuthorized() {
        return fail(ERR_NOT_AUTHORIZED);
    }

    public static Object permissionDenied() {
        return fail(ERR_PERMISSION_DENIED);
    }

    public static Object serverError() {
        return fail(ERR_SERVER_ERROR);
    }

    public static Object passwordMismatched() {
        return fail(ERR_PASSWORD_MISMATCHED);
    }

    public static Object duplicatedUsername() {
        return fail(ERR_DUPLICATED_USERNAME);
    }

    public static Object userValid() {
        return fail(ERR_USER_VALID_FAILED);
    }

}
