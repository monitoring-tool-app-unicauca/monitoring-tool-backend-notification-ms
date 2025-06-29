package co.edu.unicauca.monitoring.tool.backend.notification.ms.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ResponseDto <T> {
    private T data;
    private Integer status;
    private String message;
    private String errorCode;

    public ResponseDto(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseDto(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseDto(int status, String message, String errorCode) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }

    public ResponseEntity<ResponseDto<T>> of() {
        return ResponseEntity.status(this.status).body(this);
    }
}
