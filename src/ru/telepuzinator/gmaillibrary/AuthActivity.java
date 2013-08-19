package ru.telepuzinator.gmaillibrary;

import android.accounts.Account;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**Default oauth activity
 * if start this for result - returns E*/
public class AuthActivity extends ListActivity implements GmailTokenListener {
    public static final String EXTRA_RESULT_TOKEN = "extra_token";
    public static final String EXTRA_RESULT_ACCOUNT = "extra_account";

    private GmailAuthHelper helper;
    private Account[] accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new GmailAuthHelper(this);

        accounts = helper.getAccountsList();
        String[] accNames = new String[accounts.length];
        for(int i = 0; i < accounts.length; i++) {
            accNames[i] = accounts[i].name;
        }

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, accNames));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        helper.getToken(this, accounts[position]);
    }

    @Override
    public void onGetToken(Account account, String token) {
        Intent data = new Intent();
        data.putExtra(EXTRA_RESULT_TOKEN, token);
        data.putExtra(EXTRA_RESULT_ACCOUNT, account.name);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onError(int error) {
        switch (error) {
        case GmailTokenListener.ERROR_NO_INTERNET:
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
            break;
        case GmailTokenListener.ERROR_USER_CANCEL:
            break;
        }
    }
}
