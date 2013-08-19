package ru.telepuzinator.gmaillibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.apache.http.auth.AuthenticationException;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.mail.MessagingException;

public class MainActivity extends Activity {
    private TextView tv;
    private TextView tv2;

    private String account = null;
    private String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.text);
        tv2 = (TextView) findViewById(R.id.text2);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warn("Clicked");
                Intent i = new Intent(MainActivity.this, AuthActivity.class);
                startActivityForResult(i, 0);
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warn2("Clicked!");
                if(token == null || account == null) {
                    Intent i = new Intent(MainActivity.this, AuthActivity.class);
                    startActivityForResult(i, 0);
                } else {
                    new AsyncTask<String, Void, Boolean>() {
                        String error;

                        @Override
                        protected Boolean doInBackground(String[] params) {
                            GmailOauthSender sender = new GmailOauthSender();
                            try {
                                sender.sendMail("test", "body test", account, token, "");
                            } catch (IOException e) {
                                error = "No internet";
                                return false;
                            } catch (AuthenticationException e) {
                                error = "Auth error";
                                return false;
                            } catch (MessagingException e) {
                                error = e.getMessage();
                                return false;
                            }
                            return true;
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            if(result) {
                                warn2("result = " + result);
                            } else {
                                warn2(error);
                            }
                        }
                    }.execute();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(data != null && data.hasExtra(AuthActivity.EXTRA_RESULT_TOKEN)) {
                token = data.getStringExtra(AuthActivity.EXTRA_RESULT_TOKEN);
                account = data.getStringExtra(AuthActivity.EXTRA_RESULT_ACCOUNT);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void warn(String s) {
        tv.setText(s);
    }

    private void warn2(String s) {
        tv2.setText(s);
    }
}
