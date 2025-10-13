package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CardDto {

    @JsonProperty("responsible_use_id")
    private Integer responsibleUseId;

    @JsonProperty("custom_fields_values")
    private List<CustomFieldDto> customFieldsValues;

    @JsonProperty("status_id")
    private Integer statusId;

    @JsonProperty("_embedded")
    private Map<String, List<Map<String, Integer>>> embedded;
}
