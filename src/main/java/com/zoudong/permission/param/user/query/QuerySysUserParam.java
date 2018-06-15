package com.zoudong.permission.param.user.query;

import com.zoudong.permission.param.PageParam;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Data
public class QuerySysUserParam extends PageParam{
    /**
     * 主键id
     */
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String header;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    private Integer sex;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 状态(1：启用  2：冻结  3：删除）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 乐观锁版本号
     */
    private Long version;

    /**
     * 用户类型
     */
    private Integer userType;


}