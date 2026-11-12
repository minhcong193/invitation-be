package minhhang.invitationbe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.enums.GuestSide;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết nhóm khách họ hàng / gia đình")
public class FamilyGuestGroupResponse {

    @Schema(description = "ID định danh duy nhất của nhóm", example = "1")
    private Long id;

    @Schema(description = "Mã thiệp mời trên URL (UUID tự sinh)", example = "c9a646d3-9c61-4cc9-bc55-e7eb8863f35c")
    private String inviteCode;

    @Schema(description = "Tên đại diện nhóm khách", example = "Gia đình bác Nguyễn Văn A")
    private String groupName;

    @Schema(description = "Thuộc phía nhà gái ('bride') hoặc nhà trai ('groom')", example = "groom")
    private GuestSide guestSide;

    @Schema(description = "Mối quan hệ họ hàng", example = "Bác ruột")
    private String relationship;

    @Schema(description = "Số lượng khách dự kiến tham dự", example = "4")
    private Integer expectedGuestCount;

    @Schema(description = "Số bàn tiệc được xếp", example = "Bàn 06")
    private String tableNumber;

    @Schema(description = "Ghi chú nội bộ", example = "Có 2 người lớn, 2 trẻ em")
    private String note;

    @Schema(description = "Trạng thái hoạt động", example = "true")
    private Boolean isActive;

    @Schema(description = "Thời điểm tạo nhóm", example = "2026-11-10T02:00:00Z")
    private Instant createdAt;

    @Schema(description = "Thời điểm cập nhật lần cuối", example = "2026-11-12T08:30:00Z")
    private Instant updatedAt;

    public static FamilyGuestGroupResponse fromEntity(FamilyGuestGroup entity) {
        if (entity == null) return null;
        return FamilyGuestGroupResponse.builder()
                .id(entity.getId())
                .inviteCode(entity.getInviteCode())
                .groupName(entity.getGroupName())
                .guestSide(entity.getGuestSide())
                .relationship(entity.getRelationship())
                .expectedGuestCount(entity.getExpectedGuestCount())
                .tableNumber(entity.getTableNumber())
                .note(entity.getNote())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
