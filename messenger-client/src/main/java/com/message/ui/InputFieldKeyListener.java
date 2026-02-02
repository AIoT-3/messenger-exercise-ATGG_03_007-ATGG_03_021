package com.message.ui;

import com.message.ui.event.EventType;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputFieldKeyListener implements KeyListener {
    private final MessageClientForm form;

    public InputFieldKeyListener(MessageClientForm form) {
        this.form = form;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String text = form.getInputField().getText();
            if (text != null && !text.trim().isEmpty()) {
                form.getSubject().notifyObservers(EventType.SEND, text);
                form.getInputField().setText("");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
