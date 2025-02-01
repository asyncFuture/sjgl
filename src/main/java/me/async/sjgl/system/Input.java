package me.async.sjgl.system;

import java.awt.event.*;

public class Input {

    public static final int CURRENT_POS = 0;
    public static final int PREVIOUS_POS = 1;
    public static final int DISPLAY_VEC_POS = 2;

    private static boolean[] KEYS = new boolean[1024];
    private static boolean[] LAST_KEYS = new boolean[1024];

    private static final boolean[] BUTTONS = new boolean[4];
    private static final float[][] POS = new float[3][2];

    private static int[] WHEELS = new int[2];

    public static void update() {
        LAST_KEYS = KEYS.clone();

        WHEELS[1] = WHEELS[0];
        WHEELS[0] = 0;

        POS[DISPLAY_VEC_POS][0] = 0;
        POS[DISPLAY_VEC_POS][1] = 0;

        if (POS[PREVIOUS_POS][0] > 0 && POS[PREVIOUS_POS][1] > 0) {
            float deltaX = POS[CURRENT_POS][0] - POS[PREVIOUS_POS][0];
            float deltaY = POS[CURRENT_POS][1] - POS[PREVIOUS_POS][1];

            boolean rotateX = Math.abs(deltaX) > 1e-6;
            boolean rotateY = Math.abs(deltaY) > 1e-6;

            if (rotateX) POS[DISPLAY_VEC_POS][0] = deltaX;
            if (rotateY) POS[DISPLAY_VEC_POS][1] = deltaY;
        }

        POS[PREVIOUS_POS][0] = POS[CURRENT_POS][0];
        POS[PREVIOUS_POS][1] = POS[CURRENT_POS][1];
    }


    public static boolean isKey(int code) {
        return KEYS[code];
    }

    public static boolean isKeyDown(int code) {
        return KEYS[code] && !LAST_KEYS[code];
    }

    public static boolean isKeyUp(int code) {
        return !KEYS[code] && LAST_KEYS[code];
    }

    public static int inputX() {
        if (isKey(KeyEvent.VK_A)) return 1;
        if (isKey(KeyEvent.VK_D)) return -1;
        return 0;
    }

    public static int inputZ() {
        if (isKey(KeyEvent.VK_W)) return 1;
        if (isKey(KeyEvent.VK_S)) return -1;
        return 0;
    }

    public static int inputY() {
        if (isKey(KeyEvent.VK_SPACE)) return 1;
        if (isKey(KeyEvent.VK_SHIFT)) return -1;
        return 0;
    }

    public static boolean isButton(int code) {
        return BUTTONS[code];
    }

    public static float x() {
        return POS[CURRENT_POS][0];
    }

    public static float y() {
        return POS[CURRENT_POS][1];
    }

    public static float mouseX() {
        return POS[DISPLAY_VEC_POS][0];
    }

    public static float mouseY() {
        return POS[DISPLAY_VEC_POS][1];
    }

    public static float mouseX(int whenButton) {
        return isButton(whenButton) ? mouseX() : 0;
    }

    public static float mouseY(int whenButton) {
        return isButton(whenButton) ? mouseY() : 0;
    }

    public static int wheel() {
        return WHEELS[1];
    }

    public static class Key extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            KEYS[e.getKeyCode()] = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            KEYS[e.getKeyCode()] = false;
        }
    }

    public static class Mouse extends MouseAdapter {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            WHEELS[0] = e.getWheelRotation();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            POS[CURRENT_POS][0] = e.getX();
            POS[CURRENT_POS][1] = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            POS[CURRENT_POS][0] = e.getX();
            POS[CURRENT_POS][1] = e.getY();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            BUTTONS[e.getButton()] = true;

            POS[CURRENT_POS][0] = e.getX();
            POS[CURRENT_POS][1] = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            BUTTONS[e.getButton()] = false;

            POS[CURRENT_POS][0] = e.getX();
            POS[CURRENT_POS][1] = e.getY();
        }
    }
}