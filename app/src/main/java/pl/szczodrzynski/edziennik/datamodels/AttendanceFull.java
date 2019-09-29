package pl.szczodrzynski.edziennik.datamodels;

public class AttendanceFull extends Attendance {
    public String teacherFullName = "";

    public String subjectLongName = "";
    public String subjectShortName = "";

    // metadata
    public boolean seen;
    public boolean notified;
    public long addedDate;
}