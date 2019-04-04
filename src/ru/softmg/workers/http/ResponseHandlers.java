package ru.softmg.workers.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;

class ResponseHandlers {
    static void handleBase(HttpResponse httpResponse) throws IOException {
        int status = httpResponse.getStatusLine().getStatusCode();
        if(status < 200 || status >= 300)
            throw new ClientProtocolException("Unexpected response status: " + status);
    }
}
