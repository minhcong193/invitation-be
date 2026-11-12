package minhhang.invitationbe.service;

import minhhang.invitationbe.dto.request.GenerateVietQrRequest;
import minhhang.invitationbe.dto.response.BankResponse;
import minhhang.invitationbe.dto.response.VietQrResponse;

import java.util.List;

public interface VietQrService {

    VietQrResponse generateQr(GenerateVietQrRequest request);

    byte[] getQrImage(GenerateVietQrRequest request);

    List<BankResponse> getSupportedBanks();
}

