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
import com.HMS.hms.Tables.DiningFee;

@Service
public class DiningFeeService {

    @Autowired
    private DiningFeeRepo diningFeeRepo;

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
            createDTO.getType(),
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
        return convertToDTO(savedFee);
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
        // Convert DTO string type to enum
        DiningFee.ResidencyType enumType = DiningFee.ResidencyType.fromString(type);
        List<DiningFee> diningFees = diningFeeRepo.findByType(enumType);
        return convertToDTOList(diningFees);
    }

    public List<DiningFeeDTO> getDiningFeesByYearAsDTO(Integer year) {
        List<DiningFee> diningFees = diningFeeRepo.findByYear(year);
        return convertToDTOList(diningFees);
    }

    public List<DiningFeeDTO> getDiningFeesByTypeAndYearAsDTO(String type, Integer year) {
        // Convert DTO string type to enum
        DiningFee.ResidencyType enumType = DiningFee.ResidencyType.fromString(type);
        List<DiningFee> diningFees = diningFeeRepo.findByTypeAndYear(enumType, year);
        return convertToDTOList(diningFees);
    }

    public List<DiningFeeDTO> getActiveDiningFeesByTypeAsDTO(String type) {
        // Convert DTO string type to enum
        DiningFee.ResidencyType enumType = DiningFee.ResidencyType.fromString(type);
        List<DiningFee> diningFees = diningFeeRepo.findActiveByType(enumType, LocalDate.now());
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
            existingFee.setType(updateDTO.getType());
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
        DiningFee diningFee = new DiningFee(type, year, startDate, endDate, fee);
        return diningFeeRepo.save(diningFee);
    }

    public DiningFee createDiningFee(DiningFee.ResidencyType type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        DiningFee diningFee = new DiningFee(type, year, startDate, endDate, fee);
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
        // Convert DTO string type to enum
        DiningFee.ResidencyType enumType = DiningFee.ResidencyType.fromString(type);
        return diningFeeRepo.findByType(enumType);
    }

    public List<DiningFee> getDiningFeesByYear(Integer year) {
        return diningFeeRepo.findByYear(year);
    }

    public List<DiningFee> getDiningFeesByTypeAndYear(String type, Integer year) {
        // Convert DTO string type to enum
        DiningFee.ResidencyType enumType = DiningFee.ResidencyType.fromString(type);
        return diningFeeRepo.findByTypeAndYear(enumType, year);
    }

    public List<DiningFee> getActiveDiningFeesByType(String type) {
        // Convert DTO string type to enum
        DiningFee.ResidencyType enumType = DiningFee.ResidencyType.fromString(type);
        return diningFeeRepo.findActiveByType(enumType, LocalDate.now());
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
