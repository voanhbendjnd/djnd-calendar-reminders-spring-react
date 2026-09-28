package com.faptimetable.backend.parser;

import com.faptimetable.backend.dto.TimetableEntryDTO;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FapTimetableParserTest {

    @Test
    void testParseHTML() {
        String html = """
            <div class="table-responsive d-none d-md-block weekly-timetable-grid">
                <table class="table table-sm table-bordered align-middle mb-0">
                    <thead class="table-light">
                        <tr class="align-middle">
                            <th scope="col" class="text-center">Slot</th>
                            <th scope="col" class="text-center ">Thứ 2 (05/10)</th>
                            <th scope="col" class="text-center ">Thứ 3 (06/10)</th>
                            <th scope="col" class="text-center ">Thứ 4 (07/10)</th>
                            <th scope="col" class="text-center ">Thứ 5 (08/10)</th>
                            <th scope="col" class="text-center ">Thứ 6 (09/10)</th>
                            <th scope="col" class="text-center ">Thứ 7 (10/10)</th>
                            <th scope="col" class="text-center ">Chủ nhật (11/10)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <th scope="row" class="text-center fw-semibold">1</th>
                            <td>
                                <div class="position-relative">
                                    <div class="fw-semibold text-primary weekly-timetable-cell-title" title="Lớp học">
                                        <i class="ri-book-open-line"></i> SWD392 - SE1910
                                    </div>
                                    <div class="small text-muted" title="Giờ bắt đầu - Giờ kết thúc">
                                        <span class="fw-bold text-warning-emphasis">07h00-09h15</span> -
                                        <span title="Học online">Offline</span>
                                    </div>
                                    <div class="small text-muted" title="Phòng">R.A505</div>
                                    <div class="small text-muted" title="Giảng viên">tuvh6</div>
                                </div>
                            </td>
                            <td><span class="text-muted">-</span></td>
                            <td><span class="text-muted">-</span></td>
                            <td>
                                <div class="position-relative">
                                    <div class="fw-semibold text-primary weekly-timetable-cell-title" title="Lớp học">
                                        <i class="ri-book-open-line"></i> SBA301 - SE1910
                                    </div>
                                    <div class="small text-muted" title="Giờ bắt đầu - Giờ kết thúc">
                                        <span class="fw-bold text-warning-emphasis">07h00-09h15</span> -
                                        <span class="text-success" title="Học online">Online</span>
                                        <a class="btn btn-outline-success" href="https://teams.microsoft.com/l/meetup" title="Link Meet">Link Meet</a>
                                    </div>
                                    <div class="small text-muted" title="Phòng">R.ON19</div>
                                    <div class="small text-muted" title="Giảng viên">phucpt10</div>
                                </div>
                            </td>
                            <td><span class="text-muted">-</span></td>
                            <td><span class="text-muted">-</span></td>
                            <td><span class="text-muted">-</span></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        """;

        FapTimetableParser parser = new FapTimetableParser();
        List<TimetableEntryDTO> entries = parser.parse(html);

        assertEquals(2, entries.size());

        TimetableEntryDTO entry1 = entries.get(0);
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 10, 5), entry1.getDate());
        assertEquals(1, entry1.getSlot());
        assertEquals("SWD392", entry1.getSubjectCode());
        assertEquals("SE1910", entry1.getClassName());
        assertEquals(LocalTime.of(7, 0), entry1.getStartTime());
        assertEquals(LocalTime.of(9, 15), entry1.getEndTime());
        assertEquals("OFFLINE", entry1.getMode());
        assertEquals("R.A505", entry1.getRoom());
        assertEquals("tuvh6", entry1.getLecturer());
        assertNull(entry1.getMeetingUrl());

        TimetableEntryDTO entry2 = entries.get(1);
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 10, 8), entry2.getDate());
        assertEquals(1, entry2.getSlot());
        assertEquals("SBA301", entry2.getSubjectCode());
        assertEquals("SE1910", entry2.getClassName());
        assertEquals(LocalTime.of(7, 0), entry2.getStartTime());
        assertEquals(LocalTime.of(9, 15), entry2.getEndTime());
        assertEquals("ONLINE", entry2.getMode());
        assertEquals("R.ON19", entry2.getRoom());
        assertEquals("phucpt10", entry2.getLecturer());
        assertEquals("https://teams.microsoft.com/l/meetup", entry2.getMeetingUrl());
    }
}
