package com.sparta.newspeed.email;

import com.sparta.newspeed.dto.UserReqDto;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.entity.UserStatusEnum;
import com.sparta.newspeed.repository.UserRepository;
import com.sparta.newspeed.security.UserDetailsImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private static final String EMAIL = "ejong12311@gmail.com";
    private static int number;
    private final UserRepository userRepository;

    public static void createNumber() {
        number = (int) (Math.random() * (90000)) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public MimeMessage createMail(String mail) {
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(EMAIL);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    public int sendMail(String mail) {
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);

        return number;
    }

    @Transactional
    public String checkCode(UserReqDto userReqDto, UserDetailsImpl userDetails) {
        User user = userRepository
                .findByEmail(userDetails.getUser().getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        LocalDateTime now = LocalDateTime.now();

        String answer = null;

        switch (user.getUserStatus()) {
            case VERIFIED -> answer = "이미 인증이 완료 되었습니다.";
            case NOTVERIFIED -> {
                if (now.isAfter(user.getDepartureTime().plusSeconds(180))) {
                    sendMail(user.getEmail());
                    user.setDepartureTime(now);
                    answer = "인증 시간이 초과하였습니다. 다시 인증을 진행해주세요.";
                } else if (number != Integer.parseInt((userReqDto.getPassword()))) {
                    answer = "인증번호가 일치하지 않습니다.";
                } else {
                    user.setUserStatus(UserStatusEnum.VERIFIED);
                    answer = "인증이 완료되었습니다.";
                }

            }
        }
        return answer;
    }
}




