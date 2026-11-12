package minhhang.invitationbe.service.impl;

import lombok.RequiredArgsConstructor;
import minhhang.invitationbe.dto.request.CreateGuestbookRequest;
import minhhang.invitationbe.dto.request.UpdateModerationStatusRequest;
import minhhang.invitationbe.dto.response.GuestbookMessageResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.entity.GuestbookMessage;
import minhhang.invitationbe.entity.InvitedGuest;
import minhhang.invitationbe.enums.ModerationStatus;
import minhhang.invitationbe.exception.ResourceNotFoundException;
import minhhang.invitationbe.repository.GuestbookMessageRepository;
import minhhang.invitationbe.repository.InvitedGuestRepository;
import minhhang.invitationbe.service.GuestbookMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class GuestbookMessageServiceImpl implements GuestbookMessageService {

    private final GuestbookMessageRepository guestbookMessageRepository;
    private final InvitedGuestRepository invitedGuestRepository;

    @Override
    public GuestbookMessageResponse create(CreateGuestbookRequest request) {
        InvitedGuest guest = null;
        if (request.getInviteCode() != null && !request.getInviteCode().isBlank()) {
            guest = invitedGuestRepository.findByInviteCode(request.getInviteCode().trim()).orElse(null);
        }

        GuestbookMessage message = GuestbookMessage.builder()
                .invitedGuest(guest)
                .displayName(request.getDisplayName().trim())
                .message(request.getMessage().trim())
                .moderationStatus(ModerationStatus.PENDING)
                .build();

        return GuestbookMessageResponse.fromEntity(guestbookMessageRepository.save(message));
    }

    @Override
    public GuestbookMessageResponse updateModerationStatus(Long id, UpdateModerationStatusRequest request) {
        GuestbookMessage message = findMessageById(id);

        message.setModerationStatus(request.getModerationStatus());
        if (request.getModerationStatus() == ModerationStatus.APPROVED) {
            message.setApprovedAt(Instant.now());
        } else {
            message.setApprovedAt(null);
        }

        return GuestbookMessageResponse.fromEntity(guestbookMessageRepository.save(message));
    }

    @Override
    public void delete(Long id) {
        GuestbookMessage message = findMessageById(id);
        guestbookMessageRepository.delete(message);
    }

    @Override
    @Transactional(readOnly = true)
    public GuestbookMessageResponse getById(Long id) {
        return GuestbookMessageResponse.fromEntity(findMessageById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GuestbookMessageResponse> getApprovedMessages(Pageable pageable) {
        Page<GuestbookMessageResponse> page = guestbookMessageRepository
                .findByModerationStatusOrderByCreatedAtDesc(ModerationStatus.APPROVED, pageable)
                .map(GuestbookMessageResponse::fromEntity);

        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GuestbookMessageResponse> getAllMessages(ModerationStatus status, Pageable pageable) {
        Specification<GuestbookMessage> spec = (root, query, cb) -> cb.conjunction();

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("moderationStatus"), status));
        }

        Page<GuestbookMessageResponse> page = guestbookMessageRepository.findAll(spec, pageable)
                .map(GuestbookMessageResponse::fromEntity);

        return PageResponse.of(page);
    }

    private GuestbookMessage findMessageById(Long id) {
        return guestbookMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guestbook message with id " + id + " not found"));
    }
}

