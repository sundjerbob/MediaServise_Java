package ts.component.mediaservise_java.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MediaObjectDTO {
    private String name;
    private long size;
}
