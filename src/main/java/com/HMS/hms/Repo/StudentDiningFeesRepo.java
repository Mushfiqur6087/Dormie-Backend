package com.HMS.hms.Repo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.HMS.hms.DTO.UnpaidFeesSummaryDTO;
import com.HMS.hms.Tables.StudentDiningFees;

@Repository
public interface StudentDiningFeesRepo extends JpaRepository<StudentDiningFees, Long> {

    // Find by feeId
    Optional<StudentDiningFees> findByFeeId(Long feeId);

    // Find by userId
    List<StudentDiningFees> findByUserId(Long userId);

    // Find by studentId
    List<StudentDiningFees> findByStudentId(Long studentId);

    // Find by student type
    List<StudentDiningFees> findByStudentType(String studentType);

    // Find by year
    List<StudentDiningFees> findByYear(Integer year);

    // Find by status
    List<StudentDiningFees> findByStatus(StudentDiningFees.PaymentStatus status);

    // Find by status as string
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.status = :status")
    List<StudentDiningFees> findByStatus(@Param("status") String status);

    // Find by userId and year
    List<StudentDiningFees> findByUserIdAndYear(Long userId, Integer year);

    // Find by studentId and year
    List<StudentDiningFees> findByStudentIdAndYear(Long studentId, Integer year);

    // Find by userId and status
    List<StudentDiningFees> findByUserIdAndStatus(Long userId, StudentDiningFees.PaymentStatus status);

    // Find by studentId and status
    List<StudentDiningFees> findByStudentIdAndStatus(Long studentId, StudentDiningFees.PaymentStatus status);

    // Find by student type and year
    List<StudentDiningFees> findByStudentTypeAndYear(String studentType, Integer year);

    // Find by student type and status
    List<StudentDiningFees> findByStudentTypeAndStatus(String studentType, StudentDiningFees.PaymentStatus status);

    // Find by date range
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.startDate <= :endDate AND sdf.endDate >= :startDate")
    List<StudentDiningFees> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find active fees for a user (current date within fee period)
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.userId = :userId AND :currentDate BETWEEN sdf.startDate AND sdf.endDate")
    List<StudentDiningFees> findActiveByUserId(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);

    // Find active fees for a student
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.studentId = :studentId AND :currentDate BETWEEN sdf.startDate AND sdf.endDate")
    List<StudentDiningFees> findActiveByStudentId(@Param("studentId") Long studentId, @Param("currentDate") LocalDate currentDate);

    // Find all active fees
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE :currentDate BETWEEN sdf.startDate AND sdf.endDate")
    List<StudentDiningFees> findAllActive(@Param("currentDate") LocalDate currentDate);

    // Find unpaid fees for a user
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.userId = :userId AND sdf.status = 'UNPAID'")
    List<StudentDiningFees> findUnpaidByUserId(@Param("userId") Long userId);

    // Find unpaid fees for a student
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.studentId = :studentId AND sdf.status = 'UNPAID'")
    List<StudentDiningFees> findUnpaidByStudentId(@Param("studentId") Long studentId);

    // Find paid fees for a user
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.userId = :userId AND sdf.status = 'PAID'")
    List<StudentDiningFees> findPaidByUserId(@Param("userId") Long userId);

    // Find paid fees for a student
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.studentId = :studentId AND sdf.status = 'PAID'")
    List<StudentDiningFees> findPaidByStudentId(@Param("studentId") Long studentId);

    // Count fees by status
    @Query("SELECT COUNT(sdf) FROM StudentDiningFees sdf WHERE sdf.status = :status")
    Long countByStatus(@Param("status") String status);

    // Count fees by user and status
    @Query("SELECT COUNT(sdf) FROM StudentDiningFees sdf WHERE sdf.userId = :userId AND sdf.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    // Find fees by year and status
    List<StudentDiningFees> findByYearAndStatus(Integer year, StudentDiningFees.PaymentStatus status);

    // Find fees within date range and by status
    @Query("SELECT sdf FROM StudentDiningFees sdf WHERE sdf.startDate <= :endDate AND sdf.endDate >= :startDate AND sdf.status = :status")
    List<StudentDiningFees> findByDateRangeAndStatus(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate, 
                                                     @Param("status") String status);

    // Calculate total unpaid fees for a user by joining with DiningFee table
    @Query("SELECT COALESCE(SUM(df.fee), 0) FROM StudentDiningFees sdf " +
           "JOIN DiningFee df ON df.type = CASE " +
           "    WHEN LOWER(sdf.studentType) = 'attached' THEN com.HMS.hms.Tables.DiningFee$ResidencyType.ATTACHED " +
           "    WHEN LOWER(sdf.studentType) = 'resident' THEN com.HMS.hms.Tables.DiningFee$ResidencyType.RESIDENT " +
           "    ELSE com.HMS.hms.Tables.DiningFee$ResidencyType.ATTACHED " +
           "END AND df.year = sdf.year " +
           "AND sdf.startDate <= df.endDate AND sdf.endDate >= df.startDate " +
           "WHERE sdf.userId = :userId AND sdf.status = 'UNPAID'")
    BigDecimal getTotalUnpaidFeesByUserId(@Param("userId") Long userId);

    // Get unpaid fees with their amounts for a user
    @Query("SELECT sdf, df.fee FROM StudentDiningFees sdf " +
           "JOIN DiningFee df ON df.type = CASE " +
           "    WHEN LOWER(sdf.studentType) = 'attached' THEN com.HMS.hms.Tables.DiningFee$ResidencyType.ATTACHED " +
           "    WHEN LOWER(sdf.studentType) = 'resident' THEN com.HMS.hms.Tables.DiningFee$ResidencyType.RESIDENT " +
           "    ELSE com.HMS.hms.Tables.DiningFee$ResidencyType.ATTACHED " +
           "END AND df.year = sdf.year " +
           "AND sdf.startDate <= df.endDate AND sdf.endDate >= df.startDate " +
           "WHERE sdf.userId = :userId AND sdf.status = 'UNPAID'")
    List<Object[]> getUnpaidFeesWithAmountByUserId(@Param("userId") Long userId);

    // Get unpaid fees summary for a user (total amount, email, username)
    @Query("SELECT new com.HMS.hms.DTO.UnpaidFeesSummaryDTO(COALESCE(SUM(df.fee), 0), u.email, u.username, 'DINING', 'Dining Fees') " +
           "FROM StudentDiningFees sdf " +
           "JOIN Users u ON u.userId = sdf.userId " +
           "JOIN DiningFee df ON df.type = CASE " +
           "    WHEN LOWER(sdf.studentType) = 'attached' THEN com.HMS.hms.Tables.DiningFee$ResidencyType.ATTACHED " +
           "    WHEN LOWER(sdf.studentType) = 'resident' THEN com.HMS.hms.Tables.DiningFee$ResidencyType.RESIDENT " +
           "    ELSE com.HMS.hms.Tables.DiningFee$ResidencyType.ATTACHED " +
           "END AND df.year = sdf.year " +
           "AND sdf.startDate <= df.endDate AND sdf.endDate >= df.startDate " +
           "WHERE sdf.userId = :userId AND sdf.status = 'UNPAID' " +
           "GROUP BY u.email, u.username")
    UnpaidFeesSummaryDTO getUnpaidFeesSummaryByUserId(@Param("userId") Long userId);
}
