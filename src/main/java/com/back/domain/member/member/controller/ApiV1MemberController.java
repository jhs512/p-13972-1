package com.back.domain.member.member.controller;


import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;


    record MemberJoinReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,
            @NotBlank
            @Size(min = 2, max = 30)
            String password,
            @NotBlank
            @Size(min = 2, max = 30)
            String nickname
    ) {
    }

    @PostMapping
    public RsData<MemberDto> join(
            @Valid @RequestBody MemberJoinReqBody reqBody
    ) {
        Member member = memberService.join(
                reqBody.username(),
                reqBody.password(),
                reqBody.nickname()
        );

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()),
                new MemberDto(member)
        );
    }


    record MemberLoginReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,
            @NotBlank
            @Size(min = 2, max = 30)
            String password
    ) {
    }

    record MemberLoginResBody(
            MemberDto item,
            String apiKey
    ) {
    }

    @PostMapping("/login")
    public RsData<MemberLoginResBody> login(
            @Valid @RequestBody MemberLoginReqBody reqBody
    ) {
        Member member = memberService.findByUsername(reqBody.username())
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 아이디입니다."));

        if (!member.getPassword().equals(reqBody.password()))
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");

        rq.setCookie("apiKey", member.getApiKey());

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(member.getName()),
                new MemberLoginResBody(
                        new MemberDto(member),
                        member.getApiKey()
                )
        );
    }


    @DeleteMapping("/logout")
    public RsData<Void> logout() {
        rq.deleteCookie("apiKey");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }


    @GetMapping("/me")
    public RsData<MemberDto> me() {
        Member actor = rq.getActor();

        return new RsData<>(
                "200-1",
                "%s님의 정보입니다.".formatted(actor.getName()),
                new MemberDto(actor)
        );
    }
}
