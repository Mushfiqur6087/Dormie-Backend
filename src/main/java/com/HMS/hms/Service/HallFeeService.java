package com.HMS.hms.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HMS.hms.DTO.HallFeeDTO;
import com.HMS.hms.Repo.HallFeeRepo;
import com.HMS.hms.Tables.HallFee;

@Service
public class HallFeeService {

    @Autowired
    private HallFeeRepo hallFeeRepo;

    // Create a new hall fee
    public HallFee createHallFee(String type, Integer year, BigDecimal fee) {
        HallFee hallFee = new HallFee(type, year, fee);
        return hallFeeRepo.save(hallFee);
    }

    // Create a new hall fee with enum
    public HallFee createHallFee(HallFee.ResidencyType type, Integer year, BigDecimal fee) {
        HallFee hallFee = new HallFee(type, year, fee);
        return hallFeeRepo.save(hallFee);
    }

    // Save hall fee
    public HallFee saveHallFee(HallFee fee) {
        return hallFeeRepo.save(fee);
    }

    // Get all hall fees
    public List<HallFee> getAllHallFees() {
        return hallFeeRepo.findAll();
    }

    // Get hall fee by ID
    public Optional<HallFee> getHallFeeById(Long id) {
        return hallFeeRepo.findById(id);
    }

    // Get hall fees by type
    public List<HallFee> getHallFeesByType(HallFee.ResidencyType type) {
        return hallFeeRepo.findByType(type);
    }

    // Get hall fees by type (string)
    public List<HallFee> getHallFeesByType(String type) {
        return hallFeeRepo.findByTypeString(type);
    }

    // Get hall fees by year
    public List<HallFee> getHallFeesByYear(Integer year) {
        return hallFeeRepo.findByYear(year);
    }

    // Get hall fee by type and year
    public Optional<HallFee> getHallFeeByTypeAndYear(HallFee.ResidencyType type, Integer year) {
        return hallFeeRepo.findByTypeAndYear(type, year);
    }

    // Get hall fee by type and year (string)
    public Optional<HallFee> getHallFeeByTypeAndYear(String type, Integer year) {
        return hallFeeRepo.findByTypeStringAndYear(type, year);
    }

    // Get all hall fees ordered by year (newest first)
    public List<HallFee> getAllHallFeesOrderedByYear() {
        return hallFeeRepo.findAllByOrderByYearDesc();
    }

    // Get hall fees for a specific year ordered by type
    public List<HallFee> getHallFeesByYearOrderedByType(Integer year) {
        return hallFeeRepo.findByYearOrderByType(year);
    }

    // Check if hall fee exists for type and year
    public boolean hallFeeExists(HallFee.ResidencyType type, Integer year) {
        return hallFeeRepo.existsByTypeAndYear(type, year);
    }

    // Check if hall fee exists for type and year (string)
    public boolean hallFeeExists(String type, Integer year) {
        HallFee.ResidencyType residencyType = HallFee.ResidencyType.fromString(type);
        return hallFeeRepo.existsByTypeAndYear(residencyType, year);
    }

    // Update hall fee
    public HallFee updateHallFee(Long id, HallFee updatedFee) {
        Optional<HallFee> existingFee = hallFeeRepo.findById(id);
        if (existingFee.isPresent()) {
            HallFee fee = existingFee.get();
            fee.setType(updatedFee.getType());
            fee.setYear(updatedFee.getYear());
            fee.setFee(updatedFee.getFee());
            return hallFeeRepo.save(fee);
        }
        return null;
    }

    // Update or create hall fee for a specific type and year
    public HallFee updateOrCreateHallFee(String type, Integer year, BigDecimal fee) {
        Optional<HallFee> existingFee = getHallFeeByTypeAndYear(type, year);
        if (existingFee.isPresent()) {
            HallFee hallFee = existingFee.get();
            hallFee.setFee(fee);
            return hallFeeRepo.save(hallFee);
        } else {
            return createHallFee(type, year, fee);
        }
    }

    // Delete hall fee
    public boolean deleteHallFee(Long id) {
        if (hallFeeRepo.existsById(id)) {
            hallFeeRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // Delete hall fee by type and year
    public boolean deleteHallFeeByTypeAndYear(String type, Integer year) {
        Optional<HallFee> hallFee = getHallFeeByTypeAndYear(type, year);
        if (hallFee.isPresent()) {
            hallFeeRepo.delete(hallFee.get());
            return true;
        }
        return false;
    }

    // DTO Mapping Methods
    public HallFeeDTO convertToDTO(HallFee hallFee) {
        return new HallFeeDTO(
            hallFee.getId(),
            hallFee.getTypeAsString(),
            hallFee.getYear(),
            hallFee.getFee()
        );
    }

    public HallFee convertFromCreateDTO(HallFeeDTO createDTO) {
        return new HallFee(
            createDTO.getType(),
            createDTO.getYear(),
            createDTO.getFee()
        );
    }

    public List<HallFeeDTO> convertToDTOList(List<HallFee> hallFees) {
        return hallFees.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // DTO-based service methods
    public HallFeeDTO createHallFeeFromDTO(HallFeeDTO createDTO) {
        HallFee hallFee = convertFromCreateDTO(createDTO);
        HallFee savedFee = hallFeeRepo.save(hallFee);
        return convertToDTO(savedFee);
    }

    public List<HallFeeDTO> getAllHallFeesAsDTO() {
        List<HallFee> hallFees = hallFeeRepo.findAll();
        return convertToDTOList(hallFees);
    }

    public Optional<HallFeeDTO> getHallFeeByIdAsDTO(Long id) {
        return hallFeeRepo.findById(id)
            .map(this::convertToDTO);
    }

    public List<HallFeeDTO> getHallFeesByTypeAsDTO(String type) {
        HallFee.ResidencyType enumType = HallFee.ResidencyType.fromString(type);
        List<HallFee> hallFees = hallFeeRepo.findByType(enumType);
        return convertToDTOList(hallFees);
    }

    public List<HallFeeDTO> getHallFeesByYearAsDTO(Integer year) {
        List<HallFee> hallFees = hallFeeRepo.findByYear(year);
        return convertToDTOList(hallFees);
    }

    public Optional<HallFeeDTO> getHallFeeByTypeAndYearAsDTO(String type, Integer year) {
        HallFee.ResidencyType enumType = HallFee.ResidencyType.fromString(type);
        Optional<HallFee> hallFee = hallFeeRepo.findByTypeAndYear(enumType, year);
        return hallFee.map(this::convertToDTO);
    }

    public List<HallFeeDTO> getAllHallFeesOrderedByYearAsDTO() {
        List<HallFee> hallFees = hallFeeRepo.findAllByOrderByYearDesc();
        return convertToDTOList(hallFees);
    }

    public Optional<HallFeeDTO> updateHallFeeFromDTO(Long id, HallFeeDTO updateDTO) {
        Optional<HallFee> existingFeeOpt = hallFeeRepo.findById(id);
        if (existingFeeOpt.isPresent()) {
            HallFee existingFee = existingFeeOpt.get();
            existingFee.setType(updateDTO.getType());
            existingFee.setYear(updateDTO.getYear());
            existingFee.setFee(updateDTO.getFee());
            
            HallFee updatedFee = hallFeeRepo.save(existingFee);
            return Optional.of(convertToDTO(updatedFee));
        }
        return Optional.empty();
    }

    public HallFeeDTO updateOrCreateHallFeeFromDTO(String type, Integer year, BigDecimal fee) {
        Optional<HallFee> existingFee = getHallFeeByTypeAndYear(type, year);
        if (existingFee.isPresent()) {
            HallFee hallFee = existingFee.get();
            hallFee.setFee(fee);
            HallFee savedFee = hallFeeRepo.save(hallFee);
            return convertToDTO(savedFee);
        } else {
            HallFee newFee = createHallFee(type, year, fee);
            return convertToDTO(newFee);
        }
    }
}
