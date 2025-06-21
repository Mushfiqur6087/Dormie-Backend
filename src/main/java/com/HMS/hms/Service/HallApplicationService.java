package com.HMS.hms.Service;

import com.HMS.hms.DTO.HallApplicationRequest;
import com.HMS.hms.DTO.HallApplicationSummaryDTO; // Import the new Summary DTO
import com.HMS.hms.Repo.HallApplicationRepo;
import com.HMS.hms.Repo.StudentsRepo;
import com.HMS.hms.Repo.UsersRepo;
import com.HMS.hms.Tables.HallApplication;
import com.HMS.hms.Tables.Students;
import com.HMS.hms.Tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort; // Import Spring Data Sort
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger; // Ensure this is imported
import org.slf4j.LoggerFactory; // Ensure this is imported

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HallApplicationService {

    // Logger declaration for this service
    private static final Logger logger = LoggerFactory.getLogger(HallApplicationService.class);

    // Autowired Repositories and Services
    @Autowired
    private HallApplicationRepo hallApplicationRepo;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private StudentsRepo studentsRepo;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private StudentsService studentsService; // <--- ENSURE THIS IS AUTOWIRED for updating student residency

    /**
     * Processes and saves a new hall application submitted by a student.
     * Includes image storage, distance calculation, and various validations.
     *
     * @param applicationRequest The DTO containing application data (including the MultipartFile).
     * @param userId The ID of the authenticated user submitting the application (from JWT).
     * @return The saved HallApplication entity.
     * @throws IOException if there's an issue storing the image file.
     * @throws IllegalArgumentException if business validation rules are violated.
     */
    @Transactional // Ensures atomicity for DB operations. File storage is external to transaction.
    public HallApplication submitHallApplication(HallApplicationRequest applicationRequest, Long userId) throws IOException {

        // --- 1. Perform Business Validations ---
        // Validate conditional field: localRelativeAddress required if hasLocalRelative is "yes"
        if (applicationRequest.getHasLocalRelative().equalsIgnoreCase("yes") &&
                (applicationRequest.getLocalRelativeAddress() == null || applicationRequest.getLocalRelativeAddress().trim().isEmpty())) {
            throw new IllegalArgumentException("Local relative address is required if 'Yes' for local relative.");
        }

        // Validate that the authenticated user exists
        Optional<Users> userOpt = usersRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Authenticated user not found.");
        }
        Users user = userOpt.get();

        // Validate that the studentId provided in the form matches the authenticated student's record
        Optional<Students> studentOpt = studentsRepo.findByUserId(userId);
        if (studentOpt.isEmpty() || !studentOpt.get().getStudentId().equals(applicationRequest.getStudentId())) {
            throw new IllegalArgumentException("Student ID mismatch or student record not found for authenticated user.");
        }

        // Check if student already has a PENDING application (prevents duplicate active applications)
        Optional<HallApplication> existingApplication = hallApplicationRepo.findByUserId(userId);
        if (existingApplication.isPresent() && existingApplication.get().getApplicationStatus().equals("PENDING")) {
            throw new IllegalArgumentException("You already have a pending hall application. Please wait for it to be processed.");
        }


        // --- 2. Store the Image File ---
        String imagePath = null;
        MultipartFile studentImage = applicationRequest.getStudentImage(); // Get the file from the DTO
        if (studentImage != null && !studentImage.isEmpty()) {
            imagePath = fileStorageService.storeFile(studentImage); // Store the file and get its unique name/path
        } else {
            throw new IllegalArgumentException("Student image file is missing or empty.");
        }

        // --- 3. Calculate Distance from Hall ---
        BigDecimal distanceFromHall = null;
        try {
            Double distance = geocodingService.calculateDistanceToHall(applicationRequest.getPostcode());
            if (distance != null) {
                // Convert double to BigDecimal and round to 2 decimal places for precise storage
                distanceFromHall = BigDecimal.valueOf(distance).setScale(2, BigDecimal.ROUND_HALF_UP);
                logger.info("Calculated distance for user {} (postcode {}): {} km", user.getUsername(), applicationRequest.getPostcode(), distanceFromHall);
            } else {
                logger.warn("Could not calculate distance for postcode: {}. Saving as null.", applicationRequest.getPostcode());
                // Decide strategy: throw error, log warning, or save null. Currently saves null.
            }
        } catch (Exception e) {
            logger.error("Error calculating distance for postcode {}: {}", applicationRequest.getPostcode(), e.getMessage(), e);
            // Decide strategy: throw error, log warning. Currently logs.
        }


        // --- 4. Create and Populate HallApplication Entity ---
        HallApplication application = new HallApplication();
        application.setUserId(userId); // Link application to the user's primary ID
        application.setUser(user); // Set the Users entity relationship (for convenience in JPA)
        application.setStudentIdNo(applicationRequest.getStudentId()); // The university-assigned student ID from the form
        application.setCollege(applicationRequest.getCollege());
        application.setCollegeLocation(applicationRequest.getCollegeLocation());
        application.setFamilyIncome(applicationRequest.getFamilyIncome()); // BigDecimal from DTO
        application.setDistrict(applicationRequest.getDistrict());
        application.setPostcode(applicationRequest.getPostcode());
        application.setStudentImagePath(imagePath); // Store the path to the saved image
        // Convert "yes"/"no" string from frontend to Boolean for database storage
        application.setHasLocalRelative(applicationRequest.getHasLocalRelative().equalsIgnoreCase("yes"));
        application.setLocalRelativeAddress(applicationRequest.getLocalRelativeAddress());
        application.setApplicationDate(LocalDateTime.now()); // Set current timestamp
        application.setApplicationStatus("PENDING"); // Default status upon submission
        application.setDistanceFromHallKm(distanceFromHall);
        // NOTE: application.setApplicationType() is removed as per clarification that all accepted are 'resident'

        // --- 5. Save the HallApplication entity to the database ---
        return hallApplicationRepo.save(application);
    }


    /**
     * Retrieves all hall applications, optionally sorted by a specific field and order.
     * This is used for the Provost's list view.
     * @param sortBy Field to sort by (e.g., "familyIncome", "distanceFromHallKm", "applicationDate", "studentIdNo").
     * @param sortOrder "asc" for ascending, "desc" for descending.
     * @return List of HallApplicationSummaryDTOs.
     */
    public List<HallApplicationSummaryDTO> getAllHallApplicationSummaries(String sortBy, String sortOrder) {
        Sort sort = Sort.unsorted(); // Default: no specific sort if params are invalid/missing

        // Define a whitelist of allowed sortable fields to prevent injection attacks
        List<String> allowedSortFields = List.of("familyIncome", "distanceFromHallKm", "applicationDate", "studentIdNo");
        if (sortBy != null && allowedSortFields.contains(sortBy)) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        } else {
            // Default sort order if no valid sort field is provided: newest applications first
            sort = Sort.by(Sort.Direction.DESC, "applicationDate");
        }

        // Fetch applications from the repository with the determined sort order
        List<HallApplication> applications;
        if (sort.isUnsorted()) { // If no specific sort was effectively requested
            applications = hallApplicationRepo.findAll();
        } else {
            applications = hallApplicationRepo.findAll(sort); // Fetch with sorting applied
        }

        // Convert HallApplication entities to HallApplicationSummaryDTOs for the list view
        // The convertToSummaryDTO method handles fetching the username if needed.
        return applications.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a HallApplication entity to a HallApplicationSummaryDTO.
     * Includes a lookup for username if the 'user' object is not eagerly loaded.
     * @param application The HallApplication entity.
     * @return HallApplicationSummaryDTO.
     */
    private HallApplicationSummaryDTO convertToSummaryDTO(HallApplication application) {
        String username = null;
        // Try to get username directly from the 'user' association if it's already loaded by JPA
        if (application.getUser() != null) {
            username = application.getUser().getUsername();
        } else {
            // Fallback: If 'user' association is not eagerly loaded (default for @OneToOne can be lazy),
            // look up the user explicitly by userId to get the username.
            Optional<Users> userOpt = usersRepo.findById(application.getUserId());
            if (userOpt.isPresent()) {
                username = userOpt.get().getUsername();
            }
        }

        return new HallApplicationSummaryDTO(
                application.getApplicationId(),
                application.getStudentIdNo(),
                username, // Student's username for the summary list
                application.getApplicationStatus(),
                application.getFamilyIncome(),
                application.getDistanceFromHallKm()
        );
    }

    /**
     * Retrieves a single HallApplication entity by its ID (for Provost's detail view).
     * @param applicationId The ID of the application.
     * @return Optional containing the HallApplication entity if found.
     */
    public Optional<HallApplication> getHallApplicationById(Long applicationId) {
        // findById should fetch the full entity with all its fields.
        return hallApplicationRepo.findById(applicationId);
    }

    /**
     * Accepts a hall application. This method sets the application status to APPROVED
     * and updates the corresponding student's residency status in the Students table to "resident".
     *
     * @param applicationId The ID of the application to accept.
     * @return The updated HallApplication entity.
     * @throws IllegalArgumentException if the application is not found or is not in a PENDING status.
     */
    @Transactional // Ensures atomicity: if student update fails, application status change is rolled back.
    public HallApplication acceptHallApplication(Long applicationId) {
        Optional<HallApplication> applicationOpt = hallApplicationRepo.findById(applicationId);

        if (applicationOpt.isEmpty()) {
            throw new IllegalArgumentException("Application not found with ID: " + applicationId);
        }

        HallApplication application = applicationOpt.get();

        // Check current status: only PENDING applications can be accepted
        if (!"PENDING".equalsIgnoreCase(application.getApplicationStatus())) {
            throw new IllegalArgumentException("Application with ID " + applicationId + " is already " + application.getApplicationStatus() + ". Only PENDING applications can be accepted.");
        }

        // --- 1. Update HallApplication status to APPROVED ---
        application.setApplicationStatus("APPROVED");
        HallApplication updatedApplication = hallApplicationRepo.save(application);
        logger.info("Application {} accepted. User ID: {}", applicationId, application.getUserId());

        // --- 2. Update the corresponding student's residency status to "resident" ---
        Optional<Students> studentOpt = studentsRepo.findByUserId(application.getUserId());
        if (studentOpt.isPresent()) {
            Students student = studentOpt.get();

            // Per clarification: All accepted applications mean the student becomes 'resident'
            student.setResidencyStatus("resident");

            studentsService.saveStudent(student); // Use studentsService to save the updated student entity
            logger.info("Student {} (User ID {}) residency status updated to '{}'",
                    student.getStudentId(), student.getUserId(), student.getResidencyStatus());
        } else {
            logger.warn("Could not find student record for accepted application with user ID: {}. Data inconsistency.", application.getUserId());
            // Decide error handling: log, throw exception (which would roll back app acceptance), etc.
            // Current approach logs a warning and proceeds with application acceptance.
        }

        return updatedApplication;
    }

    /**
     * Rejects a hall application, setting its status to REJECTED.
     *
     * @param applicationId The ID of the application to reject.
     * @return The updated HallApplication entity.
     * @throws IllegalArgumentException if the application is not found or is not in a PENDING status.
     */
    @Transactional // Ensures atomicity for DB operation.
    public HallApplication rejectHallApplication(Long applicationId) {
        Optional<HallApplication> applicationOpt = hallApplicationRepo.findById(applicationId);

        if (applicationOpt.isEmpty()) {
            throw new IllegalArgumentException("Application not found with ID: " + applicationId);
        }

        HallApplication application = applicationOpt.get();

        // Ensure only PENDING applications can be rejected
        if (!"PENDING".equalsIgnoreCase(application.getApplicationStatus())) {
            throw new IllegalArgumentException("Application with ID " + applicationId + " is already " + application.getApplicationStatus() + ". Only PENDING applications can be rejected.");
        }

        // Update application status to REJECTED
        application.setApplicationStatus("REJECTED");
        HallApplication updatedApplication = hallApplicationRepo.save(application);
        logger.info("Application {} rejected. User ID: {}", applicationId, application.getUserId());

        return updatedApplication;
    }
}