package cn.cyf.chatclient.modules.session.service;



import cn.cyf.chatclient.modules.chat.model.ChatMsg;
import cn.cyf.chatclient.modules.session.model.SessionVo;

import java.util.List;

public interface SessionService {
    SessionVo create(ChatMsg chatMsg);

    List<SessionVo> list(Integer id);

    void update(SessionVo sessionVo);

    void delete(SessionVo sessionVo);
}
