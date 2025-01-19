package com.quisin.document.dto;

import com.quisin.document.model.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    private String tableNumber;
    private String startDate;
    private String endDate;
    private String format;
    private String restaurantName;
    private String restaurantLocation;
} 