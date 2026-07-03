package com.ticketa.ticket.application.service

interface QrCodeGenerator {
    fun generate(data: String): ByteArray
}
