package com.hs.support;

import java.util.List;
import java.util.Map;

public interface SupportChatBlackListService {

    /**
     * 客服聊天黑名单列表
     *
     * @param ip        知识产权
     * @param pageIndex 页面索引
     * @param pageSize  页面大小
     * @return {@link List}<{@link Map}<{@link String},{@link Object}>>
     * @throws Exception 异常
     */
    Map<String,Object> blackList(String ip,int pageIndex,int pageSize) throws Exception;


    /**
     * 添加黑名单
     *
     * @param ip      知识产权
     * @param remarks 讲话
     * @throws Exception 异常
     */
    void addBlackList(String ip,String noLoginId,String remarks) throws Exception;


    /**
     * 删除黑名单
     *
     * @param id 知识产权
     * @throws Exception 异常
     */
    void removeBlackList(String id) throws Exception;
}
