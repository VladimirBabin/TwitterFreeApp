package com.github.vladimirbabin.twitterfreeapp;

import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class UserAuthenticator {

    private Logger logger = LoggerFactory.getLogger(UserAuthenticator.class);

    /*
     * This method calls the v2 Users endpoint with usernames as query parameter
     * */
    OAuth1AccessToken getAccessToken(OAuth10aService service) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
//      Getting a request token and using it to get the authorization URL
        OAuth1RequestToken requestToken = service.getRequestToken();
        String authUrl = service.getAuthorizationUrl(requestToken);


//      Redirecting the user to authUrl to get the oauthVerifier
        logger.info("Please, go here and authorize: {}", authUrl);
        logger.info("Please, enter the pin code: ");
        Scanner scanner = new Scanner(System.in);
        String oauthVerifier = scanner.nextLine();

//      Using the oauthVerifier to get the accessToken by redirecting the user to authUrl
        OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
        return accessToken;
    }
}
