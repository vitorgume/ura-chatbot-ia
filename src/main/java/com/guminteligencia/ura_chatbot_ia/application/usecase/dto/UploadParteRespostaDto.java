package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UploadParteRespostaDto {
    private boolean finished;
    private String nextUploadUrl;
    private String fileUuid;
    private String versionUuid;
}
