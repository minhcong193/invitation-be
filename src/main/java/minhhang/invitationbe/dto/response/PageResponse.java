package minhhang.invitationbe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Cấu trúc phản hồi phân trang")
public class PageResponse<T> {

    @Schema(description = "Danh sách phần tử trong trang hiện tại")
    private List<T> content;

    @Schema(description = "Số thứ tự trang hiện tại (bắt đầu từ 0)", example = "0")
    private int page;

    @Schema(description = "Số lượng phần tử trên mỗi trang", example = "20")
    private int size;

    @Schema(description = "Tổng số phần tử thỏa mãn điều kiện", example = "100")
    private long totalElements;

    @Schema(description = "Tổng số trang", example = "5")
    private int totalPages;

    @Schema(description = "Cho biết đây có phải là trang cuối cùng hay không", example = "false")
    private boolean last;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
