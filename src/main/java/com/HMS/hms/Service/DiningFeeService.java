package com.HMS.hms.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HMS.hms.DTO.DiningFeeDTO;
import com.HMS.hms.Repo.DiningFeeRepo;
import com.HMS.hms.Repo.StudentDiningFeesRepo;
import com.HMS.hms.Repo.UsersRepo;
import com.HMS.hms.Tables.DiningFee;
import com.HMS.hms.Tables.StudentDiningFees;
import com.HMS.hms.Tables.Students;
import com.HMS.hms.Tables.Users;
import com.HMS.hms.Tables.DiningFee.ResidencyType; // Ensure this is imported

@Service
public class DiningFeeService {

    @Autowired
    private DiningFeeRepo diningFeeRepo;

    @Autowired
    private StudentDiningFeesRepo studentDiningFeesRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private StudentsService studentsService;

    // DTO Mapping Methods
    public DiningFeeDTO convertToDTO(DiningFee diningFee) {
        return new DiningFeeDTO(
                diningFee.getId(),
                diningFee.getTypeAsString().toUpperCase(),
                diningFee.getYear(),
                diningFee.getStartDate(),
                diningFee.getEndDate(),
                diningFee.getFee()
        );
    }

    public DiningFee convertFromCreateDTO(DiningFeeDTO createDTO) {
        return new DiningFee(
                "resident", // Always resident for dining fees
                createDTO.getYear(),
                createDTO.getStartDate(),
                createDTO.getEndDate(),
                createDTO.getFee()
        );
    }

    public List<DiningFeeDTO> convertToDTOList(List<DiningFee> diningFees) {
        return diningFees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // DTO-based service methods
    public DiningFeeDTO createDiningFeeFromDTO(DiningFeeDTO createDTO) {
        // Before creating, check if a fee for this year already exists
        // This is important to prevent duplicate entries if type+year should be unique
        Optional<DiningFee> existingFee = diningFeeRepo.findByTypeAndYear(ResidencyType.RESIDENT, createDTO.getYear());
        if (existingFee.isPresent()) {
            throw new IllegalArgumentException("Dining fee for year " + createDTO.getYear() + " already exists. Use update endpoint instead.");
        }

        DiningFee diningFee = convertFromCreateDTO(createDTO);
        DiningFee savedFee = diningFeeRepo.save(diningFee);

        createStudentDiningFeesForAllStudents(savedFee);

        return convertToDTO(savedFee);
    }

    /**
     * Creates StudentDiningFees entries for resident students only when a new dining fee is created.
     * Only resident students get dining fees, attached students do not.
     */
    private void createStudentDiningFeesForAllStudents(DiningFee diningFee) {
        List<Users> students = usersRepo.findByRole("STUDENT");

        for (Users student : students) {
            Optional<Students> studentDetails = studentsService.findByUserId(student.getUserId());

            if (studentDetails.isPresent()) {
                Students studentInfo = studentDetails.get();

                if ("resident".equalsIgnoreCase(studentInfo.getResidencyStatus())) {
                    StudentDiningFees studentFee = new StudentDiningFees(
                            student.getUserId(),
                            studentInfo.getStudentId(),
                            studentInfo.getResidencyStatus(),
                            diningFee.getYear(),
                            diningFee.getStartDate(),
                            diningFee.getEndDate(),
                            StudentDiningFees.PaymentStatus.UNPAID
                    );
                    studentDiningFeesRepo.save(studentFee);
                }
            }
        }
    }

    public List<DiningFeeDTO> getAllDiningFeesAsDTO() {
        List<DiningFee> diningFees = diningFeeRepo.findAll();
        return convertToDTOList(diningFees);
    }

    public Optional<DiningFeeDTO> getDiningFeeByIdAsDTO(Long id) {
        return diningFeeRepo.findById(id)
                .map(this::convertToDTO);
    }

    // --- FIX START ---
    // This method now correctly retrieves by type and converts to DTO list
    public List<DiningFeeDTO> getDiningFeesByTypeAsDTO(String type) {
        // Type is always RESIDENT for dining fees, so explicitly query for RESIDENT
        List<DiningFee> diningFees = diningFeeRepo.findByType(ResidencyType.RESIDENT);
        return convertToDTOList(diningFees);
    }

    // This method now correctly retrieves by year and converts to DTO list
    public List<DiningFeeDTO> getDiningFeesByYearAsDTO(Integer year) {
        List<DiningFee> diningFees = diningFeeRepo.findByYear(year);
        return convertToDTOList(diningFees);
    }

    // This method now correctly retrieves by type and year (using Optional) and converts
    public Optional<DiningFeeDTO> getDiningFeeByTypeAndYearAsDTO(String type, Integer year) {
        // Type is always RESIDENT for dining fees, so explicitly query for RESIDENT
        Optional<DiningFee> diningFee = diningFeeRepo.findByTypeAndYear(ResidencyType.RESIDENT, year);
        return diningFee.map(this::convertToDTO);
    }
    // --- FIX END ---

    public List<DiningFeeDTO> getActiveDiningFeesByTypeAsDTO(String type) {
        List<DiningFee> diningFees = diningFeeRepo.findActiveByType(ResidencyType.RESIDENT, LocalDate.now());
        return convertToDTOList(diningFees);
    }

    public List<DiningFeeDTO> getAllActiveDiningFeesAsDTO() {
        List<DiningFee> diningFees = getAllActiveDiningFees();
        return convertToDTOList(diningFees);
    }

    public List<DiningFeeDTO> getDiningFeesInDateRangeAsDTO(LocalDate startDate, LocalDate endDate) {
        List<DiningFee> diningFees = getDiningFeesInDateRange(startDate, endDate);
        return convertToDTOList(diningFees);
    }

    public Optional<DiningFeeDTO> updateDiningFeeFromDTO(Long id, DiningFeeDTO updateDTO) {
        Optional<DiningFee> existingFeeOpt = diningFeeRepo.findById(id);
        if (existingFeeOpt.isPresent()) {
            DiningFee existingFee = existingFeeOpt.get();
            existingFee.setType(ResidencyType.RESIDENT); // Always resident for dining fees
            existingFee.setYear(updateDTO.getYear());
            existingFee.setStartDate(updateDTO.getStartDate());
            existingFee.setEndDate(updateDTO.getEndDate());
            existingFee.setFee(updateDTO.getFee());

            DiningFee updatedFee = diningFeeRepo.save(existingFee);
            return Optional.of(convertToDTO(updatedFee));
        }
        return Optional.empty();
    }

    public DiningFeeDTO updateOrCreateHallFeeFromDTO(String type, Integer year, BigDecimal fee) {
        // Use the correct method name and logic from DiningFeeService
        Optional<DiningFee> existingFee = diningFeeRepo.findByTypeAndYear(ResidencyType.RESIDENT, year); // Use the correct repo method
        if (existingFee.isPresent()) {
            DiningFee diningFee = existingFee.get();
            diningFee.setFee(fee);
            DiningFee savedFee = diningFeeRepo.save(diningFee);
            return convertToDTO(savedFee);
        } else {
            // Create a new DiningFee (not HallFee)
            DiningFee newFee = new DiningFee(ResidencyType.RESIDENT, year, LocalDate.now(), LocalDate.now().plusYears(1), fee); // Provide start/end dates
            DiningFee savedFee = diningFeeRepo.save(newFee);
            return convertToDTO(savedFee);
        }
    }

    // Original entity-based methods
    public List<DiningFee> getAllDiningFees() {
        return diningFeeRepo.findAll();
    }

    public Optional<DiningFee> getDiningFeeById(Long id) {
        return diningFeeRepo.findById(id);
    }

    // --- FIX START ---
    // Correctly handle the Optional from repo
    public List<DiningFee> getDiningFeesByType(String type) {
        // Always RESIDENT, so call the correct repo method
        return diningFeeRepo.findByType(ResidencyType.RESIDENT);
    }

    public List<DiningFee> getDiningFeesByYear(Integer year) {
        return diningFeeRepo.findByYear(year);
    }


    // --- FIX END ---

    public List<DiningFee> getActiveDiningFeesByType(String type) {
        return diningFeeRepo.findActiveByType(ResidencyType.RESIDENT, LocalDate.now());
    }

    public List<DiningFee> getAllActiveDiningFees() {
        return diningFeeRepo.findAllActive(LocalDate.now());
    }

    public List<DiningFee> getDiningFeesInDateRange(LocalDate startDate, LocalDate endDate) {
        return diningFeeRepo.findByDateRange(startDate, endDate);
    }

    public DiningFee updateDiningFee(Long id, DiningFee updatedFee) {
        Optional<DiningFee> existingFee = diningFeeRepo.findById(id);
        if (existingFee.isPresent()) {
            DiningFee fee = existingFee.get();
            fee.setType(updatedFee.getType()); // Should be ResidencyType.RESIDENT
            fee.setYear(updatedFee.getYear());
            fee.setStartDate(updatedFee.getStartDate());
            fee.setEndDate(updatedFee.getEndDate());
            fee.setFee(updatedFee.getFee());
            return diningFeeRepo.save(fee);
        }
        return null;
    }

    public boolean deleteDiningFee(Long id) {
        if (diningFeeRepo.existsById(id)) {
            diningFeeRepo.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Removes duplicate StudentDiningFees records, keeping only the first record for each userId-year combination.
     * This method is intended to clean up existing duplicates caused by the previous bug.
     */
    public void removeDuplicateStudentDiningFees() {
        List<Users> students = usersRepo.findByRole("STUDENT");

        for (Users student : students) {
            List<StudentDiningFees> studentFees = studentDiningFeesRepo.findByUserId(student.getUserId());

            studentFees.stream()
                    .collect(Collectors.groupingBy(StudentDiningFees::getYear))
                    .forEach((year, feesForYear) -> {
                        if (feesForYear.size() > 1) {
                            for (int i = 1; i < feesForYear.size(); i++) {
                                studentDiningFeesRepo.delete(feesForYear.get(i));
                            }

                            System.out.println("Removed " + (feesForYear.size() - 1) +
                                    " duplicate dining fee records for student " + student.getUserId() +
                                    " in year " + year);
                        }
                    });
        }
    }
}