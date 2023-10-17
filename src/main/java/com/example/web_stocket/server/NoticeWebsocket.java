package com.example.web_stocket.server;

import com.alibaba.fastjson.JSONObject;
import com.example.web_stocket.data.NoticeWebsocketResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/chepaisend")
@Component
@Slf4j
public class NoticeWebsocket {

    //记录连接的客户端
    public static Map<String, Session> clients = new ConcurrentHashMap<>();

    /**
     * userId关联sid（解决同一用户id，在多个web端连接的问题）
     */
    public static Map<String, Set<String>> conns = new ConcurrentHashMap<>();

    private String sid = null;




    //一些记录发送消息状态
    private static int initFlag =0;

    private static int tempFlag =0;
    //区分新旧消息的变量
    private static int sum=0;
    /**
     * 连接成功后调用的方法
     * @param session
     *
     */
    @OnOpen
    public void onOpen(Session session) {
        this.sid = UUID.randomUUID().toString();

        clients.put(this.sid, session);

        log.info(this.sid + "连接开启！");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.info(this.sid + "连接断开！");
        clients.remove(this.sid);
    }

    /**
     * 判断是否连接的方法
     * @return
     */
    public static boolean isServerClose() {
        if (NoticeWebsocket.clients.values().size() == 0) {
            log.info("已断开");
            return true;
        }else {
            log.info("已连接");
            return false;
        }
    }

    /**
     * 发送给所有用户
     * @param noticeType
     */

    public static boolean sendMessage(String noticeType,int count){
        //判断是否是新的新增客户
        System.out.println("count= "+count+",sum= "+sum+",initFlag= "+initFlag+",tempFlag= "+tempFlag);
        if (sum != count){
            NoticeWebsocketResp noticeWebsocketResp = new NoticeWebsocketResp();
            noticeWebsocketResp.setNoticeType(noticeType);
            sendMessage(noticeWebsocketResp);
            sum = count;
        }
        //判断前端是否 回复了 收到消息  相等没收到，不相等 收到
        if (initFlag==tempFlag){
            NoticeWebsocketResp noticeWebsocketResp = new NoticeWebsocketResp();
            noticeWebsocketResp.setNoticeType(noticeType);
            sendMessage(noticeWebsocketResp);
        }else {
            //收到消息了，别发同一个消息了
            tempFlag = initFlag;
            return true;
        }
        tempFlag = initFlag;
        //没收到消息继续发
        return false;
    }


    /**
     * 发送给所有用户
     * @param noticeWebsocketResp
     */
    public static void sendMessage(NoticeWebsocketResp noticeWebsocketResp){
        String message = JSONObject.toJSONString(noticeWebsocketResp);
        for (Session session1 : NoticeWebsocket.clients.values()) {
            try {
                session1.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据用户id发送给某一个用户
     * **/
    public static void sendMessageByUserId(String userId, NoticeWebsocketResp noticeWebsocketResp) {
        if (!StringUtils.isEmpty(userId)) {
            String message = JSONObject.toJSONString(noticeWebsocketResp);
            Set<String> clientSet = conns.get(userId);
            if (clientSet != null) {
                for (String sid : clientSet) {
                    Session session = clients.get(sid);
                    if (session != null) {
                        try {
                            session.getBasicRemote().sendText(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     */

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口的信息:"+message);
        if ("已接收到消息".equals(message)){
            //收到消息，改变flag的值
            System.out.println("前端已经收到消息，开始改变 initFlag的值，此时initFlag= "+initFlag);
            initFlag = initFlag +1;
            System.out.println("前端已经收到消息，已经改变 initFlag的值，此时initFlag== "+initFlag);
        }

    }

    /**
     * 发生错误时的回调函数
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        log.info("错误");
        error.printStackTrace();
    }

}
