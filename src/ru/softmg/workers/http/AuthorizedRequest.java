package ru.softmg.workers.http;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;

@Getter
@Setter
@NoArgsConstructor
class AuthorizedRequest<T> {
    private HttpRequestBase requestBase;
    private ResponseHandler<T> responseHandler;
}
