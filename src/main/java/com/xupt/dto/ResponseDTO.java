package com.xupt.dto;

import com.xupt.constant.ErrorCode;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Restful接口的统一返回体
 * @author robersluo
 * @since 2019/1/30
 */
@Accessors(chain = true)
@Data(staticConstructor = "of")
public class ResponseDTO<T> {

    private int code;

    private String msg;

    private T data;

    /**
     * 成功返回体
     * @param data
     * @return
     */
    public ResponseDTO<T> success(T data) {
        setCode(ErrorCode.OK);
        setData(data);
        return this;
    }

    /**
     * 失败返回体
     * @param errorCode
     * @return
     */
    public ResponseDTO<T> fail(int errorCode, String extMsg) {
        setCode(errorCode);
        setMsg(extMsg);
        return this;
    }
}
