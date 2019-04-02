package ru.softmg.workers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("project_id")
    private Integer projectId;

    @JsonProperty("task_id")
    private Integer taskId;

    @JsonProperty("task_name")
    private String taskName;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("spent_time")
    private Integer spentTime;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("daily_work_report_id")
    private String dailyWorkReportId;

    @JsonProperty("jira_key")
    private String jiraKey;

    @JsonProperty("jira_url")
    private String jiraUrl;
}
