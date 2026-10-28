package com.kostly.repository;
import com.kostly.entity.Room;
import com.kostly.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoomRepository extends JpaRepository<Room, Long> {
    long countByStatus(RoomStatus status);

    public Object findByStatus(RoomStatus roomStatus);
}
