package minhhang.invitationbe.service;

import minhhang.invitationbe.dto.request.CreateFamilyGuestGroupRequest;
import minhhang.invitationbe.dto.request.UpdateFamilyGuestGroupRequest;
import minhhang.invitationbe.dto.response.FamilyGuestGroupResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.enums.GuestSide;
import org.springframework.data.domain.Pageable;

public interface FamilyGuestGroupService {

    FamilyGuestGroupResponse create(CreateFamilyGuestGroupRequest request);

    FamilyGuestGroupResponse update(Long id, UpdateFamilyGuestGroupRequest request);

    void delete(Long id);

    FamilyGuestGroupResponse getById(Long id);

    FamilyGuestGroupResponse getByInviteCode(String inviteCode);

    PageResponse<FamilyGuestGroupResponse> getAll(String inviteCode, GuestSide guestSide, String tableNumber, Boolean isActive, Pageable pageable);

    byte[] exportFamilyGroups(String domain, GuestSide guestSide, String tableNumber, Boolean isActive);
}

