package app.sphere.query.param;

import lombok.Data;

@Data
public class IndustryTemplateListParam extends PageParam {

    /**
     * 行业ID
     */
    private Long id;


    /**
     * 行业名称
     */
    private String name;
}
