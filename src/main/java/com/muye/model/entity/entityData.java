package com.muye.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: muye-interview-backend
 * @BelongsPackage: com.muye.model.entity
 * @Author: San Jin
 * @CreateTime: 2024-09-02 20:41
 * @Version: 1.0
 */

@Data
public class entityData {

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

}
