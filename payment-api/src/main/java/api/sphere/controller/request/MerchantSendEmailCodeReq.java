package api.sphere.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantSendEmailCodeReq {

    /**
     * 邮箱
     */
    @NotBlank(message = "email is required")
    @Email(message = "email format is illegible")
    @Length(max = 64, message = "email max length 64-character")
    private String email;
}
