package com.HMS.hms.Service;


import java.time.LocalDate;

import java.util.List;

import java.util.Optional;

import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import com.HMS.hms.DTO.StudentDiningFeeDTO;

import com.HMS.hms.Repo.StudentDiningFeesRepo;

import com.HMS.hms.Tables.StudentDiningFees;


@Service

public class StudentDiningFeeService {


    @Autowired

    private StudentDiningFeesRepo studentDiningFeesRepo;


// DTO Conversion Methods

    private StudentDiningFeeDTO convertToDTO(StudentDiningFees entity) {

        return new StudentDiningFeeDTO(

                entity.getFeeId(),

                entity.getStudentId(),

                entity.getStudentType(),

                entity.getYear(),

                entity.getStartDate(),

                entity.getEndDate(),

                entity.getStatus().toString()

        );

    }


    private List<StudentDiningFeeDTO> convertToDTOList(List<StudentDiningFees> entities) {

        return entities.stream()

                .map(this::convertToDTO)

                .collect(Collectors.toList());

    }


// DTO-based service methods

    public List<StudentDiningFeeDTO> getAllStudentDiningFeesDTO() {

        return convertToDTOList(studentDiningFeesRepo.findAll());

    }


    public Optional<StudentDiningFeeDTO> getStudentDiningFeeByIdDTO(Long id) {

        return studentDiningFeesRepo.findById(id)

                .map(this::convertToDTO);

    }


    public List<StudentDiningFeeDTO> getStudentDiningFeesByUserIdDTO(Long userId) {

        return convertToDTOList(studentDiningFeesRepo.findByUserId(userId));

    }


    public List<StudentDiningFeeDTO> getStudentDiningFeesByStudentIdDTO(Long studentId) {

        return convertToDTOList(studentDiningFeesRepo.findByStudentId(studentId));

    }


    public List<StudentDiningFeeDTO> getStudentDiningFeesByYearDTO(Integer year) {

        return convertToDTOList(studentDiningFeesRepo.findByYear(year));

    }


    public List<StudentDiningFeeDTO> getStudentDiningFeesByTypeDTO(String studentType) {

        return convertToDTOList(studentDiningFeesRepo.findByStudentType(studentType));

    }


    public List<StudentDiningFeeDTO> getStudentDiningFeesByStatusDTO(String status) {

        return convertToDTOList(studentDiningFeesRepo.findByStatus(status));

    }


    public List<StudentDiningFeeDTO> getUnpaidStudentDiningFeesByUserIdDTO(Long userId) {

        return convertToDTOList(studentDiningFeesRepo.findUnpaidByUserId(userId));

    }


    public List<StudentDiningFeeDTO> getPaidStudentDiningFeesByUserIdDTO(Long userId) {

        return convertToDTOList(studentDiningFeesRepo.findPaidByUserId(userId));

    }


    public List<StudentDiningFeeDTO> getActiveStudentDiningFeesDTO() {

        LocalDate currentDate = LocalDate.now();

        return convertToDTOList(studentDiningFeesRepo.findAllActive(currentDate));

    }


    public List<StudentDiningFeeDTO> getActiveStudentDiningFeesByStudentIdDTO(Long studentId) {

        LocalDate currentDate = LocalDate.now();

        return convertToDTOList(studentDiningFeesRepo.findActiveByStudentId(studentId, currentDate));

    }


    public List<StudentDiningFeeDTO> getStudentDiningFeesByUserIdAndYearDTO(Long userId, Integer year) {

        return convertToDTOList(studentDiningFeesRepo.findByUserIdAndYear(userId, year));

    }


    public List<StudentDiningFeeDTO> getStudentDiningFeesByYearAndStatusDTO(Integer year, StudentDiningFees.PaymentStatus status) {

        return convertToDTOList(studentDiningFeesRepo.findByYearAndStatus(year, status));

    }


// Original entity-based methods (keeping for backward compatibility)

// Get all student dining fees

    public List<StudentDiningFees> getAllStudentDiningFees() {

        return studentDiningFeesRepo.findAll();

    }


// Get student dining fee by ID

    public Optional<StudentDiningFees> getStudentDiningFeeById(Long id) {

        return studentDiningFeesRepo.findById(id);

    }


// Get student dining fees by user ID

    public List<StudentDiningFees> getStudentDiningFeesByUserId(Long userId) {

        return studentDiningFeesRepo.findByUserId(userId);

    }


// Get student dining fees by student ID

    public List<StudentDiningFees> getStudentDiningFeesByStudentId(Long studentId) {

        return studentDiningFeesRepo.findByStudentId(studentId);

    }


// Get student dining fees by year

    public List<StudentDiningFees> getStudentDiningFeesByYear(Integer year) {

        return studentDiningFeesRepo.findByYear(year);

    }


// Get student dining fees by student type

    public List<StudentDiningFees> getStudentDiningFeesByType(String studentType) {

        return studentDiningFeesRepo.findByStudentType(studentType);

    }


// Get student dining fees by payment status

    public List<StudentDiningFees> getStudentDiningFeesByStatus(String status) {

        return studentDiningFeesRepo.findByStatus(status);

    }


// Get unpaid student dining fees for a specific user

    public List<StudentDiningFees> getUnpaidStudentDiningFeesByUserId(Long userId) {

        return studentDiningFeesRepo.findUnpaidByUserId(userId);

    }


// Get paid student dining fees for a specific user

    public List<StudentDiningFees> getPaidStudentDiningFeesByUserId(Long userId) {

        return studentDiningFeesRepo.findPaidByUserId(userId);

    }


// Get active student dining fees (current date within fee period)

    public List<StudentDiningFees> getActiveStudentDiningFees() {

        LocalDate currentDate = LocalDate.now();

        return studentDiningFeesRepo.findAllActive(currentDate);

    }


// Get active student dining fees by student ID

    public List<StudentDiningFees> getActiveStudentDiningFeesByStudentId(Long studentId) {

        LocalDate currentDate = LocalDate.now();

        return studentDiningFeesRepo.findActiveByStudentId(studentId, currentDate);

    }


// Get student dining fees by user ID and year

    public List<StudentDiningFees> getStudentDiningFeesByUserIdAndYear(Long userId, Integer year) {

        return studentDiningFeesRepo.findByUserIdAndYear(userId, year);

    }


// Get student dining fees by year and status

    public List<StudentDiningFees> getStudentDiningFeesByYearAndStatus(Integer year, StudentDiningFees.PaymentStatus status) {

        return studentDiningFeesRepo.findByYearAndStatus(year, status);

    }

}