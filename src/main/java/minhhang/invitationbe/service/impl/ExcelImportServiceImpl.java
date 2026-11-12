package minhhang.invitationbe.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import minhhang.invitationbe.dto.response.ExcelImportResult;
import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.entity.InvitedGuest;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;
import minhhang.invitationbe.exception.BadRequestException;
import minhhang.invitationbe.repository.FamilyGuestGroupRepository;
import minhhang.invitationbe.repository.InvitedGuestRepository;
import minhhang.invitationbe.service.ExcelImportService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelImportServiceImpl implements ExcelImportService {

    private final InvitedGuestRepository invitedGuestRepository;
    private final FamilyGuestGroupRepository familyGuestGroupRepository;

    @Override
    @Transactional
    public ExcelImportResult importGuests(MultipartFile file) {
        validateExcelFile(file);

        List<InvitedGuest> guestsToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalRows = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isRowEmpty(row)) {
                    continue;
                }
                totalRows++;
                int displayRow = rowIndex + 1;

                // 1. Cột Họ và tên
                String fullName = getCellValueAsString(row.getCell(0));
                if (fullName == null || fullName.isBlank()) {
                    errors.add("Dòng " + displayRow + ": Họ và tên không được để trống");
                    continue;
                }
                if (fullName.length() > 150) {
                    errors.add("Dòng " + displayRow + ": Họ và tên không được vượt quá 150 ký tự");
                    continue;
                }

                // 2. Cột Phía nhà
                String guestSideStr = getCellValueAsString(row.getCell(1));
                GuestSide guestSide;
                try {
                    guestSide = GuestSide.fromValue(guestSideStr);
                    if (guestSide == null) {
                        errors.add("Dòng " + displayRow + ": Phía nhà là bắt buộc ('Chú rể' hoặc 'Cô dâu')");
                        continue;
                    }
                } catch (IllegalArgumentException e) {
                    errors.add("Dòng " + displayRow + ": Phía nhà '" + guestSideStr + "' không hợp lệ (chỉ chấp nhận 'Chú rể' hoặc 'Cô dâu')");
                    continue;
                }

                // 3. Cột Thông tin thêm (map vào field message)
                String message = null;
                if (row.getCell(2) != null) {
                    String msgStr = getCellValueAsString(row.getCell(2));
                    if (msgStr != null && !msgStr.isBlank()) {
                        if (msgStr.length() > 1000) {
                            errors.add("Dòng " + displayRow + ": Thông tin thêm không được vượt quá 1000 ký tự");
                            continue;
                        }
                        message = msgStr.trim();
                    }
                }

                // Mặc định khách mới: rsvpStatus = PENDING, respondedAt = null, isActive = true
                RsvpStatus rsvpStatus = RsvpStatus.PENDING;
                Instant respondedAt = null;
                boolean isActive = true;

                // Tương thích nếu file có cột Kích hoạt (cột 3)
                if (row.getCell(3) != null) {
                    String activeStr = getCellValueAsString(row.getCell(3));
                    if (activeStr != null && !activeStr.isBlank()) {
                        isActive = !"false".equalsIgnoreCase(activeStr.trim());
                    }
                }

                InvitedGuest guest = InvitedGuest.builder()
                        .inviteCode(UUID.randomUUID().toString())
                        .fullName(fullName.trim())
                        .guestSide(guestSide)
                        .message(message)
                        .rsvpStatus(rsvpStatus)
                        .respondedAt(respondedAt)
                        .isActive(isActive)
                        .build();

                guestsToSave.add(guest);
            }

            if (!guestsToSave.isEmpty()) {
                invitedGuestRepository.saveAll(guestsToSave);
            }

        } catch (Exception e) {
            log.error("Error reading guests excel file", e);
            throw new BadRequestException("Không thể đọc file Excel: " + e.getMessage());
        }

        return ExcelImportResult.builder()
                .totalRows(totalRows)
                .successCount(guestsToSave.size())
                .failedCount(totalRows - guestsToSave.size())
                .errors(errors)
                .build();
    }

    @Override
    @Transactional
    public ExcelImportResult importFamilyGroups(MultipartFile file) {
        validateExcelFile(file);

        List<FamilyGuestGroup> groupsToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalRows = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isRowEmpty(row)) {
                    continue;
                }
                totalRows++;
                int displayRow = rowIndex + 1;

                // 1. Tên nhóm / Gia đình
                String groupName = getCellValueAsString(row.getCell(0));
                if (groupName == null || groupName.isBlank()) {
                    errors.add("Dòng " + displayRow + ": Tên nhóm/gia đình không được để trống");
                    continue;
                }
                if (groupName.length() > 200) {
                    errors.add("Dòng " + displayRow + ": Tên nhóm/gia đình không được vượt quá 200 ký tự");
                    continue;
                }

                // 2. Phía nhà
                String guestSideStr = getCellValueAsString(row.getCell(1));
                GuestSide guestSide;
                try {
                    guestSide = GuestSide.fromValue(guestSideStr);
                    if (guestSide == null) {
                        errors.add("Dòng " + displayRow + ": Phía nhà là bắt buộc ('Chú rể' hoặc 'Cô dâu')");
                        continue;
                    }
                } catch (IllegalArgumentException e) {
                    errors.add("Dòng " + displayRow + ": Phía nhà '" + guestSideStr + "' không hợp lệ (chỉ chấp nhận 'Chú rể' hoặc 'Cô dâu')");
                    continue;
                }

                // 3. Mối quan hệ
                String relationship = getCellValueAsString(row.getCell(2));
                if (relationship != null && relationship.length() > 100) {
                    errors.add("Dòng " + displayRow + ": Mối quan hệ không được vượt quá 100 ký tự");
                    continue;
                }

                // 4. Số lượng khách dự kiến
                String countStr = getCellValueAsString(row.getCell(3));
                int expectedGuestCount = 1;
                if (countStr != null && !countStr.isBlank()) {
                    try {
                        expectedGuestCount = (int) Double.parseDouble(countStr);
                        if (expectedGuestCount < 1 || expectedGuestCount > 50) {
                            errors.add("Dòng " + displayRow + ": Số lượng khách dự kiến phải nằm trong khoảng từ 1 đến 50");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        errors.add("Dòng " + displayRow + ": Số lượng khách dự kiến '" + countStr + "' phải là số nguyên");
                        continue;
                    }
                }

                // 5. Số bàn tiệc
                String tableNumber = getCellValueAsString(row.getCell(4));
                if (tableNumber != null && tableNumber.length() > 30) {
                    errors.add("Dòng " + displayRow + ": Số bàn tiệc không được vượt quá 30 ký tự");
                    continue;
                }

                // 6. Ghi chú
                String note = getCellValueAsString(row.getCell(5));
                if (note != null && note.length() > 1000) {
                    errors.add("Dòng " + displayRow + ": Ghi chú không được vượt quá 1000 ký tự");
                    continue;
                }

                // 7. Kích hoạt
                String activeStr = getCellValueAsString(row.getCell(6));
                boolean isActive = true;
                if (activeStr != null && !activeStr.isBlank()) {
                    isActive = !"false".equalsIgnoreCase(activeStr.trim());
                }

                FamilyGuestGroup group = FamilyGuestGroup.builder()
                        .inviteCode(UUID.randomUUID().toString())
                        .groupName(groupName.trim())
                        .guestSide(guestSide)
                        .relationship(relationship != null && !relationship.isBlank() ? relationship.trim() : null)
                        .expectedGuestCount(expectedGuestCount)
                        .tableNumber(tableNumber != null && !tableNumber.isBlank() ? tableNumber.trim() : null)
                        .note(note != null && !note.isBlank() ? note.trim() : null)
                        .isActive(isActive)
                        .build();

                groupsToSave.add(group);
            }

            if (!groupsToSave.isEmpty()) {
                familyGuestGroupRepository.saveAll(groupsToSave);
            }

        } catch (Exception e) {
            log.error("Error reading family groups excel file", e);
            throw new BadRequestException("Không thể đọc file Excel: " + e.getMessage());
        }

        return ExcelImportResult.builder()
                .totalRows(totalRows)
                .successCount(groupsToSave.size())
                .failedCount(totalRows - groupsToSave.size())
                .errors(errors)
                .build();
    }

    private void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng tải lên file Excel");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls"))) {
            throw new BadRequestException("Định dạng file không hợp lệ. Vui lòng sử dụng file Excel (.xlsx hoặc .xls)");
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = getCellValueAsString(cell);
                if (val != null && !val.isBlank()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toInstant().toString();
                }
                double num = cell.getNumericCellValue();
                if (num == (long) num) {
                    yield String.valueOf((long) num);
                } else {
                    yield String.valueOf(num);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }
}

