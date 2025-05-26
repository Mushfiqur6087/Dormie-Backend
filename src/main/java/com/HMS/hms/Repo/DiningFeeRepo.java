package com.HMS.hms.Repo;

import com.HMS.hms.Tables.DiningFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiningFeeRepo extends JpaRepository<DiningFee, Long> {

    // Find dining fees by type (attached/resident)
    List<DiningFee> findByType(String type);

    // Find dining fees by enum type
    List<DiningFee> findByType(DiningFee.ResidencyType type);

    // Find dining fees by year
    List<DiningFee> findByYear(Integer year);

    // Find dining fees by type and year
    List<DiningFee> findByTypeAndYear(String type, Integer year);

    // Find dining fees by enum type and year
    List<DiningFee> findByTypeAndYear(DiningFee.ResidencyType type, Integer year);

    // Find dining fee by type and year (using string, should return single result)
    @Query("SELECT df FROM DiningFee df WHERE LOWER(df.type) = LOWER(:type) AND df.year = :year")
    java.util.Optional<DiningFee> findByTypeStringAndYear(@Param("type") String type, @Param("year") Integer year);

    // Find dining fees within a date range
    @Query("SELECT df FROM DiningFee df WHERE df.startDate <= :endDate AND df.endDate >= :startDate")
    List<DiningFee> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find current active dining fees for a specific type
    @Query("SELECT df FROM DiningFee df WHERE df.type = :type AND :currentDate BETWEEN df.startDate AND df.endDate")
    List<DiningFee> findActiveByType(@Param("type") String type, @Param("currentDate") LocalDate currentDate);

    // Find current active dining fees for a specific enum type
    @Query("SELECT df FROM DiningFee df WHERE df.type = :type AND :currentDate BETWEEN df.startDate AND df.endDate")
    List<DiningFee> findActiveByType(@Param("type") DiningFee.ResidencyType type, @Param("currentDate") LocalDate currentDate);

    // Find all active dining fees
    @Query("SELECT df FROM DiningFee df WHERE :currentDate BETWEEN df.startDate AND df.endDate")
    List<DiningFee> findAllActive(@Param("currentDate") LocalDate currentDate);
}
