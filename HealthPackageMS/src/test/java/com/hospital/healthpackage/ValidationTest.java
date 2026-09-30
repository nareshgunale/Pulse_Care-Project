package com.hospital.healthpackage;

import com.hospital.healthpackage.dto.BookingRequest;
import com.hospital.healthpackage.dto.HealthPackageRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ─── HealthPackageRequest validation tests ────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("✅ Valid package request passes all validations")
    void validPackageRequest_shouldPassValidation() {
        HealthPackageRequest request = HealthPackageRequest.builder()
                .name("Cancer Package Females Above 40 Yrs")
                .description("Comprehensive cancer screening for women over 40.")
                .price(new BigDecimal("2420.00"))
                .category("Cancer")
                .idealFor("Female")
                .ageGroup("40+")
                .parameters(7)
                .tests(List.of("Haemogram", "Pap Smear", "Mammography"))
                .build();

        Set<ConstraintViolation<HealthPackageRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no violations but got: " + violations);
    }

    @Test
    @Order(2)
    @DisplayName("❌ Blank package name fails validation")
    void blankPackageName_shouldFailValidation() {
        HealthPackageRequest request = HealthPackageRequest.builder()
                .name("")
                .description("Valid description here.")
                .price(new BigDecimal("1000"))
                .category("Cancer")
                .idealFor("Both")
                .ageGroup("30+")
                .parameters(3)
                .tests(List.of("Test1"))
                .build();

        Set<ConstraintViolation<HealthPackageRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @Order(3)
    @DisplayName("❌ Negative price fails validation")
    void negativePrice_shouldFailValidation() {
        HealthPackageRequest request = HealthPackageRequest.builder()
                .name("Valid Name")
                .description("Valid description here.")
                .price(new BigDecimal("-100"))
                .category("Cardiac")
                .idealFor("Male")
                .ageGroup("30+")
                .parameters(3)
                .tests(List.of("ECG"))
                .build();

        Set<ConstraintViolation<HealthPackageRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @Order(4)
    @DisplayName("❌ Invalid idealFor value fails validation")
    void invalidGender_shouldFailValidation() {
        HealthPackageRequest request = HealthPackageRequest.builder()
                .name("Test Package")
                .description("Valid description here.")
                .price(new BigDecimal("500"))
                .category("General")
                .idealFor("Unknown")   // must be Male/Female/Both
                .ageGroup("All")
                .parameters(2)
                .tests(List.of("Test1"))
                .build();

        Set<ConstraintViolation<HealthPackageRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("idealFor")));
    }

    @Test
    @Order(5)
    @DisplayName("❌ Empty tests list fails validation")
    void emptyTests_shouldFailValidation() {
        HealthPackageRequest request = HealthPackageRequest.builder()
                .name("Test Package")
                .description("Valid description here.")
                .price(new BigDecimal("500"))
                .category("General")
                .idealFor("Both")
                .ageGroup("All")
                .parameters(0)         // min 1
                .tests(List.of())      // must not be empty
                .build();

        Set<ConstraintViolation<HealthPackageRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    // ─── BookingRequest validation tests ─────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("✅ Valid booking request passes all validations")
    void validBookingRequest_shouldPassValidation() {
        BookingRequest request = BookingRequest.builder()
                .packageId(1L)
                .patientName("Rohan Patil")
                .phone("9876543210")
                .email("rohan@example.com")
                .preferredDate(LocalDate.now().plusDays(3))
                .timeSlot("10:00 AM - 11:00 AM")
                .build();

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no violations but got: " + violations);
    }

    @Test
    @Order(7)
    @DisplayName("❌ Invalid phone number fails validation")
    void invalidPhone_shouldFailValidation() {
        BookingRequest request = BookingRequest.builder()
                .packageId(1L)
                .patientName("Rohan Patil")
                .phone("1234567890")  // must start with 6-9
                .email("rohan@example.com")
                .preferredDate(LocalDate.now().plusDays(3))
                .timeSlot("10:00 AM")
                .build();

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @Order(8)
    @DisplayName("❌ Past date fails booking validation")
    void pastDate_shouldFailValidation() {
        BookingRequest request = BookingRequest.builder()
                .packageId(1L)
                .patientName("Rohan Patil")
                .phone("9876543210")
                .email("rohan@example.com")
                .preferredDate(LocalDate.now().minusDays(1))  // must be future
                .timeSlot("10:00 AM")
                .build();

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("preferredDate")));
    }

    @Test
    @Order(9)
    @DisplayName("❌ Invalid email fails booking validation")
    void invalidEmail_shouldFailValidation() {
        BookingRequest request = BookingRequest.builder()
                .packageId(1L)
                .patientName("Rohan Patil")
                .phone("9876543210")
                .email("not-an-email")
                .preferredDate(LocalDate.now().plusDays(2))
                .timeSlot("10:00 AM")
                .build();

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @Order(10)
    @DisplayName("❌ Null packageId fails booking validation")
    void nullPackageId_shouldFailValidation() {
        BookingRequest request = BookingRequest.builder()
                .packageId(null)
                .patientName("Rohan Patil")
                .phone("9876543210")
                .email("rohan@example.com")
                .preferredDate(LocalDate.now().plusDays(2))
                .timeSlot("10:00 AM")
                .build();

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("packageId")));
    }
}
