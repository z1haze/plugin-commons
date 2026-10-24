package me.z1haze.commons.update;

import me.z1haze.commons.util.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UpdateCheckerTest {
    private static final String NOTICE = "A new version of SpawnerShards is available: 2.1.0 (you have 2.0.0). https://www.spigotmc.org/resources/112220";

    private final List<String> console = new ArrayList<>();

    @BeforeEach
    void prefix() {
        ChatUtils.init(null, "[SpawnerShards]");
    }

    @Test
    void newerVersionLogsOneConsoleLine() {
        checked(() -> "2.1.0");

        assertEquals(List.of(NOTICE), console);
    }

    @Test
    void newerVersionNotifiesJoiningPlayersWithThePermission() {
        UpdateChecker checker = checked(() -> "2.1.0");
        Player player = player(true);

        checker.onJoin(join(player));

        verify(player).sendMessage("[SpawnerShards] " + NOTICE);
    }

    @Test
    void newerVersionNotifiesOnEveryJoin() {
        UpdateChecker checker = checked(() -> "2.1.0");
        Player player = player(true);

        checker.onJoin(join(player));
        checker.onJoin(join(player));

        verify(player, times(2)).sendMessage("[SpawnerShards] " + NOTICE);
    }

    @Test
    void newerVersionDoesNotNotifyPlayersWithoutThePermission() {
        UpdateChecker checker = checked(() -> "2.1.0");
        Player player = player(false);

        checker.onJoin(join(player));

        verify(player, never()).sendMessage(anyString());
    }

    @Test
    void sameVersionIsSilent() {
        assertSilent(checked(() -> "2.0.0"));
    }

    @Test
    void unparsableVersionIsSilent() {
        assertSilent(checked(() -> "<html>Error</html>"));
    }

    @Test
    void failedRequestIsSilent() {
        assertSilent(checked(() -> {
            throw new IOException("offline");
        }));
    }

    @Test
    void unexpectedFailureIsSilent() {
        assertSilent(checked(() -> {
            throw new IllegalStateException("broken client");
        }));
    }

    @Test
    void joinBeforeTheCheckFinishesIsSilent() {
        UpdateChecker checker = checker(() -> "2.1.0");
        Player player = player(true);

        checker.onJoin(join(player));

        verify(player, never()).sendMessage(anyString());
    }

    private void assertSilent(UpdateChecker checker) {
        Player player = player(true);

        checker.onJoin(join(player));

        assertEquals(List.of(), console);
        verify(player, never()).sendMessage(anyString());
    }

    private UpdateChecker checked(VersionSource source) {
        UpdateChecker checker = checker(source);
        checker.check();
        return checker;
    }

    private UpdateChecker checker(VersionSource source) {
        return new UpdateChecker(source, "SpawnerShards", "2.0.0", 112220, "spawnershards.notify", console::add);
    }

    private static Player player(boolean notify) {
        Player player = mock(Player.class);
        when(player.hasPermission("spawnershards.notify")).thenReturn(notify);
        return player;
    }

    private static PlayerJoinEvent join(Player player) {
        PlayerJoinEvent event = mock(PlayerJoinEvent.class);
        when(event.getPlayer()).thenReturn(player);
        return event;
    }
}
