package com.HMS.hms.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.DTO.DiningFeeDTO;
import com.HMS.hms.Service.DiningFeeService;

@RestController
@RequestMapping("/api/dining-fees")
@CrossOrigin(origins = "*")
public class DiningFeeController {

    @Autowired
    private DiningFeeService diningFeeService;

    // Create a new dining fee
    @PostMapping
    public ResponseEntity<DiningFeeDTO> createDiningFee(@RequestBody DiningFeeDTO createDiningFeeDTO) {
        try {
            DiningFeeDTO savedFee = diningFeeService.createDiningFeeFromDTO(createDiningFeeDTO);
            return new ResponseEntity<>(savedFee, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all dining fees
    @GetMapping
    public ResponseEntity<List<DiningFeeDTO>> getAllDiningFees() {
        try {
            List<DiningFeeDTO> diningFees = diningFeeService.getAllDiningFeesAsDTO();
            return new ResponseEntity<>(diningFees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get dining fee by ID
    @GetMapping("/{id}")
    public ResponseEntity<DiningFeeDTO> getDiningFeeById(@PathVariable Long id) {
        try {
            Optional<DiningFeeDTO> diningFee = diningFeeService.getDiningFeeByIdAsDTO(id);
            if (diningFee.isPresent()) {
                return new ResponseEntity<>(diningFee.get(), HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get dining fees by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<DiningFeeDTO>> getDiningFeesByType(@PathVariable String type) {
        try {
            List<DiningFeeDTO> diningFees = diningFeeService.getDiningFeesByTypeAsDTO(type);
            return new ResponseEntity<>(diningFees, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get dining fees by year
    @GetMapping("/year/{year}")
    public ResponseEntity<List<DiningFeeDTO>> getDiningFeesByYear(@PathVariable Integer year) {
        try {
            List<DiningFeeDTO> diningFees = diningFeeService.getDiningFeesByYearAsDTO(year);
            return new ResponseEntity<>(diningFees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get dining fees by type and year
    @GetMapping("/type/{type}/year/{year}")
    public ResponseEntity<List<DiningFeeDTO>> getDiningFeesByTypeAndYear(@PathVariable String type, @PathVariable Integer year) {
        try {
            List<DiningFeeDTO> diningFees = diningFeeService.getDiningFeesByTypeAndYearAsDTO(type, year);
            return new ResponseEntity<>(diningFees, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get currently active dining fees for a specific type
    @GetMapping("/active/type/{type}")
    public ResponseEntity<List<DiningFeeDTO>> getActiveDiningFeesByType(@PathVariable String type) {
        try {
            List<DiningFeeDTO> diningFees = diningFeeService.getActiveDiningFeesByTypeAsDTO(type);
            return new ResponseEntity<>(diningFees, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all currently active dining fees
    @GetMapping("/active")
    public ResponseEntity<List<DiningFeeDTO>> getAllActiveDiningFees() {
        try {
            List<DiningFeeDTO> diningFees = diningFeeService.getAllActiveDiningFeesAsDTO();
            return new ResponseEntity<>(diningFees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get dining fees within a date range
    @GetMapping("/date-range")
    public ResponseEntity<List<DiningFeeDTO>> getDiningFeesInDateRange(
            @RequestParam String startDate, 
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<DiningFeeDTO> diningFees = diningFeeService.getDiningFeesInDateRangeAsDTO(start, end);
            return new ResponseEntity<>(diningFees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update dining fee
    @PutMapping("/{id}")
    public ResponseEntity<DiningFeeDTO> updateDiningFee(@PathVariable Long id, @RequestBody DiningFeeDTO updateDTO) {
        try {
            Optional<DiningFeeDTO> updatedFee = diningFeeService.updateDiningFeeFromDTO(id, updateDTO);
            if (updatedFee.isPresent()) {
                return new ResponseEntity<>(updatedFee.get(), HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete dining fee
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDiningFee(@PathVariable Long id) {
        try {
            boolean deleted = diningFeeService.deleteDiningFee(id);
            if (deleted) {
                return new ResponseEntity<>("Dining fee deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Dining fee not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting dining fee", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
