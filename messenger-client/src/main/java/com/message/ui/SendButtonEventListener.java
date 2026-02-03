package com.message.ui;

import com.message.ui.event.EventType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendButtonEventListener implements ActionListener {
    private final MessageClientForm form;

    public SendButtonEventListener(MessageClientForm form) {
        this.form = form;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = form.getInputField().getText();
        if (text != null && !text.trim().isEmpty()) {
            form.getSubject().notifyObservers(EventType.SEND, text);
            form.getInputField().setText("");
        }
    }
}
