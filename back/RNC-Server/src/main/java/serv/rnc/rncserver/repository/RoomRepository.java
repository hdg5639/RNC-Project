package serv.rnc.rncserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import serv.rnc.rncserver.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
