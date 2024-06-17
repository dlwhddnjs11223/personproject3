package com.sparta.newspeed.controller;

import com.sparta.newspeed.dto.PeedRequestDto;
import com.sparta.newspeed.dto.PeedResponseDto;
import com.sparta.newspeed.security.UserDetailsImpl;
import com.sparta.newspeed.service.PeedService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api")
public class PeedController {

    private final PeedService peedService;

    public PeedController(PeedService peedService) {
        this.peedService = peedService;
    }

    @Operation(summary = "새로운 피드 작성", description = "새로운 피드를 작성합니다.")
    @PostMapping("/peeds")
    public PeedResponseDto createSchedule(@RequestBody PeedRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return peedService.createPeed(requestDto, userDetailsImpl);
    }


    @Operation(summary = "모든 피드 조회", description = "모든 피드를 조회합니다.")
    @GetMapping("/peeds")
    public Page<PeedResponseDto> getAllPeeds(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc

    ) {
        return peedService.getAllPeeds(page - 1, size, sortBy, isAsc);
    }

    @Operation(summary = "피드 수정", description = "피드를 수정합니다.")
    @PutMapping("/peeds/{id}")
    public PeedResponseDto updateMemo(@PathVariable Long id,
                                      @RequestBody PeedRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return peedService.updatePeed(id, requestDto, userDetailsImpl.getUser());
    }

    @Operation(summary = "피드 삭제", description = "피드를 삭제합니다.")
    @DeleteMapping("/peeds/{id}")
    public Long deleteMemo(@PathVariable Long id, @RequestBody PeedRequestDto requestDto) {
        return peedService.deletePeed(id, requestDto);
    }


}
