package com.ticketa.ticket.application

import com.ticketa.ticket.application.service.ZxingQrCodeGenerator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class ZxingQrCodeGeneratorTest {

    private val generator = ZxingQrCodeGenerator()

    @Test
    fun `should generate QR code as PNG bytes`() {
        val data = UUID.randomUUID().toString()
        val qrBytes = generator.generate(data)
        assertTrue(qrBytes.isNotEmpty())
    }

    @Test
    fun `should generate valid PNG header`() {
        val data = UUID.randomUUID().toString()
        val qrBytes = generator.generate(data)
        assertTrue(qrBytes.size >= 8)
        assertTrue(qrBytes[0] == 0x89.toByte())
        assertTrue(qrBytes[1] == 0x50.toByte())
        assertTrue(qrBytes[2] == 0x4E.toByte())
        assertTrue(qrBytes[3] == 0x47.toByte())
    }
}
