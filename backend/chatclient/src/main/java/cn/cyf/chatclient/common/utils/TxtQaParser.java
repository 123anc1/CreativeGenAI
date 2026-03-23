package cn.cyf.chatclient.common.utils;


import cn.cyf.chatclient.common.pojo.QAPair;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TxtQaParser {
    private static final Pattern QUESTION = Pattern.compile("^(问:|Q:|【问】)\\s*(.*)");
    private static final Pattern ANSWER   = Pattern.compile("^(答:|A:|【答】)\\s*(.*)");

    public static List<QAPair> parse(String text) {
        List<QAPair> list = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");
        log.info("获取到内容：{}",lines);
        StringBuilder qBuf = null, aBuf = null;
        for (String raw : lines) {
            String trim = raw.trim();
            Matcher qm = QUESTION.matcher(trim);
            Matcher am = ANSWER.matcher(trim);
            if (qm.find()) {          // 遇到新问题
                if (qBuf != null && aBuf != null) {
                    list.add(new QAPair(qBuf.toString().trim(), aBuf.toString().trim()));
                }
                qBuf = new StringBuilder(qm.group(2));
                aBuf = null;
            } else if (am.find()) {   // 遇到答
                if (aBuf == null) aBuf = new StringBuilder(am.group(2));
            } else {                  // 续行
                if (aBuf != null) aBuf.append("\n").append(trim);
                else if (qBuf != null) qBuf.append("\n").append(trim);
            }
        }
        // 最后一条
        if (qBuf != null && aBuf != null) {
            list.add(new QAPair(qBuf.toString().trim(), aBuf.toString().trim()));
        }
        log.info("txt内容：{}",list);
        return list;
    }
}
