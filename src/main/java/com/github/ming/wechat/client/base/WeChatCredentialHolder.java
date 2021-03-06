package com.github.ming.wechat.client.base;

import com.github.ming.wechat.client.apiurl.WeChatApiUrls;
import com.github.ming.wechat.client.bean.credential.WeChatAccessToken;
import com.github.ming.wechat.client.bean.credential.wrapper.WeChatAccessTokenWrapper;
import com.github.ming.wechat.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * WeChatCredentialHolder
 *
 * @author : ZM
 * @date : 2019-01-02 22:42
 */
public class WeChatCredentialHolder {

    private static final Logger logger = LoggerFactory.getLogger(WeChatCredentialHolder.class);

    /**
     * 第三方用户唯一凭证
     */
    private String appId;

    /**
     * 第三方用户唯一凭证密钥，即appsecret
     */
    private String appSecret;

    /**
     * 微信access_token超时重试次数
     */
    private int accessTokenTimeoutRetry;

    /**
     * 属于此appId的accessToken
     */
    private WeChatAccessTokenWrapper accessToken;

    private ReentrantLock lock;

    public WeChatCredentialHolder(String appId, String appSecret, int accessTokenTimeoutRetry) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.accessTokenTimeoutRetry = accessTokenTimeoutRetry + 1;
        lock = new ReentrantLock();
    }

    /**
     * 项目内获取当前可用的accessToken
     *
     * @param refreshToken 是否重新获取accessToken true=是；false=否
     * @return 新的accessToken
     */
    String getAccessToken(boolean refreshToken) {
        if (this.accessToken == null || refreshToken || this.validAccessTokenTimeDiff()) {
            this.resetAccessToken();
        }
        return accessToken.getAccessToken();
    }

    /**
     * 重置客户端内部使用的accessToken
     */
    private void resetAccessToken() {
        if (lock.tryLock()) {
            try {
                this.accessToken = new WeChatAccessTokenWrapper(this.accessToken());
                logger.debug("重置了access_token");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                lock.unlock();
            }
        } else {
            while (lock.isLocked()) {
                if (!lock.isLocked()) {
                    logger.debug("啊~我发现有人刷新完token了");
                    return;
                }
            }
        }
    }

    /**
     * 获取accessToken
     */
    private WeChatAccessToken accessToken() {
        String result = HttpUtil.get(WeChatApiUrls.ACCESS_TOKEN_URL.replace("APPID", this.appId).replace(
                "APPSECRET", this.appSecret));
        return WeChatResponse.result2Bean(result, WeChatAccessToken.class);
    }

    /**
     * 判断accessToken是否过期
     *
     * @return true=过期
     */
    private boolean validAccessTokenTimeDiff() {
        return this.nowSeconds() - this.accessToken.getCreateTime() >= this.accessToken.getExpiresIn();
    }

    /**
     * 获取当前时间的秒数
     *
     * @return 当前时间的秒数
     */
    private long nowSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    int getAccessTokenTimeoutRetry() {
        return accessTokenTimeoutRetry;
    }
}
