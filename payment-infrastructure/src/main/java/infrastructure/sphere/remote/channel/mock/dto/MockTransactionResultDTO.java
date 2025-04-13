package infrastructure.sphere.remote.channel.mock.dto;

import infrastructure.sphere.remote.channel.BaseTransactionDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MockTransactionResultDTO extends BaseTransactionDTO {

    private String txId;

    private Integer status;

    private String message;

}
