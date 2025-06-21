package com.HMS.hms.Repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Import Optional

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.HMS.hms.Tables.DiningFee;
import com.HMS.hms.Tables.DiningFee.ResidencyType; // Import ResidencyType enum

@Repository
public interface DiningFeeRepo extends JpaRepository<DiningFee, Long> {

    // Find dining fees by type (String) - if needed, but consider using Enum version
    List<DiningFee> findByType(String type);

    // Find dining fees by enum type
    List<DiningFee> findByType(ResidencyType type);

    // Find dining fees by year
    List<DiningFee> findByYear(Integer year);

    // --- CRITICAL FIX START ---
    // Modify existing findByTypeAndYear methods to return Optional<DiningFee>
    // Assuming a type and year combination should be unique for a single fee record.

    // Changed String type to return Optional
    Optional<DiningFee> findByTypeAndYear(String type, Integer year);

    // Changed Enum type to return Optional
    Optional<DiningFee> findByTypeAndYear(ResidencyType type, Integer year);
    // --- CRITICAL FIX END ---

    // Find dining fee by type and year (using string, should return single result)
    // This is explicitly a @Query, so let's keep it as is, but ensure its return type is Optional
    @Query("SELECT df FROM DiningFee df WHERE LOWER(df.type) = LOWER(:type) AND df.year = :year")
    Optional<DiningFee> findByTypeStringAndYear(@Param("type") String type, @Param("year") Integer year);

    // Find dining fees within a date range
    @Query("SELECT df FROM DiningFee df WHERE df.startDate <= :endDate AND df.endDate >= :startDate")
    List<DiningFee> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find current active dining fees for a specific type (String)
    @Query("SELECT df FROM DiningFee df WHERE LOWER(df.type) = LOWER(:type) AND :currentDate BETWEEN df.startDate AND df.endDate")
    List<DiningFee> findActiveByType(@Param("type") String type, @Param("currentDate") LocalDate currentDate);

    // Find current active dining fees for a specific enum type
    @Query("SELECT df FROM DiningFee df WHERE df.type = :type AND :currentDate BETWEEN df.startDate AND df.endDate")
    List<DiningFee> findActiveByType(@Param("type") ResidencyType type, @Param("currentDate") LocalDate currentDate);

    // Find all active dining fees
    @Query("SELECT df FROM DiningFee df WHERE :currentDate BETWEEN df.startDate AND df.endDate")
    List<DiningFee> findAllActive(@Param("currentDate") LocalDate currentDate);
}