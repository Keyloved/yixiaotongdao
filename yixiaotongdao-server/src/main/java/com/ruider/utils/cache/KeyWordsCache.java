package com.ruider.utils.cache;

import java.util.HashMap;

public class KeyWordsCache {

    public static HashMap<String,Integer> map = new HashMap<>();
    static {
        System.out.println("KeyWordsCache载入信息");
        put();
    }
    public static void put() {
        //一级类型
        map.put("日常",1);map.put("娱乐",2);map.put("驾校",3);map.put("旅游",4);
        map.put("拼车",5); map.put("学习",6);map.put("运动",7);map.put("体育",7);
        map.put("其他",75);map.put("其它",75);

        //二级类型 日常
        map.put("租房子",11);map.put("合租",11);map.put("房子",11);map.put("社团",12);map.put("聊天",13);
        map.put("外卖拼单",14);map.put("外卖",14);map.put("拼单",14);map.put("购物",15);map.put("买东西",15);

        //二级类型 娱乐
        map.put("K歌",21);map.put("唱歌",21);map.put("听音乐",22);map.put("音乐",22);map.put("电影",23);map.put("看电影",23);
        map.put("刷剧",24);map.put("演唱会",25);map.put("游戏",26);map.put("打游戏",26);

        //二级类型 驾校
        map.put("驾校",31);map.put("练车",31);

        //二级类型 旅游
        map.put("旅游",41);

        //二级类型 拼车
        map.put("拼车",51);

        //二级类型 学习
        map.put("讲座",61);map.put("演讲",61);map.put("自习",62);map.put("竞赛",63);map.put("选修课",64);map.put("选修",64);
        map.put("考研",65);map.put("研究生",65);
        //二级类型 运动
        map.put("比赛",71);map.put("跑步",72);
        map.put("约球",73);map.put("篮球",73);map.put("足球",73);map.put("排球",73);map.put("乒乓球",73);map.put("羽毛球",73);
        map.put("健身",74);map.put("健身房",74);

    }

    public static Integer get(String keyWords) {
        return map.get(keyWords);
    }

    public static boolean contains(String keyWords) {
        return map.containsKey(keyWords);
    }
}
