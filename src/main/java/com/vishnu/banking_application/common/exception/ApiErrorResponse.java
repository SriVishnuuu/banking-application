package com.vishnu.banking_application.common.exception;


import java.time.Instant;
import java.util.Map;


public record ApiErrorResponse (
         Instant timestamp,
         int status,
         String error,
         String message,
         String method,
         String path,
         Map<String, String> fieldErrors){}
