package com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice;

import com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.impl.ExampleCodeSandBox;
import com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.impl.RemoteCodeSandBox;
import com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.impl.ThirdPartyCodeSandBox;

/**
 * 代码沙箱工厂 -> 根据代码沙箱类型（字符串）创建指定的代码沙箱实例
 */
public class CodeSandBoxFactory {
    public static CodeSandBox newInstance (String type){
        switch (type){
            case "example":
                return new ExampleCodeSandBox();
            case "remote":
                return new RemoteCodeSandBox();
            case "thirdParty":
                return new ThirdPartyCodeSandBox();
            default:
                return new ExampleCodeSandBox();
        }
    }
}
