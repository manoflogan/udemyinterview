// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class HangmanServiceTest {

    private HangmanService hangmanService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WordService wordService;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.hangmanService = new HangmanService(this.restTemplate, this.wordService);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        Mockito.reset(this.restTemplate);
        this.hangmanService = null;
    }

    @Test
    public void testRegister() throws Exception {
        String email = "foo@foomail.com";
        InitialisationResponse expected = new InitialisationResponse();
        expected.setGameId("gameId");
        expected.setGuessesLeft(100);
        expected.setWord("______");
        @SuppressWarnings("unchecked")
        ResponseEntity<InitialisationResponse> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(response.getBody()).thenReturn(expected);
        Mockito.doReturn(response).when(this.restTemplate).exchange(
            Mockito.any(String.class), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class),
            Mockito.eq(InitialisationResponse.class));
        InitialisationResponse actual = this.hangmanService.register(email);
        Assert.assertEquals(actual, expected);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(this.restTemplate).exchange(
            stringCaptor.capture(), Mockito.eq(HttpMethod.POST), entityCaptor.capture(), Mockito
                .eq(InitialisationResponse.class));
        Assert.assertEquals(stringCaptor.getValue(),
            "http://int-sys.usr.space/hangman/games");
        @SuppressWarnings("unchecked")
        HttpEntity<MultiValueMap<String, String>> actualEntity = entityCaptor.getValue();
        MultiValueMap<String, String> map = actualEntity.getBody();
        MultiValueMap<String, String> expectedMap = new LinkedMultiValueMap<>();
        expectedMap.add("email", email);
        Assert.assertEquals(map, expectedMap);
        HttpHeaders headers = actualEntity.getHeaders();
        Assert.assertEquals(headers.get("Content-Type").get(0),
            "application/x-www-form-urlencoded");
    }

    @Test()
    public void testPlayHangman() throws Exception {

        InitialisationResponse input = new InitialisationResponse();
        input.setGameId("gameId");
        input.setWord("______");
        InitialisationResponse spyInput = Mockito.spy(input);
        Mockito.when(spyInput.getGuessesLeft()).thenReturn(Long.valueOf(1), Long.valueOf(0));


        GameStatusResponse expected = new GameStatusResponse();
        expected.setGameId("gameId");
        expected.setWord("_____a__");
        expected.setGuessesLeft(0);
        expected.setStatus("Active");
        expected.setMsg("You have selected a");

        ResponseEntity<GameStatusResponse> response = Mockito.mock(ResponseEntity.class);
        Mockito.doReturn(response).when(this.restTemplate).exchange(
            Mockito.any(String.class), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class),
            Mockito.eq(GameStatusResponse.class));
        Mockito.when(response.getBody()).thenReturn(expected);

        Mockito.when(this.wordService.getWordCountForWordLength(spyInput.getWord())).thenReturn(new
            int[input.getWord().length()]);


        Mockito.doReturn(response).when(this.restTemplate).exchange(
            Mockito.any(String.class), Mockito.eq(HttpMethod.GET), (HttpEntity<?>) Mockito.isNull(),
            Mockito.eq(GameStatusResponse.class));
        Mockito.when(response.getBody()).thenReturn(expected);
        GameStatusResponse actual = this.hangmanService.playHangman(spyInput);

        Assert.assertEquals(actual, expected);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(this.restTemplate).exchange(
            stringCaptor.capture(), Mockito.eq(HttpMethod.POST), entityCaptor.capture(), Mockito
                .eq(GameStatusResponse.class));
        Assert.assertEquals(stringCaptor.getValue(),
            "http://int-sys.usr.space/hangman/games/" + input.getGameId() + "/guesses");
        @SuppressWarnings("unchecked")
        HttpEntity<MultiValueMap<String, String>> actualEntity = entityCaptor.getValue();
        MultiValueMap<String, String> map = actualEntity.getBody();
        MultiValueMap<String, String> expectedMap = new LinkedMultiValueMap<>();
        expectedMap.add("char", String.valueOf('a'));
        Assert.assertEquals(map, expectedMap);
        HttpHeaders headers = actualEntity.getHeaders();
        Assert.assertEquals(headers.get("Content-Type").get(0),
            "application/x-www-form-urlencoded");
    }

    @Test
    public void testIsPuzzleSolved_ForIncompleteTest() throws Exception {
        GameStatusResponse expected = new GameStatusResponse();
        expected.setWord("_____a__");
        expected.setGuessesLeft(99);
        expected.setStatus("Active");
        expected.setMsg("You have selected a");
        Assert.assertFalse(this.hangmanService.isPuzzleSolved(expected));
    }

    @Test
    public void testIsPuzzleSolved_ForCompleteTest() throws Exception {
        GameStatusResponse expected = new GameStatusResponse();
        expected.setError("game completed");
        Assert.assertTrue(this.hangmanService.isPuzzleSolved(expected));

    }

    @Test
    public void testIsPuzzleSolved_ForErrorCondition() throws Exception {
        GameStatusResponse expected = new GameStatusResponse();
        expected.setError("character u already played");
        Assert.assertFalse(this.hangmanService.isPuzzleSolved(expected));
    }

    @Test
    public void testAreAttemptsExhausted_ForIncompleteTest() throws Exception {
        GameStatusResponse expected = new GameStatusResponse();
        expected.setWord("_____a__");
        expected.setGuessesLeft(99);
        expected.setStatus("Active");
        expected.setMsg("You have selected a");
        Assert.assertFalse(this.hangmanService.areAttemptsExhausted(expected));
    }

    @Test
    public void testAreAttempts_ForCompleteTest() throws Exception {
        GameStatusResponse expected = new GameStatusResponse();
        expected.setWord("__comple");
        expected.setGuessesLeft(0);
        expected.setStatus("inactive");
        expected.setMsg("You have selected e");
        Assert.assertTrue(this.hangmanService.areAttemptsExhausted(expected));

    }

    @Test
    public void testAreAttemptsExhausted_ForErrorCondition() throws Exception {
        GameStatusResponse expected = new GameStatusResponse();
        expected.setError("character u already played");
        Assert.assertFalse(this.hangmanService.areAttemptsExhausted(expected));
    }
}
