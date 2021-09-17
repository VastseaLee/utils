package com.young.utils.bean;

import lombok.Data;

import java.util.Objects;

@Data
public class Equity {

    /**
     * 名称
     */
    private String name;

    /**
     * 出资对象
     */
    private String czdx;

    /**
     * 出资比例
     */
    private String czbl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equity that = (Equity) o;
        return Objects.equals(czdx, that.czdx) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Equity{" +
                "czdx='" + czdx + '\'' +
                '}';
    }
}
