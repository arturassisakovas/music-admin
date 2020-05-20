package com.mAdmin.controller;

import com.mAdmin.enumerator.ContractType;
import com.mAdmin.enumerator.MedicalCertificateStatus;
import com.mAdmin.repository.AttendanceRepository;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.repository.MedicalCertificateRepository;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Attendance;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.MedicalCertificate;
import com.mAdmin.service.EmailService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Controller
@Scope(value = "view")
public class AttendanceController {

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private AttendanceRepository attendanceRepository;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private EmailService emailService;

    
    @Autowired
    private TeacherGroupsController teacherGroupsController;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private MedicalCertificateRepository medicalCertificateRepository;

    
    private Group group;

    
    private List<Attendance> attendanceList = new ArrayList<>();

    
    private LocalDate workoutsDate;

    
    private String workoutTime;

    
    private int workoutHour;

    
    private boolean selectAllPresent, selectAllMissing;

    
    private boolean enableEdit = false;

    
    private boolean disableSave;

    
    private static final int ABSENCES_3 = 3;

    
    private static final int ABSENCES_5 = 5;

    
    private String healthProblemDescription;

    
    private ArrayList<Boolean> displayNewcomes = new ArrayList<>();

    
    private Client client;

    
    private MedicalCertificate certificate;

    
    private boolean validCertificate;

    
    public void onLoad() {
        group = Faces.getSessionAttribute("group");
        workoutsDate = Faces.getSessionAttribute("workoutsDate");
        workoutTime = Faces.getSessionAttribute("workoutTime");
        workoutHour = Faces.getSessionAttribute("workoutHour");
        fillAttendanceList();
        setCheckBoxes();
        enableEdit = false;
        disableSave = true;
    }

    
    private void fillAttendanceList() {
        

        List<Attendance> groupAttendance = attendanceRepository.findByGroup(group);
        for (Attendance attendance : groupAttendance) {
            if (attendance.getWorkoutDate().isEqual(workoutsDate) && attendance.getWorkoutHour() == workoutHour) {
                attendanceList.add(attendance);
            }
        }
        if ((attendanceList).isEmpty()) {
            if (!group.getAttendees().isEmpty()) {
                List<Attendee> attendees = attendeeRepository.findByGroupAndWorkoutStartDateLessThanQuery(group,
                        workoutsDate);
                attendanceList = new ArrayList<>();
                for (Attendee attendee : attendees) {
                    Attendance attendance = new Attendance();
                    attendance.setAttendee(attendee);
                    attendance.setGroup(group);
                    attendance.setWorkoutDate(workoutsDate);
                    attendance.setWorkoutHour(workoutHour);
                    attendance.setIsPresent(null);
                    checkIfNewcomer(attendance);
                    attendanceList.add(attendance);
                }
            }
        }
    }



    
    private void checkIfAttendeeIsMissing() {

        
        List<Attendance> attendeeAttendance;
        for (Attendance attendance : attendanceList) {
            int absences = 0;
            if (!attendance.getIsPresent() && group.getWorkoutsPerWeek().getValue() == 1) {
                attendeeAttendance = attendanceRepository
                        .findFirst3ByAttendeeOrderByWorkoutDateDescWorkoutHourDesc(attendance.getAttendee());
                for (Attendance pastAttendance : attendeeAttendance) {
                    if (!pastAttendance.getIsPresent()) {
                        absences++;
                    }
                }
                if (absences == ABSENCES_3) {
                    attendance.getAttendee().setIsMissing(true);
                    attendeeRepository.save(attendance.getAttendee());
                }
            } else if (!attendance.getIsPresent() && group.getWorkoutsPerWeek().getValue() == 2) {
                attendeeAttendance = attendanceRepository
                        .findFirst5ByAttendeeOrderByWorkoutDateDescWorkoutHourDesc(attendance.getAttendee());
                for (Attendance pastAttendance : attendeeAttendance) {
                    if (!pastAttendance.getIsPresent()) {
                        absences++;
                    }
                }
                if (absences == ABSENCES_5) {
                    attendance.getAttendee().setIsMissing(true);
                    attendeeRepository.save(attendance.getAttendee());
                }
            }
        }

    }

    
    public void setAllAsPresent() {
        if (selectAllPresent) {
            for (Attendance attendance : attendanceList) {
                attendance.setIsPresent(true);
            }
            selectAllMissing = false;
            disableSave = false;
        } else {
            for (Attendance attendance : attendanceList) {
                attendance.setIsPresent(null);
            }
            disableSave = true;
        }
    }

    
    public void setAllAsMissing() {
        if (selectAllMissing) {
            for (Attendance attendance : attendanceList) {
                attendance.setIsPresent(false);
            }
            selectAllPresent = false;
            disableSave = false;
        } else {
            for (Attendance attendance : attendanceList) {
                attendance.setIsPresent(null);
            }
            disableSave = true;
        }
    }

    
    public void setCheckBoxes() {
        int presenceCounter = 0;
        int absenceCounter = 0;
        int nullCounter = 0;
        for (Attendance attendance : attendanceList) {
            if (attendance.getIsPresent() != null && attendance.getIsPresent()) {
                presenceCounter++;
            } else if (attendance.getIsPresent() != null && !attendance.getIsPresent()) {
                absenceCounter++;
            } else if (attendance.getIsPresent() == null) {
                nullCounter++;
            }
        }
        if (attendanceList.size() == presenceCounter) {
            selectAllPresent = true;
            selectAllMissing = false;
        } else if (attendanceList.size() == absenceCounter) {
            selectAllMissing = true;
            selectAllPresent = false;
        } else {
            selectAllMissing = false;
            selectAllPresent = false;
        }
        disableSave = nullCounter >= 1;
    }

    
    public void enableEditing() {
        enableEdit = true;
    }

    
    public void disableEditing() {
        enableEdit = false;
        attendanceList.clear();
        fillAttendanceList();
        setCheckBoxes();
    }


    
    private void checkIfNewcomer(Attendance attendance) {
        Contract contract = contractRepository.findByAttendeeAndValidFromBeforeAndValidToAfterAndType(
                attendance.getAttendee(), LocalDate.now(), LocalDate.now(), ContractType.ACTIVE);
        if (contract != null) {
            if (contract.getValidFrom().plusWeeks(2).isAfter(LocalDate.now())
                    || contract.getValidFrom().plusWeeks(2).isEqual(LocalDate.now())) {
                displayNewcomes.add(true);
            } else {
                displayNewcomes.add(false);
            }
        } else {
            displayNewcomes.add(false);
        }
    }

    
    public void findCertificateExpireDate(Attendee attendee) {
        List<MedicalCertificate> certificates = medicalCertificateRepository
                .findByStatusAndValidToBeforeAndAttendeeEquals(MedicalCertificateStatus.APPROVED, workoutsDate,
                        attendee);
        if (certificates.isEmpty()) {
            certificate = null;
            setValidCertificate(false);
        } else {
            certificate = certificates.get(0);
            setValidCertificate(true);
        }
        setClient(attendee.getClient());
        setHealthProblemDescription(attendee.getHealthProblems());
    }

    
    private void checkIfAttendeeIsPresent() {
        for (Attendance attendance : attendanceList) {
            if (attendance.getAttendee().getIsMissing() && attendance.getIsPresent()) {
                if (group.getWorkoutsPerWeek().getValue() == 1) {
                    attendance.getAttendee().setIsMissing(false);
                    attendeeRepository.save(attendance.getAttendee());
                } else if (group.getWorkoutsPerWeek().getValue() == 2) {
                    List<Attendance> attendeeAttendance = attendanceRepository
                            .findFirst2ByAttendeeOrderByWorkoutDateDescWorkoutHourDesc(attendance.getAttendee());
                    int attendedWorkouts = 0;
                    for (Attendance pastAttendance : attendeeAttendance) {
                        if (pastAttendance.getIsPresent()) {
                            attendedWorkouts++;
                        }
                    }
                    if (attendedWorkouts == 2) {
                        attendance.getAttendee().setIsMissing(false);
                        attendeeRepository.save(attendance.getAttendee());
                    }
                }
            }
        }
    }

    
    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    
    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    
    public LocalDate getWorkoutsDate() {
        return workoutsDate;
    }

    
    public void setWorkoutsDate(LocalDate workoutsDate) {
        this.workoutsDate = workoutsDate;
    }

    
    public String getWorkoutTime() {
        return workoutTime;
    }

    
    public void setWorkoutTime(String workoutTime) {
        this.workoutTime = workoutTime;
    }

    
    public boolean getSelectAllPresent() {
        return selectAllPresent;
    }

    
    public void setSelectAllPresent(boolean selectAllPresent) {
        this.selectAllPresent = selectAllPresent;
    }

    
    public boolean getSelectAllMissing() {
        return selectAllMissing;
    }

    
    public void setSelectAllMissing(boolean selectAllMissing) {
        this.selectAllMissing = selectAllMissing;
    }

    
    public Group getGroup() {
        return group;
    }

    
    public void setGroup(Group group) {
        this.group = group;
    }

    
    public boolean isEnableEdit() {
        return enableEdit;
    }

    
    public void setEnableEdit(boolean enableEdit) {
        this.enableEdit = enableEdit;
    }

    
    public boolean isDisableSave() {
        return disableSave;
    }

    
    public void setDisableSave(boolean disableSave) {
        this.disableSave = disableSave;
    }

    
    public String getHealthProblemDescription() {
        return healthProblemDescription;
    }

    
    public void setHealthProblemDescription(String healthProblemDescription) {
        this.healthProblemDescription = healthProblemDescription;
    }

    
    public ArrayList<Boolean> getDisplayNewcomes() {
        return displayNewcomes;
    }

    
    public void setDisplayNewcomes(ArrayList<Boolean> displayNewcomes) {
        this.displayNewcomes = displayNewcomes;
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public MedicalCertificate getCertificate() {
        return certificate;
    }

    
    public void setCertificate(MedicalCertificate certificate) {
        this.certificate = certificate;
    }

    
    public boolean isValidCertificate() {
        return validCertificate;
    }

    
    public void setValidCertificate(boolean validCertificate) {
        this.validCertificate = validCertificate;
    }

}
