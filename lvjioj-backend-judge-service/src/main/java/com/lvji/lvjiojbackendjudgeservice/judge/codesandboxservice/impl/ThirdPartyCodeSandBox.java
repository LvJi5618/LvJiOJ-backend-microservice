package com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.impl;

import com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.CodeSandBox;
import com.lvji.lvjiojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.lvji.lvjiojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱 -> 调用网上写好的代码沙箱服务
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
