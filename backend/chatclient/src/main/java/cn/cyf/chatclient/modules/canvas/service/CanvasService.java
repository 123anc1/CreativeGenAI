package cn.cyf.chatclient.modules.canvas.service;

import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.canvas.model.CanvasImage;
import cn.cyf.chatclient.modules.canvas.model.CanvasSessionImage;
import cn.cyf.chatclient.modules.canvas.model.SessionInfo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface CanvasService {
     Result inpainting(CanvasImage canvasImage);

     Result asyncInpainting(CanvasImage canvasImage) throws IOException;

     SessionInfo sessionCreate(SessionInfo sessionInfo) throws Exception;

     List<String> saveImage(SessionInfo sessionInfo) throws Exception;

     List<SessionInfo> sessionList(String userId);

     List<CanvasSessionImage> sessionListImage(String sessionId);

     void sessionUpdate(String sessionId, String title);

     void sessionDelete(String id) throws Exception;

     void sessionImageDelete(String id) throws Exception;
}
