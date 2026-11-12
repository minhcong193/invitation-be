package minhhang.invitationbe.service;

import minhhang.invitationbe.dto.request.CreateGuestbookRequest;
import minhhang.invitationbe.dto.request.UpdateModerationStatusRequest;
import minhhang.invitationbe.dto.response.GuestbookMessageResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.enums.ModerationStatus;
import org.springframework.data.domain.Pageable;

public interface GuestbookMessageService {

    GuestbookMessageResponse create(CreateGuestbookRequest request);

    GuestbookMessageResponse updateModerationStatus(Long id, UpdateModerationStatusRequest request);

    void delete(Long id);

    GuestbookMessageResponse getById(Long id);

    PageResponse<GuestbookMessageResponse> getApprovedMessages(Pageable pageable);

    PageResponse<GuestbookMessageResponse> getAllMessages(ModerationStatus status, Pageable pageable);
}

