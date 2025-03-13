package dev.mrflyn.redstorm_master;

import redis.clients.jedis.UnifiedJedis;

import java.util.Set;

public class HeartBeatListener extends Thread{


    private final UnifiedJedis jedis;
    private final int CHECK_INTERVAL;
    public HeartBeatListener(int checkInterval, UnifiedJedis jedis) {
        this.CHECK_INTERVAL = checkInterval;
        this.jedis = jedis;
    }

    @Override
    public void run() {
        while (true) {
            long currentTime = System.currentTimeMillis();
            Set<String> slaves = jedis.keys("slave:*");

//            System.out.println("Active Slaves:");
            for (String slaveKey : slaves) {
                long lastSeen = Long.parseLong(jedis.get(slaveKey));
                if (currentTime - lastSeen < 10000) {
//                    System.out.println(" - " + slaveKey.replace("slave:", ""));
                } else {
                    jedis.del(slaveKey);
                }
            }

            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException ignored) {}
        }
    }
}
