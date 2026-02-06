package com.message.ui.form;

import com.message.TypeManagement;
import com.message.dto.data.impl.ChatDto;
import com.message.session.ClientSession;
import com.message.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 귓속말 다이얼로그
 * 특정 유저와의 귓속말 기록 표시 및 전송
 */
public class PrivateMessageDialog extends JDialog {
    private static final Logger log = LoggerFactory.getLogger(PrivateMessageDialog.class);

    // 열려있는 다이얼로그 관리 (userId -> dialog)
    private static final Map<String, PrivateMessageDialog> openDialogs = new ConcurrentHashMap<>();

    private final String targetUserId;
    private final String targetUserName;
    private final Subject subject;

    private JTextPane chatArea;
    private StyledDocument chatDocument;
    private JTextField messageField;
    private JButton sendButton;

    // 스타일
    private Style myMessageStyle;
    private Style otherMessageStyle;
    private Style timestampStyle;
    private Style senderStyle;
    private Style systemStyle;

    public PrivateMessageDialog(Frame parent, String targetUserId, String targetUserName, Subject subject) {
        super(parent, targetUserName + "님과의 귓속말", false);
        this.targetUserId = targetUserId;
        this.targetUserName = targetUserName;
        this.subject = subject;

        initializeUI();
        initializeStyles();
        registerDialog();
        requestHistory();
    }

    private void initializeUI() {
        setSize(400, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // 헤더
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 채팅 영역
        JScrollPane chatScrollPane = createChatArea();
        add(chatScrollPane, BorderLayout.CENTER);

        // 입력 영역
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.SOUTH);

        // 창 닫힐 때 맵에서 제거
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                unregisterDialog();
            }
        });
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(155, 89, 182));
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel titleLabel = new JLabel(targetUserName + "님과의 귓속말");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JScrollPane createChatArea() {
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        chatArea.setBackground(new Color(248, 249, 250));
        chatDocument = chatArea.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(8, 10, 8, 10));

        messageField = new JTextField();
        messageField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        sendButton = new JButton("전송");
        sendButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        sendButton.setBackground(new Color(155, 89, 182));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setOpaque(true);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(80, 35)); // TODO 수정사항 - 기존 (60, 35)
        sendButton.addActionListener(e -> sendMessage());

        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private void initializeStyles() {
        // 내 메시지 스타일
        myMessageStyle = chatArea.addStyle("myMessage", null);
        StyleConstants.setForeground(myMessageStyle, new Color(155, 89, 182));

        // 상대방 메시지 스타일
        otherMessageStyle = chatArea.addStyle("otherMessage", null);
        StyleConstants.setForeground(otherMessageStyle, new Color(33, 37, 41));

        // 타임스탬프 스타일
        timestampStyle = chatArea.addStyle("timestamp", null);
        StyleConstants.setForeground(timestampStyle, new Color(173, 181, 189));
        StyleConstants.setFontSize(timestampStyle, 10);

        // 발신자 스타일
        senderStyle = chatArea.addStyle("sender", null);
        StyleConstants.setBold(senderStyle, true);

        // 시스템 스타일
        systemStyle = chatArea.addStyle("system", null);
        StyleConstants.setForeground(systemStyle, new Color(108, 117, 125));
        StyleConstants.setItalic(systemStyle, true);
    }

    private void registerDialog() {
        openDialogs.put(targetUserId, this);
    }

    private void unregisterDialog() {
        openDialogs.remove(targetUserId);
    }

    /**
     * 귓속말 기록 요청
     */
    private void requestHistory() {
        log.debug("귓속말 기록 요청 - targetId: {}", targetUserId);
        try {
            String historyCommand = TypeManagement.Chat.PRIVATE_HISTORY + " " + targetUserId;
            subject.sendMessage(historyCommand);
            appendSystemMessage("기록을 불러오는 중...");
        } catch (Exception e) {
            log.error("귓속말 기록 요청 실패", e);
            appendSystemMessage("기록을 불러오지 못했습니다.");
        }
    }

    /**
     * 메시지 전송
     */
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        log.debug("귓속말 전송 - to: {}, message: {}", targetUserId, message);

        try {
            String privateCommand = TypeManagement.Chat.PRIVATE + " " + targetUserId + " " + message;
            subject.sendMessage(privateCommand);

            // 내가 보낸 메시지 표시
            appendMessage(ClientSession.getUserId(), "나", message, getCurrentTimestamp(), true);
            messageField.setText("");
        } catch (Exception e) {
            log.error("귓속말 전송 실패", e);
            appendSystemMessage("메시지 전송에 실패했습니다.");
        }
    }

    /**
     * 메시지 추가
     */
    public void appendMessage(String senderId, String senderName, String content, String timestamp, boolean isMyMessage) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 발신자
                String displayName = isMyMessage ? "나" : senderName;
                chatDocument.insertString(chatDocument.getLength(), displayName + ": ", senderStyle);

                // 메시지 내용
                Style messageStyle = isMyMessage ? myMessageStyle : otherMessageStyle;
                chatDocument.insertString(chatDocument.getLength(), content, messageStyle);

                // 타임스탬프
                chatDocument.insertString(chatDocument.getLength(), " [" + timestamp + "]", timestampStyle);

                // 줄바꿈
                chatDocument.insertString(chatDocument.getLength(), "\n", null);

                // 스크롤
                chatArea.setCaretPosition(chatDocument.getLength());
            } catch (BadLocationException e) {
                log.error("메시지 표시 실패", e);
            }
        });
    }

    /**
     * 시스템 메시지 추가
     */
    public void appendSystemMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                chatDocument.insertString(chatDocument.getLength(), "--- " + message + " ---\n", systemStyle);
                chatArea.setCaretPosition(chatDocument.getLength());
            } catch (BadLocationException e) {
                log.error("시스템 메시지 표시 실패", e);
            }
        });
    }

    /**
     * 채팅 영역 초기화
     */
    public void clearChat() {
        SwingUtilities.invokeLater(() -> {
            try {
                chatDocument.remove(0, chatDocument.getLength());
            } catch (BadLocationException e) {
                log.error("채팅 영역 초기화 실패", e);
            }
        });
    }

    /**
     * 귓속말 기록 수신 처리
     */
    public void onHistoryReceived(List<ChatDto.PrivateRequest> messages, boolean hasMore) {
        SwingUtilities.invokeLater(() -> {
            clearChat();

            if (messages != null && !messages.isEmpty()) {
                // 메시지를 시간순으로 표시
                for (ChatDto.PrivateRequest msg : messages) {
                    boolean isMyMessage = msg.senderId().equals(ClientSession.getUserId());
                    String senderName = isMyMessage ? "나" : targetUserName;
                    appendMessage(msg.senderId(), senderName, msg.message(), "이전", isMyMessage);
                }
                appendSystemMessage("대화 기록 로드 완료");
            } else {
                appendSystemMessage("이전 대화 기록이 없습니다.");
            }
        });
    }

    /**
     * 수신된 귓속말 추가 (실시간)
     */
    public void onMessageReceived(String senderId, String message, String timestamp) {
        boolean isMyMessage = senderId.equals(ClientSession.getUserId());
        String senderName = isMyMessage ? "나" : targetUserName;
        appendMessage(senderId, senderName, message, timestamp, isMyMessage);

        // 다이얼로그가 보이지 않으면 포커스
        if (!isVisible()) {
            setVisible(true);
        }
        toFront();
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // === 정적 메서드 ===

    /**
     * 특정 유저와의 다이얼로그 열기 또는 가져오기
     */
    public static PrivateMessageDialog openOrGet(Frame parent, String userId, String userName, Subject subject) {
        PrivateMessageDialog dialog = openDialogs.get(userId);
        if (dialog == null || !dialog.isDisplayable()) {
            dialog = new PrivateMessageDialog(parent, userId, userName, subject);
        }
        dialog.setVisible(true);
        dialog.toFront();
        dialog.messageField.requestFocus();
        return dialog;
    }

    /**
     * 특정 유저와의 열린 다이얼로그 가져오기
     */
    public static PrivateMessageDialog getOpenDialog(String userId) {
        return openDialogs.get(userId);
    }

    /**
     * 특정 유저와의 다이얼로그가 열려있는지 확인
     */
    public static boolean isDialogOpen(String userId) {
        PrivateMessageDialog dialog = openDialogs.get(userId);
        return dialog != null && dialog.isDisplayable();
    }

    /**
     * 모든 열린 다이얼로그 닫기
     */
    public static void closeAll() {
        for (PrivateMessageDialog dialog : openDialogs.values()) {
            dialog.dispose();
        }
        openDialogs.clear();
    }
}