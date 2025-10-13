package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactDto {

    private Integer id;
    private String name;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("responsible_user_id")
    private Integer responsibleUserId;
    @JsonProperty("group_id")
    private Integer groupId;
    @JsonProperty("created_by")
    private Integer createdBy;
    @JsonProperty("updated_by")
    private Integer updatedBy;
    @JsonProperty("created_at")
    private Long createdAt;   // Unix timestamp
    @JsonProperty("updated_at")
    private Long updatedAt;   // Unix timestamp
    @JsonProperty("closest_task_at")
    private Long closestTaskAt; // Unix timestamp

    @JsonProperty("custom_fields_values")
    private List<CustomFieldValue> customFieldsValues;

    @JsonProperty("account_id")
    private Integer accountId;

    @JsonProperty("_embedded")
    private Embedded embedded;

    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Embedded {
        private List<Tag> tags;
        private List<CompanyRef> companies;

        // Só aparece se você usar ?with=leads
        private List<LeadRef> leads;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tag {
        private Integer id;
        private String name;
        private String color;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompanyRef {
        private Integer id;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeadRef {
        private Integer id;
    }

    // Estrutura genérica do Kommo para custom_fields_values
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomFieldValue {
        @JsonProperty("field_id")
        private Integer fieldId;
        @JsonProperty("field_code")
        private String fieldCode;
        private List<Value> values;

        @AllArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Value {
            // Nem todos têm enum; mantenha como opcionais
            @JsonProperty("enum_id")
            private Integer enumId;
            @JsonProperty("enum_code")
            private String enumCode;

            // "value" pode ser string, número, boolean ou objeto; JsonNode é flexível
            private JsonNode value;
        }
    }
}
