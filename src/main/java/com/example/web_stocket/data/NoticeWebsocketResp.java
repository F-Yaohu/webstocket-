package com.example.web_stocket.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("ws通知返回对象")
public class NoticeWebsocketResp<T> {

    @ApiModelProperty(value = "通知类型")
    private String noticeType;

    @ApiModelProperty(value = "通知内容")
    private T noticeInfo;

}
