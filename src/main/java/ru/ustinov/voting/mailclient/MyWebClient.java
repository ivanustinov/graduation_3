package ru.ustinov.voting.mailclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ustinov.voting.web.json.JsonUtil;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 16.12.2023
 */
@Slf4j
@Service
public class MyWebClient {

    RestTemplate restTemplate = new RestTemplate();

    @Value("${sendMails.url}/sendMails")
    private String apiUrl;

    public void sendEmailsRequest(RequestPayLoad requestPayLoad) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RequestPayLoad> requestEntity = new HttpEntity<>(requestPayLoad, headers);
        final ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
        System.out.println("Response: " + stringResponseEntity.getBody() + " " + stringResponseEntity.getStatusCode());
    }
}
