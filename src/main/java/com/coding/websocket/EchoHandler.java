package com.coding.websocket;


import com.coding.utils.JsonUtil;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Component
@RequiredArgsConstructor
@ServerEndpoint("/ws/event")
public class EchoHandler {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static final CopyOnWriteArraySet<EchoHandler> webSocketSet = new CopyOnWriteArraySet<EchoHandler>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String activityId = "";
    private Long projectId;

    public static List<Long> getAllClient() {
        Set<Long> idList = webSocketSet.stream().map(EchoHandler::getProjectId).collect(Collectors.toSet());
        return Lists.newArrayList(idList.iterator());
    }




    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();
        List<String> activityId = requestParameterMap.getOrDefault("activityId", Lists.newArrayList());
        List<String> projectId = requestParameterMap.getOrDefault("projectId", Lists.newArrayList());
        if (CollectionUtils.isEmpty(activityId)) {
//            session.close();
//            return;
        }
        webSocketSet.add(this);     //加入set中
        this.activityId = activityId.get(0);
        if (!CollectionUtils.isEmpty(projectId)) {
            this.projectId = NumberUtils.toLong(projectId.get(0));
        }
        addOnlineCount();           //在线数加1
        log.warn("有新窗口开始监听:" + activityId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                log.info("线程还在开着，给他关掉");
            }
        }
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        //断开连接情况下，更新主板占用情况为释放
        log.info("释放的sid为：" + activityId);
        //这里写你 释放的时候，要处理的业务
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }


    /**
     * @ Param session
     * @ Param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.info("ws发生错误");
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message, @PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口" + sid + "，推送内容:" + message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        EchoHandler.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        EchoHandler.onlineCount--;
    }

    public static CopyOnWriteArraySet<EchoHandler> getWebSocketSet() {
        return webSocketSet;
    }

    public static void sendToAll(String json) {
        webSocketSet.forEach(echoHandler -> {
            try {
                echoHandler.sendMessage(json);
            } catch (Exception e) {
                log.warn("推送失败", e);
            }
        });
    }

    public static void sendToActivity(String json) {
        webSocketSet.forEach(echoHandler -> {
            if (echoHandler.getProjectId() != null) {
                return;
            }
            try {
                echoHandler.sendMessage(json);
            } catch (Exception e) {
                log.warn("推送失败", e);
            }
        });
    }

    public static void sendToProject(String qiang, Long projectId) {
        webSocketSet.forEach(echoHandler -> {
            if (Objects.equals(echoHandler.getProjectId(), projectId)) {
                try {
                    echoHandler.sendMessage(qiang);
                } catch (Exception e) {
                    log.warn("推送失败", e);
                }
            }

        });
    }
}
