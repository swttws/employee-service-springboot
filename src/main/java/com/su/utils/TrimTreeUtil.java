package com.su.utils;

import org.apache.commons.lang3.CharUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author suweitao
 * 字典树算法，铭感词过滤
 */
public class TrimTreeUtil {

    public static Trim trim = new Trim();

    // 字典树的添加词
    public static void add(String word) {
        Trim templateTrim = trim;
        for (int i = 0; i < word.length(); i++) {
            char w = word.charAt(i);
            //为标点符号
            if (isSymbol(w)) {
                continue;
            }
            //获取子节点
            Trim subTrim = templateTrim.getSubTrim(w);
            if (Objects.isNull(subTrim)) {
                subTrim = new Trim();
                templateTrim.addTrim(w, subTrim);
            }
            templateTrim = subTrim;
            if (i == word.length() - 1) {
                templateTrim.setEnd();
            }
        }
    }

    // 判断是否为符号
    public static boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    //字典树
    public static class Trim {
        //标记是否为最后一个字符
        private boolean isEnd = false;

        //子节点,(k:子字符，v:子节点)
        private Map<Character, Trim> map = new HashMap<>();

        //子节点添加
        public void addTrim(Character word, Trim subTrim) {
            map.put(word, subTrim);
        }

        //获取子节点
        public Trim getSubTrim(Character word) {
            return map.get(word);
        }

        //设置结束标志
        public void setEnd() {
            this.isEnd = true;
        }

        //返回结束标志
        public boolean getIsEnd() {
            return this.isEnd;
        }
    }
}
