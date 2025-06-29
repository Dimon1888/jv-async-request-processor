package mate.academy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Feel free to play with AsyncRequestProcessor in this main method if you want
        ExecutorService executor = Executors.newFixedThreadPool(5);
        // Provide implementation that fits your needs
        AsyncRequestProcessor asyncRequestProcessor = new AsyncRequestProcessor(executor);

        // Simulating multiple concurrent requests
        String[] userIds = {"user1", "user2", "user3", "user1"}; // Note: "user1" is repeated
        CompletableFuture<?>[] futures = new CompletableFuture[userIds.length];

        for (int i = 0; i < userIds.length; i++) {
            String userId = userIds[i];
            futures[i] = asyncRequestProcessor.processRequest(userId)
                    .thenAccept(userData -> System.out.println("Processed: " + userData));
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures).join();

        System.out.println("\n-- All initial requests have"
                + " been processed. Resubmitting a request ---");
        asyncRequestProcessor.processRequest("user1")
                .thenAccept(userData -> System.out.println(" Repeated request"
                        + " for user1: " + userData))
                .join();

        executor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("\nThe application has completed its work. ");
    }
}
