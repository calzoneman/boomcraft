package net.calzoneman.minecraft030;

import com.mojang.minecraft.MinecraftApplet;

import java.awt.Dimension;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.Thread;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

class LocalApplet extends MinecraftApplet {
    @Override
    public Dimension getPreferredSize() {
        // Taken from the original website
        return new Dimension(854, 480);
    }

    @Override
    public String getParameter(String param) {
        // Multiplayer params: username, sessionid, server, port, mppass
        return System.getProperty("minecraft.param." + param);
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URL("http://minecraft.net:80/");
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public URL getDocumentBase() {
        try {
            return new URL("http://minecraft.net:80/play.jsp");
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private com.mojang.minecraft.l getMinecraft() {
        try {
            Field minecraft = MinecraftApplet.class.getDeclaredField("minecraft");
            minecraft.setAccessible(true);

            return (com.mojang.minecraft.l) minecraft.get(this);
        } catch (Exception exception) {
            throw new RuntimeException("Could not reflect game instance", exception);
        }
    }

    void loadSounds(Path base) {
        com.mojang.minecraft.l minecraft = getMinecraft();
        File[] sfx = base.resolve(Paths.get("sounds", "step")).toFile().listFiles();

        if (sfx == null) {
            throw new IllegalArgumentException("Could not find sfx in " + base);
        }

        for (File effect : sfx) {
            minecraft.s.a(effect, String.format("step/%s", effect.getName()));
        }

        File[] music = base.resolve("music").toFile().listFiles();

        if (music == null) {
            throw new IllegalArgumentException("Could not find music in " + base);
        }

        for (File track : music) {
            minecraft.s.a(track.getName(), track);
        }
    }

    void setLoadLevelFlag() {
        if (!new File("level.dat").exists()) {
            throw new IllegalStateException("Cannot load level: level.dat does not exist");
        }

        getMinecraft().k = false;
    }

    void setPlayerAdmin() {
        com.mojang.minecraft.l minecraft = getMinecraft();

        // The player object is initialized by the game thread, so we need to wait.
        // The simple/hacky way is to just sleep in a loop until it's ready.
        while (minecraft.f == null) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }

        minecraft.f.userType = 100;
    }
}
