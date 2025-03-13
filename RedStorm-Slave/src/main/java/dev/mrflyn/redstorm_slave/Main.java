package dev.mrflyn.redstorm_slave;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mrflyn.redstorm_slave.listeners.TaskListener;
import okhttp3.OkHttpClient;
import redis.clients.jedis.*;

import java.util.UUID;

public class Main {
    public static final String REDIS_HOST = "localhost"; // Change if Redis is remote
    public static final String TASK_CHANNEL = "http-tasks";

    public static void main(String[] args) {
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user("default")
                .password("AzkFfCcv7tgfmnrwo9jGG7NO1gbfjdJl")
                .build();


        try (UnifiedJedis jedis = new UnifiedJedis(
                new HostAndPort("redis-11699.c212.ap-south-1-1.ec2.redns.redis-cloud.com", 11699),
                config)) {
            System.out.println("Slave Node Started! Subscribing to tasks...");
            new HeartBeat(3000, UUID.randomUUID().toString(), jedis).start();
            jedis.subscribe(new TaskListener(), TASK_CHANNEL);

        }
    }
}