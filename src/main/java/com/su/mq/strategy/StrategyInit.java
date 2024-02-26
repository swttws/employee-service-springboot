package com.su.mq.strategy;

import com.su.mq.strategy.annotation.EsUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author suweitao
 */
@Component
public class StrategyInit {

    public static Map<String, EsUpdateConsumer> strategyMap = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 初始化策略类
     *
     */
    @PostConstruct
    public void initialize() {
        //获取注解为EsUpdate类
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(EsUpdate.class);

        beansWithAnnotation.values().stream()
                .filter(bean -> bean instanceof EsUpdateConsumer)
                .forEach(bean -> {
                    EsUpdate annotation = bean.getClass().getAnnotation(EsUpdate.class);
                    strategyMap.put(annotation.value(), (EsUpdateConsumer) bean);
                });
    }

}
