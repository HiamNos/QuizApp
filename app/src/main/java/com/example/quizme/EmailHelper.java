package com.example.quizme;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHelper {
    private static final String TAG = "EmailHelper";
    
    // Gmail SMTP credentials
    private static final String FROM_EMAIL = "mainkaisa19@gmail.com";
    private static final String FROM_PASSWORD = "ltjp dfms dwkl gcqp";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public interface EmailCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void sendResetCodeEmail(String toEmail, String resetCode, EmailCallback callback) {
        String subject = "Mã xác nhận đặt lại mật khẩu - QuizMe";
        String body = "Xin chào!\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản QuizMe.\n\n" +
                "Mã xác nhận của bạn là: **" + resetCode + "**\n\n" +
                "Mã này có hiệu lực trong 10 phút.\n\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ QuizMe";
        
        new SendEmailTask(toEmail, subject, body, callback).execute();
    }

    public static void sendVerificationCodeEmail(String toEmail, String verificationCode, EmailCallback callback) {
        String subject = "Mã xác nhận đăng ký - QuizMe";
        String body = "Xin chào!\n\n" +
                "Cảm ơn bạn đã đăng ký tài khoản QuizMe.\n\n" +
                "Mã xác nhận của bạn là: **" + verificationCode + "**\n\n" +
                "Vui lòng nhập mã này để hoàn tất quá trình đăng ký.\n\n" +
                "Mã này có hiệu lực trong 10 phút.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ QuizMe";
        
        // For testing purposes, show the code in log
        Log.d(TAG, "=== TEST MODE ===");
        Log.d(TAG, "Verification code: " + verificationCode);
        Log.d(TAG, "Would send to: " + toEmail);
        Log.d(TAG, "Subject: " + subject);
        Log.d(TAG, "Body: " + body);
        Log.d(TAG, "=== END TEST MODE ===");
        
        new SendEmailTask(toEmail, subject, body, callback).execute();
    }

    private static class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        private String toEmail;
        private String subject;
        private String body;
        private String errorMessage;
        private EmailCallback callback;

        public SendEmailTask(String toEmail, String subject, String body, EmailCallback callback) {
            this.toEmail = toEmail;
            this.subject = subject;
            this.body = body;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Log.d(TAG, "Starting email send process...");
                Log.d(TAG, "To: " + toEmail);
                Log.d(TAG, "Subject: " + subject);
                
                // Configure Gmail SMTP properties
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.ssl.trust", SMTP_HOST);
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                Log.d(TAG, "SMTP Properties configured");

                // Create session with authentication
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        Log.d(TAG, "Authenticating with Gmail...");
                        return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
                    }
                });

                Log.d(TAG, "Session created successfully");

                // Create message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(FROM_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);

                Log.d(TAG, "Message created, attempting to send...");

                // Send message
                Transport.send(message);
                
                Log.d(TAG, "Email sent successfully to: " + toEmail);
                return true;
                
            } catch (MessagingException e) {
                Log.e(TAG, "MessagingException: " + e.getMessage());
                Log.e(TAG, "Exception type: " + e.getClass().getSimpleName());
                errorMessage = "Lỗi gửi email: " + e.getMessage();
                return false;
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error: " + e.getMessage());
                Log.e(TAG, "Exception type: " + e.getClass().getSimpleName());
                e.printStackTrace();
                errorMessage = "Lỗi không xác định: " + e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d(TAG, "Email task completed successfully");
                callback.onSuccess();
            } else {
                Log.e(TAG, "Email task failed: " + errorMessage);
                callback.onFailure(errorMessage);
            }
        }
    }
} 