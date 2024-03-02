package com.su.constant;

/**
 * @author suweitao
 */
public class RedisConstant {

    /**
     * 邮箱验证码key
     */
    private static final String EMAIL_CODE_KEY_PREFIX = "email:code:";
    public static final String EMAIL_TIMEOUT = "60";

    public static String getEmailKey(String email) {
        return EMAIL_CODE_KEY_PREFIX + email;
    }

    /**
     * 用户搜索记录key
     *
     * @param username
     * @return
     */
    private static final String SEARCH = "SEARCH:";
    public static final String HOT_SEARCH_KEY = "hot_search_key";

    public static String getSearchWordKey(String username) {
        return SEARCH + username;
    }

    private static final String FRIEND_LIST = "friend_list:";

    public static String getFriendListKey(Integer myId) {
        return FRIEND_LIST + myId;
    }

    public static String getOtherIdMsg(Integer otherId, Integer count,long time) {
        return otherId + CommonConstant.REDIS_SEPARATORS + count+CommonConstant.REDIS_SEPARATORS+time;
    }

    private static final String CHAT_KEY = "chat:";

    public static String getChatKey(Integer sendId, Integer receiverId) {
        return CHAT_KEY + sendId + ":" + receiverId;
    }

    public static String getChatValue(String sendUserName, String msg) {
        return sendUserName + CommonConstant.REDIS_SEPARATORS + msg;
    }
}
