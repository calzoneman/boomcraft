package net.calzoneman.minecraft030;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class Launcher {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Minecraft Classic");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        LocalApplet applet = new LocalApplet();
        frame.add(applet);

        AtomicBoolean gameStartLatch = new AtomicBoolean(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent windowEvent) {
                if (!gameStartLatch.getAndSet(true)) {
                    System.out.println("Starting game");
                    startGame(applet);
                }
            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.out.println("Shutting down");

                applet.stop();

                try {
                    applet.destroy();
                } catch (Exception exception) {
                    System.out.println("destroy() failed");
                    exception.printStackTrace();
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private static void startGame(LocalApplet applet) {
        applet.init();

        System.out.println("Loading sounds");
        applet.loadSounds(Paths.get("resources"));

        if (System.getProperty("minecraft.loadlevel") != null) {
            System.out.println("Loading level from level.dat");
            applet.setLoadLevelFlag();
        }

        applet.start();

        if (System.getProperty("minecraft.admin") != null) {
            System.out.println("Setting player type to admin");
            applet.setPlayerAdmin();
        }
    }
}
