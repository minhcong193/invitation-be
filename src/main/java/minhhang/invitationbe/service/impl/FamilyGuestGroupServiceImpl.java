package minhhang.invitationbe.service.impl;

import lombok.RequiredArgsConstructor;
import minhhang.invitationbe.dto.request.CreateFamilyGuestGroupRequest;
import minhhang.invitationbe.dto.request.UpdateFamilyGuestGroupRequest;
import minhhang.invitationbe.dto.response.FamilyGuestGroupResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.exception.BadRequestException;
import minhhang.invitationbe.exception.ResourceNotFoundException;
import minhhang.invitationbe.repository.FamilyGuestGroupRepository;
import minhhang.invitationbe.service.ExcelExportService;
import minhhang.invitationbe.service.FamilyGuestGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyGuestGroupServiceImpl implements FamilyGuestGroupService {

    private final FamilyGuestGroupRepository familyGuestGroupRepository;
    private final ExcelExportService excelExportService;

    @Override
    public FamilyGuestGroupResponse create(CreateFamilyGuestGroupRequest request) {
        String inviteCode = request.getInviteCode();
        if (inviteCode == null || inviteCode.isBlank()) {
            inviteCode = generateUniqueInviteCode();
        } else {
            inviteCode = inviteCode.trim();
            if (familyGuestGroupRepository.existsByInviteCode(inviteCode)) {
                throw new BadRequestException("Invite code '" + inviteCode + "' already exists");
            }
        }

        FamilyGuestGroup group = FamilyGuestGroup.builder()
                .inviteCode(inviteCode)
                .groupName(request.getGroupName().trim())
                .guestSide(request.getGuestSide())
                .relationship(request.getRelationship() != null ? request.getRelationship().trim() : null)
                .expectedGuestCount(request.getExpectedGuestCount() != null ? request.getExpectedGuestCount() : 1)
                .tableNumber(request.getTableNumber() != null ? request.getTableNumber().trim() : null)
                .note(request.getNote() != null ? request.getNote().trim() : null)
                .isActive(true)
                .build();

        return FamilyGuestGroupResponse.fromEntity(familyGuestGroupRepository.save(group));
    }

    @Override
    public FamilyGuestGroupResponse update(Long id, UpdateFamilyGuestGroupRequest request) {
        FamilyGuestGroup group = findGroupById(id);

        if (request.getGroupName() != null && !request.getGroupName().isBlank()) {
            group.setGroupName(request.getGroupName().trim());
        }
        if (request.getGuestSide() != null) {
            group.setGuestSide(request.getGuestSide());
        }
        if (request.getRelationship() != null) {
            group.setRelationship(request.getRelationship().trim());
        }
        if (request.getExpectedGuestCount() != null) {
            group.setExpectedGuestCount(request.getExpectedGuestCount());
        }
        if (request.getTableNumber() != null) {
            group.setTableNumber(request.getTableNumber().trim());
        }
        if (request.getNote() != null) {
            group.setNote(request.getNote().trim());
        }
        if (request.getIsActive() != null) {
            group.setIsActive(request.getIsActive());
        }

        return FamilyGuestGroupResponse.fromEntity(familyGuestGroupRepository.save(group));
    }

    @Override
    public void delete(Long id) {
        FamilyGuestGroup group = findGroupById(id);
        familyGuestGroupRepository.delete(group);
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyGuestGroupResponse getById(Long id) {
        return FamilyGuestGroupResponse.fromEntity(findGroupById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyGuestGroupResponse getByInviteCode(String inviteCode) {
        String code = inviteCode != null ? inviteCode.trim() : "";
        FamilyGuestGroup group = familyGuestGroupRepository.findByInviteCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Family guest group with invite code '" + inviteCode + "' not found"));
        return FamilyGuestGroupResponse.fromEntity(group);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FamilyGuestGroupResponse> getAll(String inviteCode, GuestSide guestSide, String tableNumber, Boolean isActive, Pageable pageable) {
        Specification<FamilyGuestGroup> spec = (root, query, cb) -> cb.conjunction();

        if (inviteCode != null && !inviteCode.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("inviteCode"), inviteCode.trim()));
        }
        if (guestSide != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("guestSide"), guestSide));
        }
        if (tableNumber != null && !tableNumber.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tableNumber"), tableNumber.trim()));
        }
        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        Page<FamilyGuestGroupResponse> page = familyGuestGroupRepository.findAll(spec, pageable)
                .map(FamilyGuestGroupResponse::fromEntity);

        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportFamilyGroups(String domain, GuestSide guestSide, String tableNumber, Boolean isActive) {
        Specification<FamilyGuestGroup> spec = (root, query, cb) -> cb.conjunction();

        if (guestSide != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("guestSide"), guestSide));
        }
        if (tableNumber != null && !tableNumber.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tableNumber"), tableNumber.trim()));
        }
        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        List<FamilyGuestGroup> groups = familyGuestGroupRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));
        return excelExportService.exportFamilyGroups(groups, domain);
    }

    private FamilyGuestGroup findGroupById(Long id) {
        return familyGuestGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family guest group with id " + id + " not found"));
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            code = UUID.randomUUID().toString();
        } while (familyGuestGroupRepository.existsByInviteCode(code));
        return code;
    }
}

