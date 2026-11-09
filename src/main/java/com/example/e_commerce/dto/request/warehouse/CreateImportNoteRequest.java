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
public class CreateImportNoteRequest {
    String supplier;

    @NotEmpty(message = "Danh sách sản phẩm nhập không được để trống")
    @Valid
    List<ImportNoteItemRequest> items;
}

