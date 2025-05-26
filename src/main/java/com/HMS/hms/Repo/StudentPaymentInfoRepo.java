package com.HMS.hms.Repo;

import com.HMS.hms.Tables.StudentPaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPaymentInfoRepo extends JpaRepository<StudentPaymentInfo, Long> {

    // Find by feeId
    Optional<StudentPaymentInfo> findByFeeId(Long feeId);

    // Find by transaction ID
    Optional<StudentPaymentInfo> findByTranId(String tranId);

    // Find by validation ID
    Optional<StudentPaymentInfo> findByValId(String valId);

    // Find by fee type
    List<StudentPaymentInfo> findByFeeType(StudentPaymentInfo.FeeType feeType);

    // Find by fee type as string
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.feeType = :feeType")
    List<StudentPaymentInfo> findByFeeType(@Param("feeType") String feeType);

    // Find by payment method
    List<StudentPaymentInfo> findByPaymentMethod(String paymentMethod);

    // Find hall fee payments
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.feeType = 'HALL'")
    List<StudentPaymentInfo> findHallPayments();

    // Find dining fee payments
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.feeType = 'DINING'")
    List<StudentPaymentInfo> findDiningPayments();

    // Find payments by transaction ID pattern (for batch queries)
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.tranId LIKE :pattern")
    List<StudentPaymentInfo> findByTranIdPattern(@Param("pattern") String pattern);

    // Find payments by specific payment methods
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.paymentMethod IN :methods")
    List<StudentPaymentInfo> findByPaymentMethodIn(@Param("methods") List<String> methods);

    // Check if transaction exists
    @Query("SELECT CASE WHEN COUNT(spi) > 0 THEN true ELSE false END FROM StudentPaymentInfo spi WHERE spi.tranId = :tranId")
    Boolean existsByTranId(@Param("tranId") String tranId);

    // Check if validation ID exists
    @Query("SELECT CASE WHEN COUNT(spi) > 0 THEN true ELSE false END FROM StudentPaymentInfo spi WHERE spi.valId = :valId")
    Boolean existsByValId(@Param("valId") String valId);

    // Count payments by fee type
    @Query("SELECT COUNT(spi) FROM StudentPaymentInfo spi WHERE spi.feeType = :feeType")
    Long countByFeeType(@Param("feeType") String feeType);

    // Count payments by payment method
    Long countByPaymentMethod(String paymentMethod);

    // Find recent payments (ordered by feeId descending, assuming auto-increment)
    @Query("SELECT spi FROM StudentPaymentInfo spi ORDER BY spi.feeId DESC")
    List<StudentPaymentInfo> findRecentPayments();

    // Find payments by multiple fee types
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.feeType IN :feeTypes")
    List<StudentPaymentInfo> findByFeeTypeIn(@Param("feeTypes") List<String> feeTypes);

    // Find payments with non-null transaction IDs (successful SSLCommerz payments)
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.tranId IS NOT NULL")
    List<StudentPaymentInfo> findPaymentsWithTransactionId();

    // Find payments with non-null validation IDs
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.valId IS NOT NULL")
    List<StudentPaymentInfo> findPaymentsWithValidationId();

    // Find payments without transaction ID (potentially failed or manual payments)
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.tranId IS NULL")
    List<StudentPaymentInfo> findPaymentsWithoutTransactionId();

    // Find duplicate transaction IDs (for audit purposes)
    @Query("SELECT spi.tranId FROM StudentPaymentInfo spi GROUP BY spi.tranId HAVING COUNT(spi.tranId) > 1")
    List<String> findDuplicateTransactionIds();

    // Find payments by fee type and payment method
    List<StudentPaymentInfo> findByFeeTypeAndPaymentMethod(StudentPaymentInfo.FeeType feeType, String paymentMethod);

    // Find payments by fee type and payment method (string version)
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.feeType = :feeType AND spi.paymentMethod = :paymentMethod")
    List<StudentPaymentInfo> findByFeeTypeAndPaymentMethod(@Param("feeType") String feeType, 
                                                          @Param("paymentMethod") String paymentMethod);

    // Statistics: Get payment method distribution
    @Query("SELECT spi.paymentMethod, COUNT(spi) FROM StudentPaymentInfo spi GROUP BY spi.paymentMethod")
    List<Object[]> getPaymentMethodStatistics();

    // Statistics: Get fee type distribution
    @Query("SELECT spi.feeType, COUNT(spi) FROM StudentPaymentInfo spi GROUP BY spi.feeType")
    List<Object[]> getFeeTypeStatistics();

    // Find payments by ID range (for pagination or batch processing)
    @Query("SELECT spi FROM StudentPaymentInfo spi WHERE spi.feeId BETWEEN :startId AND :endId")
    List<StudentPaymentInfo> findByIdRange(@Param("startId") Long startId, @Param("endId") Long endId);
}
