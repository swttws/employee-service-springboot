package com.su.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.su.netty.protocol.MyMessage;
import com.su.netty.strategy.HandlerMessage;
import com.su.service.ChatService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

//消息处理器
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private RedisTemplate<String, Object> redisTemplate;

    private ChatService chatService;

    private HandlerMessage handlerMessage;

    public WebSocketServerHandler(RedisTemplate<String, Object> redisTemplate, ChatService chatService
            , HandlerMessage handlerMessage) {
        this.redisTemplate = redisTemplate;
        this.chatService = chatService;
        this.handlerMessage = handlerMessage;
    }

    //处理客户端发送过来的消息
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        log.info("客户端传来的消息：{}", textWebSocketFrame.text() + ":");
        //获取消息，管道
        String text = textWebSocketFrame.text();
        Channel channel = channelHandlerContext.channel();
        //获取事件类型
        JSONObject jsonObject = JSONObject.parseObject(text);
        MyMessage message = jsonObject.toJavaObject(MyMessage.class);

        //处理发送过来的消息
        //采用不同策略处理不同的消息事件
        handlerMessage.handlerMessage(channel, message);

    }

    //发生异常时关闭连接
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel client = ctx.channel();
        System.out.println("出现异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }


}
