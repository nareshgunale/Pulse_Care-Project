package com.ai_service.tool;

import com.ai_service.agent.UserContext;
import com.ai_service.client.AppointmentClient;
import com.ai_service.client.ProfileClient;
import com.ai_service.dto.BookAppointmentRequest;
import com.ai_service.util.DateTimeFormater;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P; //Gemini parameter mapping
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * - A LangChain4j Tool class that gives the AI access to appointment operations.
 * <p>
 * Why use it?
 * - By default, an LLM only knows information from its internate (training) data and
 * cannot access live application data.
 * - This class allows the AI to fetch real-time appointment information
 * by calling backend microservices.
 * <p>
 * What problem ?
 * - Enables the AI to perform real actions such as viewing appointments,
 * checking available slots, and cancelling appointments instead of
 * giving generic answers.
 * <p>
 * How work?
 * - When a user's question requires appointment data, LangChain4j
 * automatically selects the @Tool method.
 * - The tool retrieves the logged-in user's ID from UserContext,
 * calls the required microservices, and returns the result to the AI.
 * - The AI then uses this data to generate a natural language response.
 */

@Component
@RequiredArgsConstructor
public class AppointmentTool {

    private final AppointmentClient appointmentClient;
    private final ProfileClient profileClient;

    /**
     * Returns all appointments of the logged-in patient.
     * <p>
     * How does it work?
     * 1. Reads the logged-in user's ID from UserContext.
     * 2. Gets the corresponding patient ID from Profile Service.
     * 3. Fetches appointments from Appointment Service.
     * 4. Returns the data to the AI Assistant.
     */
    @Tool("Get all appointments of the logged-in patient.")
    public String getMyAppointments() {
        Long userId = UserContext.getUserId();
        Long patientId = profileClient.getPatientIdByUserId(userId);
        return appointmentClient.getAllAppointmentsByPatient(patientId);
    }

    /**
     * Returns detailed information for a specific appointment.
     */
    @Tool("Get details of a specific appointment. Use this when user asks about a particular appointment ID.")
    public String getAppointmentDetails(@P("The unique ID of the appointment") Long appointmentId) {
        Long userId = UserContext.getUserId();
        Long patientId = profileClient.getPatientIdByUserId(userId);
        String details = appointmentClient.getAppointmentDetails(appointmentId);

        if (details == null || !details.contains("\"patientId\":" + patientId)) {
            return "You are not authorized to view this appointment.";
        }
        return details;
    }

    /**
     * Returns available appointment slots for a doctor on a given date.
     */
    @Tool("""
          Find available doctor appointment slots. Use this when a user wants to check slot availability before booking.
          The date parameter must strictly match the format: dd MMMM yyyy (e.g., '10 August 2026').
          """)
    public String getAvailableSlots(
            @P("The unique database ID of the doctor") Long doctorId,
            @P("The target date formatted exactly as: dd MMMM yyyy. Example: 10 August 2026") String date) {

        LocalDate parsedDate = DateTimeFormater.parseDate(date);
        return appointmentClient.getAvailableSlots(doctorId, parsedDate.toString());
    }

    /**
     * Cancels an existing appointment.
     */
    @Tool("Cancel patient's appointment. Use this when user requests cancellation of a specific appointment ID.")
    public String cancelAppointment(@P("The unique ID of the appointment to cancel") Long appointmentId) {
        Long userId = UserContext.getUserId();
        Long patientId = profileClient.getPatientIdByUserId(userId);
        String details = appointmentClient.getAppointmentDetails(appointmentId);

        if (details == null || !details.contains("\"patientId\":" + patientId)) {
            return "You are not authorized to cancel this appointment.";
        }
        return appointmentClient.cancelAppointment(appointmentId);
    }

    /**
     * Books a new appointment.
     */
    @Tool("""
          Book a new medical appointment with a specific doctor.
          Required elements include the Doctor ID, reason text, and an explicitly formatted string tracking the target execution timestamp.
          """)
    public String bookAppointment(
            @P("The unique database ID of the chosen doctor") Long doctorId,
            @P("The targeted execution date and time string formatted exactly as: dd MMMM yyyy hh:mm a. Example: 10 August 2026 05:00 PM") String appointmentTime,
            @P("The primary medical reason for visiting the doctor") String reason,
            @P("Any optional extra notes or symptoms provided by the patient") String notes) {

        Long userId = UserContext.getUserId();
        Long patientId = profileClient.getPatientIdByUserId(userId);

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setAppointmentTime(DateTimeFormater.parseDateTime(appointmentTime));
        request.setReason(reason);
        request.setNotes(notes);

        return appointmentClient.bookAppointment(request);
    }
}