package com.mung.product.request;

import com.mung.common.request.BaseSearchRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchProductCondition extends BaseSearchRequest {

    private Long id;
    private String name;
    private Long compId;
    private Boolean activeForSale;
    private List<Long> categoryIds;
    private List<Long> optionIds;

    @Builder
    public SearchProductCondition(Long id, String name, Long compId, Boolean activeForSale, List<Long> categoryIds, List<Long> optionIds) {
        this.id = id;
        this.name = name;
        this.compId = compId;
        this.activeForSale = activeForSale;
        this.categoryIds = categoryIds;
        this.optionIds = optionIds;
    }
}
