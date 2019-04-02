package ru.softmg.workers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ReportsPage {
    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("first_page_url")
    private String firstPageUrl;

    @JsonProperty("from")
    private Integer from;

    @JsonProperty("last_page")
    private Integer lastPage;

    @JsonProperty("last_page_url")
    private String lastPageUrl;

    @JsonProperty("next_page_url")
    private String nextPageUrl;

    @JsonProperty("path")
    private String path;

    @JsonProperty("per_page")
    private String perPage;

    @JsonProperty("prev_page_url")
    private String prevPageUrl;

    @JsonProperty("to")
    private Integer to;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("data")
    private List<Report> data = new ArrayList<>();
}
