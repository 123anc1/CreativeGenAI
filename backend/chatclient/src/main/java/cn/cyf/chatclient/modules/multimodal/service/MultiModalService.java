package cn.cyf.chatclient.modules.multimodal.service;

import cn.cyf.chatclient.modules.multimodal.model.MultiModalInput;
import reactor.core.publisher.Flux;

public interface MultiModalService {
    Flux<String> processInputStream(MultiModalInput input) throws Exception;
}