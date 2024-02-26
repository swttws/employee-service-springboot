package com.su.mq.strategy;

/**
 * @author suweitao
 */
public interface EsUpdateConsumer {

    /**
     * es更新逻辑
     * @param message
     */
    void consumer(String message);

}
