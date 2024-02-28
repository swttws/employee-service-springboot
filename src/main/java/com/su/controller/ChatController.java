package com.su.controller;

import com.su.common.response.ResultResponse;
import com.su.service.ChatService;
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
    @GetMapping("saveFriendList/{myId}/{otherId}")
    public ResultResponse saveFriendList(@PathVariable("myId") Integer myId
            , @PathVariable("otherId") Integer otherId) {
        return ResultResponse.success(chatService.saveFriendList(myId,otherId));
    }

}
