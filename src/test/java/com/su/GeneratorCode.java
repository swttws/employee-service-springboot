package com.su;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.util.Collections;

/**
 * @author suweitao
 */
public class GeneratorCode {

    /**
     * 代码生产器
     */
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/obtain-employee?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8",
                "root","217812.com")
                .globalConfig(builder -> {
                    builder.author("swt 2023-12-18") // 设置作者
                            .fileOverride()
                            .outputDir("D:\\java-bishe\\employee-service-springboot\\src\\main\\java")
                            .enableSwagger(); // 开启 swagger 模式
                })
                .packageConfig(builder -> {
                    builder.parent("com.su") // 设置父包名
                            .entity("pojo")
                            .service("service")
                            .controller("controller")
                            .mapper("mapper")
                            .serviceImpl("service.impl")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D:\\java-mianshi\\Audition\\java-PlateForm-app\\src\\main\\resources\\mapper")); // 设置mapperXml生成路;
                })
                .strategyConfig(builder -> {
                    builder.addInclude("account") // 设置需要生成的表名
                            .entityBuilder()
                            .enableTableFieldAnnotation()
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .idType(IdType.AUTO)
                            .controllerBuilder()
                            .enableRestStyle()
                            .formatFileName("%sController")
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImp")
                            .mapperBuilder()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")
                            .enableMapperAnnotation();
                })
                .execute();
    }

}
