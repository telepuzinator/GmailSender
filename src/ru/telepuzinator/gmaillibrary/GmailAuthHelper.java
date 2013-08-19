package ru.telepuzinator.gmaillibrary;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class GmailAuthHelper {
    private Context context;

    private AccountManager am;

    private GmailTokenListener listener;

    public GmailAuthHelper(Context context) {
        this.context = context;
        am = AccountManager.get(context);
    }

    /**Returns all available user google accounts*/
    public Account[] getAccountsList() {
        return am.getAccountsByType("com.google");
    }

    public void getToken(GmailTokenListener listener, String account) {
        getToken(listener, new Account(account, "com.google"));
    }

    public void getToken(GmailTokenListener listener, Account account) {
        this.listener = listener;
        am.getAuthToken(
                account, "oauth2:https://mail.google.com/", null, (Activity) context,
                new OnTokenAcquired(account), null);
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        private final Account account;

        public OnTokenAcquired(Account account) {
            this.account = account;
        }

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle response;
            try {
                if(result != null && (response = result.getResult()) != null) {
                    if(response.containsKey(AccountManager.KEY_INTENT)) {
                        //user interaction needed
                        Intent launch = (Intent) response.get(AccountManager.KEY_INTENT);
                        if (launch != null) {
                            ((Activity) context).startActivityForResult(launch, 0);
                            return;
                        }
                    } else if(response.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                        String token = response.getString(AccountManager.KEY_AUTHTOKEN);
                        if(listener != null) {
                            listener.onGetToken(account, token);
                        }
                        return;
                    }
                }
            } catch (OperationCanceledException e) {
                //user cancelled
                if(listener != null) {
                    listener.onError(GmailTokenListener.ERROR_USER_CANCEL);
                }
            } catch (IOException e) {
                //no internet?
                if(listener != null) {
                    listener.onError(GmailTokenListener.ERROR_USER_CANCEL);
                }
            } catch (AuthenticatorException e) {
                if(listener != null) {
                    listener.onError(GmailTokenListener.ERROR_UNKNOWN);
                }
            }
        }
    }
}
