package com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.impl;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.lvji.lvjiojbackendcommon.common.ErrorCode;
import com.lvji.lvjiojbackendcommon.exception.BusinessException;
import com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.CodeSandBox;
import com.lvji.lvjiojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.lvji.lvjiojbackendmodel.codesandbox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱 -> 判题业务实际调用的代码沙箱接口
 */
public class RemoteCodeSandBox implements CodeSandBox {

    // 鉴权请求头
    private static final String AUTH_REQUEST_HEADER = "auth";

    // 鉴权密钥
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://localhost:8122/executeCode";
        String executeCodeRequestStr = JSONUtil.toJsonStr(executeCodeRequest);
        // 对鉴权密钥加密
        Digester MD5 = new Digester(DigestAlgorithm.MD5);
        String userSecret = MD5.digestHex(AUTH_REQUEST_SECRET);
        String executeCodeResponseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,userSecret)
                .body(executeCodeRequestStr)
                .execute()
                .body();
        if(StringUtils.isBlank(executeCodeResponseStr)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"executeCode RemoteCodeSandBox Error,message = " + executeCodeResponseStr);
        }
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(executeCodeResponseStr, ExecuteCodeResponse.class);
        System.out.printf("executeCodeResponse = " + executeCodeResponse + "\n");
        System.out.println("远程代码沙箱完成！");
        return executeCodeResponse;
    }
}
