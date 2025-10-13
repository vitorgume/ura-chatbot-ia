package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class SessaoArquivoDto {

    @JsonProperty("session_id")
    private Integer sessionId;

    @JsonProperty("upload_url")
    private String uploadUrl;

    @JsonProperty("max_file_size")
    private Integer maxFileSize;

    @JsonProperty("max_part_size")
    private Integer maxPartSize;
}
