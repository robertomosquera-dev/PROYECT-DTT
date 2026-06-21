package org.dtt.msorder.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

public class RunLinnerPrint implements CommandLineRunner {
    @Value("${api.logic}")
    private String msLogic;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Microservice Catalog: "+msLogic);
    }
}
