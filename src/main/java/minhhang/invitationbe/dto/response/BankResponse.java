package minhhang.invitationbe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin ngân hàng hỗ trợ VietQR")
public class BankResponse {

    @Schema(description = "Mã định danh ngân hàng", example = "17")
    private Integer id;

    @Schema(description = "Tên đầy đủ của ngân hàng", example = "Ngân hàng TMCP Quân Đội")
    private String name;

    @Schema(description = "Mã viết tắt ngân hàng", example = "MB")
    private String code;

    @Schema(description = "Mã định danh BIN Napas 6 chữ số", example = "970422")
    private String bin;

    @Schema(description = "Tên thương mại ngắn gọn", example = "MBBank")
    private String shortName;

    @Schema(description = "URL logo ngân hàng")
    private String logo;
}

