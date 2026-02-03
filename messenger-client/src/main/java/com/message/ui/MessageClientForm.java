package com.message.ui;

import com.message.ui.event.*;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class MessageClientForm {
    private JPanel panel;
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane scrollPane;

    private final Subject subject;
    public MessageClientForm(Subject subject) {

        this.subject = subject;

        frame = new JFrame();
        panel = new JPanel();
        messageArea = new JTextArea();
        inputField = new JTextField();
        sendButton = new JButton();
        scrollPane = new JScrollPane(messageArea);

        initializeUi();
        configureEvent();
        configureRecvObserver();
    }

    public JFrame getFrame() {
        return frame;
    }
    public JPanel getPanel() {
        return panel;
    }
    public JTextArea getMessageArea() {
        return messageArea;
    }
    public JTextField getInputField() {
        return inputField;
    }
    public JButton getSendButton() {
        return sendButton;
    }
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    public Subject getSubject() {
        return subject;
    }

    private void initializeUi(){

        messageArea.setEditable(false);
        //자동 줄바꿈, 가로 스크롤 생김 방지
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setRows(50);
        messageArea.setColumns(100);

        inputField.setColumns(100);

        sendButton.setText("Send");

        panel.setLayout(new BorderLayout());
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.setTitle("Message Client");
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 화면 크기 가져오기
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // 프레임 크기 가져오기
        Dimension frameSize = frame.getSize();
        // 프레임을 화면 가운데로 이동
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }

    private void configureEvent(){
        //sendButton : event
        sendButton.addActionListener(new SendButtonEventListener(this));
        //inputField : key event
        inputField.addKeyListener(new InputFieldKeyListener(this));
        //messageArea : text change event
        messageArea.getDocument().addDocumentListener(new MessageAreaChangeListener(this));
    }

    private void configureRecvObserver(){
        MessageAction messageAction = new RecvMessageAction(this);
        Observer observer = new MessageRecvObserver(messageAction);
        subject.register(EventType.RECV, observer);
    }

    public static void showUI(Subject subject){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MessageClientForm(subject);
            }
        });
    }
}