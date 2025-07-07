package com.back.global.rq;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Rq {
    private final MemberService memberService;
    private final HttpServletRequest req;

    public Member getActor() {
        String headerAuthorization = req.getHeader("Authorization");

        if (headerAuthorization == null || headerAuthorization.isBlank())
            throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if (!headerAuthorization.startsWith("Bearer "))
            throw new ServiceException("401-2", "Authorization 헤더가 Bearer 형식이 아닙니다.");

        String apiKey = headerAuthorization.substring("Bearer ".length()).trim();

        Member member = memberService
                .findByApiKey(apiKey)
                .orElseThrow(() -> new ServiceException("401-3", "API 키가 유효하지 않습니다."));

        return member;
    }
}
