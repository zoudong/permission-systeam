package com.zoudong.permission.result;

import com.zoudong.permission.constant.ResultCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResult implements Serializable {
    //状态码
    private String code= ResultCode.succes.getCode();
    private String msg=ResultCode.succes.getMsg();
    private Object data=null;
    public BaseResult(){

    }


}
