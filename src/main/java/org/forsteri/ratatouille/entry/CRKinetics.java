package org.forsteri.ratatouille.entry;

import net.createmod.catnip.config.ConfigBase;

public class CRKinetics extends ConfigBase {
    public final CRStress stressValues;
    public CRKinetics() {
        this.stressValues =  (CRStress)this.nested(1, CRStress::new, new String[]{CRKinetics.Comments.stress});;
    }

    public String getName() {
        return "kinetics";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
    }
}