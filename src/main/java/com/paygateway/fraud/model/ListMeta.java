package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Pagination metadata")
public class ListMeta {
    private Boolean hasMore;
    private Integer totalCount;
    private String url;
}
