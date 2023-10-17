package com.example.web_stocket.comm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 回调
 */
@Data
@ApiModel("后端返回数据格式")
public class R {

    @ApiModelProperty(value = "200/400/500")
    int code;
    @ApiModelProperty(value = "返回消息")
    String msg;
    @ApiModelProperty(value = "返回数据")
    Object data;

    public static R ok(){
        R r = new R();
        r.setCode(200);
        r.setMsg("success");
        return r;
    }

    public static R ok(Object data){
        R r = new R();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }
}
