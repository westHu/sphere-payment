package infrastructure.sphere.remote.channel.mock.dto;

import infrastructure.sphere.remote.channel.BaseDisbursementDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MockDisbursementResultDTO extends BaseDisbursementDTO {

    private String txId;

    private Integer status;

    private String message;
}
