package com.mung.common.request;

import lombok.Getter;

@Getter
public class BaseSearchRequest {

    private String sortBy;
    private String sortDirection;

    private int pageNumber;
    private int pageSize;

}
