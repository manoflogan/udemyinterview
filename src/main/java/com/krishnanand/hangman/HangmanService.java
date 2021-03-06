// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private static final String HANGMAN_GAME_URL =
        "http://int-sys.usr.space/hangman/games/%s/guesses";

    private static final String HANGMAN_STATUS_URL = "http://int-sys.usr.space/hangman/games/%s/";

    private static final Log LOG = LogFactory.getLog(HangmanService.class);


    private final RestTemplate restTemplate;

    private final IWordService wordService;


    /**
     * Constructor for Hangman Delegate.
     *
     * @param restTemplate template to use to generate a value.
     */
    @Autowired
    public HangmanService(RestTemplate restTemplate, IWordService wordService) {
        this.restTemplate = restTemplate;
        this.wordService = wordService;
    }

    /**
     * Invokes the restful service to trigger hangman game initialisation.
     *
     * <p>The request URI accepts "email address" as a value for POST parameter "email".</p>
     *
     * @param email email address to be initialised.
     * @return parsed initialisation response
     *
     * @param email email address to be registered
     * @return value object representing the response returned by the backend.
     */
    // Package scope for only testing purposes.
    @Override public InitialisationResponse register(String email) {
        // Appending form data.
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", email);
        return this.makeHttpRequest(
            INITIALISATION_URL, HttpMethod.POST, map, InitialisationResponse.class);
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
     * @return {@code true if the hangman was successful}
     */
    @Override
    public GameStatusResponse playHangman(InitialisationResponse response) {
        String requestUri = String.format(HANGMAN_GAME_URL, response.getGameId());
        String word = response.getWord();
        int[] initialCount = this.wordService.getWordCountForWordLength(word);
        Set<Character> charactersUsed = new HashSet<>();
        Queue<Character> queue = this.getOrderedCharacters(initialCount, charactersUsed);

        GameStatusResponse gameStatusResponse = null;
        while(response.getGuessesLeft() > 0){
            char currentCharacter = queue.remove();

            charactersUsed.add(currentCharacter);
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("char", String.valueOf(currentCharacter));
            // Make the call
            GameStatusResponse attempt = this.makeHttpRequest(
                requestUri, HttpMethod.POST, requestParams, GameStatusResponse.class);
            if (attempt.getError() != null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(attempt.getError());
                }
            } else {
                if (LOG.isInfoEnabled()) {
                    LOG.info(attempt.getMsg());
                    if (attempt.getGuessesLeft() > 0) {
                        LOG.info("The hangman puzzle = " + attempt.getWord());
                        LOG.info("You have " + attempt.getGuessesLeft() + " guesses remaining.");
                    }
                }
            }
            GameStatusResponse currentStatus = this.findCurrentGameStatus(attempt.getGameId());
            if (this.isPuzzleSolved(currentStatus)) {
                currentStatus.setWord(attempt.getWord());
                return currentStatus;
            } else if (this.areAttemptsExhausted(currentStatus)) {
                return attempt;
            }


            if(!attempt.getWord().equals(response.getWord())) {
                LOG.info("character is present with word " + currentStatus.getWord());

                attempt = currentStatus;
                int[] characterCountForGuesses=new int[26];
                this.wordService.search(attempt.getWord(), characterCountForGuesses);
                queue=getOrderedCharacters(characterCountForGuesses, charactersUsed);
            }
            gameStatusResponse = attempt;
        }
        return gameStatusResponse;
    }

    /**
     * Queue of all successful characters.
     *
     * @param chars character array
     * @param characterUsed all characters
     * @return
     */
    private Queue<Character>  getOrderedCharacters(int[] chars, Set<Character> characterUsed) {
        Queue<Character> queue = new PriorityQueue<>(new Comparator<Character>() {
            @Override public int compare(Character a, Character b) {
                return chars[b -'a']-chars[a-'a'];
            }
        });
        for (int i = 0; i < chars.length; i++) {
            char ch = (char) (i + 'a');
            if (!characterUsed.contains(Character.valueOf(ch))) {
                queue.add(ch);
            }
        }
        return queue;
    }

    /**
     * Returns {@code true} if the puzzle is solved.
     */
    @Override
    public boolean isPuzzleSolved(GameStatusResponse response) {
        return response != null && response.getError() != null &&
            response.getError().equals("game completed");
    }

    /**
     * Returns {@code true} if all attempts are exhausted
     */
    @Override public boolean areAttemptsExhausted(GameStatusResponse response) {
        return response != null && response.getGuessesLeft() == 0 && response.getStatus() != null &&
            response.getStatus().equals("inactive");
    }

    @Override
    public GameStatusResponse findCurrentGameStatus(String gameId) {
        String url = String.format(HANGMAN_STATUS_URL, gameId);
        return this.makeHttpRequest(url, HttpMethod.GET, null,
            GameStatusResponse.class);
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
            // Don't let Spring handle all the errors.
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

        // Since post parameters are required to be sent as form data, the content type must
        // be "application/x-www-form-urlencoded".
        HttpEntity<MultiValueMap<String, String>> httpEntity = null;
        if (httpMethod == HttpMethod.POST) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpEntity =
                new HttpEntity<>(requestParams, httpHeaders);
        }

        ResponseEntity<T> responseEntity =
            this.restTemplate.exchange(url, httpMethod, httpEntity, clazz);
        return responseEntity.getBody();
    }

    /**
     * Loads the words in a data structure onces the beans are initalised.
     */
    @PostConstruct
    @Override public void loadWords() {
        this.wordService.loadWordsFromFile();
    }
}
