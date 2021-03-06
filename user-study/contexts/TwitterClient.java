package jp.senchan.android.wasatter.client;

import java.io.File;
import java.util.ArrayList;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.impl.twitter.TwitterStatus;
import android.net.Uri;
import android.text.TextUtils;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClient implements WasatterApiClient {
	
	private TwitterFactory mFactory;
	private RequestToken mRequestToken;
	private AccessToken mToken;
	private Wasatter mApp;
	private Configuration mConf;

	public TwitterClient(Wasatter app) {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(OAuthTwitter.CONSUMER_KEY);
		builder.setOAuthConsumerSecret(OAuthTwitter.CONSUMER_SECRET);
		mConf = builder.build();
		mApp = app;
		mFactory = new TwitterFactory(mConf);
		fetchToken();
	}
	
	public void fetchToken() {
		String token = mApp.getTwitterToken();
		String tokenSecret = mApp.getTwitterTokenSecret();
		if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(tokenSecret)) {
			mToken = new AccessToken(token, tokenSecret);
		}
	}
	
	public Twitter getClient() {
		fetchToken();
		return mFactory.getInstance(mToken);
	}
	
	public OAuthAuthorization getAuthorizer() {
		return new OAuthAuthorization(mConf);
	}
	

	public String getAuthorizationURL () throws TwitterException {
		mRequestToken = getAuthorizer().getOAuthRequestToken();
		return mRequestToken.getAuthorizationURL();
	}
	
	public AccessToken getAccessTokenFromURL (Uri uri) throws TwitterException {
		String verifier = uri.getQueryParameter("oauth_verifier");
		return getAuthorizer().getOAuthAccessToken(mRequestToken, verifier);
	}
	
	public boolean updateStatus(String body, String imagePath, String replyId) {
		if (TextUtils.isEmpty(body)) {
			return false;
		}
		StatusUpdate status = new StatusUpdate(body);
		if (imagePath != null) {
			status.media(new File(imagePath));
		}
		if (!TextUtils.isEmpty(replyId)) {
			status.setInReplyToStatusId(Long.parseLong(replyId));
		}
		try {
			getClient().updateStatus(status);
			return true;
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Paging getPaging(long maxId) {
		Paging paging = new Paging();
		if (maxId == 0) {
			paging.setPage(1);
		} else {
			//max_id????max_id???????????-1??
			paging.setMaxId(maxId - 1);
		}
		return paging;
}}