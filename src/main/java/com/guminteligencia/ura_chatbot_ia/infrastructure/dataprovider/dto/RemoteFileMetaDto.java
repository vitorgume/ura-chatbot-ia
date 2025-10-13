package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RemoteFileMetaDto {
    private long length;
    private String contentType;
}
