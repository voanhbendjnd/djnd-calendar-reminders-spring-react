package com.faptimetable.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDTO {
    private boolean success;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private int imported;
    private int updated;
    private int skipped;
}
