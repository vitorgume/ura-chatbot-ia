package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactsResponse {

    @JsonProperty("_embedded")
    private Embedded embedded;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Embedded {
        private List<ContactDto> contacts;
    }
}
