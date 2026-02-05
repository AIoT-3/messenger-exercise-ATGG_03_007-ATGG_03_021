package com.message.ui.form;

import com.message.TypeManagement;
import com.message.dto.data.impl.RoomDto;
import com.message.dto.data.impl.UserDto;
import com.message.session.ClientSession;
import com.message.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    // 사이드바 컴포넌트
    private JList<RoomDto.RoomSummary> roomList;
    private DefaultListModel<RoomDto.RoomSummary> roomListModel;
    private JList<UserDto.UserInfo> userList;
    private DefaultListModel<UserDto.UserInfo> userListModel;

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

        // 중앙: 사이드바 + 채팅 영역
        JSplitPane splitPane = createMainArea();
        add(splitPane, BorderLayout.CENTER);

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

    private JSplitPane createMainArea() {
        // 왼쪽 사이드바
        JPanel sidebarPanel = createSidebarPanel();

        // 오른쪽 채팅 영역
        JScrollPane chatScrollPane = createChatArea();

        // 분할 패널
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, chatScrollPane);
        splitPane.setDividerLocation(180);
        splitPane.setDividerSize(3);
        splitPane.setContinuousLayout(true);

        return splitPane;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new GridLayout(2, 1, 0, 5));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 채팅방 목록 패널
        JPanel roomPanel = createRoomListPanel();
        sidebar.add(roomPanel);

        // 접속 유저 목록 패널
        JPanel userPanel = createUserListPanel();
        sidebar.add(userPanel);

        return sidebar;
    }

    private JPanel createRoomListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "채팅방",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("맑은 고딕", Font.BOLD, 12),
            new Color(51, 122, 183)
        ));

        // 채팅방 목록
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer(new RoomListCellRenderer());
        roomList.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(roomList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 채팅방 생성 버튼
        JButton createRoomButton = new JButton("+ 방 만들기");
        createRoomButton.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        createRoomButton.setBackground(new Color(51, 122, 183));
        createRoomButton.setForeground(Color.WHITE);
        createRoomButton.setFocusPainted(false);
        createRoomButton.setBorderPainted(false);
        createRoomButton.setOpaque(true);
        createRoomButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createRoomButton.addActionListener(e -> showCreateRoomDialog());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(createRoomButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 채팅방 생성 다이얼로그 표시
     */
    private void showCreateRoomDialog() {
        String roomName = JOptionPane.showInputDialog(
            parentForm,
            "채팅방 이름을 입력하세요:",
            "채팅방 생성",
            JOptionPane.PLAIN_MESSAGE
        );

        if (roomName != null && !roomName.trim().isEmpty()) {
            createRoom(roomName.trim());
        }
    }

    /**
     * 채팅방 생성 요청
     */
    private void createRoom(String roomName) {
        log.debug("채팅방 생성 요청 - roomName: {}", roomName);

        try {
            String createCommand = TypeManagement.Room.CREATE + " " + roomName;
            subject.sendMessage(createCommand);
        } catch (Exception e) {
            log.error("채팅방 생성 요청 실패", e);
            JOptionPane.showMessageDialog(
                parentForm,
                "채팅방 생성에 실패했습니다.",
                "오류",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel createUserListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "접속 유저",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("맑은 고딕", Font.BOLD, 12),
            new Color(46, 204, 113)
        ));

        // 유저 목록
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new UserListCellRenderer());
        userList.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

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

        // 서버에 채팅방 목록 및 유저 목록 요청
        requestInitialData();
    }

    /**
     * 서버에 초기 데이터 요청 (채팅방 목록, 유저 목록)
     */
    private void requestInitialData() {
        log.debug("초기 데이터 요청 - 채팅방 목록, 유저 목록");

        try {
            // 채팅방 목록 요청
            subject.sendMessage(TypeManagement.Room.LIST);

            // 유저 목록 요청
            subject.sendMessage(TypeManagement.User.LIST);
        } catch (Exception e) {
            log.error("초기 데이터 요청 실패", e);
        }
    }

    /**
     * 채팅방 목록 업데이트
     */
    public void updateRoomList(List<RoomDto.RoomSummary> rooms) {
        SwingUtilities.invokeLater(() -> {
            roomListModel.clear();
            if (rooms != null) {
                for (RoomDto.RoomSummary room : rooms) {
                    roomListModel.addElement(room);
                }
            }
            log.debug("채팅방 목록 업데이트 완료 - {} 개의 방", rooms != null ? rooms.size() : 0);
        });
    }

    /**
     * 유저 목록 업데이트
     */
    public void updateUserList(List<UserDto.UserInfo> users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            if (users != null) {
                for (UserDto.UserInfo user : users) {
                    userListModel.addElement(user);
                }
            }
            log.debug("유저 목록 업데이트 완료 - {} 명의 유저", users != null ? users.size() : 0);
        });
    }

    /**
     * 채팅방 생성 성공 처리
     */
    public void onRoomCreated(long roomId, String roomName) {
        SwingUtilities.invokeLater(() -> {
            appendSystemMessage("채팅방 '" + roomName + "'이(가) 생성되었습니다.");
            // 채팅방 목록 새로고침 요청
            try {
                subject.sendMessage(TypeManagement.Room.LIST);
            } catch (Exception e) {
                log.error("채팅방 목록 새로고침 실패", e);
            }
        });
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

    /**
     * 채팅방 목록 셀 렌더러
     */
    private static class RoomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof RoomDto.RoomSummary room) {
                setText(String.format("%s (%d명)", room.roomName(), room.userCount()));
                setIcon(null);
            }

            setBorder(new EmptyBorder(5, 8, 5, 8));
            return this;
        }
    }

    /**
     * 유저 목록 셀 렌더러
     */
    private static class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof UserDto.UserInfo user) {
                String status = user.online() ? "온라인" : "오프라인";
                setText(String.format("%s (%s)", user.name(), status));

                // 온라인 상태에 따라 색상 변경
                if (!isSelected) {
                    setForeground(user.online() ? new Color(46, 125, 50) : Color.GRAY);
                }
            }

            setBorder(new EmptyBorder(5, 8, 5, 8));
            return this;
        }
    }
}
