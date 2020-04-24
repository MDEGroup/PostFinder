



import java.io.BufferedReader;
import java.io.InputStreamReader;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class AccessTokenGenerator
{
   
    public static void main(final String... args) throws Exception
    {
        Twitter twitter = new TwitterFactory().getInstance();
        RequestToken requestToken = twitter.getOAuthRequestToken();

        // Ask for the PIN
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        AccessToken accessToken = null;

}}