package minhhang.invitationbe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Kết quả import dữ liệu từ file Excel")
public class ExcelImportResult {

    @Schema(description = "Tổng số dòng dữ liệu đọc được trong file", example = "10")
    private int totalRows;

    @Schema(description = "Số dòng import thành công", example = "9")
    private int successCount;

    @Schema(description = "Số dòng import thất bại", example = "1")
    private int failedCount;

    @Builder.Default
    @Schema(description = "Danh sách thông báo lỗi chi tiết theo từng dòng")
    private List<String> errors = new ArrayList<>();
}

