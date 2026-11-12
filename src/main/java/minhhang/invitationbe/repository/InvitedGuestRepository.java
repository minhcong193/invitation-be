package minhhang.invitationbe.repository;

import minhhang.invitationbe.entity.InvitedGuest;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitedGuestRepository extends JpaRepository<InvitedGuest, Long>, JpaSpecificationExecutor<InvitedGuest> {

    Optional<InvitedGuest> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);

    List<InvitedGuest> findByGuestSide(GuestSide guestSide);

    Page<InvitedGuest> findByGuestSide(GuestSide guestSide, Pageable pageable);

    List<InvitedGuest> findByRsvpStatus(RsvpStatus rsvpStatus);

    Page<InvitedGuest> findByRsvpStatus(RsvpStatus rsvpStatus, Pageable pageable);

    List<InvitedGuest> findByIsActiveTrue();

    Page<InvitedGuest> findByIsActiveTrue(Pageable pageable);

    long countByGuestSide(GuestSide guestSide);

    long countByRsvpStatus(RsvpStatus rsvpStatus);

    long countByIsActiveTrue();
}

