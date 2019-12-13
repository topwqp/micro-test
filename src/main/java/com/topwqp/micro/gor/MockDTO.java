package com.topwqp.micro.gor;

import java.math.BigDecimal;

/**
 * @author wangqiupeng
 * @date 2019年12月13日15:04:04
 * @desc mock 数据传输
 */
public class MockDTO {
    private Integer  id;

    private String name ;

    private String desc;

    private BigDecimal scope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BigDecimal getScope() {
        return scope;
    }

    public void setScope(BigDecimal scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "MockDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", scope=" + scope +
                '}';
    }
}
