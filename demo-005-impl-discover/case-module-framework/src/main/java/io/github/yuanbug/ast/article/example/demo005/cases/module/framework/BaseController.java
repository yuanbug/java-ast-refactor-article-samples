package io.github.yuanbug.ast.article.example.demo005.cases.module.framework;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author yuanbug
 * @since 2024-05-10
 */
public abstract class BaseController {

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;

    protected Optional<String> getHeader(@Nonnull String key) {
        return Optional.ofNullable(request.getHeader(key));
    }

    protected Stream<String> getHeaders(@Nonnull String key) {
        return Collections.list(request.getHeaders(key)).stream();
    }

}
