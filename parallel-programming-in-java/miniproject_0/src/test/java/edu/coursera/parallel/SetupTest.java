package edu.coursera.parallel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SetupTest
        // extends TestCase
{
    @Test
    public void testSetup() {
        final int result = Setup.setup(42);
        assertEquals(42, result);
    }
}
