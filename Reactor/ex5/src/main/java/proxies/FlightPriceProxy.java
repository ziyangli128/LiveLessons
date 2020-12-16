package proxies;

import datamodels.Trip;
import io.reactivex.rxjava3.core.Single;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.function.Function;

/**
 * This class serves as a proxy to the FlightPrice microservice.
 */
public class FlightPriceProxy {
    /**
     * The URI that denotes the remote method to find the best price for a flight leg.
     */
    private final String mFindBestPriceURIAsync =
        "/microservices/flightPrice/_bestPriceAsync";

    /**
     * The WebClient provides the means to access the FlightPrice
     * microservice.
     */
    private final WebClient mFlightPrice;

    /**
     * Host/post where the server resides.
     */
    private final String mSERVER_BASE_URL =
        "http://localhost:8080";

    /**
     * Constructor initializes the fields.
     */
    public FlightPriceProxy() {
        mFlightPrice = WebClient
            // Start building.
            .builder()

            // The URL where the server is running.
            .baseUrl(mSERVER_BASE_URL)

            // Build the webclient.
            .build();
    }

    /**
     * Finds the best price for the {@code flightLeg} asynchronously.
     *
     * @param scheduler The Scheduler context in which to run the operation
     * @param trip The trip to price
     * @return A Mono containing the best price.
     */
    public Mono<Double> findBestPriceAsync(Scheduler scheduler,
                                           Trip trip) {
        // Return a Mono to the best price.
        return Mono
            .fromCallable(() -> mFlightPrice
                          // Create an HTTP GET request.
                          .get()

                          // Add the uri to the baseUrl.
                          .uri(UriComponentsBuilder
                               .fromPath(mFindBestPriceURIAsync)
                               .queryParam("trip", trip)
                               .build()
                               .toString())

                          // Retrieve the response.
                          .retrieve()

                          // Convert it to a Mono of Double.
                          .bodyToMono(Double.class))
            
            // Schedule this to run on the given scheduler.
            .subscribeOn(scheduler)

            // De-nest the result so it's a Mono<Double>.
            .flatMap(Function.identity());
    }

    /**
     * Finds the best price for the {@code trip} asynchronously.
     *
     * @param trip The trip to price
     * @return A Single containing the best price.
     */
    public Single<Double> findBestPriceAsyncRx(Trip trip) {
        return Single
            // Return a Single to the best price.
            .fromPublisher(Mono
                           // Create a Mono from a Callable.
                           .fromCallable(() -> mFlightPrice
                                         // Create an HTTP GET
                                         // request.
                                         .get()

                                         // Add the uri to the
                                         // baseUrl.
                                         .uri(UriComponentsBuilder
                                              .fromPath(mFindBestPriceURIAsync)
                                              .queryParam("trip", trip)
                                              .build()
                                              .toString())

                                         // Retrieve the response.
                                         .retrieve()

                                         // Convert it to a Mono of
                                         // Double.
                                         .bodyToMono(Double.class))

                           // Schedule this to run on the given
                           // scheduler.
                           .subscribeOn(Schedulers.parallel())

                           // De-nest the result so it's a
                           // Mono<Double>.
                           .flatMap(Function.identity()));
    }

    /**
     * Finds the best price for the {@code flightLeg} synchronously.
     *
     * @param trip The trip to price
     * @param maxTime Max time to wait before throwing TimeoutException
     * @return A Mono containing the best price
     */
    public Mono<Double> findBestPriceSync(Trip trip,
                                          Duration maxTime) {
        // Return a Mono to the best price.
        return Mono
            .fromCallable(() -> mFlightPrice
                          // Create an HTTP GET request.
                          .get()

                          // Add the uri to the baseUrl.
                          .uri(UriComponentsBuilder
                               .fromPath(mFindBestPriceURIAsync)
                               .queryParam("trip", trip)
                               .build()
                               .toString())

                          // Retrieve the response.
                          .retrieve()

                          // Convert it to a Mono of Double.
                          .bodyToMono(Double.class))
            
            // De-nest the result so it's a Mono<Double>.
            .flatMap(Function.identity())

            // If the total processing takes more than maxTime a
            // TimeoutException will be thrown.
            .timeout(maxTime);
    }
}
