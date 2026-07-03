package com.ticketa.ticket.application.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream

@Component
class ZxingQrCodeGenerator : QrCodeGenerator {

    override fun generate(data: String): ByteArray {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 300, 300)
        val outputStream = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)
        return outputStream.toByteArray()
    }
}
