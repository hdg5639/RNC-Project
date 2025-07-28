package serv.rnc.rncserver.entity.readPointer;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadPointerId implements Serializable {
    private Long userId;
    private Long roomId;
}
