package com.elitefolk.authservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalErrorDto<T> {
    private String errorMessage;
    private String errorCode;
    private T errorDetails;
}
