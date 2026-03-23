package cn.cyf.chatclient.modules.session.service.impl;

import cn.cyf.chatclient.modules.chat.mapper.ChatMapper;
import cn.cyf.chatclient.modules.multimodal.mapper.MultiModalMapper;
import cn.cyf.chatclient.modules.multimodal.mapper.MultiModalResultMapper;
import cn.cyf.chatclient.modules.session.mapper.SessionMapper;
import cn.cyf.chatclient.modules.chat.model.ChatMsg;
import cn.cyf.chatclient.modules.session.model.SessionVo;
import cn.cyf.chatclient.modules.session.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private MultiModalResultMapper multiModalResultMapper;

    @Override
    public SessionVo create(ChatMsg chatMsg) {
        SessionVo sessionVo = new SessionVo();
        sessionVo.setUserid(chatMsg.getUid());
        sessionVo.setSessionid(chatMsg.getSessionid());
        sessionVo.setTitle(chatMsg.getTitle());
        if (sessionVo.getSessionid() == null){
            String sessionid = UUID.randomUUID().toString().replace("-", "");
            sessionVo.setSessionid(sessionid);
            return sessionVo;
        }
        if(sessionMapper.active(sessionVo) == null && sessionMapper.chack(sessionVo) == null) {
            sessionVo.setCreatedat(LocalDateTime.now());
            sessionVo.setUpdatedat(LocalDateTime.now());
            sessionMapper.create(sessionVo);
            return sessionVo;
        }
        return sessionMapper.active(sessionVo);
    }

    @Override
    public List<SessionVo> list(Integer id) {
        return sessionMapper.list(id);
    }

    @Override
    public void update(SessionVo sessionVo) {
        sessionMapper.update(sessionVo);
    }


    @Override
    public void delete(SessionVo sessionVo) {
        String coversationId = sessionVo.getUserid() + ":" + sessionVo.getSessionid();
//        log.info("删除会话ID:" + sessionVo.getSessionid());
        chatMapper.delete(coversationId);
        multiModalResultMapper.deleteBySessionid(sessionVo.getSessionid());
        sessionMapper.delete(sessionVo);
    }


}
