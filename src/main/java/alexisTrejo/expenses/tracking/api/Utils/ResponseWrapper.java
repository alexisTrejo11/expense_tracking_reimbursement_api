package alexisTrejo.expenses.tracking.api.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("data")
    private T data;

    @JsonProperty("message")
    private String message;

    @JsonProperty("code")
    private int code;

    @JsonProperty("time_stamp")
    private LocalDateTime timestamp;


    public static <T> ResponseWrapper<T> ok(T data, String message) {
        return new ResponseWrapper<>(
                true,
                data,
                message,
                HttpStatus.OK.value(),
                LocalDateTime.now()
        );
    }

    public static <T> ResponseWrapper<T> error(String message, int code) {
        return new ResponseWrapper<>(
                false,
                null,
                message,
                code,
                LocalDateTime.now()
        );
    }

    public static <T> ResponseWrapper<T> notFound(String message) {
        return new ResponseWrapper<>(
                false,
                null,
                message,
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
    }

    public static <T> ResponseWrapper<T> badRequest(String message) {
        return new ResponseWrapper<>(
                false,
                null,
                message,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
    }

}

