
package com.gsbina.android.adot4j4a.loader;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.gsbina.android.adot4j4a.ADOT4J4A;
import com.gsbina.android.adot4j4a.Timeline;
import com.gsbina.android.adot4j4a.Tweet;
import com.gsbina.android.adot4j4a.Twitter4JApis;
import com.gsbina.android.adot4j4a.TwitterStatus;
import com.gsbina.android.adot4j4a.TwitterUser;
import com.gsbina.android.utils.ImageUtil;

public class AsyncTimelineLoader extends AsyncTaskLoader<List<TwitterStatus>> {

    Twitter mTwitter;
    List<TwitterStatus> mResult;

    int mMode = -1;
    long mStatusId = -1;

    public AsyncTimelineLoader(Context context, int mode) {
        this(context, mode, -1);
    }

    public AsyncTimelineLoader(Context context, int mode, long statusId) {
        super(context);

        ADOT4J4A adot4j4a = (ADOT4J4A) context.getApplicationContext();

        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        confbuilder.setOAuthConsumerKey(ADOT4J4A.CONSUMER_KEY);
        confbuilder.setOAuthConsumerSecret(ADOT4J4A.CONSUMER_SECRET);
        confbuilder.setOAuthAccessToken(adot4j4a.getToken());
        confbuilder.setOAuthAccessTokenSecret(adot4j4a.getTokenSecret());
        mTwitter = new TwitterFactory(confbuilder.build()).getInstance();

        mMode = mode;
        mStatusId = statusId;
    }

    @Override
    public List<TwitterStatus> loadInBackground() {
        List<TwitterStatus> newStatus = new ArrayList<TwitterStatus>();
        try {
            List<Status> timeline = null;
            switch (mMode) {
                case Timeline.PUBLIC_LINE:
                    timeline = mTwitter.getPublicTimeline();
                    break;
                case Timeline.HOME_LINE:
                    timeline = mTwitter.getHomeTimeline();
                    break;
                case Timeline.USER_LINE:
                    timeline = mTwitter.getUserTimeline();
                    break;
                case Timeline.MENTIONS_LINE:
                    timeline = mTwitter.getMentions();
                    break;
                case Timeline.RETWEET_BY_ME_LINE:
                    timeline = mTwitter.getRetweetedByMe();
                    break;
                case Timeline.RETWEET_OF_ME_LINE:
                    timeline = mTwitter.getRetweetsOfMe();
                    break;
                case Timeline.RETWEET_TO_ME_LINE:
                    timeline = mTwitter.getRetweetedToMe();
                    break;
                case Timeline.RETWEET_TO_USER_LINE:
                    timeline = mTwitter.getRetweetedToUser(TwitterUser.YUSUKEY, new Paging(1));
                    break;
                case Timeline.RETWEET_BY_USER_LINE:
                    timeline = mTwitter.getRetweetedByUser(TwitterUser.YUSUKEY, new Paging(1));
                    break;
                case Tweet.GET_RETWEETS + Twitter4JApis.TWEET_MODE:
                    timeline = mTwitter.getRetweets(mStatusId);
                    break;
                case Tweet.FAVORITES + Twitter4JApis.TWEET_MODE:
}}}}