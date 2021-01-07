package com.starfireaviation.gate;

public class DataSet {

    private Long key = 0L;

    private Integer value = 0;

    private Long diff = 0L;

    public DataSet() {
        // Do something
    }

    public DataSet(final Long k, final Integer v, final Long d) {
        key = k;
        value = v;
        diff = d;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Long getDiff() {
        return diff;
    }

    public void setDiff(Long diff) {
        this.diff = diff;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "key=" + key +
                ", value=" + value +
                ", diff=" + diff +
                '}';
    }
}
