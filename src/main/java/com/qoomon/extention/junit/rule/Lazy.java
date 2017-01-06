package com.qoomon.extention.junit.rule;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Created by qoomon on 21/07/16.
 * <p>
 * In case of dependent rules in a {@link RuleChain},<br>
 * {@link Lazy} give you the possibility to initialize a dependent rule only after previous rule
 */
public class Lazy<T extends TestRule> implements TestRule {

    private final Supplier<T> ruleSupplier;
    private T rule = null;


    public Lazy(Supplier<T> ruleSupplier) {
        this.ruleSupplier = ruleSupplier;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                rule = ruleSupplier.get();
                rule.apply(base, description).evaluate();
            }
        };
    }

    /**
     * Get lazy initialized rule after rule statement has been applied
     *
     * @return lazy initialized rule
     */
    public T get() {
        if (rule == null) {
            throw new IllegalStateException("Lazy rule statement has not been evaluated yet");
        }
        return rule;
    }
}
