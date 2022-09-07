package com.iscas.iccbot.client.obj.model.ctg;

import com.iscas.iccbot.analyze.utils.output.PrintUtils;
import soot.Unit;

import java.util.Set;

/**
 * @Author hanada
 * @Date 2022/9/7 11:06
 * @Version 1.0
 */
public class SendOrReceiveICCInfo {
    Unit unit;
    String methodSig;
    int instructionId;
    String key;
    Set<String>  value;

    public SendOrReceiveICCInfo(Unit unit, String methodSig, int instructionId) {
        this.unit = unit;
        this.methodSig = methodSig;
        this.instructionId = instructionId;
    }

    @Override
    public String toString() {
        return  "unit=" + unit +
                ", methodSig='" + methodSig + '\'' +
                ", instructionId=" + instructionId +
                ", key='" + key + '\'' +
                ", value=" + PrintUtils.printSet(value);

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setKeyAndValue(String key,Set<String> value) {
        this.key = key;
        this.value = value;
    }
    public Set<String>  getValue() {
        return value;
    }

    public void setValue(Set<String> value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getMethodSig() {
        return methodSig;
    }

    public int getInstructionId() {
        return instructionId;
    }

}
