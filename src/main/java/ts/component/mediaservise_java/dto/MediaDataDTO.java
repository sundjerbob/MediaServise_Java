package ts.component.mediaservise_java.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@Builder
public class MediaDataDTO {
    private final String contentType;
    private final InputStream dataStream;

    public MediaDataDTO(String contentType, InputStream dataStream) {
        this.contentType = contentType;
        this.dataStream = dataStream;
    }


}

