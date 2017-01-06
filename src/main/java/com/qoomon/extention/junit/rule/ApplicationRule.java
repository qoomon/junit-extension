package com.qoomon.extention.junit.rule;

import org.junit.rules.ExternalResource;

import java.io.File;

/**
 * Created by qoomon on 14/10/2016.
 */
public class ApplicationRule extends ExternalResource {

    private final ProcessBuilder processBuidler = new ProcessBuilder();


    private Process application;

    public ApplicationRule(String... command) {

        processBuidler.command(command)
                .directory(new File("target/"))
                .inheritIO();
    }

    @Override
    protected void before() throws Throwable {
        application = processBuidler.start();
        // wait for process is up and running
        // TODO

    }

    @Override
    protected void after() {
        if (application == null) {
            throw new IllegalStateException("Application has not been started yet");
        }
        application.destroy();
        // wait for process is terminated
        // TODO
    }
}
