package ru.softmg.workers.http;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.softmg.workers.model.*;
import ru.softmg.workers.util.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WorkersApiService {

    private static CompletableFuture formCompletableFuture(AuthorizedRequest authorizedRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                Object o = httpClient.execute(authorizedRequest.getRequestBase(), authorizedRequest.getResponseHandler());
                httpClient.close();
                return o;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private Map<String, String> getMapFromResponseBody(String body) throws IOException {
        TypeReference<HashMap<String, String>> typeReference = new TypeReference<HashMap<String, String>>() {};
        return JsonMapper.getInstance().readValue(body, typeReference);
    }

    public CompletableFuture postLogin(String email, String password) throws IOException {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        AuthorizedRequest<User> authorizedRequest = new AuthorizedRequestBuilder<User>()
                .post(RestUrls.LOGIN)
                .json().entity(new LoginRequest(email, password))
                .handler(httpResponse -> {
                    ResponseHandlers.handleBase(httpResponse);
                    String body = EntityUtils.toString(httpResponse.getEntity());
                    return JsonMapper.getInstance().readValue(body, User.class);
                })
                .build();

        return formCompletableFuture(authorizedRequest);
    }

    public CompletableFuture postGetReports() {
        AuthorizedRequest<ReportsPage> authorizedRequest = new AuthorizedRequestBuilder<ReportsPage>()
                .post(RestUrls.REPORTS_LIST)
                .json().authorized().handler(httpResponse -> {
                    ResponseHandlers.handleBase(httpResponse);
                    String body = EntityUtils.toString(httpResponse.getEntity());
                    return JsonMapper.getInstance().readValue(body, ReportsPage.class);
                }).build();
        return formCompletableFuture(authorizedRequest);
    }

    public CompletableFuture getProjectList() {
        AuthorizedRequest<List<Project>> authorizedRequest = new AuthorizedRequestBuilder<List<Project>>()
                .get(RestUrls.PROJECTS_LIST)
                .json().authorized().handler(httpResponse -> {
                    ResponseHandlers.handleBase(httpResponse);
                    String body = EntityUtils.toString(httpResponse.getEntity());

                    List<Project> projects = new ArrayList<>();
                    getMapFromResponseBody(body).forEach((id, name) -> projects.add(new Project(id, name)));
                    return projects;
                }).build();

        return formCompletableFuture(authorizedRequest);
    }

    public CompletableFuture getTasks(Integer projectId) throws IOException {
        AuthorizedRequest<List<Task>> authorizedRequest = new AuthorizedRequestBuilder<List<Task>>()
                .post(RestUrls.TASKS_LIST)
                .json().authorized().entity(new GetTasksRequest(projectId)).handler(httpResponse -> {
                    ResponseHandlers.handleBase(httpResponse);
                    String body = EntityUtils.toString(httpResponse.getEntity());

                    List<Task> taskList = new ArrayList<>();
                    getMapFromResponseBody(body).forEach((id, name) -> taskList.add(new Task(Integer.parseInt(id), name)));
                    return taskList;
                }).build();
        return formCompletableFuture(authorizedRequest);
    }

    public CompletableFuture postCreateWorkReport(Integer projectId, Integer taskId, Integer spentTime, String comment) throws IOException {
        AuthorizedRequest<Void> authorizedRequest = new AuthorizedRequestBuilder<Void>()
                .post(RestUrls.CREATE_REPORT).json().authorized()
                .entity(new WorkReport(projectId, taskId, spentTime, comment)).handler(httpResponse -> {
                    ResponseHandlers.handleBase(httpResponse);
                    return null;
                }).build();
        return formCompletableFuture(authorizedRequest);
    }

    public CompletableFuture postCreateDailyReport() {
        AuthorizedRequest<Void> authorizedRequest = new AuthorizedRequestBuilder<Void>()
                .post(RestUrls.CREATE_DAILY_REPORT).json().authorized().handler(httpResponse -> {
                    ResponseHandlers.handleBase(httpResponse);
                    return null;
                }).build();
        return formCompletableFuture(authorizedRequest);
    }
}
