package com.su.runner;

import com.su.domain.es.annoation.EsIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author suweitao
 * 初始化es索引
 */
@Component
@Slf4j
public class EsRunner implements ApplicationRunner, ApplicationContextAware {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private ApplicationContext applicationContext;

    /**
     * 初始化es索引
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //获取EsIndex注解的实体类
        Map<String, Object> esBeanMap = applicationContext.getBeansWithAnnotation(EsIndex.class);
        List<? extends Class<?>> classList = esBeanMap.values().stream()
                .map(Object::getClass)
                .collect(Collectors.toList());
        classList.forEach(clazz -> {
            //有加载过索引，不加载
            if(elasticsearchOperations.indexOps(clazz).exists()){
                return;
            }
            //初始化索引
            elasticsearchOperations.indexOps(clazz).create();
            elasticsearchOperations.indexOps(clazz).putMapping();
            elasticsearchOperations.indexOps(clazz).refresh();
            log.info("-----ES初始化索引类型为{}------",clazz);
        });
    }

    /**
     * 获取上下文
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
