package vn.huy.clinic.dto.Doctor.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.huy.clinic.model.DoctorSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponse {

    private DayOfWeek dow;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;

    public static ScheduleResponse fromEntity(DoctorSchedule schedule) {
        return ScheduleResponse.builder()
                .dow(schedule.getDow())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .isAvailable(schedule.getIsAvailable())
                .build();
    }
}
