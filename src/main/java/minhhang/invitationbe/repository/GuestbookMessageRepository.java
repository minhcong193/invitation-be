package minhhang.invitationbe.repository;

import minhhang.invitationbe.entity.GuestbookMessage;
import minhhang.invitationbe.enums.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestbookMessageRepository extends JpaRepository<GuestbookMessage, Long>, JpaSpecificationExecutor<GuestbookMessage> {

    List<GuestbookMessage> findByModerationStatus(ModerationStatus moderationStatus);

    Page<GuestbookMessage> findByModerationStatus(ModerationStatus moderationStatus, Pageable pageable);

    Page<GuestbookMessage> findByModerationStatusOrderByCreatedAtDesc(ModerationStatus moderationStatus, Pageable pageable);

    List<GuestbookMessage> findByInvitedGuestId(Long invitedGuestId);

    Page<GuestbookMessage> findByInvitedGuestId(Long invitedGuestId, Pageable pageable);

    long countByModerationStatus(ModerationStatus moderationStatus);
}

