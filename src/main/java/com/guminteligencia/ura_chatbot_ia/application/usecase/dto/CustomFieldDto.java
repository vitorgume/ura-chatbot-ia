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
public class CustomFieldDto {

    @JsonProperty("field_id")
    private Integer fieldId;

    @JsonProperty("values")
    private List<Map<String, Object>> values;
}
