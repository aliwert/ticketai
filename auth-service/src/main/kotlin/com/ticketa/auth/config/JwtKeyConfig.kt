package com.ticketa.auth.config

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.UUID

@Configuration
class JwtKeyConfig {

    @Bean
    fun rsaKeyPair(@Value("\${app.jwk.key-size:2048}") keySize: Int): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(keySize)
        return generator.generateKeyPair()
    }

    @Bean
    fun jwkSet(keyPair: KeyPair): JWKSet {
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val rsaKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .algorithm(JWSAlgorithm.RS256)
            .build()
        return JWKSet(rsaKey)
    }

    @Bean
    fun jwtEncoder(jwkSet: JWKSet): NimbusJwtEncoder {
        return NimbusJwtEncoder(ImmutableJWKSet(jwkSet))
    }

    @Bean
    fun rsaPublicKey(keyPair: KeyPair): RSAPublicKey = keyPair.public as RSAPublicKey

    @Bean
    fun reactiveJwtDecoder(rsaPublicKey: RSAPublicKey): NimbusReactiveJwtDecoder {
        return NimbusReactiveJwtDecoder(rsaPublicKey)
    }
}
