package minhhang.invitationbe.service.impl;

import lombok.RequiredArgsConstructor;
import minhhang.invitationbe.dto.request.CreateInvitedGuestRequest;
import minhhang.invitationbe.dto.request.RsvpRequest;
import minhhang.invitationbe.dto.request.UpdateInvitedGuestRequest;
import minhhang.invitationbe.dto.response.InvitedGuestResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.entity.InvitedGuest;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;
import minhhang.invitationbe.exception.BadRequestException;
import minhhang.invitationbe.exception.ResourceNotFoundException;
import minhhang.invitationbe.repository.FamilyGuestGroupRepository;
import minhhang.invitationbe.repository.InvitedGuestRepository;
import minhhang.invitationbe.service.ExcelExportService;
import minhhang.invitationbe.service.InvitedGuestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitedGuestServiceImpl implements InvitedGuestService {

    private final InvitedGuestRepository invitedGuestRepository;
    private final FamilyGuestGroupRepository familyGuestGroupRepository;
    private final ExcelExportService excelExportService;

    @Override
    public InvitedGuestResponse create(CreateInvitedGuestRequest request) {
        String inviteCode = generateUniqueInviteCode();

        InvitedGuest guest = InvitedGuest.builder()
                .inviteCode(inviteCode)
                .fullName(request.getFullName().trim())
                .guestSide(request.getGuestSide())
                .message(request.getMessage() != null && !request.getMessage().isBlank() ? request.getMessage().trim() : null)
                .rsvpStatus(RsvpStatus.PENDING)
                .isActive(true)
                .build();

        return InvitedGuestResponse.fromEntity(invitedGuestRepository.save(guest));
    }

    @Override
    public InvitedGuestResponse update(Long id, UpdateInvitedGuestRequest request) {
        InvitedGuest guest = findGuestById(id);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            guest.setFullName(request.getFullName().trim());
        }
        if (request.getGuestSide() != null) {
            guest.setGuestSide(request.getGuestSide());
        }
        if (request.getMessage() != null) {
            guest.setMessage(request.getMessage().trim().isEmpty() ? null : request.getMessage().trim());
        }
        if (request.getRsvpStatus() != null) {
            guest.setRsvpStatus(request.getRsvpStatus());
            guest.setRespondedAt(request.getRsvpStatus() == RsvpStatus.PENDING ? null : Instant.now());
        }
        if (request.getIsActive() != null) {
            guest.setIsActive(request.getIsActive());
        }

        return InvitedGuestResponse.fromEntity(invitedGuestRepository.save(guest));
    }

    @Override
    public void delete(Long id) {
        InvitedGuest guest = findGuestById(id);
        invitedGuestRepository.delete(guest);
    }

    @Override
    @Transactional(readOnly = true)
    public InvitedGuestResponse getById(Long id) {
        return InvitedGuestResponse.fromEntity(findGuestById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public InvitedGuestResponse getByInviteCode(String inviteCode) {
        String code = inviteCode != null ? inviteCode.trim() : "";
        InvitedGuest guest = invitedGuestRepository.findByInviteCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Guest with invite code '" + inviteCode + "' not found"));
        return InvitedGuestResponse.fromEntity(guest);
    }

    @Override
    public InvitedGuestResponse submitRsvp(String inviteCode, RsvpRequest request) {
        String code = inviteCode != null ? inviteCode.trim() : "";
        InvitedGuest guest = invitedGuestRepository.findByInviteCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Guest with invite code '" + inviteCode + "' not found"));

        if (Boolean.FALSE.equals(guest.getIsActive())) {
            throw new BadRequestException("This invitation is no longer active");
        }

        guest.setRsvpStatus(request.getRsvpStatus());
        guest.setRespondedAt(request.getRsvpStatus() == RsvpStatus.PENDING ? null : Instant.now());

        return InvitedGuestResponse.fromEntity(invitedGuestRepository.save(guest));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InvitedGuestResponse> getAll(String inviteCode, GuestSide guestSide, RsvpStatus rsvpStatus, Boolean isActive, Pageable pageable) {
        Specification<InvitedGuest> spec = (root, query, cb) -> cb.conjunction();

        if (inviteCode != null && !inviteCode.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("inviteCode"), inviteCode.trim()));
        }
        if (guestSide != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("guestSide"), guestSide));
        }
        if (rsvpStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("rsvpStatus"), rsvpStatus));
        }
        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        Page<InvitedGuestResponse> page = invitedGuestRepository.findAll(spec, pageable)
                .map(InvitedGuestResponse::fromEntity);

        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportGuests(String domain, GuestSide guestSide, RsvpStatus rsvpStatus, Boolean isActive) {
        Specification<InvitedGuest> spec = (root, query, cb) -> cb.conjunction();

        if (guestSide != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("guestSide"), guestSide));
        }
        if (rsvpStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("rsvpStatus"), rsvpStatus));
        }
        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        List<InvitedGuest> guests = invitedGuestRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));
        return excelExportService.exportGuests(guests, domain);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportAll(String domain) {
        List<InvitedGuest> guests = invitedGuestRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<FamilyGuestGroup> groups = familyGuestGroupRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return excelExportService.exportAll(guests, groups, domain);
    }

    private InvitedGuest findGuestById(Long id) {
        return invitedGuestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest with id " + id + " not found"));
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            code = UUID.randomUUID().toString();
        } while (invitedGuestRepository.existsByInviteCode(code));
        return code;
    }
}
