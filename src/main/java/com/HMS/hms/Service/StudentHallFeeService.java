package com.HMS.hms.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HMS.hms.DTO.StudentHallFeeDTO;
import com.HMS.hms.Repo.HallFeeRepo;
import com.HMS.hms.Repo.StudentHallFeesRepo;
import com.HMS.hms.Tables.HallFee;
import com.HMS.hms.Tables.StudentHallFees;

@Service
public class StudentHallFeeService {

    @Autowired
    private StudentHallFeesRepo studentHallFeesRepo;

    @Autowired
    private HallFeeRepo hallFeeRepo;

    // DTO Conversion Methods
    public StudentHallFeeDTO convertToDTO(StudentHallFees studentHallFee) {
        // Find the base HallFee amount for this student's type and year
        Optional<HallFee> hallFeeOpt = hallFeeRepo.findByTypeAndYear(
                HallFee.ResidencyType.fromString(studentHallFee.getStudentType()),
                studentHallFee.getYear()
        );
        // Get the base fee, default to ZERO if not found (e.g., fee not set for that year/type)
        BigDecimal baseFeeAmount = hallFeeOpt.map(HallFee::getFee).orElse(BigDecimal.ZERO);

        // Calculate the due amount based on status
        BigDecimal calculatedDueAmount = BigDecimal.ZERO;
        if ("unpaid".equalsIgnoreCase(studentHallFee.getStatusAsString())) {
            calculatedDueAmount = baseFeeAmount;
        }

        // --- FIX IS HERE: Add studentHallFee.getUserId() as the second argument ---
        return new StudentHallFeeDTO(
                studentHallFee.getFeeId(),
                studentHallFee.getStudentId(),
                studentHallFee.getStudentType(),
                studentHallFee.getYear(),
                studentHallFee.getStatusAsString(),
                calculatedDueAmount // Pass the calculated due amount
        );
        // --- END FIX ---
    }

    private List<StudentHallFeeDTO> convertToDTOList(List<StudentHallFees> entities) {
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Optional<StudentHallFeeDTO> convertToOptionalDTO(Optional<StudentHallFees> entity) {
        return entity.map(this::convertToDTO);
    }

    // DTO-based service methods
    public List<StudentHallFeeDTO> getAllStudentHallFeesDTO() {
        return convertToDTOList(studentHallFeesRepo.findAll());
    }

    public Optional<StudentHallFeeDTO> getStudentHallFeeByIdDTO(Long id) {
        return convertToOptionalDTO(studentHallFeesRepo.findById(id));
    }

    public Optional<StudentHallFeeDTO> getStudentHallFeesByUserIdDTO(Long userId) {
        return convertToOptionalDTO(studentHallFeesRepo.findByUserId(userId));
    }

    public List<StudentHallFeeDTO> getStudentHallFeesByStudentIdDTO(Long studentId) {
        return convertToDTOList(studentHallFeesRepo.findByStudentId(studentId));
    }

    public List<StudentHallFeeDTO> getStudentHallFeesByYearDTO(Integer year) {
        return convertToDTOList(studentHallFeesRepo.findByYear(year));
    }

    public List<StudentHallFeeDTO> getStudentHallFeesByTypeDTO(String studentType) {
        return convertToDTOList(studentHallFeesRepo.findByStudentType(studentType));
    }

    public List<StudentHallFeeDTO> getStudentHallFeesByStatusDTO(String status) {
        return convertToDTOList(studentHallFeesRepo.findByStatus(status));
    }

    public List<StudentHallFeeDTO> getUnpaidStudentHallFeesDTO() {
        return convertToDTOList(studentHallFeesRepo.findAllUnpaid());
    }

    public List<StudentHallFeeDTO> getPaidStudentHallFeesDTO() {
        return convertToDTOList(studentHallFeesRepo.findAllPaid());
    }

    public List<StudentHallFeeDTO> getUnpaidStudentHallFeesByUserIdDTO(Long userId) {
        return convertToDTOList(studentHallFeesRepo.findByUserIdAndStatus(userId, StudentHallFees.PaymentStatus.UNPAID));
    }

    public List<StudentHallFeeDTO> getPaidStudentHallFeesByUserIdDTO(Long userId) {
        return convertToDTOList(studentHallFeesRepo.findByUserIdAndStatus(userId, StudentHallFees.PaymentStatus.PAID));
    }

    public List<StudentHallFeeDTO> getStudentHallFeesByUserIdAndYearDTO(Long userId, Integer year) {
        return convertToDTOList(studentHallFeesRepo.findByUserIdAndYear(userId, year));
    }

    public List<StudentHallFeeDTO> getStudentHallFeesByYearAndStatusDTO(Integer year, StudentHallFees.PaymentStatus status) {
        return convertToDTOList(studentHallFeesRepo.findByYearAndStatus(year, status));
    }

    // Original entity-based methods (keeping for backward compatibility)
    public List<StudentHallFees> getAllStudentHallFees() {
        return studentHallFeesRepo.findAll();
    }

    public Optional<StudentHallFees> getStudentHallFeeById(Long id) {
        return studentHallFeesRepo.findById(id);
    }

    public Optional<StudentHallFees> getStudentHallFeesByUserId(Long userId) {
        return studentHallFeesRepo.findByUserId(userId);
    }

    public List<StudentHallFees> getStudentHallFeesByStudentId(Long studentId) {
        return studentHallFeesRepo.findByStudentId(studentId);
    }

    public List<StudentHallFees> getStudentHallFeesByYear(Integer year) {
        return studentHallFeesRepo.findByYear(year);
    }

    public List<StudentHallFees> getStudentHallFeesByType(String studentType) {
        return studentHallFeesRepo.findByStudentType(studentType);
    }

    public List<StudentHallFees> getStudentHallFeesByStatus(String status) {
        return studentHallFeesRepo.findByStatus(status);
    }

    public List<StudentHallFees> getUnpaidStudentHallFees() {
        return studentHallFeesRepo.findAllUnpaid();
    }

    public List<StudentHallFees> getPaidStudentHallFees() {
        return studentHallFeesRepo.findAllPaid();
    }

    public List<StudentHallFees> getUnpaidStudentHallFeesByUserId(Long userId) {
        return studentHallFeesRepo.findByUserIdAndStatus(userId, StudentHallFees.PaymentStatus.UNPAID);
    }

    public List<StudentHallFees> getPaidStudentHallFeesByUserId(Long userId) {
        return studentHallFeesRepo.findByUserIdAndStatus(userId, StudentHallFees.PaymentStatus.PAID);
    }

    public List<StudentHallFees> getStudentHallFeesByUserIdAndYear(Long userId, Integer year) {
        return studentHallFeesRepo.findByUserIdAndYear(userId, year);
    }

    public List<StudentHallFees> getStudentHallFeesByYearAndStatus(Integer year, StudentHallFees.PaymentStatus status) {
        return studentHallFeesRepo.findByYearAndStatus(year, status);
    }
}