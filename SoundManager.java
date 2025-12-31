package com.client;

import javax.sound.sampled.*;

public class SoundManager {

    private static boolean soundEnabled = true;

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void playSuccessSound() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Simple "ding" for success
                generateTone(600, 150); 
                Thread.sleep(50);
                generateTone(800, 250);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void playErrorSound() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Lower "buzz" for error
                generateTone(300, 300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void playNotificationSound() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Short "blip" for notifications
                generateTone(700, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void generateTone(int hz, int msecs) throws LineUnavailableException {
        float SAMPLE_RATE = 8000f;
        byte[] buf = new byte[1];
        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        for (int i = 0; i < msecs * 8; i++) {
            double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * 127.0 * 0.5); // 0.5 is volume
            sdl.write(buf, 0, 1);
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    }
}
