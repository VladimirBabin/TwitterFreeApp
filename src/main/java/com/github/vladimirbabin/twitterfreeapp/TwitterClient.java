package com.github.vladimirbabin.twitterfreeapp;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class TwitterClient {
    private static Logger logger = LoggerFactory.getLogger(TwitterClient.class);
    private String consumerKey = System.getenv("TWITTER_CONSUMER_KEY");
    private String consumerKeySecret = System.getenv("TWITTER_CONSUMER_KEY_SECRET");
    private Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        try {
            new TwitterClient().run();
        } catch (IOException | URISyntaxException | ExecutionException | InterruptedException e) {
            logger.error("Exception on authorisation: ", e);
        }
    }

    private void run() throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        logger.info("Starting the Twitter client");
        OAuth10aService service = new ServiceBuilder(consumerKey)
                .apiSecret(consumerKeySecret)
                .build(TwitterApi.instance());

        UserAuthenticator authenticator = new UserAuthenticator();
        OAuth1AccessToken accessToken = authenticator.getAccessToken(service);

        retrieveUserData(service, accessToken);

        logger.info("Please, type the text of your next tweet here:");
        String tweetText = scanner.nextLine();
        postNewTweet(service, accessToken, tweetText);


    }

    private void postNewTweet(OAuth10aService service, OAuth1AccessToken accessToken, String tweetText)
            throws InterruptedException, ExecutionException, IOException {
        String payload = String.format("{\"text\": \"%s\"}", tweetText);
        OAuthRequest postRequest = new OAuthRequest(Verb.POST,
                "https://api.twitter.com/2/tweets");
        postRequest.addHeader("Content-type", "application/json");
        postRequest.setPayload(payload);
        service.signRequest(accessToken, postRequest);

        logger.info("Post request: " + postRequest);
        logger.info("Post request body parameter : " + postRequest.getStringPayload());
        logger.info("Post request headers: " + postRequest.getHeaders());


        Response postResponse = service.execute(postRequest);
        if (postResponse.isSuccessful()) {
            logger.info("Tweet created successfully!");
        } else {
            logger.info("There was a problem creating a tweet");
            logger.info(postResponse.getMessage());
            logger.info(postResponse.toString());
        }
    }

    private void retrieveUserData(OAuth10aService service, OAuth1AccessToken accessToken)
            throws InterruptedException, ExecutionException, IOException {
        //      Creating a request using OAuthRequest object and adding the token to it with signRequest() method:
        OAuthRequest request = new OAuthRequest(Verb.GET,
                "https://api.twitter.com/2/users/me");
        service.signRequest(accessToken, request);

        Response getUserResponse = service.execute(request);
        if (getUserResponse.isSuccessful()) {
            logger.info("User retrieved successfully");
            logger.info("Request headings: {}", getUserResponse.getHeaders());
            logger.info("User data: {}", getUserResponse.getBody());
        } else {
            logger.info("Error on user retrieval");
        }
    }

    private void deleteTweetById(OAuth10aService service, OAuth1AccessToken accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.DELETE,
                "https://api.twitter.com/2/tweets/");
        service.signRequest(accessToken, request);
    }

}
