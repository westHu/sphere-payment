package infrastructure.sphere.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaTypeEnum {

    JSON("application/json"),  API_JSON("application/vnd.api+json"), FORM("application/x-www-form-urlencoded");

    private final String type;
}
