package com.qiuzhitech.onlineshopping.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;


@Slf4j
@Service
public class RedisService {
    @Resource
    private JedisPool jedisPool;

    public RedisService setValue(String key, Long value) {
        Jedis client = jedisPool.getResource();  // 通过线程池，拿到一个线程，将此resource用于后续的增删改查操作
        client.set(key, value.toString());
        client.close();  // 用完释放线程池
        return this;
    }

    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
        jedisClient.close();
    }

    public String getValue(String key) {
        Jedis client = jedisPool.getResource();
        String value = client.get(key);
        client.close();
        return value;
    }

    public long stockDeduct(String key) {
        try (Jedis client = jedisPool.getResource()) { // 获取一个 Jedis 实例 client，该实例来自 Redis 连接池 jedisPool。
            // LUA 脚本
            String script =
                    // KEYS[1] 表示KEYS数组中的第一个元素，即传递给Lua脚本的第一个键
                    "if redis.call('exists', KEYS[1]) == 1 then\n" +  // 首先判断 Redis 中是否存在指定的键 KEYS[1]，KEYS 是 Lua 脚本中用于存储键的数组。如果存在，执行接下来的逻辑。判断commodity是否存在（key=1存在，key=-1不存在）
                    "   local stock = tonumber(redis.call('get',KEYS[1]))\n" +  // 将键 KEYS[1] 对应的值获取并转换为 Lua 中的数字类型 tonumber，并存储在本地变量 stock 中。
                    "   if (stock<=0) then\n" +  // 如果 stock（库存）小于或等于 0，则返回 -1，表示库存不足。
                    "       return -1\n" +
                    "   end\n" +
                    "\n" +
                    "   redis.call('decr', KEYS[1]);\n" +  //使用 Redis 的 decr 命令将键 KEYS[1] 对应的值减 1，即更新库存。
                    "   return stock - 1;\n" +  // 返回更新后的库存值 stock - 1。
                    "end\n" +
                    "\n" +
                    "return -1;";
            // 执行LUA脚本 - 将Lua脚本、键 KEYS[1]（以集合形式传递，因为 Lua 脚本中用到了 KEYS 数组）、参数列表传递给 Redis 服务器执行
            Long stock = (Long) client.eval(script, Collections.singletonList(key), Collections.emptyList());
            if (stock < 0) {
                System.out.println("There is no stock available");
                return -1;
            } else {
                System.out.println("Validate and decreased redis stock, current available stock： " + stock);
                return stock;
            }
        } catch (Throwable throwable) {
            System.out.println("Exception on stockDeductValidation： " + throwable.toString());
            return -1;
        }
    }

    public boolean tryDistributedLock(String lockKey, String requestID, int expireTime) {
        Jedis resource = jedisPool.getResource();
        String result = resource.set(lockKey, requestID, "NX", "PX", expireTime); // NX 代表key不存在时才执行set操作, PX 有过期时间
        resource.close();
        if ("OK".equals(result)){
            return true;
        }
        return false;
    }

    public boolean releaseDistributedLock(String lockKey, String requestID){
        Jedis resource = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1])" +
                " else return 0 end";
        Long result = (Long)resource.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestID));
        resource.close();
        if (result == 1L){
            return true;
        }
        return false;
    }


    public void revertStock(String redisKey) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.incr(redisKey);
        jedisClient.close();
    }
}