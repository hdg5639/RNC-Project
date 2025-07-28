package serv.rnc.rncserver.entity.readPointer;

import jakarta.persistence.*;
import lombok.*;
import serv.rnc.rncserver.entity.Message;
import serv.rnc.rncserver.entity.Room;
import serv.rnc.rncserver.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_read_pointer")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadPointer {
    @EmbeddedId
    private ReadPointerId readPointerId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("userId")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("roomId")
    private Room room;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_message_id", referencedColumnName = "id")
    private Message lastReadMessage;

    private LocalDateTime updatedAt;

}
