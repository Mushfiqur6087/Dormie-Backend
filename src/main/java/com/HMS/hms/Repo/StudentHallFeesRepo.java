package com.HMS.hms.Repo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.HMS.hms.DTO.UnpaidFeesSummaryDTO;
import com.HMS.hms.Tables.StudentHallFees;

@Repository
public interface StudentHallFeesRepo extends JpaRepository<StudentHallFees, Long> {

    // Find by feeId
    Optional<StudentHallFees> findByFeeId(Long feeId);

    // Find by userId (primary key)
    Optional<StudentHallFees> findByUserId(Long userId);

    // Find by studentId
    List<StudentHallFees> findByStudentId(Long studentId);

    // Find by student type
    List<StudentHallFees> findByStudentType(String studentType);

    // Find by year
    List<StudentHallFees> findByYear(Integer year);

    // Find by status
    List<StudentHallFees> findByStatus(StudentHallFees.PaymentStatus status);

    // Find by status as string
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.status = :status")
    List<StudentHallFees> findByStatus(@Param("status") String status);

    // Find by studentId and year
    List<StudentHallFees> findByStudentIdAndYear(Long studentId, Integer year);

    // Find by studentId and status
    List<StudentHallFees> findByStudentIdAndStatus(Long studentId, StudentHallFees.PaymentStatus status);

    // Find by student type and year
    List<StudentHallFees> findByStudentTypeAndYear(String studentType, Integer year);

    // Find by student type and status
    List<StudentHallFees> findByStudentTypeAndStatus(String studentType, StudentHallFees.PaymentStatus status);

    // Find by year and status
    List<StudentHallFees> findByYearAndStatus(Integer year, StudentHallFees.PaymentStatus status);

    // Find unpaid fees by student type
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.studentType = :studentType AND shf.status = 'UNPAID'")
    List<StudentHallFees> findUnpaidByStudentType(@Param("studentType") String studentType);

    // Find paid fees by student type
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.studentType = :studentType AND shf.status = 'PAID'")
    List<StudentHallFees> findPaidByStudentType(@Param("studentType") String studentType);

    // Find unpaid fees by year
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.year = :year AND shf.status = 'UNPAID'")
    List<StudentHallFees> findUnpaidByYear(@Param("year") Integer year);

    // Find paid fees by year
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.year = :year AND shf.status = 'PAID'")
    List<StudentHallFees> findPaidByYear(@Param("year") Integer year);

    // Check if user has unpaid hall fees
    @Query("SELECT CASE WHEN COUNT(shf) > 0 THEN true ELSE false END FROM StudentHallFees shf WHERE shf.userId = :userId AND shf.status = 'UNPAID'")
    Boolean hasUnpaidFees(@Param("userId") Long userId);

    // Check if user has paid hall fees for a specific year
    @Query("SELECT CASE WHEN COUNT(shf) > 0 THEN true ELSE false END FROM StudentHallFees shf WHERE shf.userId = :userId AND shf.year = :year AND shf.status = 'PAID'")
    Boolean hasPaidFeesForYear(@Param("userId") Long userId, @Param("year") Integer year);

    // Count fees by status
    @Query("SELECT COUNT(shf) FROM StudentHallFees shf WHERE shf.status = :status")
    Long countByStatus(@Param("status") String status);

    // Count fees by student type and status
    @Query("SELECT COUNT(shf) FROM StudentHallFees shf WHERE shf.studentType = :studentType AND shf.status = :status")
    Long countByStudentTypeAndStatus(@Param("studentType") String studentType, @Param("status") String status);

    // Count fees by year and status
    @Query("SELECT COUNT(shf) FROM StudentHallFees shf WHERE shf.year = :year AND shf.status = :status")
    Long countByYearAndStatus(@Param("year") Integer year, @Param("status") String status);

    // Find all unpaid fees
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.status = 'UNPAID'")
    List<StudentHallFees> findAllUnpaid();

    // Find all paid fees
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.status = 'PAID'")
    List<StudentHallFees> findAllPaid();

    // Find fees by multiple student types
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.studentType IN :studentTypes")
    List<StudentHallFees> findByStudentTypeIn(@Param("studentTypes") List<String> studentTypes);

    // Find fees by year range
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.year BETWEEN :startYear AND :endYear")
    List<StudentHallFees> findByYearRange(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);

    // Find recent fees (limit by year)
    @Query("SELECT shf FROM StudentHallFees shf WHERE shf.year >= :year ORDER BY shf.year DESC")
    List<StudentHallFees> findRecentFees(@Param("year") Integer year);

    // Calculate total unpaid fees for a user by joining with HallFee table
    @Query("SELECT COALESCE(SUM(hf.fee), 0) FROM StudentHallFees shf " +
           "JOIN HallFee hf ON hf.type = CASE " +
           "    WHEN LOWER(shf.studentType) = 'attached' THEN com.HMS.hms.Tables.HallFee$ResidencyType.ATTACHED " +
           "    WHEN LOWER(shf.studentType) = 'resident' THEN com.HMS.hms.Tables.HallFee$ResidencyType.RESIDENT " +
           "    ELSE com.HMS.hms.Tables.HallFee$ResidencyType.ATTACHED " +
           "END AND hf.year = shf.year " +
           "WHERE shf.userId = :userId AND shf.status = 'UNPAID'")
    BigDecimal getTotalUnpaidFeesByUserId(@Param("userId") Long userId);

    // Get unpaid fees with their amounts for a user
    @Query("SELECT shf, hf.fee FROM StudentHallFees shf " +
           "JOIN HallFee hf ON hf.type = CASE " +
           "    WHEN LOWER(shf.studentType) = 'attached' THEN com.HMS.hms.Tables.HallFee$ResidencyType.ATTACHED " +
           "    WHEN LOWER(shf.studentType) = 'resident' THEN com.HMS.hms.Tables.HallFee$ResidencyType.RESIDENT " +
           "    ELSE com.HMS.hms.Tables.HallFee$ResidencyType.ATTACHED " +
           "END AND hf.year = shf.year " +
           "WHERE shf.userId = :userId AND shf.status = 'UNPAID'")
    List<Object[]> getUnpaidFeesWithAmountByUserId(@Param("userId") Long userId);

    // Find by userId and status
    List<StudentHallFees> findByUserIdAndStatus(Long userId, StudentHallFees.PaymentStatus status);

    // Find by userId and year
    List<StudentHallFees> findByUserIdAndYear(Long userId, Integer year);

    // Get unpaid fees summary for a user (total amount, email, username)
    @Query("SELECT new com.HMS.hms.DTO.UnpaidFeesSummaryDTO(COALESCE(SUM(hf.fee), 0), u.email, u.username, 'Hall Fees') " +
           "FROM StudentHallFees shf " +
           "JOIN Users u ON u.userId = shf.userId " +
           "JOIN HallFee hf ON hf.type = CASE " +
           "    WHEN LOWER(shf.studentType) = 'attached' THEN com.HMS.hms.Tables.HallFee$ResidencyType.ATTACHED " +
           "    WHEN LOWER(shf.studentType) = 'resident' THEN com.HMS.hms.Tables.HallFee$ResidencyType.RESIDENT " +
           "    ELSE com.HMS.hms.Tables.HallFee$ResidencyType.ATTACHED " +
           "END AND hf.year = shf.year " +
           "WHERE shf.userId = :userId AND shf.status = 'UNPAID' " +
           "GROUP BY u.email, u.username")
    UnpaidFeesSummaryDTO getUnpaidFeesSummaryByUserId(@Param("userId") Long userId);
}
