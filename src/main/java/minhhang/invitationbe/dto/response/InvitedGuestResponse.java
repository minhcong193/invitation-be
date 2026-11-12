package minhhang.invitationbe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import minhhang.invitationbe.entity.InvitedGuest;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết khách mời")
public class InvitedGuestResponse {

    @Schema(description = "ID định danh duy nhất của khách mời", example = "1")
    private Long id;

    @Schema(description = "Mã thiệp mời trên URL (UUID tự sinh)", example = "c9a646d3-9c61-4cc9-bc55-e7eb8863f35c")
    private String inviteCode;

    @Schema(description = "Họ và tên khách mời", example = "Nguyễn Văn Tuấn")
    private String fullName;

    @Schema(description = "Phía nhà trai ('groom') hoặc nhà gái ('bride')", example = "groom")
    private GuestSide guestSide;

    @Schema(description = "Thông tin thêm / Lời nhắn", example = "Bạn cấp 3 của chú rể")
    private String message;

    @Schema(description = "Trạng thái phản hồi tham dự: 'pending' (chờ phản hồi), 'attending' (tham dự), 'declined' (từ chối)", example = "pending")
    private RsvpStatus rsvpStatus;

    @Schema(description = "Thời điểm khách phản hồi RSVP", example = "2026-11-12T08:30:00Z")
    private Instant respondedAt;

    @Schema(description = "Trạng thái thiệp có đang hoạt động hay không", example = "true")
    private Boolean isActive;

    @Schema(description = "Thời điểm tạo khách mời", example = "2026-11-10T02:00:00Z")
    private Instant createdAt;

    @Schema(description = "Thời điểm cập nhật thông tin lần cuối", example = "2026-11-12T08:30:00Z")
    private Instant updatedAt;

    public static InvitedGuestResponse fromEntity(InvitedGuest entity) {
        if (entity == null) return null;
        return InvitedGuestResponse.builder()
                .id(entity.getId())
                .inviteCode(entity.getInviteCode())
                .fullName(entity.getFullName())
                .guestSide(entity.getGuestSide())
                .message(entity.getMessage())
                .rsvpStatus(entity.getRsvpStatus())
                .respondedAt(entity.getRespondedAt())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
