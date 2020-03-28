
package com.ruider.AOP;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloMain {

    @Test
    public void  main() {
        Hello hello = new Hello();
        hello.print();
    }
}