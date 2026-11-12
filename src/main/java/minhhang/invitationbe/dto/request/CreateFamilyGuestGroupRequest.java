package minhhang.invitationbe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import minhhang.invitationbe.enums.GuestSide;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu tạo nhóm khách họ hàng / gia đình")
public class CreateFamilyGuestGroupRequest {

    @Schema(
            description = "Mã thiệp mời trên URL (tùy chọn, để trống hệ thống sẽ tự động sinh UUID chuẩn duy nhất)",
            example = "c9a646d3-9c61-4cc9-bc55-e7eb8863f35c",
            maxLength = 80
    )
    @Size(max = 80, message = "Invite code must not exceed 80 characters")
    private String inviteCode;

    @Schema(
            description = "Tên đại diện nhóm khách họ hàng / gia đình",
            example = "Gia đình bác Nguyễn Văn A",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 200
    )
    @NotBlank(message = "Group name is required")
    @Size(max = 200, message = "Group name must not exceed 200 characters")
    private String groupName;

    @Schema(
            description = "Thuộc phía nhà gái ('bride') hoặc nhà trai ('groom')",
            example = "groom",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Guest side is required ('groom' or 'bride')")
    private GuestSide guestSide;

    @Schema(
            description = "Mối quan hệ họ hàng (ví dụ: 'Bác ruột', 'Cô chú', 'Họ ngoại')",
            example = "Bác ruột (bên nội)",
            maxLength = 100
    )
    @Size(max = 100, message = "Relationship must not exceed 100 characters")
    private String relationship;

    @Schema(
            description = "Số lượng khách dự kiến tham dự (từ 1 đến 50 người)",
            example = "4",
            defaultValue = "1",
            minimum = "1",
            maximum = "50"
    )
    @Min(value = 1, message = "Expected guest count must be at least 1")
    @Max(value = 50, message = "Expected guest count must not exceed 50")
    @Builder.Default
    private Integer expectedGuestCount = 1;

    @Schema(
            description = "Số bàn tiệc được xếp trước",
            example = "Bàn 06",
            maxLength = 30
    )
    @Size(max = 30, message = "Table number must not exceed 30 characters")
    private String tableNumber;

    @Schema(
            description = "Ghi chú nội bộ cho nhóm khách",
            example = "Có 2 người lớn, 2 trẻ em, cần ghế trẻ em",
            maxLength = 1000
    )
    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;
}
