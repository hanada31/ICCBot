package com.iscas.iccbot.client.related.gator.model;

import com.iscas.iccbot.client.obj.model.atg.ATGModel;

public class GatorModel {
    private ATGModel gatorAtgModel;

    public GatorModel() {
        setGatorAtgModel(new ATGModel());
    }

    /**
     * @return the gatorAtgModel
     */
    public ATGModel getGatorAtgModel() {
        return gatorAtgModel;
    }

    /**
     * @param gatorAtgModel the gatorAtgModel to set
     */
    public void setGatorAtgModel(ATGModel gatorAtgModel) {
        this.gatorAtgModel = gatorAtgModel;
    }
}
