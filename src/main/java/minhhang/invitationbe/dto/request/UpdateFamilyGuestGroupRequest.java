package minhhang.invitationbe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import minhhang.invitationbe.enums.GuestSide;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu cập nhật thông tin nhóm khách họ hàng")
public class UpdateFamilyGuestGroupRequest {

    @Schema(description = "Tên đại diện nhóm khách họ hàng", example = "Gia đình bác Nguyễn Văn A", maxLength = 200)
    @Size(max = 200, message = "Group name must not exceed 200 characters")
    private String groupName;

    @Schema(description = "Thuộc phía nhà gái ('bride') hoặc nhà trai ('groom')", example = "groom")
    private GuestSide guestSide;

    @Schema(description = "Mối quan hệ họ hàng", example = "Bác ruột (bên nội)", maxLength = 100)
    @Size(max = 100, message = "Relationship must not exceed 100 characters")
    private String relationship;

    @Schema(description = "Số lượng khách dự kiến tham dự (từ 1 đến 50 người)", example = "5", minimum = "1", maximum = "50")
    @Min(value = 1, message = "Expected guest count must be at least 1")
    @Max(value = 50, message = "Expected guest count must not exceed 50")
    private Integer expectedGuestCount;

    @Schema(description = "Số bàn tiệc được xếp trước", example = "Bàn 08", maxLength = 30)
    @Size(max = 30, message = "Table number must not exceed 30 characters")
    private String tableNumber;

    @Schema(description = "Ghi chú nội bộ cho nhóm khách", example = "Xếp cạnh bàn gia đình cô B", maxLength = 1000)
    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;

    @Schema(description = "Trạng thái hoạt động", example = "true")
    private Boolean isActive;
}
