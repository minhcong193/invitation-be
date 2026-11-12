package minhhang.invitationbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import minhhang.invitationbe.dto.request.CreateFamilyGuestGroupRequest;
import minhhang.invitationbe.dto.request.UpdateFamilyGuestGroupRequest;
import minhhang.invitationbe.dto.response.ExcelImportResult;
import minhhang.invitationbe.dto.response.FamilyGuestGroupResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.service.ExcelImportService;
import minhhang.invitationbe.service.ExcelTemplateService;
import minhhang.invitationbe.service.FamilyGuestGroupService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/family-groups")
@RequiredArgsConstructor
@Tag(name = "Family Guest Groups", description = "APIs quản lý khách họ hàng / nhóm gia đình nội bộ (xếp bàn, số lượng)")
public class FamilyGuestGroupController {

    private final FamilyGuestGroupService familyGuestGroupService;
    private final ExcelTemplateService excelTemplateService;
    private final ExcelImportService excelImportService;

    @PostMapping
    @Operation(summary = "Tạo mới nhóm khách họ hàng / gia đình")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo nhóm khách thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<FamilyGuestGroupResponse>> create(
            @Valid @RequestBody CreateFamilyGuestGroupRequest request
    ) {
        FamilyGuestGroupResponse response = familyGuestGroupService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(minhhang.invitationbe.dto.response.ApiResponse.success("Family group created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách nhóm khách", description = "Hỗ trợ phân trang và lọc theo mã thiệp (inviteCode), nhà trai/nhà gái, số bàn, trạng thái hoạt động")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<PageResponse<FamilyGuestGroupResponse>>> getAll(
            @Parameter(description = "Lọc theo mã thiệp mời (inviteCode)") @RequestParam(required = false) String inviteCode,
            @Parameter(description = "Lọc theo phía nhà: 'groom' hoặc 'bride'") @RequestParam(required = false) GuestSide guestSide,
            @Parameter(description = "Lọc theo số bàn tiệc") @RequestParam(required = false) String tableNumber,
            @Parameter(description = "Lọc theo trạng thái hoạt động") @RequestParam(required = false) Boolean isActive,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<FamilyGuestGroupResponse> response = familyGuestGroupService.getAll(inviteCode, guestSide, tableNumber, isActive, pageable);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết nhóm khách theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy thông tin nhóm"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhóm với ID tương ứng")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<FamilyGuestGroupResponse>> getById(
            @Parameter(description = "ID nhóm khách họ hàng", example = "1") @PathVariable Long id
    ) {
        FamilyGuestGroupResponse response = familyGuestGroupService.getById(id);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @GetMapping(value = {"/code/{inviteCode}", "/by-code/{inviteCode}", "/invite/{inviteCode}"})
    @Operation(summary = "Lấy chi tiết nhóm khách theo mã inviteCode (UUID)", description = "Hỗ trợ tra cứu nhóm khách theo inviteCode trên URL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy thông tin nhóm"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhóm với mã inviteCode tương ứng")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<FamilyGuestGroupResponse>> getByInviteCode(
            @Parameter(description = "Mã UUID thiệp mời trên URL", example = "c9a646d3-9c61-4cc9-bc55-e7eb8863f35c") @PathVariable String inviteCode
    ) {
        FamilyGuestGroupResponse response = familyGuestGroupService.getByInviteCode(inviteCode);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin nhóm khách họ hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thông tin thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhóm")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<FamilyGuestGroupResponse>> update(
            @Parameter(description = "ID nhóm khách họ hàng", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateFamilyGuestGroupRequest request
    ) {
        FamilyGuestGroupResponse response = familyGuestGroupService.update(id, request);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Family group updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhóm khách họ hàng theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhóm")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<Void>> delete(
            @Parameter(description = "ID nhóm khách họ hàng cần xóa", example = "1") @PathVariable Long id
    ) {
        familyGuestGroupService.delete(id);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Family group deleted successfully", null));
    }

    @GetMapping("/excel-template")
    @Operation(summary = "Tải file mẫu Excel để import nhóm khách họ hàng / gia đình",
            description = "File Excel .xlsx gồm các cột: Tên nhóm / Gia đình (*), Phía nhà (*), Mối quan hệ với dropdown chọn 'Chú rể' hoặc 'Cô dâu'")
    @ApiResponse(responseCode = "200", description = "Tải file template thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    public ResponseEntity<byte[]> downloadFamilyGroupTemplate() {
        byte[] excelBytes = excelTemplateService.generateFamilyGroupTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=family_group_import_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import nhóm khách họ hàng / gia đình từ file Excel",
            description = "Tải lên file Excel (.xlsx hoặc .xls) theo đúng mẫu template để import hàng loạt nhóm gia đình")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Import hoàn tất (trả về chi tiết số lượng thành công, thất bại và danh sách lỗi theo dòng)"),
            @ApiResponse(responseCode = "400", description = "File không đúng định dạng hoặc không thể đọc")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<ExcelImportResult>> importFamilyGroups(
            @Parameter(description = "File Excel chứa dữ liệu nhóm gia đình (.xlsx, .xls)", required = true)
            @RequestParam("file") MultipartFile file
    ) {
        ExcelImportResult result = excelImportService.importFamilyGroups(file);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Import processed successfully", result));
    }

    @GetMapping("/export")
    @Operation(summary = "Xuất danh sách nhóm gia đình ra file Excel",
            description = "Xuất file Excel chứa thông tin nhóm gia đình / họ hàng kèm link mở thiệp theo domain/base-url (gắn hyperlink)")
    @ApiResponse(responseCode = "200", description = "Xuất file Excel thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    public ResponseEntity<byte[]> exportFamilyGroups(
            @Parameter(description = "Domain hoặc URL trang thiệp (tùy chọn, ví dụ: https://damcuoi.vn)") @RequestParam(required = false) String domain,
            @Parameter(description = "Lọc theo phía nhà: 'groom' hoặc 'bride'") @RequestParam(required = false) GuestSide guestSide,
            @Parameter(description = "Lọc theo số bàn tiệc") @RequestParam(required = false) String tableNumber,
            @Parameter(description = "Lọc theo trạng thái hoạt động") @RequestParam(required = false) Boolean isActive
    ) {
        byte[] excelBytes = familyGuestGroupService.exportFamilyGroups(domain, guestSide, tableNumber, isActive);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_nhom_gia_dinh.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
