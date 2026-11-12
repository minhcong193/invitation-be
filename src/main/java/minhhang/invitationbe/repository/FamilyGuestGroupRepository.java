package minhhang.invitationbe.repository;

import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.enums.GuestSide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyGuestGroupRepository extends JpaRepository<FamilyGuestGroup, Long>, JpaSpecificationExecutor<FamilyGuestGroup> {

    Optional<FamilyGuestGroup> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);

    List<FamilyGuestGroup> findByGuestSide(GuestSide guestSide);

    Page<FamilyGuestGroup> findByGuestSide(GuestSide guestSide, Pageable pageable);

    List<FamilyGuestGroup> findByTableNumber(String tableNumber);

    Page<FamilyGuestGroup> findByTableNumber(String tableNumber, Pageable pageable);

    List<FamilyGuestGroup> findByIsActiveTrue();

    Page<FamilyGuestGroup> findByIsActiveTrue(Pageable pageable);

    long countByGuestSide(GuestSide guestSide);

    long countByIsActiveTrue();
}

