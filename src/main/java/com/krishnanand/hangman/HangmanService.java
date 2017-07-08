// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Stategy implementation that implements Hangman.
 */
@Service
public class HangmanService implements IHangmanService {

    private static final String INITIALISATION_URL = "http://int-sys.usr.space/hangman/games";

    private static final String HANGMAN_STATUS_URL =
        "http://int-sys.usr.space/hangman/games/%s/guesses";


    private final RestTemplate restTemplate;


    /**
     * Constructor for Hangman Delegate.
     *
     * @param restTemplate template to use to generate a value.
     */
    @Autowired
    public HangmanService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Register's the email to play hangman.
     *
     * @param email email address to be registered
     * @return value object representing the response returned by the backend.
     */
    @Override public InitialisationResponse register(String email) {
        // Check if the token exists. If not, then mail the initialisation call.
        // Otherwise, reuse the token. Not checking for expiry or invalidity.
        return this.startTheGame(email);
    }

    /**
     * <p>The implementation is responsible for the following actions.
     * <p>The logic is given below:
     * <ul>
     *     <li>The number of available guesses is checked. The game can only proceed if the
     *     the number of available guesses is greater than 0. If number of guesses equal zero,
     *     then the game is supposed to end.</li>
     *     <li>The application makes a HTTP post request to the endpoint with the appropriate
     *     game id in the request URI.</li>
     *     <li>Verify the response value. For an unsolved puzzle, the sample response is
     *         <pre class="code">
     *             {"gameId":"<randomly generated value>"","status":"active",
     *             "word":"___r__d","guessesLeft":9, "msg":"You have guessed h"}
     *         </pre>
     *    </li>
     *     <li> If the {@code guessesLeft} value equals {@code 0}, and {@code status} equals
     *         "inactive", then it implies that the game is over. That means that the player has
     *         either solved the puzzle, or exhausted the number of attempts.
     *         <ul>
     *             <li>In this event, the {@code msg} is checked for any success indicator.</li>
     *             <li>If the {@code msg} contains string {@code Congrats}, then the player has
     *             won the game. In this event, the sample response is
     *             {@code {"gameId":"aab305a27efb","status":"inactive",
     *               "word":"phoneticogrammatical","guessesLeft":0,
     *               "msg":"Congrats! You have solved this hangman!"}}
     *             </li>
     *             <li>If the player has exhausted all attempts, then the following message
     *             format is observed.
     *             {@code
     *               {"gameId":"30b1f76980df","status":"inactive",
     *               "word":"de_ce_de_tali__","guessesLeft":0,
     *               "msg":"You have guessed k. But you didn't solve hangman! The answer was
     *               descendentalism"}}</li>
     *              <li>The implementation checks for {@code didn't solve hangman} string. If
     *              found, then the puzzle was not solved.</li>
     *              <li>An error message in the format will be returned as 4XX error, and needs
     *              to be handled.</li>
     *         </ul>
     *     </li>
     *
     * </ul>
     *
     * @param response value object representing the initialisation data.
     * @param c character to be for verification
     * @return {@code true if the hangman was successful}
     */
    @Override
    public CharacterSelectionResponse playHangman(InitialisationResponse response, char c) {
        return this.solve(response, c);
    }

    /**
     * Returns {@code true} if the puzzle is solved.
     */
    @Override
    public boolean isPuzzleSolved(CharacterSelectionResponse response) {
        if (response == null || (response.getError() != null && !response.getError().isEmpty())) {
            return false;
        }
        // If the puzzle is solved, then the word does not contain any underscores (_)
        return response.getGuessesLeft() == 0 && !response.getWord().contains("_");
    }

    /**
     * Returns {@code true} if all attempts are exhausted
     */
    @Override public boolean areAttemptsExhausted(CharacterSelectionResponse response) {
        if (response == null || (response.getError() != null && !response.getError().isEmpty())) {
            return false;
        }

        return response.getGuessesLeft() == 0 && (response.getWord() != null && response.getWord()
            .contains("_"));
    }

    /**
     * Invokes the restful service to trigger hangman game initialisation.
     *
     * <p>The request URI accepts "email address" as a value for POST parameter "email".</p>
     *
     * @param email email address to be initialised.
     * @return parsed initialisation response
     */
    // Package scope for only testing purposes.
    InitialisationResponse startTheGame(String email) {
        // Appending form data.
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", email);
        return this.makeHttpRequest(
            INITIALISATION_URL, HttpMethod.POST, map, InitialisationResponse.class);
    }

    /**
     * Abstraction to make a http request.
     *
     * @param url url to connect to
     * @param httpMethod http method to use (GET, POST etc.)
     * @param requestParams map of request parameter name to its value
     * @param clazz class instance of response entity.
     * @return response
     */
    private <T> T makeHttpRequest(String url, HttpMethod httpMethod,
            MultiValueMap<String, String>  requestParams, Class<T> clazz) {

        ResponseErrorHandler handler = new DefaultResponseErrorHandler() {
            // Only consider this error if no error response is passed.
            @Override public boolean hasError(ClientHttpResponse clientHttpResponse)
                    throws IOException {
                return false;
            }

            // Not to be called.
            @Override public void handleError(ClientHttpResponse clientHttpResponse)
                    throws IOException {
                super.handleError(clientHttpResponse);
            }
        };
        this.restTemplate.setErrorHandler(handler);
        HttpHeaders httpHeaders = new HttpHeaders();
        // Since post parameters are required to be sent as form data, the content type must
        // be "application/x-www-form-urlencoded".
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity =
            new HttpEntity<>(requestParams, httpHeaders);
        ResponseEntity<T> responseEntity =
            this.restTemplate.exchange(url, httpMethod, httpEntity, clazz);
        return responseEntity.getBody();
    }

    /**
     * Invokes a restful verification service to check if the character is present in the string.
     *
     *
     * @return {@code true} if the game is solved; {@code false} otherwise
     */
    CharacterSelectionResponse solve(InitialisationResponse response, char c) {

        String requestUri = String.format(HANGMAN_STATUS_URL, response.getGameId());

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("char", String.valueOf(c));
        return
            this.makeHttpRequest(
                requestUri, HttpMethod.POST, requestParams, CharacterSelectionResponse.class);
    }


}
