package minhhang.invitationbe.service;

import minhhang.invitationbe.service.impl.ExcelTemplateServiceImpl;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ExcelTemplateServiceTest {

    private final ExcelTemplateService service = new ExcelTemplateServiceImpl();

    @Test
    void testGenerateGuestTemplate() throws IOException {
        byte[] bytes = service.generateGuestTemplate();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet sheet = workbook.getSheet("Khach_Moi");
            assertNotNull(sheet);
            assertEquals(3, sheet.getRow(0).getLastCellNum());
            assertEquals("Họ và tên (*)", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Phía nhà (*)", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Thông tin thêm", sheet.getRow(0).getCell(2).getStringCellValue());
            assertEquals("Chú rể", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("Bạn cấp 3 của chú rể", sheet.getRow(1).getCell(2).getStringCellValue());
            assertEquals(1, sheet.getDataValidations().size());
        }
    }

    @Test
    void testGenerateFamilyGroupTemplate() throws IOException {
        byte[] bytes = service.generateFamilyGroupTemplate();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet sheet = workbook.getSheet("Nhom_Gia_Dinh");
            assertNotNull(sheet);
            assertEquals(3, sheet.getRow(0).getLastCellNum());
            assertEquals("Tên nhóm / Gia đình (*)", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Phía nhà (*)", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Mối quan hệ", sheet.getRow(0).getCell(2).getStringCellValue());
            assertEquals("Chú rể", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals(1, sheet.getDataValidations().size());
        }
    }
}

