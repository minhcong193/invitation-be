package minhhang.invitationbe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import minhhang.invitationbe.entity.GuestbookMessage;
import minhhang.invitationbe.enums.ModerationStatus;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết lời chúc trong sổ lưu bút")
public class GuestbookMessageResponse {

    @Schema(description = "ID định danh duy nhất của lời chúc", example = "1")
    private Long id;

    @Schema(description = "ID khách mời gửi lời chúc (nếu có)", example = "10")
    private Long invitedGuestId;

    @Schema(description = "Tên khách mời gắn với mã thiệp (nếu có)", example = "Nguyễn Văn Tuấn")
    private String invitedGuestName;

    @Schema(description = "Tên hiển thị người gửi", example = "Tuấn & Mai")
    private String displayName;

    @Schema(description = "Nội dung lời chúc", example = "Chúc hai bạn trăm năm hạnh phúc!")
    private String message;

    @Schema(description = "Trạng thái kiểm duyệt ('pending', 'approved', 'rejected')", example = "approved")
    private ModerationStatus moderationStatus;

    @Schema(description = "Thời điểm gửi lời chúc", example = "2026-11-12T09:00:00Z")
    private Instant createdAt;

    @Schema(description = "Thời điểm được phê duyệt hiển thị", example = "2026-11-12T09:15:00Z")
    private Instant approvedAt;

    public static GuestbookMessageResponse fromEntity(GuestbookMessage entity) {
        if (entity == null) return null;
        return GuestbookMessageResponse.builder()
                .id(entity.getId())
                .invitedGuestId(entity.getInvitedGuest() != null ? entity.getInvitedGuest().getId() : null)
                .invitedGuestName(entity.getInvitedGuest() != null ? entity.getInvitedGuest().getFullName() : null)
                .displayName(entity.getDisplayName())
                .message(entity.getMessage())
                .moderationStatus(entity.getModerationStatus())
                .createdAt(entity.getCreatedAt())
                .approvedAt(entity.getApprovedAt())
                .build();
    }
}
