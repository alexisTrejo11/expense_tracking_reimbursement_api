package alexisTrejo.expenses.tracking.api.Utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class Result<T> {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("data")
    private T data;

    @JsonProperty("error")
    private String errorMessage;

    @JsonProperty("status")
    private HttpStatus status = HttpStatus.OK;


    @JsonCreator
    public Result(@JsonProperty("success") boolean success,
                  @JsonProperty("data") T data,
                  @JsonProperty("error") String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, null);
    }

    public static <T> Result<T> error(String errorMessage) {
        return new Result<>(false, null, errorMessage);
    }

    public static <T> Result<T> error(String errorMessage, HttpStatus status) {
        Result<T> result = new Result<>();
        result.success = false;
        result.errorMessage = errorMessage;
        result.status = status;
        return result;
    }

    public static Result<Void> success() {
        return new Result<>(true, null, null);
    }

}
