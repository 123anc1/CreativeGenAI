package cn.cyf.chatclient.common.utils;


import cn.cyf.chatclient.common.pojo.Unit;
import cn.cyf.chatclient.common.pojo.WeatherRequest;
import cn.cyf.chatclient.common.pojo.WeatherResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class WeatherServiceTool implements Function<WeatherRequest, WeatherResponse> {
    public WeatherResponse apply(WeatherRequest request) {
        return new WeatherResponse(10.0, Unit.C);
    }
}
