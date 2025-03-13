package dev.mrflyn.redstorm_slave;


import redis.clients.jedis.UnifiedJedis;

public class HeartBeat extends Thread {

    private final int HEARTBEAT_INTERVAL;
    private final String SLAVE_ID;

    private final UnifiedJedis jedis;

    public HeartBeat(int interval, String id, UnifiedJedis jedis) {
        this.HEARTBEAT_INTERVAL = interval;
        this.SLAVE_ID = id;
        this.jedis = jedis;
    }

    @Override
    public void run() {
        System.out.println("Heartbeat thread started!");
        while (true) {
            long timestamp = System.currentTimeMillis();
            jedis.setex("slave:" + SLAVE_ID, 10, String.valueOf(timestamp)); // Auto-expire after 10s
            System.out.println("Sent heartbeat: " + SLAVE_ID);
            try {
                Thread.sleep(HEARTBEAT_INTERVAL);
            } catch (InterruptedException ignored) {}
        }
    }
}
