package minhhang.invitationbe.service;

import minhhang.invitationbe.dto.request.GenerateVietQrRequest;
import minhhang.invitationbe.dto.response.BankResponse;
import minhhang.invitationbe.dto.response.VietQrResponse;
import minhhang.invitationbe.service.impl.VietQrServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VietQrServiceTest {

    private VietQrServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VietQrServiceImpl((RestClient) null);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "imageUrl", "https://img.vietqr.io/image");
    }

    @Test
    void testResolveBankBin() {
        // Direct BIN
        assertEquals("970422", service.resolveBankBin("970422"));

        // Case-insensitive short name & code
        assertEquals("970422", service.resolveBankBin("MB"));
        assertEquals("970422", service.resolveBankBin("mbbank"));
        assertEquals("970436", service.resolveBankBin("VCB"));
        assertEquals("970436", service.resolveBankBin("vietcombank"));
        assertEquals("970407", service.resolveBankBin("tcb"));
        assertEquals("970432", service.resolveBankBin("VPB"));
        assertEquals("970416", service.resolveBankBin("acb"));
        assertEquals("970418", service.resolveBankBin("BIDV"));
        assertEquals("970405", service.resolveBankBin("Agribank"));
    }

    @Test
    void testBuildQuickLink() {
        String quickLink = service.buildQuickLink(
                "970422",
                "0987654321",
                "compact2",
                500000L,
                "Mung cuoi Minh va Hang",
                "TRAN CONG MINH"
        );

        assertNotNull(quickLink);
        assertTrue(quickLink.startsWith("https://img.vietqr.io/image/970422-0987654321-compact2.png"));
        assertTrue(quickLink.contains("amount=500000"));
        assertTrue(quickLink.contains("addInfo=Mung+cuoi+Minh+va+Hang"));
        assertTrue(quickLink.contains("accountName=TRAN+CONG+MINH"));
    }

    @Test
    void testGenerateEmvCoPayload_Dynamic() {
        String payload = VietQrServiceImpl.generateEmvCoPayload(
                "970422",
                "0987654321",
                500000L,
                "Mung cuoi"
        );

        assertNotNull(payload);
        assertTrue(payload.startsWith("000201010212")); // Dynamic (poi 12)
        assertTrue(payload.contains("970422"));
        assertTrue(payload.contains("0987654321"));
        assertTrue(payload.contains("5303704")); // VND
        assertTrue(payload.contains("5406500000")); // Amount
        assertTrue(payload.contains("5802VN")); // Country code
        assertTrue(payload.contains("Mung cuoi"));
        assertTrue(payload.contains("6304")); // Tag 63 len 04 for CRC
        assertEquals(4, payload.substring(payload.lastIndexOf("6304") + 4).length()); // 4-char CRC hex
    }

    @Test
    void testGenerateEmvCoPayload_Static() {
        String payload = VietQrServiceImpl.generateEmvCoPayload(
                "970436",
                "1234567890",
                null,
                null
        );

        assertNotNull(payload);
        assertTrue(payload.startsWith("000201010211")); // Static (poi 11)
        assertTrue(payload.contains("970436"));
        assertTrue(payload.contains("1234567890"));
        // When amount is absent, Currency (5303704) is followed directly by Country (5802VN)
        assertTrue(payload.contains("53037045802VN"));
    }

    @Test
    void testGenerateQr() {
        GenerateVietQrRequest request = GenerateVietQrRequest.builder()
                .bankBin("MB")
                .accountNumber("0987654321")
                .accountName("TRAN CONG MINH")
                .amount(200000L)
                .description("Chuc mung hanh phuc")
                .template("compact2")
                .build();

        VietQrResponse response = service.generateQr(request);

        assertNotNull(response);
        assertEquals("970422", response.getBankBin());
        assertEquals("MBBank", response.getBankName());
        assertEquals("0987654321", response.getAccountNumber());
        assertEquals("TRAN CONG MINH", response.getAccountName());
        assertEquals(200000L, response.getAmount());
        assertNotNull(response.getQrQuickLink());
        assertNotNull(response.getQrCode());
        assertEquals("compact2", response.getTemplate());
    }

    @Test
    void testResolveTemplate() {
        // Without amount, compact2 is auto-converted to compact to avoid showing "0 đ" on image
        assertEquals("compact", service.resolveTemplate("compact2", null));
        assertEquals("compact", service.resolveTemplate(null, null));
        assertEquals("compact", service.resolveTemplate("", 0L));

        // With amount, compact2 is kept
        assertEquals("compact2", service.resolveTemplate("compact2", 500000L));
        assertEquals("compact2", service.resolveTemplate(null, 500000L));

        // Explicit other templates are preserved
        assertEquals("qr_only", service.resolveTemplate("qr_only", null));
        assertEquals("print", service.resolveTemplate("print", 100000L));
    }

    @Test
    void testGenerateQr_WithoutAmount() {
        GenerateVietQrRequest request = GenerateVietQrRequest.builder()
                .bankBin("MB")
                .accountNumber("0987654321")
                .accountName("TRAN CONG MINH")
                .description("Mung cuoi")
                .build();

        VietQrResponse response = service.generateQr(request);

        assertNotNull(response);
        assertNull(response.getAmount());
        assertEquals("compact", response.getTemplate());
        assertTrue(response.getQrQuickLink().contains("-compact.png"));
        assertFalse(response.getQrQuickLink().contains("amount="));
    }

    @Test
    void testGetSupportedBanks() {
        List<BankResponse> banks = service.getSupportedBanks();
        assertNotNull(banks);
        assertFalse(banks.isEmpty());

        boolean hasMb = banks.stream().anyMatch(b -> "970422".equals(b.getBin()));
        boolean hasVcb = banks.stream().anyMatch(b -> "970436".equals(b.getBin()));
        boolean hasTcb = banks.stream().anyMatch(b -> "970407".equals(b.getBin()));

        assertTrue(hasMb);
        assertTrue(hasVcb);
        assertTrue(hasTcb);
    }
}

