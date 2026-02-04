package com.message.ui.form;

import com.message.dto.data.impl.RoomDto;
import com.message.dto.data.impl.UserDto;
import com.message.session.ClientSession;
import com.message.subject.Subject;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 메신저 클라이언트의 메인 UI 폼
 * CardLayout을 사용하여 로그인 화면과 채팅 화면을 전환
 */
public class MessageClientForm extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(MessageClientForm.class);

    private static final String LOGIN_PANEL = "LOGIN";
    private static final String CHAT_PANEL = "CHAT";

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    @Getter
    private final LoginPanel loginPanel;
    @Getter
    private final ChatPanel chatPanel;

    private final Subject subject;

    public MessageClientForm(Subject subject) {
        this.subject = subject;
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        // 패널 초기화
        this.loginPanel = new LoginPanel(this, subject);
        this.chatPanel = new ChatPanel(this, subject);

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Messenger Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // 카드 레이아웃에 패널 추가
        mainPanel.add(loginPanel, LOGIN_PANEL);
        mainPanel.add(chatPanel, CHAT_PANEL);

        add(mainPanel);

        // 초기 화면은 로그인
        showLoginPanel();
    }

    /**
     * 로그인 화면으로 전환
     */
    public void showLoginPanel() {
        cardLayout.show(mainPanel, LOGIN_PANEL);
        loginPanel.reset();
        log.debug("로그인 화면으로 전환");
    }

    /**
     * 채팅 화면으로 전환
     */
    public void showChatPanel() {
        cardLayout.show(mainPanel, CHAT_PANEL);
        chatPanel.onShow();
        log.debug("채팅 화면으로 전환");
    }

    /**
     * 로그인 성공 처리
     */
    public void onLoginSuccess(String userId, String sessionId, String message) {
        SwingUtilities.invokeLater(() -> {
            ClientSession.setUserId(userId);
            ClientSession.setSessionId(sessionId);
            chatPanel.setWelcomeMessage(message);
            showChatPanel();
            JOptionPane.showMessageDialog(this, message, "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * 로그인 실패 처리
     */
    public void onLoginFailed(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            loginPanel.setEnabled(true);
            JOptionPane.showMessageDialog(this, errorMessage, "로그인 실패", JOptionPane.ERROR_MESSAGE);
        });
    }

    /**
     * 로그아웃 성공 처리
     */
    public void onLogoutSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            ClientSession.clear();
            showLoginPanel();
            JOptionPane.showMessageDialog(this, message, "로그아웃", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * 에러 메시지 표시
     */
    public void showError(String code, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                String.format("[%s] %s", code, message),
                "오류",
                JOptionPane.ERROR_MESSAGE);
        });
    }

    /**
     * 채팅 메시지 추가
     */
    public void appendChatMessage(String sender, String content, String timestamp) {
        SwingUtilities.invokeLater(() -> {
            chatPanel.appendMessage(sender, content, timestamp);
        });
    }

    /**
     * 시스템 메시지 추가
     */
    public void appendSystemMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatPanel.appendSystemMessage(message);
        });
    }

    /**
     * 채팅방 목록 업데이트
     */
    public void updateRoomList(List<RoomDto.RoomSummary> rooms) {
        SwingUtilities.invokeLater(() -> {
            chatPanel.updateRoomList(rooms);
        });
    }

    /**
     * 유저 목록 업데이트
     */
    public void updateUserList(List<UserDto.UserInfo> users) {
        SwingUtilities.invokeLater(() -> {
            chatPanel.updateUserList(users);
        });
    }

    /**
     * 채팅방 생성 성공 처리
     */
    public void onRoomCreated(long roomId, String roomName) {
        SwingUtilities.invokeLater(() -> {
            chatPanel.onRoomCreated(roomId, roomName);
        });
    }

    /**
     * UI 표시
     */
    public static MessageClientForm showUI(Subject subject) {
        MessageClientForm form = new MessageClientForm(subject);
        form.setVisible(true);
        return form;
    }
}
