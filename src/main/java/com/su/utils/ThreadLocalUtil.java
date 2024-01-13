package com.su.utils;

import com.su.domain.pojo.Account;

/**
 * @author suweitao
 */
public class ThreadLocalUtil {

    private static ThreadLocal<Account> account=new ThreadLocal<>();

    public static Account getAccount(){
       return account.get();
    }

    public static void setAccountThreadLocal(Account accountThreadLocal){
        account.set(accountThreadLocal);
    }

    public static void clear(){
        account.remove();
    }

}
