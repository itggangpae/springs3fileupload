package com.adamsoft.fileupload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Component
@RestControllerAdvice
public class FileUploadFailedException {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ErrorResponse response = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "용량 초과").build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
