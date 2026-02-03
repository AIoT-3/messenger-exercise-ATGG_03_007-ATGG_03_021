package com.message.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MessageAreaChangeListener implements DocumentListener {
    private final MessageClientForm form;

    public MessageAreaChangeListener(MessageClientForm form) {
        this.form = form;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        scrollToBottom();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        scrollToBottom();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        scrollToBottom();
    }

    private void scrollToBottom() {
        form.getMessageArea().setCaretPosition(form.getMessageArea().getDocument().getLength());
    }
}
