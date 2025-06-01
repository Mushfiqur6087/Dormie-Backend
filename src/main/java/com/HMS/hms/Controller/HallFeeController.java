package com.HMS.hms.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.DTO.HallFeeDTO;
import com.HMS.hms.Service.HallFeeService;

@RestController
@RequestMapping("/api/hall-fees")
@CrossOrigin(origins = "*")
public class HallFeeController {

    @Autowired
    private HallFeeService hallFeeService;

    // Create a new hall fee
    @PostMapping
    public ResponseEntity<HallFeeDTO> createHallFee(@RequestBody HallFeeDTO hallFeeDTO) {
        try {
            HallFeeDTO savedFee = hallFeeService.createHallFeeFromDTO(hallFeeDTO);
            return new ResponseEntity<>(savedFee, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all hall fees
    @GetMapping
    public ResponseEntity<List<HallFeeDTO>> getAllHallFees() {
        try {
            List<HallFeeDTO> hallFees = hallFeeService.getAllHallFeesAsDTO();
            return new ResponseEntity<>(hallFees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get hall fee by ID
    @GetMapping("/{id}")
    public ResponseEntity<HallFeeDTO> getHallFeeById(@PathVariable Long id) {
        try {
            Optional<HallFeeDTO> hallFee = hallFeeService.getHallFeeByIdAsDTO(id);
            if (hallFee.isPresent()) {
                return new ResponseEntity<>(hallFee.get(), HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get hall fees by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<HallFeeDTO>> getHallFeesByType(@PathVariable String type) {
        try {
            List<HallFeeDTO> hallFees = hallFeeService.getHallFeesByTypeAsDTO(type);
            return new ResponseEntity<>(hallFees, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get hall fees by year
    @GetMapping("/year/{year}")
    public ResponseEntity<List<HallFeeDTO>> getHallFeesByYear(@PathVariable Integer year) {
        try {
            List<HallFeeDTO> hallFees = hallFeeService.getHallFeesByYearAsDTO(year);
            return new ResponseEntity<>(hallFees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Get all hall fees ordered by year
    @GetMapping("/ordered-by-year")
    public ResponseEntity<List<HallFeeDTO>> getAllHallFeesOrderedByYear() {
        try {
            List<HallFeeDTO> hallFees = hallFeeService.getAllHallFeesOrderedByYearAsDTO();
            return new ResponseEntity<>(hallFees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
