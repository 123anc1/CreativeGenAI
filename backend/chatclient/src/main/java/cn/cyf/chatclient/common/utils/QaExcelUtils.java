package cn.cyf.chatclient.common.utils;

import cn.cyf.chatclient.common.pojo.QAPair;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

@Slf4j
public class QaExcelUtils {

    /**
     * 把 Excel 第一页读成 List<QAPair>
     * 要求表头必须是“问题”“答案”两列
     */
    public static List<QAPair> read(InputStream in) {
        return EasyExcel.read(in)
                .head(QAPair.class)
                .sheet()          // 默认第一页
                .doReadSync();    // 同步读，直接返回 List
    }
}
