package com.message.ui.form;

import com.message.TypeManagement;
import com.message.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 로그인 화면 패널
 */
public class LoginPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(LoginPanel.class);

    private final MessageClientForm parentForm;
    private final Subject subject;

    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginPanel(MessageClientForm parentForm, Subject subject) {
        this.parentForm = parentForm;
        this.subject = subject;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(50, 50, 50, 50));
        setBackground(new Color(240, 240, 240));

        // 제목 패널
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // 입력 폼 패널
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // 하단 상태 패널
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Messenger", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        titleLabel.setForeground(new Color(51, 122, 183));

        JLabel subtitleLabel = new JLabel("로그인하여 대화를 시작하세요", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));

        // 사용자 ID 입력
        JLabel userIdLabel = new JLabel("사용자 ID");
        userIdLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        userIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userIdField = new JTextField(20);
        userIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        userIdField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        userIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        userIdField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 비밀번호 입력
        JLabel passwordLabel = new JLabel("비밀번호");
        passwordLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        loginButton.setBackground(new Color(51, 122, 183));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 이벤트 리스너 추가
        loginButton.addActionListener(this::performLogin);

        // Enter 키로 로그인
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin(null);
                }
            }
        };
        userIdField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // 패널에 컴포넌트 추가
        panel.add(userIdLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(userIdField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(30));
        panel.add(loginButton);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(240, 240, 240));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);

        panel.add(statusLabel);

        return panel;
    }

    private void performLogin(ActionEvent e) {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (userId.isEmpty()) {
            showError("사용자 ID를 입력해주세요.");
            userIdField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("비밀번호를 입력해주세요.");
            passwordField.requestFocus();
            return;
        }

        // 로그인 요청 전송
        setLoading(true);
        statusLabel.setText("로그인 중...");

        // 서버에 로그인 요청 전송
        // 형식: "LOGIN userId password"
        String loginCommand = TypeManagement.Auth.LOGIN + " " + userId + " " + password;
        log.debug("로그인 요청: {}", loginCommand);

        try {
            subject.sendMessage(loginCommand);
        } catch (Exception ex) {
            log.error("로그인 요청 실패", ex);
            showError("서버 연결에 실패했습니다.");
            setLoading(false);
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(192, 57, 43));
    }

    private void setLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        userIdField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        if (loading) {
            loginButton.setText("로그인 중...");
        } else {
            loginButton.setText("로그인");
        }
    }

    /**
     * 패널 초기화 (화면 전환 시 호출)
     */
    public void reset() {
        userIdField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        statusLabel.setForeground(Color.GRAY);
        setLoading(false);
        userIdField.requestFocus();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setLoading(!enabled);
    }
}