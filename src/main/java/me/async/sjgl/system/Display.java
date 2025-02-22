package me.async.sjgl.system;

import me.async.sjgl.buffer.FrameBuffer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class Display {

    public static int BUFFERS = 2;

    private final Canvas canvas;
    private final Frame frame;

    private final FrameBuffer frameBuffer;

    private boolean requestClosing;

    public Display(String title, int width, int height) {
        this.canvas = new Canvas();

        Dimension dimension = new Dimension(width, height);
        this.canvas.setPreferredSize(dimension);
        this.canvas.setMinimumSize(dimension);
        this.canvas.setMinimumSize(dimension);

        this.canvas.addKeyListener(new Input.Key());

        Input.Mouse mouse = new Input.Mouse();
        this.canvas.addMouseListener(mouse);
        this.canvas.addMouseMotionListener(mouse);
        this.canvas.addMouseWheelListener(mouse);

        this.canvas.setFocusable(true);

        this.frame = new Frame(title);
        this.frame.add(canvas);
        this.frame.pack();

        this.frameBuffer = new FrameBuffer(width, height);

        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                requestClosing = true;
            }

            @Override
            public void windowClosed(WindowEvent e) {
                requestClosing = false;
            }
        });
    }

    public FrameBuffer frameBuffer() {
        return frameBuffer;
    }

    public void swap() {
        BufferStrategy strategy = canvas.getBufferStrategy();
        if (strategy == null) {
            canvas.createBufferStrategy(BUFFERS);
            strategy = canvas.getBufferStrategy();
        }
        Graphics graphics = strategy.getDrawGraphics();
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        graphics.drawImage(frameBuffer.buffer(), 0, 0, canvas);
        strategy.show();
    }

    public boolean sleep(long duration) {
        try {
            synchronized (this) {
                wait(duration);
                return false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    public void destroy() {
        frame.dispose();
    }

    public boolean isRequestClosing() {
        return requestClosing;
    }

    public int getWidth() {
        return canvas.getWidth();
    }

    public int getHeight() {
        return canvas.getHeight();
    }
}