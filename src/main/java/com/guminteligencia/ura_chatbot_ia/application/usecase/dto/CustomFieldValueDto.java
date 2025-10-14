package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomFieldValueDto {

    @JsonProperty("value")
    private Object value;

    @JsonProperty("enum_id")
    private Integer enumId;
}
