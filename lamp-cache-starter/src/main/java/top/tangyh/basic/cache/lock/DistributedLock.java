package top.tangyh.basic.cache.lock;

/**
 * 分布式锁顶级接口
 * 例如：
 * RETRY_TIMES=100，SLEEP_MILLIS=100
 * RETRY_TIMES * SLEEP_MILLIS = 10000 意味着如果一直获取不了锁，最长会等待10秒后抛超时异常
 *
 * @author zuihou
 * @date 2019-08-06 10:43
 */
public interface DistributedLock {

    /**
     * 默认超时时间
     * 单位：毫秒
     */
    long TIMEOUT_MILLIS = 5000;

    /**
     * 重试次数
     */
    int RETRY_TIMES = 100;

    /**
     * 每次重试后等待的时间
     * 单位：毫秒
     */
    long SLEEP_MILLIS = 100;

    /**
     * 获取锁
     *
     * @param key key
     * @return 成功/失败
     */
    default boolean lock(String key) {
        return lock(key, TIMEOUT_MILLIS, RETRY_TIMES, SLEEP_MILLIS);
    }

    /**
     * 获取锁
     *
     * @param key        key
     * @param retryTimes 重试次数
     * @return 成功/失败
     */
    default boolean lock(String key, int retryTimes) {
        return lock(key, TIMEOUT_MILLIS, retryTimes, SLEEP_MILLIS);
    }

    /**
     * 获取锁
     *
     * @param key         key
     * @param retryTimes  重试次数
     * @param sleepMillis 获取锁失败的重试间隔 单位：毫秒
     * @return 成功/失败
     */
    default boolean lock(String key, int retryTimes, long sleepMillis) {
        return lock(key, TIMEOUT_MILLIS, retryTimes, sleepMillis);
    }

    /**
     * 获取锁
     *
     * @param key    key
     * @param expire 获取锁超时时间
     * @return 成功/失败
     */
    default boolean lock(String key, long expire) {
        return lock(key, expire, RETRY_TIMES, SLEEP_MILLIS);
    }

    /**
     * 获取锁
     *
     * @param key        key
     * @param expire     获取锁超时时间
     * @param retryTimes 重试次数
     * @return 成功/失败
     */
    default boolean lock(String key, long expire, int retryTimes) {
        return lock(key, expire, retryTimes, SLEEP_MILLIS);
    }

    /**
     * 获取锁
     *
     * @param key         key
     * @param expire      获取锁超时时间
     * @param retryTimes  重试次数
     * @param sleepMillis 获取锁失败的重试间隔
     * @return 成功/失败
     */
    boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    /**
     * 释放锁
     *
     * @param key key值
     * @return 释放结果
     */
    boolean releaseLock(String key);
}
