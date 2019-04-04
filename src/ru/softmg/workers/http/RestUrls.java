package ru.softmg.workers.http;

public interface RestUrls {
    String LOGIN = "http://workers.softmg.ru/api/login";
    String REPORTS_LIST = "http://workers.softmg.ru/frontend/profile/getWorkReportList";
    String PROJECTS_LIST = "http://workers.softmg.ru/frontend/profile/getProjectsList";
    String TASKS_LIST = "http://workers.softmg.ru/frontend/profile/getProjectTasks";
    String CREATE_REPORT = "http://workers.softmg.ru/frontend/profile/createWorkReport";
    String CREATE_DAILY_REPORT = "http://workers.softmg.ru/frontend/profile/createDailyWorkReport";
}
