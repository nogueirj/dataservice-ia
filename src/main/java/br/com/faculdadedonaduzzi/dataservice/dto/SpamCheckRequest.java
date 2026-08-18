package br.com.faculdadedonaduzzi.dataservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para verificação de spam",
        example = "{\"messageContent\": \"This is a spam message!\"}",
        requiredMode = Schema.RequiredMode.REQUIRED)
public record SpamCheckRequest(String messageContent) {
}
