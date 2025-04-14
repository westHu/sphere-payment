package api.sphere.controller.response;

import lombok.Data;

@Data
public class MerchantMenuVO {

    private String id;

    /**
     * 资源name
     */
    private String name;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 父节点
     */
    private Integer parentId;

    /**
     * 菜单类型
     */
    private String type;

    /**
     * 显示顺序
     */
    private Integer orderNum;


    /**
     * 菜单状态（1显示 0隐藏）
     */
    private Integer visible = 1;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;
}
