package com.example.spring.controller;

import com.example.spring.domain.Member;
import com.example.spring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class MemberController {

    private final MemberService memberService;

    @Autowired  // DI (생성자 주입)
    public MemberController(MemberService memberService) {
        this.memberService = memberService;

        // AOP 확인
        System.out.println("memberService => "  + memberService.getClass());
        // output:
        // memberService => class com.example.spring.service.MemberService$$SpringCGLIB$$0
        // ~~$$SpringCGLIB$$0 은 MemberService를 복제한 것
    }

    @GetMapping("/members/new")
    public String createForm() {
        return "members/createMemberFrom";
    }

    @PostMapping("/members/new")
    public String create(MemberForm form) {
        Member member = new Member();
        member.setUsername(form.getUsername());

        memberService.join(member);

        return "redirect:/";  // home으로 보냄
    }

    @GetMapping("/members")
    public String getList(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberlist";
    }
}
