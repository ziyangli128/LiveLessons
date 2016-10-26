package livelessons.streamgangs;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import livelessons.utils.SearchResults;
import livelessons.utils.StreamsUtils;

import static java.util.stream.Collectors.toList;

/**
 * Customizes the SearchStreamGang framework to use CompletableFutures
 * in conjunction with Java Streams to search how many times each word
 * in an array of words appears in input data.
 */
public class SearchWithCompletableFuturesWords
    extends SearchStreamGang {
    /**
     * Constructor initializes the super class.
     */
    public SearchWithCompletableFuturesWords(List<String> wordsToFind,
                                             String[][] stringsToSearch) {
        // Pass input to superclass constructor.
        super(wordsToFind,
              stringsToSearch);
    }

    /**
     * Perform the processing, which uses a Java 8 Stream and
     * CompletableFutures to asynchronously search for words in the
     * input data.
     */
    @Override
    protected List<List<SearchResults>> processStream() {
        // Convert the words to find into a list of CompletableFutures
        // to lists of SearchResults.
        List<CompletableFuture<List<SearchResults>>> listOfFutures = mWordsToFind
            // Create a sequential stream of words to find.
            .stream()

            // Map each word to a CompletableFuture to a list of
            // SearchResults.
            .map(this::processWordAsync)

            // Terminate stream and return a list of
            // CompletableFutures.
            .collect(toList());
                    
        // Wait for all operations associated with the futures to
        // complete.
        return StreamsUtils.joinAll(listOfFutures)
                            // join() blocks the calling thread until
                            // all the futures have been completed.
                            .join();
    }

    /**
     * Asynchronously search all the input strings for occurrences of
     * the word to find.
     */
    private CompletableFuture<List<SearchResults>> processWordAsync(String word) {
        // Convert the input strings into a list of
        // CompletableFutures to SearchResults.
        List<CompletableFuture<SearchResults>> listOfFutures = getInput()
            // Create a sequential stream of words to find.
            .stream()

            // Map each input string to a CompletableFuture to
            // SearchResults.
            .map(inputString -> {
                    // Get the title.
                    String title = getTitle(inputString);

                    // Get the input string (skipping over the title).
                    String input = inputString.substring(title.length());

                    // Asynchronously search for the word in the input string.
                    return CompletableFuture.supplyAsync(() 
                                                         -> searchForWord(word, 
                                                                          input,
                                                                          title));
                })

            // Terminate stream and return a list of
            // CompletableFutures.
            .collect(toList());

        // Return a CompletableFuture to a list of SearchResults.
        // that will be available when all the CompletableFutures in
        // the listOfFutures have completed.
        return StreamsUtils.joinAll(listOfFutures);
    }
}