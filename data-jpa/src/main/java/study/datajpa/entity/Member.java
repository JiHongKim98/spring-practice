package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

// JPA 의 프록시를 위한 default 생성자 (private 으로 막아두면 안되고, protected 까지 해놔야한다.)
// Lombok 의 NoArgsConstructor 을 통해 생략 가능
@Entity
@Getter
@ToString(of = {"id", "username", "age"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
public class Member extends BaseEntity { //JpaBaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    private Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            updateTeam(team);
        }
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateAge(int age) {
        this.age = age;
    }

    public void updateTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);    // Team 엔티티에서도 변경
    }
}
