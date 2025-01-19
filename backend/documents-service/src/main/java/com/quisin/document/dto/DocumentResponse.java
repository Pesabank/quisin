package com.quisin.document.dto;

import com.quisin.document.model.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String fileName;
    private String contentType;
    private DocumentType documentType;
    private byte[] content;
    private String restaurantId;
    private String generatedAt;
} 