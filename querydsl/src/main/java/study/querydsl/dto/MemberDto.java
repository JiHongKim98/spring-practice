package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 기본 생성자 호출
public class MemberDto {

    private String username;
    private int age;

    @QueryProjection    // DTO 도 Q 파일로 만들어준다.
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
