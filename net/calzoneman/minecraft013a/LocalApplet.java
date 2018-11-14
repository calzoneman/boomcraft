package net.calzoneman.minecraft013a;

import com.mojang.minecraft.MinecraftApplet;

import java.awt.Dimension;
import java.io.File;
import java.net.URL;

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
}
