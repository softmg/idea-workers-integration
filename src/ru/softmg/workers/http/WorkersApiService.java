package ru.softmg.workers.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.softmg.workers.model.*;
import ru.softmg.workers.repository.CurrentUserComponent;
import ru.softmg.workers.util.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkersApiService implements AutoCloseable, ApplicationContextAware {
    private CloseableHttpClient closeableHttpClient;
    private ApplicationContext applicationContext;

    public User postLogin(String email, String password) throws IOException {
        this.closeableHttpClient = HttpClients.createDefault();

        LoginRequest loginRequest = new LoginRequest(email, password);
        String loginRequestRaw = JsonMapper.getInstance().writeValueAsString(loginRequest);

        HttpPost httpPost = new HttpPost("http://workers.softmg.ru/api/login");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(loginRequestRaw));

        ResponseHandler<String> responseHandler = httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status >= 200 && status < 300) {
                HttpEntity responseEntity = httpResponse.getEntity();
                return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            } else if(status == 422) {
                throw  new ClientProtocolException("Username or password is invalid");
            }
            else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
        String responseBody = closeableHttpClient.execute(httpPost, responseHandler);
        return JsonMapper.getInstance().readValue(responseBody, User.class);
    }

    public ReportsPage postGetReports() throws IOException {
        this.closeableHttpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://workers.softmg.ru/frontend/profile/getWorkReportList");
        httpPost.setHeader("Content-Type", "application/json");
        CurrentUserComponent currentUserComponent = ApplicationManager.getApplication().getComponent(CurrentUserComponent.class);
        String apiToken = currentUserComponent.getState().getCurrentUser().getApiToken();

        httpPost.setHeader("Authorization", "Bearer " + apiToken);
        ResponseHandler<String> responseHandler = httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status >= 200 && status < 300) {
                HttpEntity responseEntity = httpResponse.getEntity();
                return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };

        String responseBody = closeableHttpClient.execute(httpPost, responseHandler);
        return JsonMapper.getInstance().readValue(responseBody, ReportsPage.class);
    }

    public List<Project> getProjectList() throws IOException {
        this.closeableHttpClient = HttpClients.createDefault();

        CurrentUserComponent currentUserComponent = ApplicationManager.getApplication().getComponent(CurrentUserComponent.class);
        String apiToken = currentUserComponent.getState().getCurrentUser().getApiToken();

        HttpGet httpGet = new HttpGet("http://workers.softmg.ru/frontend/profile/getProjectsList");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Authorization", "Bearer " + apiToken);

        ResponseHandler<String> responseHandler = httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status >= 200 && status < 300) {
                HttpEntity responseEntity = httpResponse.getEntity();
                return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };

        String responseBody = closeableHttpClient.execute(httpGet, responseHandler);
        TypeReference<HashMap<String, String>> typeReference = new TypeReference<HashMap<String, String>>() {};
        Map<String, String> map = JsonMapper.getInstance().readValue(responseBody, typeReference);
        List<Project> projects = new ArrayList<>();
        map.forEach((id, name) -> projects.add(new Project(id, name)));
        return projects;
    }

    public List<Task> getTasks(Integer projectId) throws IOException {
        this.closeableHttpClient = HttpClients.createDefault();

        CurrentUserComponent currentUserComponent = ApplicationManager.getApplication().getComponent(CurrentUserComponent.class);
        String apiToken = currentUserComponent.getState().getCurrentUser().getApiToken();
        String userName = currentUserComponent.getState().getCurrentUser().getName();

        HttpPost httpPost = new HttpPost("http://workers.softmg.ru/frontend/profile/getProjectTasks");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + apiToken);

        String rawRequest = JsonMapper.getInstance().writeValueAsString(new GetTasksRequest(projectId));
        httpPost.setEntity(new StringEntity(rawRequest));

        ResponseHandler<String> responseHandler = httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status >= 200 && status < 300) {
                HttpEntity responseEntity = httpResponse.getEntity();
                return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };

        String responseBody = closeableHttpClient.execute(httpPost, responseHandler);
        TypeReference<HashMap<String, String>> typeReference = new TypeReference<HashMap<String, String>>() {};
        Map<String, String> map = JsonMapper.getInstance().readValue(responseBody, typeReference);
        List<Task> taskList = new ArrayList<>();

        map.forEach((id, name) -> taskList.add(new Task(Integer.parseInt(id), name)));
        return taskList;
    }

    public void postCreateWorkReport(Integer projectId, Integer taskId, Integer spentTime, String comment) throws IOException {
        this.closeableHttpClient = HttpClients.createDefault();
        WorkReport workReport = new WorkReport(projectId, taskId, spentTime, comment);
        String rawReport = JsonMapper.getInstance().writeValueAsString(workReport);

        HttpPost httpPost = new HttpPost("http://workers.softmg.ru/frontend/profile/createWorkReport");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(rawReport));

        CurrentUserComponent currentUserComponent = ApplicationManager.getApplication().getComponent(CurrentUserComponent.class);
        String apiToken = currentUserComponent.getState().getCurrentUser().getApiToken();

        httpPost.setHeader("Authorization", "Bearer " + apiToken);
        ResponseHandler<String> responseHandler = httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status >= 200 && status < 300) {
                HttpEntity responseEntity = httpResponse.getEntity();
                return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };

        closeableHttpClient.execute(httpPost, responseHandler);
    }

    public void postCreateDailyReport() throws IOException {
        this.closeableHttpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://workers.softmg.ru/frontend/profile/createDailyWorkReport");
        httpPost.setHeader("Content-Type", "application/json");

        CurrentUserComponent currentUserComponent = ApplicationManager.getApplication().getComponent(CurrentUserComponent.class);
        String apiToken = currentUserComponent.getState().getCurrentUser().getApiToken();

        httpPost.setHeader("Authorization", "Bearer " + apiToken);
        ResponseHandler<Void> responseHandler = httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status < 200 || status >= 300) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            return null;
        };

        closeableHttpClient.execute(httpPost, responseHandler);
    }

    @Override
    public void close() throws Exception {
        closeableHttpClient.close();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
