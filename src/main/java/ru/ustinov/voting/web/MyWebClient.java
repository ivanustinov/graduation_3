package ru.ustinov.voting.web;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 16.12.2023
 */
@Service
@Getter
public class MyWebClient {

    RestTemplate restTemplate = new RestTemplate();

    @Value("${sendMails.url}/sendMails")
    private String apiUrl;

    public void postRequest(int restaurant_id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Integer> requestEntity = new HttpEntity<>(restaurant_id, headers);
        final ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
        System.out.println("Response: " + stringResponseEntity.getBody() + " " + stringResponseEntity.getStatusCode());
    }
}
