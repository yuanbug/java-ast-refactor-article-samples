package io.github.yuanbug.ast.article.example.demo007.cases.case4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
public class BaseClassWithLogger {

    protected final Logger myLog;

    public BaseClassWithLogger() {
        this.myLog = LoggerFactory.getLogger(this.getClass());
    }

}
