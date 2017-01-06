package com.qoomon.extention.junit.rule;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by qoomon on 21/07/16.
 */
public class LazyTest {

    @Test
    public void constructor_SHOULD_not_call_supplier() throws Throwable {

        // Given
        Supplier<TestRule> ruleSupplier = mock(Supplier.class);

        // When
        new Lazy<>(ruleSupplier);

        // Then
        verify(ruleSupplier, never()).get();
    }


    @Test
    public void apply_SHOULD_not_call_supplier() throws Throwable {

        // Given
        Supplier<TestRule> ruleSupplier = mock(Supplier.class);
        Lazy<TestRule> lazyRule = new Lazy<>(ruleSupplier);

        Statement base = mock(Statement.class);
        Description description = mock(Description.class);

        // When
        lazyRule.apply(base, description);

        // Then
        verify(ruleSupplier, never()).get();

    }

    @Test
    public void apply_WHEN_evaluate_statement_THEN_lazy_rule_should_be_initialized() throws Throwable {

        // Given
        TestRule givenTestRule = new Timeout(100, TimeUnit.SECONDS);
        Lazy<TestRule> lazyRule = new Lazy<>(() -> givenTestRule);

        Statement base = mock(Statement.class);
        Description description = mock(Description.class);

        // When
        Statement lazyStatement = lazyRule.apply(base, description);
        lazyStatement.evaluate();
        TestRule rule = lazyRule.get();

        // Then
        assertThat(rule).isSameAs(givenTestRule);

    }

    @Test
    public void get_WHEN_statement_has_not_ben_evaluated_yet_THROW_exception() throws Throwable {

        // Given
        Supplier<TestRule> ruleSupplier = mock(Supplier.class);
        Lazy<TestRule> lazyRule = new Lazy<>(ruleSupplier);

        // When
        Throwable exception = catchThrowable(() -> lazyRule.get());

        // Then
        assertThat(exception).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void SHOULD_work_with_rule_chain() throws Throwable {

        // Given
        DummyRule dependencyRule = new DummyRule("abc");
        Lazy<DummyRule> dependentLazyRule = new Lazy<>(() -> new DummyRule(dependencyRule.getParameters()));

        Statement base = mock(Statement.class);
        Description description = mock(Description.class);

        // When
        RuleChain.emptyRuleChain()
                .around(dependencyRule)
                .around(dependentLazyRule)
                .apply(base,description)
                .evaluate();
        DummyRule dependentRule = dependentLazyRule.get();

        // Then
        Assertions.assertThat(dependentRule.getParameters()).isEqualTo(dependencyRule.getParameters());
    }

    class DummyRule extends ExternalResource {

        private final String parameters;

        DummyRule(String parameters) {
            this.parameters = parameters;
        }

        String getParameters() {
            return parameters;
        }
    }
}