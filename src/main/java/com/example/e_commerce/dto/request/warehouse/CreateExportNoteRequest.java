package com.example.e_commerce.dto.request.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateExportNoteRequest {
    String purpose;

    @NotEmpty(message = "Danh sách sản phẩm xuất không được để trống")
    @Valid
    List<ExportNoteItemRequest> items;
}

