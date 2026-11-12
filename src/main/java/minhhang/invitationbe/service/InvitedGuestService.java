package minhhang.invitationbe.service;

import minhhang.invitationbe.dto.request.CreateInvitedGuestRequest;
import minhhang.invitationbe.dto.request.RsvpRequest;
import minhhang.invitationbe.dto.request.UpdateInvitedGuestRequest;
import minhhang.invitationbe.dto.response.InvitedGuestResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;
import org.springframework.data.domain.Pageable;

public interface InvitedGuestService {

    InvitedGuestResponse create(CreateInvitedGuestRequest request);

    InvitedGuestResponse update(Long id, UpdateInvitedGuestRequest request);

    void delete(Long id);

    InvitedGuestResponse getById(Long id);

    InvitedGuestResponse getByInviteCode(String inviteCode);

    InvitedGuestResponse submitRsvp(String inviteCode, RsvpRequest request);

    PageResponse<InvitedGuestResponse> getAll(String inviteCode, GuestSide guestSide, RsvpStatus rsvpStatus, Boolean isActive, Pageable pageable);

    byte[] exportGuests(String domain, GuestSide guestSide, RsvpStatus rsvpStatus, Boolean isActive);

    byte[] exportAll(String domain);
}

