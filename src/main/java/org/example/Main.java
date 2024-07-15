package org.example;

import controller.UserInterfaceController;
import view.menu.LoginPage;

import javax.swing.*;
import java.awt.*;

import static controller.constants.UIConstants.DEFAULT_FONT_SIZE;
import static controller.constants.UIConstants.ORBITRON_FONT;
import static view.containers.GlassFrame.getGlassFrame;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                System.setProperty("sun.java2d.opengl", "true");
                System.setProperty("awt.useSystemAAFontSettings", "on");
                Runtime.getRuntime().addShutdownHook(new Thread(UserInterfaceController::safeExitApplication, "Shutdown-thread"));
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                UIManager.getLookAndFeelDefaults().put("defaultFont", ORBITRON_FONT.deriveFont(DEFAULT_FONT_SIZE.getValue()));
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {throw new AWTError("Failed to set AWT settings");}
            getGlassFrame();
            LoginPage.getINSTANCE().togglePanel();
        });
    }
}