package minhhang.invitationbe.service;

import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.entity.InvitedGuest;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;
import minhhang.invitationbe.service.impl.ExcelExportServiceImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelExportServiceTest {

    private ExcelExportServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ExcelExportServiceImpl();
        ReflectionTestUtils.setField(service, "defaultBaseUrl", "http://localhost:3000");
    }

    @Test
    void testExportGuests() throws IOException {
        InvitedGuest guest = InvitedGuest.builder()
                .id(1L)
                .fullName("Nguyễn Văn A")
                .guestSide(GuestSide.GROOM)
                .inviteCode("abc-123")
                .rsvpStatus(RsvpStatus.ATTENDING)
                .respondedAt(Instant.now())
                .message("Lời chúc tốt đẹp")
                .isActive(true)
                .build();

        byte[] bytes = service.exportGuests(List.of(guest), "damcuoi.vn");
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet sheet = workbook.getSheet("Khach_Moi");
            assertNotNull(sheet);

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("Họ và tên", headerRow.getCell(1).getStringCellValue());
            assertEquals("Link thiệp mời", headerRow.getCell(4).getStringCellValue());

            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
            assertEquals("Nguyễn Văn A", dataRow.getCell(1).getStringCellValue());
            assertEquals("Chú rể", dataRow.getCell(2).getStringCellValue());
            assertEquals("abc-123", dataRow.getCell(3).getStringCellValue());

            Cell linkCell = dataRow.getCell(4);
            assertEquals("https://damcuoi.vn/invite/abc-123", linkCell.getStringCellValue());
            assertNotNull(linkCell.getHyperlink());
            assertEquals("https://damcuoi.vn/invite/abc-123", linkCell.getHyperlink().getAddress());

            assertEquals("Tham dự", dataRow.getCell(5).getStringCellValue());
            assertEquals("Lời chúc tốt đẹp", dataRow.getCell(7).getStringCellValue());
            assertEquals("Đang hoạt động", dataRow.getCell(8).getStringCellValue());
        }
    }

    @Test
    void testExportFamilyGroups() throws IOException {
        FamilyGuestGroup group = FamilyGuestGroup.builder()
                .id(10L)
                .groupName("Gia đình Bác Hải")
                .guestSide(GuestSide.BRIDE)
                .relationship("Bác ruột cô dâu")
                .inviteCode("fam-789")
                .expectedGuestCount(4)
                .tableNumber("Bàn 05")
                .note("Ăn chay 1 người")
                .isActive(true)
                .build();

        byte[] bytes = service.exportFamilyGroups(List.of(group), "https://damcuoi.vn/");
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet sheet = workbook.getSheet("Nhom_Gia_Dinh");
            assertNotNull(sheet);

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("Tên nhóm / Gia đình", headerRow.getCell(1).getStringCellValue());
            assertEquals("Phía nhà", headerRow.getCell(2).getStringCellValue());
            assertEquals("Link thiệp mời", headerRow.getCell(5).getStringCellValue());

            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
            assertEquals("Gia đình Bác Hải", dataRow.getCell(1).getStringCellValue());
            assertEquals("Cô dâu", dataRow.getCell(2).getStringCellValue());
            assertEquals("Bác ruột cô dâu", dataRow.getCell(3).getStringCellValue());
            assertEquals("fam-789", dataRow.getCell(4).getStringCellValue());

            Cell linkCell = dataRow.getCell(5);
            assertEquals("https://damcuoi.vn/invite/fam-789", linkCell.getStringCellValue());
            assertNotNull(linkCell.getHyperlink());
            assertEquals("https://damcuoi.vn/invite/fam-789", linkCell.getHyperlink().getAddress());

            assertEquals(4, (int) dataRow.getCell(6).getNumericCellValue());
            assertEquals("Bàn 05", dataRow.getCell(7).getStringCellValue());
            assertEquals("Ăn chay 1 người", dataRow.getCell(8).getStringCellValue());
            assertEquals("Đang hoạt động", dataRow.getCell(9).getStringCellValue());
        }
    }

    @Test
    void testExportAll() throws IOException {
        InvitedGuest guest = InvitedGuest.builder()
                .id(1L)
                .fullName("Trần Thị B")
                .guestSide(GuestSide.BRIDE)
                .inviteCode("bride-001")
                .rsvpStatus(RsvpStatus.PENDING)
                .isActive(true)
                .build();

        FamilyGuestGroup group = FamilyGuestGroup.builder()
                .id(2L)
                .groupName("Họ hàng nội")
                .guestSide(GuestSide.GROOM)
                .inviteCode("fam-002")
                .expectedGuestCount(2)
                .isActive(true)
                .build();

        byte[] bytes = service.exportAll(List.of(guest), List.of(group), null);
        assertNotNull(bytes);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals(2, workbook.getNumberOfSheets());
            assertNotNull(workbook.getSheet("Khach_Moi"));
            assertNotNull(workbook.getSheet("Nhom_Gia_Dinh"));

            // Default base url should be used: http://localhost:3000
            Row guestRow = workbook.getSheet("Khach_Moi").getRow(1);
            assertEquals("http://localhost:3000/invite/bride-001", guestRow.getCell(4).getStringCellValue());
            assertEquals("Chờ phản hồi", guestRow.getCell(5).getStringCellValue());

            Row groupRow = workbook.getSheet("Nhom_Gia_Dinh").getRow(1);
            assertEquals("http://localhost:3000/invite/fam-002", groupRow.getCell(5).getStringCellValue());
        }
    }
}

