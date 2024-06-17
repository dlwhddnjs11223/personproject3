package com.sparta.newspeed.service;

import com.sparta.newspeed.dto.PeedRequestDto;
import com.sparta.newspeed.dto.PeedResponseDto;
import com.sparta.newspeed.entity.Peed;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.exception.SuccessHandler;
import com.sparta.newspeed.repository.PeedRepository;
import com.sparta.newspeed.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PeedService {
    private final PeedRepository peedRepository;
    private final UserService userService;

    public PeedService(PeedRepository peedRepository, UserService userService) {
        this.peedRepository = peedRepository;
        this.userService = userService;
    }

    public PeedResponseDto createPeed(PeedRequestDto requestDto, UserDetailsImpl userDetailsImpl) {
        String email = userDetailsImpl.getUser().getEmail();
        User user = userService.findUserByEmail(email);

        Peed peed = new Peed(requestDto, user);
        peedRepository.save(peed);
        return new PeedResponseDto(peed);

    }

    @Transactional
    public PeedResponseDto updatePeed(Long id, PeedRequestDto requestDto, User user) {

        Peed peed = findPeed(id);
        if(!peed.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("사용자가 일치하지 않습니다.");
        }
        // 작성자만 수정 삭제 가능하게 예외처리
        peed.update(requestDto);
        return new PeedResponseDto(peed);

    }

    public Long deletePeed(Long peed_id, PeedRequestDto requestDto) {
        Peed peed = findPeed(peed_id);
        // 작성자만 수정 삭제 가능하게 예외처리
        peedRepository.delete(peed);
        return peed_id;
    }

    public Page<PeedResponseDto> getAllPeeds(int page, int size, String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);


        Page<Peed> peeds = peedRepository.findAll(pageable);
        if (peeds.isEmpty()) {
            throw new SuccessHandler("먼저 작성하여 소식을 알려보세요!", HttpServletResponse.SC_OK);
        }
        return peeds.map(PeedResponseDto::new);
    }

    public Peed findPeed(Long id) {
        return peedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("peed not found"));
    }
}
