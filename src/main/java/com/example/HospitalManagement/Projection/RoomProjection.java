package com.example.HospitalManagement.Projection;

import com.example.HospitalManagement.Entity.Room;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "tableView", types = { Room.class })
public interface RoomProjection {

    Integer getRoomNumber();
    String getRoomType();

    @Value("#{ 'Floor ' + target.blockFloor + ' • Block ' + target.blockCode }")
    String getBlock();



    default String getStatus() {
        if (Boolean.TRUE.equals(getUnavailable())) {
            return "Blocked";
        }
        // Check if occupied: any active stay (customize logic as needed)
        return hasActiveStay() ? "Occupied" : "Available";
    }

    Boolean getUnavailable();

    // Placeholder for stay check; customize based on your Stay logic
    default boolean hasActiveStay() {
        // Example: if stays not empty and active (adapt to your Stay model)
        // return getStays() != null && !getStays().isEmpty();
        return false; // Stub; implement via SpEL or service if needed
    }
}