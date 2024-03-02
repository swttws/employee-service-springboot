package com.su.controller;

import com.su.common.response.ResultResponse;
import com.su.service.ChatService;
import com.su.utils.ThreadLocalUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author suweitao
 */
@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatService chatService;

    @ApiOperation("保存用户聊天列表")
    @GetMapping("saveFriendList/{otherId}")
    public ResultResponse saveFriendList(@PathVariable("otherId") Integer otherId) {
        Integer myId = ThreadLocalUtil.getAccount().getId();
        return ResultResponse.success(chatService.saveFriendList(myId,otherId));
    }

    @ApiOperation("获取聊天列表")
    @GetMapping("getFriendList")
    public ResultResponse getFriendList(){
        Integer id = ThreadLocalUtil.getAccount().getId();
        return ResultResponse.success(chatService.getFriendList(id));
    }

}
