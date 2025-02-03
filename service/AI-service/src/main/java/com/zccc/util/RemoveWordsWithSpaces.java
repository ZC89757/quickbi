package com.zccc.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveWordsWithSpaces {

    public static List<String> unsafe_l = new ArrayList<>();
    public static List<String> unsafe_r = new ArrayList<>();


    public static String replicer(String s){
        s=s.replace("】","【");
        s=s.replace("```javascript","");
        s=s.replace("```json","");
        s=s.replace("```","");
        s=s.replace("'","\"");
        s=s.replace("option = ","");
        repliceLeft(5,0,new StringBuilder());
        repliceRight(5,0,new StringBuilder());
        for(String s1:unsafe_l){
            if(s.contains(s1)){
                s=s.replace(s1,"【【【【【");
                break;
            }
        }
        for(String s2:unsafe_r){
            if(s.contains(s2)){
                s=s.replace(s2,"【【【【【");
                break;
            }
        }
        return s;
    }

    public static void repliceLeft(int num,int i,StringBuilder sb) {
        if(i==num){
            if(!sb.equals("【【【【【"))
                unsafe_l.add(sb.toString());
            return;
        }
        repliceLeft(num,i+1,sb.append("["));
        sb.deleteCharAt(sb.length()-1);
        repliceLeft(num,i+1,sb.append("【"));
        sb.deleteCharAt(sb.length()-1);
    }
    public static void repliceRight(int num,int i,StringBuilder sb) {
        if(i==num){
            if(!sb.equals("】】】】】"))
                unsafe_r.add(sb.toString());
            return;
        }
        repliceRight(num,i+1,sb.append("]"));
        sb.deleteCharAt(sb.length()-1);
        repliceRight(num,i+1,sb.append("】"));
        sb.deleteCharAt(sb.length()-1);
    }
    public static void main(String[] args) {
        String jsonString = "{\n" +
                "\"ti  tle\": {\n" +
                "\"tex  t\": \"用户数统计\"\n" +
                "},\n" +
                "\"  too  ltip\": {},\n" +
                "\"legend\": {\n" +
                "\"data\":[\"用户数\"]\n" +
                "},\n" +
                "\" xAxis\": {\n" +
                "\"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\", \"6号\", \"7号\"]\n" +
                "},\n" +
                "\" yAx  is\": {},\n" +
                "\"series\": [{\n" +
                "\"name\": \"用户数\",\n" +
                "\"type\": \"bar\",\n" +
                "\"da ta\": [10, 20, 30, 90, 0, 10, 20]\n" +
                "}]\n" +
                "}";

        // 调用函数移除包含空格的单词
//        String result = removeWordsWithSpaces(jsonString);

        // 打印结果
//        System.out.println(result);
    }

//    public static void main(String[] args) {
//        System.out.println(replicer("【【【【[ds]】】】】"));
//    }

    public static String removeWordsWithSpaces(String jsonString) {
        // 正则表达式匹配带空格的单词
        Pattern pattern = Pattern.compile("\\s+([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(jsonString);
        return matcher.replaceAll("$1");
//        return jsonString.replaceAll("(\\w+)\\s+(\\w+)", "$1$2");
    }
}
