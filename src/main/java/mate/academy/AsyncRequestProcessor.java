package mate.academy;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class AsyncRequestProcessor {
    private final Executor executor;
    private final Map<String, UserData> cache = new ConcurrentHashMap<>();

    public AsyncRequestProcessor(Executor executor) {
        this.executor = executor;
    }

    public CompletableFuture<UserData> processRequest(String userId) {
        UserData cachedData = cache.get(userId);
        if (cachedData != null) {
            System.out.println("Data received for user " + userId + " from the cache.");
            return CompletableFuture.completedFuture(cachedData);
        }
        System.out.println("Asynchronous request processing for the user " + userId + " ....");
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                long currentTimestamp = System.currentTimeMillis();
                String detalis = "Details for" + userId + " (obtained in "
                        + currentTimestamp + ")";
                UserData userData = new UserData(userId, detalis);

                cache.put(userId, userData);
                System.out.println("User data processing "
                        + "and caching completed " + userId + ".");
                return userData;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Failed to process"
                        + " request for user " + userId, e);
            }
        }, executor);

    }
}
