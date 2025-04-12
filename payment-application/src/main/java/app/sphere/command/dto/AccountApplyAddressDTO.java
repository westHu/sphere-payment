package app.sphere.command.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AccountApplyAddressDTO {

    /**
     * 国家
     */
    @NotNull(message = "country is required")
    @Length(max = 16, message = "country max length 16-character")
    private String country;

    /**
     * 省
     */
    @NotNull(message = "province is required")
    @Length(max = 32, message = "province max length 32-character")
    private String province;

    /**
     * 城市
     */
    @NotNull(message = "city is required")
    @Length(max = 32, message = "city max length 32-character")
    private String city;

    /**
     * 区域
     */
    @NotNull(message = "area is required")
    @Length(max = 64, message = "area max length 64-character")
    private String area;

    /**
     * 详细地址
     */
    @NotNull(message = "address is required")
    @Length(max = 128, message = "address max length 128-character")
    private String address;

}
