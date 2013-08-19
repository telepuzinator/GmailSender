GmailSender
===========

android gmail sender with oauth

===========
USAGE
===========

1) Manifest:
	<uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="android.permission.GET_ACCOUNTS"/>
    	<uses-permission android:name="android.permission.USE_CREDENTIALS"/>

2) Get token as in AuthActivity
3) Send email

    new AsynkTask ... blabla {
	GmailOauthSender sender = new GmailOauthSender();
        sender.sendMail("test", "body test", account, token, "");
    }
