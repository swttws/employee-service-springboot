package com.su.runner;

import com.su.utils.TrimTreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author suweitao
 */
@Component
@Slf4j
public class TrimWordAddRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InputStream resourceAsStream=null;
        BufferedReader bufferedReader=null;
        //获取文件流
        try {
            resourceAsStream =
                    this.getClass().getClassLoader().getResourceAsStream("sensitiveword.txt");
            bufferedReader=new BufferedReader(new InputStreamReader(resourceAsStream));
            String keyword;
            while ((keyword=bufferedReader.readLine())!=null){
                TrimTreeUtil.add(keyword);//添加字典树
            }
            log.info("加载敏感词");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                resourceAsStream.close();
                bufferedReader.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
