package com.bin.sm.circuitbreak;


import com.bin.sm.circuitbreak.sre.Sre;
import org.junit.jupiter.api.Test;


public class SreTest {


    @Test
    public void shouldSre() {
        Sre.Config config = new Sre.Config();
        config.k = 1.5;
        Sre sre = new Sre(config);

        for (int i = 0; i < 400; i++) {
            sre.markSuccess();

        }

        for (int i = 0; i < 100; i++) {
            sre.markFailed();
        }

        for (int i = 0; i < 100; i++) {
            boolean allow = sre.allow();
            System.out.println(allow);
        }


        int errorThresholdPercentage = 20;
        int maxHalfOpenPass = 20;
        System.out.println( (double)(100 - errorThresholdPercentage) / 100 * maxHalfOpenPass );


    }
}
