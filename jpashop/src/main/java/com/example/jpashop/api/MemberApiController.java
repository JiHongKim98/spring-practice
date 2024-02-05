package com.example.jpashop.api;

import com.example.jpashop.domain.Member;
import com.example.jpashop.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * read 로직
     */
    @GetMapping("/api/v1/members")
    public List<Member> memberListV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberListV2() {
        List<Member> findMembers = memberService.findMembers();

        List<MemberDto> collect = findMembers.stream()
                .map(member -> new MemberDto(member.getName()))
                .collect(Collectors.toList());

        return new Result<>(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    private static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    private static class MemberDto {
        private String name;
    }

    /**
     * create 로직
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse sendMemberV1(@RequestBody @Valid Member member) {
        Long memberId = memberService.join(member);
        return new CreateMemberResponse(memberId);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse sendMemberV2(@RequestBody @Valid CreateMemberDto request) {

        Member member = new Member();
        member.setName(request.name);

        Long memberId = memberService.join(member);
        return new CreateMemberResponse(memberId);
    }

    @Data
    @NotEmpty
    private static class CreateMemberDto {
        private String name;
    }

    @Data
    private static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    /**
     * update 로직
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long memberId,
            @RequestBody @Valid UpdateMemberDto request
    ) {
        Member member = new Member();

        memberService.update(memberId, request.getName());
        Member findMember = memberService.findOne(memberId);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    @AllArgsConstructor
    private static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    private static class UpdateMemberDto {
        @NotEmpty
        private String name;
    }
}
