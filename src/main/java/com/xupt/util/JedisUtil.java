package com.xupt.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * redis工具类
 *
 */
@Slf4j
@Component
public class JedisUtil {

    @Resource
    private JedisPool jedisPool = null;

    /**
     * 从jedis连接池中获取获取jedis对象
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 模糊查询key
     */
    public Set<String> keys(String pattern) {
        Jedis jedis = getJedis();
        Set<String> keys = null;
        try {
            keys = jedis.keys(pattern);
        } catch (Exception e) {
            log.error("keys erro e:", e);
        } finally {
            jedis.close();
        }
        return keys;
    }

    /**
     * 添加 set
     */
    public Long sadd(String key, String[] value) {
        Jedis jedis = getJedis();
        Long count = null;
        try {
            count = jedis.sadd(key, value);
        } catch (Exception e) {
            log.error("sadd erro e:", e);
        } finally {
            jedis.close();
        }
        return count;
    }

    /**
     * 存set
     *
     * @param key
     * @param value
     * @return
     */
    public void sadd(String key, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.sadd(key, value);
        } catch (Exception e) {
            log.error("sadd erro e:", e);
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取 set
     */
    public Set<String> smembers(String key) {
        Jedis jedis = getJedis();
        Set<String> values = null;
        try {
            values = jedis.smembers(key);
        } catch (Exception e) {
            log.error("smembers erro e:", e);
        } finally {
            jedis.close();
        }
        return values;
    }

    /**
     * 是否存在KEY
     */
    public boolean exists(String key) {
        Jedis jedis = getJedis();
        boolean exists = false;
        try {
            exists = jedis.exists(key);
        } catch (Exception e) {
            log.error("exists erro e:", e);
        } finally {
            jedis.close();
        }
        return exists;
    }

    /**
     * 设置过期时间
     *
     * @param time 单位秒,如果为负数，则表示永不过期
     */
    public void expire(String key, int time) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, time);
        } catch (Exception e) {
            log.error("expire erro e:", e);
        } finally {
            jedis.close();
        }
    }

    /**
     * **************************** redis Hash start***************************
     * Redis hash 是一个string类型的field和value的映射表，hash特别适合用于存储对象。
     * <p>
     * 仅当field不存在时设置值，成功返回true
     */
    public boolean hsetNX(String key, String field, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return false;
        }
        Jedis jedis = getJedis();
        try {
            Long statusCode = jedis.hsetnx(key, field, value);
            if (1 == statusCode) {
                return true;
            }
        } catch (Exception e) {
            log.error("hsetNX erro e:", e);
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在。
     */
    public boolean hexists(String key, String field) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return false;
        }
        Jedis jedis = getJedis();
        boolean result = false;
        try {
            result = jedis.hexists(key, field);
        } catch (Exception e) {
            log.error("hexists erro e:", e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment 。
     *
     * @param increment 正负数、0、正整数
     */
    public long hincrBy(String key, String field, long increment) {
        Jedis jedis = getJedis();
        long result = 0;
        try {
            result = jedis.hincrBy(key, field, increment);
        } catch (Exception e) {
            log.error("hincrBy erro e:", e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 获取在哈希表中指定 key 的所有字段和值
     */
    public Map<String, String> hgetAll(String key) {
        long startTime = System.currentTimeMillis();
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = getJedis();
        Map<String, String> map = null;
        try {
            map = jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("hgetAll erro e:", e);
        } finally {
            jedis.close();
        }
        log.info("jedis hGetAll cost : {} ms",System.currentTimeMillis() - startTime);
        return map;
    }

    /**
     * 获取在哈希表中指定 key1，key2 的所有字段和值
     */
    public Integer hgetCount(String key1, String key2) {
        Jedis jedis = getJedis();
        String count = null;
        try {
            count = jedis.hget(key1, key2);

        } catch (Exception e) {
            log.error("getCount erro e:", e);
        } finally {
            jedis.close();
        }

        if (count == null) {
            return 0;
        } else {
            return Integer.parseInt(count);
        }
    }

    /**
     * 存序列化后的java对象
     *
     * @param key
     * @param value
     * @return
     */
    public String set(byte[] key, byte[] value) {
        Jedis jedis = getJedis();
        String res = null;
        try {
            res = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set erro e:", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    /**
     * 取序列化后的java对象
     *
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        Jedis jedis = getJedis();
        byte[] res = null;
        try {
            res = jedis.get(key);
        } catch (Exception e) {
            log.error("get erro e:", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param time 单位秒,如果为负数，则表示永不过期
     * @return
     */
    public void expireKeyByte(byte[] key, int time) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, time);
        } catch (Exception e) {
            log.error("expireKeyByte erro e:", e);
        } finally {
            jedis.close();
        }
    }

    /**
     * 判断元素是否在集合中
     *
     * @param key
     * @param value
     * @return
     */
    public boolean sismember(String key, String value) {
        Jedis jedis = getJedis();
        boolean res = false;
        try {
            res = jedis.sismember(key, value);
        } catch (Exception e) {
            log.error("sismember erro e:", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = getJedis();
        String res = null;
        try {
            res = jedis.hmset(key, hash);
        } catch (Exception e) {
            log.error("hmset erro e:", e);
        } finally {
            jedis.close();
        }
        return res;
    }

    public String hget(String key, String filed) {
        Jedis jedis = getJedis();
        String res = null;
        try {
            res = jedis.hget(key, filed);
        } catch (Exception e) {
            log.error("hmset erro e:", e);
        } finally {
            jedis.close();
        }
        return res;
    }
}
