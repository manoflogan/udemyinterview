UDEMY Hangman Test
------------------

We have a Hangman game on a server and we would like you to build a solver that can play this game and be

able to solve the Hangman (consistently, but it doesn’t always have to work). This solver should be able to

interface RESTfully with the API endpoints.

There will be 3 API end points, 2 of them are POST requests and 1 of them is a GET request.

To start a game you will want to POST to http://int-sys.usr.space/hangman/games with email

and your email address as the value.

Once you post to the server with a valid email address, it should return a JSON object containing these values:

{ 'gameId': XXXX, 'word': YYYY, 'guessesLeft': N }

The gameId value will be your game token, in which you'll need to pass in future requests.

The word value will be blank underscore characters, so if the hangman word is Udemy then the value is

_____ (5 underscores).

The guessesLeft value will be how many tries you have left. Valid guesses will not decrement this value

but invalid guesses will.

You can check the status of your game by sending a GET request to

http://int-sys.usr.space/hangman/games/{gameId} -- where the gameId is the value given

before. This should return a JSON object containing the values of:

{ 'gameId': XXXX, 'word': YYYY, 'guessesLeft': N, 'active': ZZZZ } In addition to the

previous JSON object, a value of active is also written, to determine whether the game has been completed or

has ended already.

To play the hangman you will need to send a POST request to

http://int-sys.usr.space/hangman/games/{gameId}/guesses -- where the gameId is the

value given before.

In addition, you will need to add a data field of char with the letter of your choice. This should return a JSON

object containing the values of:

{ 'gameId': XXXX, 'word': YYYY, 'guessesLeft': N, 'active': ZZZZ, 'msg': AAAAAAA }

In addition to the previous JSON object, a value of msg will tell you what event happened when you tried to

play the letter. The word and guessesLeft fields will be updated with the action you just took.

Hangman

ie:

If the word is bamboo and you guessed o (without any other guesses) then the word returned will be

____oo . However, if the word is bamboo and you guessed r and you had 7 guesses left, then the

word returned will still be ______ but the guessesLeft value will decrement to 6.

In addition, if there are any invalid inputs then the return JSON object will be

{ 'error': 'error message' } with a 4XX status code

Hint: The server that plays this game doesn't
