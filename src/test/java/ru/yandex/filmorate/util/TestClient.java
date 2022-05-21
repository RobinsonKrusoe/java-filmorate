package ru.yandex.filmorate.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//Вспомогательный класс для отправки тестовых HTTP запросов к тестироемому API
public class TestClient {
    private int lastResponseStatusCode;

    public int getLastResponseStatusCode() {
        return lastResponseStatusCode;
    }

    //Метод для отправки GET запроса
    public String get(String url){
        lastResponseStatusCode = -1;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            lastResponseStatusCode = response.statusCode();
            return response.body();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
        }

        return "";
    }

    //Метод для отправки POST запроса
    public void post(String url, String body){
        lastResponseStatusCode = -1;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            lastResponseStatusCode = response.statusCode();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
        }
    }

    //Метод для отправки POST запроса
    public void put(String url, String body){
        lastResponseStatusCode = -1;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            lastResponseStatusCode = response.statusCode();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
        }
    }
}
