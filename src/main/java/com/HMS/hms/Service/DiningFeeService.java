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

@Service
public class DiningFeeService {

    @Autowired
    private DiningFeeRepo diningFeeRepo;
    
    @Autowired
    private UsersRepo usersRepo;
    
    @Autowired
    private StudentDiningFeesRepo studentDiningFeesRepo;
    
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
        DiningFee diningFee = convertFromCreateDTO(createDTO);
        DiningFee savedFee = diningFeeRepo.save(diningFee);
        
        // Automatically create StudentDiningFees entries for all students
        createStudentDiningFeesForAllStudents(savedFee);
        
        return convertToDTO(savedFee);
    }
    
    /**
     * Creates StudentDiningFees entries for resident students only when a new dining fee is created.
     * Only resident students get dining fees, attached students do not.
     */
    private void createStudentDiningFeesForAllStudents(DiningFee diningFee) {
        // Get all users with STUDENT role
        List<Users> students = usersRepo.findByRole("STUDENT");
        
        for (Users student : students) {
            // Get student details to check residency status
            Optional<Students> studentDetails = studentsService.findByUserId(student.getUserId());
            
            if (studentDetails.isPresent()) {
                Students studentInfo = studentDetails.get();
                
                // Only create dining fee entry for resident students
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
                // Skip attached students - they don't get dining fees
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

    public List<DiningFeeDTO> getDiningFeesByTypeAsDTO(String type) {
        // Type is always RESIDENT for dining fees, ignore the parameter
        List<DiningFee> diningFees = diningFeeRepo.findAll();
        return convertToDTOList(diningFees);
    }

    public List<DiningFeeDTO> getDiningFeesByYearAsDTO(Integer year) {
        List<DiningFee> diningFees = diningFeeRepo.findByYear(year);
        return convertToDTOList(diningFees);
    }

    public List<DiningFeeDTO> getDiningFeesByTypeAndYearAsDTO(String type, Integer year) {
        // Type is always RESIDENT for dining fees, ignore the type parameter
        List<DiningFee> diningFees = diningFeeRepo.findByYear(year);
        return convertToDTOList(diningFees);
    }

    public List<DiningFeeDTO> getActiveDiningFeesByTypeAsDTO(String type) {
        // Type is always RESIDENT for dining fees, ignore the type parameter
        List<DiningFee> diningFees = diningFeeRepo.findActiveByType(DiningFee.ResidencyType.RESIDENT, LocalDate.now());
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
            existingFee.setType("resident"); // Always resident for dining fees
            existingFee.setYear(updateDTO.getYear());
            existingFee.setStartDate(updateDTO.getStartDate());
            existingFee.setEndDate(updateDTO.getEndDate());
            existingFee.setFee(updateDTO.getFee());
            
            DiningFee updatedFee = diningFeeRepo.save(existingFee);
            return Optional.of(convertToDTO(updatedFee));
        }
        return Optional.empty();
    }

    // Legacy entity-based methods (kept for backward compatibility)
    public DiningFee createDiningFee(String type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        DiningFee diningFee = new DiningFee("resident", year, startDate, endDate, fee); // Always resident
        return diningFeeRepo.save(diningFee);
    }

    public DiningFee createDiningFee(DiningFee.ResidencyType type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        DiningFee diningFee = new DiningFee(DiningFee.ResidencyType.RESIDENT, year, startDate, endDate, fee); // Always resident
        return diningFeeRepo.save(diningFee);
    }

    public DiningFee saveDiningFee(DiningFee fee) {
        return diningFeeRepo.save(fee);
    }

    public List<DiningFee> getAllDiningFees() {
        return diningFeeRepo.findAll();
    }

    public Optional<DiningFee> getDiningFeeById(Long id) {
        return diningFeeRepo.findById(id);
    }

    public List<DiningFee> getDiningFeesByType(String type) {
        // Type is always RESIDENT for dining fees, ignore the parameter
        return diningFeeRepo.findByType(DiningFee.ResidencyType.RESIDENT);
    }

    public List<DiningFee> getDiningFeesByYear(Integer year) {
        return diningFeeRepo.findByYear(year);
    }

    public List<DiningFee> getDiningFeesByTypeAndYear(String type, Integer year) {
        // Type is always RESIDENT for dining fees, ignore the type parameter
        return diningFeeRepo.findByTypeAndYear(DiningFee.ResidencyType.RESIDENT, year);
    }

    public List<DiningFee> getActiveDiningFeesByType(String type) {
        // Type is always RESIDENT for dining fees, ignore the type parameter
        return diningFeeRepo.findActiveByType(DiningFee.ResidencyType.RESIDENT, LocalDate.now());
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
            fee.setType(updatedFee.getType());
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
}
