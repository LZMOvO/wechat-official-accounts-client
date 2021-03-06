package com.github.ming.wechat.client.bean.user;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 用户列表
 *
 * @author ZM
 * @date : 2018-12-29 03:16
 */
public class WeChatUserOpenIdList {

    /**
     * 关注该公众账号的总用户数
     */
    private int total;

    /**
     * 拉取的OPENID个数，最大值为10000
     */
    private int count;

    /**
     * 列表数据，OPENID的列表
     */
    @JSONField(name = "openid")
    private List<String> openIdList;

    /**
     * 拉取列表的最后一个用户的OPENID
     */
    @JSONField(name = "next_openid")
    private String nextOpenid;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getOpenIdList() {
        return openIdList;
    }

    public void setOpenIdList(List<String> openIdList) {
        this.openIdList = openIdList;
    }

    public String getNextOpenid() {
        return nextOpenid;
    }

    public void setNextOpenid(String nextOpenid) {
        this.nextOpenid = nextOpenid;
    }
}
