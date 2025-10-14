package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CardDto {
    @JsonProperty("responsible_user_id")
    private Integer responsibleUserId;

    @JsonProperty("status_id")
    private Integer statusId;

    @JsonProperty("custom_fields_values")
    private List<CustomFieldDto> customFieldsValues;

    @JsonProperty("_embedded")
    private Map<String, Object> embedded;
}
