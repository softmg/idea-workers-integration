package ru.softmg.workers.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import ru.softmg.workers.model.RequestBase;
import ru.softmg.workers.model.User;
import ru.softmg.workers.repository.CurrentUserComponent;
import ru.softmg.workers.util.JsonMapper;

import java.nio.charset.Charset;
import java.util.Optional;

class AuthorizedRequestBuilder<T> {
    private AuthorizedRequest<T> authorizedRequest = null;

    private Optional<String> getApiToken() {
        CurrentUserComponent currentUserComponent = ApplicationManager.getApplication().getComponent(CurrentUserComponent.class);
        if(currentUserComponent == null)
            return Optional.empty();
        CurrentUserComponent.State userState = currentUserComponent.getState();
        if(userState == null)
            return Optional.empty();
        User user = userState.getCurrentUser();
        if(user == null)
            return Optional.empty();
        return Optional.of(user.getApiToken());
    }

    AuthorizedRequestBuilder<T> handler(ResponseHandler<T> responseHandler) {
        this.authorizedRequest.setResponseHandler(responseHandler);
        return this;
    }

    AuthorizedRequestBuilder<T> authorized() {
        getApiToken().ifPresent(token -> authorizedRequest.getRequestBase().setHeader("Authorization", "Bearer " + token));
        return this;
    }

    AuthorizedRequestBuilder<T> entity(RequestBase entity) throws JsonProcessingException {
        String raw = JsonMapper.getInstance().writeValueAsString(entity);
        StringEntity stringEntity = new StringEntity(raw, Charset.forName("UTF-8"));
        stringEntity.setContentType("application/json");
        ((HttpPost)authorizedRequest.getRequestBase()).setEntity(stringEntity);
        return this;
    }

    AuthorizedRequestBuilder<T> json() {
        authorizedRequest.getRequestBase().setHeader("ContentType", "application/json");
        authorizedRequest.getRequestBase().setHeader("Accept", "application/json");
        return this;
    }

    AuthorizedRequestBuilder<T> post(String url) {
        authorizedRequest = new AuthorizedRequest<>();
        authorizedRequest.setRequestBase(new HttpPost(url));
        return this;
    }

    AuthorizedRequestBuilder<T> get(String url) {
        authorizedRequest = new AuthorizedRequest<>();
        authorizedRequest.setRequestBase(new HttpGet(url));
        return this;
    }

    AuthorizedRequest<T> build() {
        return authorizedRequest;
    }
}
