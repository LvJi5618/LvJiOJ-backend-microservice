package com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice;

import com.lvji.lvjiojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.lvji.lvjiojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱服务接口
 */
public interface CodeSandBox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode (ExecuteCodeRequest executeCodeRequest);
}
