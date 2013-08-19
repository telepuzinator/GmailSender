package ru.telepuzinator.gmaillibrary;

import android.accounts.Account;

public interface GmailTokenListener {
    public static final int ERROR_NO_INTERNET = 0;
    public static final int ERROR_USER_CANCEL = 1;
    public static final int ERROR_UNKNOWN = 2;

    public void onGetToken(Account account, String token);

    public void onError(int error);
}
