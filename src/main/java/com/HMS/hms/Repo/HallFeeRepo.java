package com.HMS.hms.Repo;

import com.HMS.hms.Tables.HallFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallFeeRepo extends JpaRepository<HallFee, Long> {

    // Find hall fees by type
    List<HallFee> findByType(HallFee.ResidencyType type);

    // Find hall fees by year
    List<HallFee> findByYear(Integer year);

    // Find hall fee by type and year (should be unique)
    Optional<HallFee> findByTypeAndYear(HallFee.ResidencyType type, Integer year);

    // Find hall fees by type (using string)
    @Query("SELECT h FROM HallFee h WHERE h.type = :type")
    List<HallFee> findByTypeString(@Param("type") String type);

    // Find hall fee by type and year (using strings)
    @Query("SELECT h FROM HallFee h WHERE h.type = :type AND h.year = :year")
    Optional<HallFee> findByTypeStringAndYear(@Param("type") String type, @Param("year") Integer year);

    // Find all hall fees ordered by year descending
    List<HallFee> findAllByOrderByYearDesc();

    // Find all hall fees for a specific year ordered by type
    List<HallFee> findByYearOrderByType(Integer year);

    // Check if hall fee exists for a specific type and year
    boolean existsByTypeAndYear(HallFee.ResidencyType type, Integer year);
}
