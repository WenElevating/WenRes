package com.wmm.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.wmm.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
* 元数据对象处理器
*
* */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());

        metaObject.setValue("updateTime", LocalDateTime.now());

        metaObject.setValue("createUser", BaseContext.getCurrentId());

        metaObject.setValue("updateUser", BaseContext.getCurrentId());

        System.out.println("公共字段自动填充[insert]....");

        log.info(metaObject.toString());
    }

    /**
     * 更新操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        long id = Thread.currentThread().getId();
        log.info("线程id = {}",id);

        metaObject.setValue("updateTime", LocalDateTime.now());

        metaObject.setValue("updateUser", BaseContext.getCurrentId());

        System.out.println("公共字段自动填充[update]....");

        log.info(metaObject.toString());
    }
}
