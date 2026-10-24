package me.z1haze.commons.update;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

final class SpigotVersionSource implements VersionSource {
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final URI uri;

    SpigotVersionSource(int resourceId) {
        this.uri = URI.create("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
    }

    @Override
    public String latest() throws IOException {
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(TIMEOUT).GET().build();
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(TIMEOUT).build()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new IOException("HTTP " + response.statusCode());
            return response.body().trim();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }
}
