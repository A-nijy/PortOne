package test.pay.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.pay.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
