package com.message.ui.form;

import com.message.TypeManagement;
import com.message.session.ClientSession;
import com.message.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 채팅 화면 패널
 */
public class ChatPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(ChatPanel.class);

    private final MessageClientForm parentForm;
    private final Subject subject;

    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton logoutButton;
    private JLabel userInfoLabel;
    private StyledDocument chatDocument;

    // 스타일 정의
    private Style systemStyle;
    private Style myMessageStyle;
    private Style otherMessageStyle;
    private Style timestampStyle;
    private Style senderStyle;

    public ChatPanel(MessageClientForm parentForm, Subject subject) {
        this.parentForm = parentForm;
        this.subject = subject;
        initializeUI();
        initializeStyles();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 헤더
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 채팅 영역
        JScrollPane chatScrollPane = createChatArea();
        add(chatScrollPane, BorderLayout.CENTER);

        // 하단 메시지 입력 영역
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(51, 122, 183));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // 사용자 정보
        userInfoLabel = new JLabel("로그인 중...");
        userInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        userInfoLabel.setForeground(Color.WHITE);

        // 로그아웃 버튼
        logoutButton = new JButton("로그아웃");
        logoutButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(this::performLogout);

        panel.add(userInfoLabel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createChatArea() {
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatArea.setBackground(new Color(248, 249, 250));
        chatDocument = chatArea.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // 메시지 입력 필드
        messageField = new JTextField();
        messageField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Enter 키로 전송
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        // 전송 버튼
        sendButton = new JButton("전송");
        sendButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        sendButton.setBackground(new Color(51, 122, 183));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setOpaque(true);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(70, 40));
        sendButton.addActionListener(e -> sendMessage());

        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private void initializeStyles() {
        // 시스템 메시지 스타일
        systemStyle = chatArea.addStyle("system", null);
        StyleConstants.setForeground(systemStyle, new Color(108, 117, 125));
        StyleConstants.setItalic(systemStyle, true);
        StyleConstants.setAlignment(systemStyle, StyleConstants.ALIGN_CENTER);

        // 내 메시지 스타일
        myMessageStyle = chatArea.addStyle("myMessage", null);
        StyleConstants.setForeground(myMessageStyle, new Color(0, 123, 255));

        // 다른 사람 메시지 스타일
        otherMessageStyle = chatArea.addStyle("otherMessage", null);
        StyleConstants.setForeground(otherMessageStyle, new Color(33, 37, 41));

        // 타임스탬프 스타일
        timestampStyle = chatArea.addStyle("timestamp", null);
        StyleConstants.setForeground(timestampStyle, new Color(173, 181, 189));
        StyleConstants.setFontSize(timestampStyle, 11);

        // 발신자 스타일
        senderStyle = chatArea.addStyle("sender", null);
        StyleConstants.setBold(senderStyle, true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        // 메시지 전송
        // 형식: "CHAT-MESSAGE message"
        String chatCommand = TypeManagement.Chat.MESSAGE + " " + message;
        log.debug("메시지 전송: {}", chatCommand);

        try {
            subject.sendMessage(chatCommand);
            // 내가 보낸 메시지는 즉시 표시
            appendMessage(ClientSession.getUserId(), message, getCurrentTimestamp());
            messageField.setText("");
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
            appendSystemMessage("메시지 전송에 실패했습니다.");
        }
    }

    private void performLogout(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(
            parentForm,
            "로그아웃 하시겠습니까?",
            "로그아웃",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            // 로그아웃 요청 전송
            String logoutCommand = TypeManagement.Auth.LOGOUT;
            log.debug("로그아웃 요청: {}", logoutCommand);

            try {
                subject.sendMessage(logoutCommand);
            } catch (Exception ex) {
                log.error("로그아웃 요청 실패", ex);
                parentForm.showError("LOGOUT_ERROR", "로그아웃 요청에 실패했습니다.");
            }
        }
    }

    /**
     * 일반 채팅 메시지 추가
     */
    public void appendMessage(String sender, String content, String timestamp) {
        try {
            String currentUser = ClientSession.getUserId();
            boolean isMyMessage = sender != null && sender.equals(currentUser);

            // 발신자
            chatDocument.insertString(chatDocument.getLength(), sender + ": ", senderStyle);

            // 메시지 내용
            Style messageStyle = isMyMessage ? myMessageStyle : otherMessageStyle;
            chatDocument.insertString(chatDocument.getLength(), content, messageStyle);

            // 타임스탬프
            chatDocument.insertString(chatDocument.getLength(), " [" + timestamp + "]", timestampStyle);

            // 줄바꿈
            chatDocument.insertString(chatDocument.getLength(), "\n", null);

            // 스크롤 맨 아래로
            chatArea.setCaretPosition(chatDocument.getLength());
        } catch (BadLocationException e) {
            log.error("메시지 표시 실패", e);
        }
    }

    /**
     * 시스템 메시지 추가
     */
    public void appendSystemMessage(String message) {
        try {
            chatDocument.insertString(chatDocument.getLength(), "--- " + message + " ---\n", systemStyle);
            chatArea.setCaretPosition(chatDocument.getLength());
        } catch (BadLocationException e) {
            log.error("시스템 메시지 표시 실패", e);
        }
    }

    /**
     * 환영 메시지 설정
     */
    public void setWelcomeMessage(String message) {
        appendSystemMessage(message);
    }

    /**
     * 화면 표시 시 호출
     */
    public void onShow() {
        String userId = ClientSession.getUserId();
        if (userId != null) {
            userInfoLabel.setText(userId + "님, 환영합니다!");
        }
        messageField.requestFocus();
    }

    /**
     * 채팅 영역 초기화
     */
    public void clearChat() {
        try {
            chatDocument.remove(0, chatDocument.getLength());
        } catch (BadLocationException e) {
            log.error("채팅 영역 초기화 실패", e);
        }
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}