package com.example.web_stocket.controller;

import com.example.web_stocket.comm.R;
import com.example.web_stocket.server.NoticeWebsocket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @GetMapping("/test")
    public R test() {
        NoticeWebsocket.sendMessage("你好，WebSocket",1);
        return R.ok();
    }
}
