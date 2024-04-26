package io.github.yuanbug.ast.article.example.demo006.script;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import io.github.yuanbug.ast.article.example.base.entity.AstScriptIndexContext;
import io.github.yuanbug.ast.article.example.base.utils.AstUtils;
import lombok.CustomLog;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.flogger.Flogger;
import lombok.extern.java.Log;
import lombok.extern.jbosslog.JBossLog;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
public class ExceptionPrintStackTraceReplaceVisitor extends GenericVisitorAdapter<Boolean, AstScriptIndexContext> {

    private static final String IGNORED = "ignored";
    private static final String LOMBOK_LOGGER_NAME = "log";
    private static final List<Class<? extends Annotation>> LOMBOK_LOGGER_ANNOTATIONS = List.of(
            Slf4j.class,
            CommonsLog.class,
            Log.class,
            Log4j.class,
            Log4j2.class,
            XSlf4j.class,
            JBossLog.class,
            Flogger.class,
            CustomLog.class
    );

    @Override
    public Boolean visit(CatchClause catchClause, AstScriptIndexContext indexContext) {
        // 获取异常对象的变量名
        String exceptionParamName = catchClause.getParameter().getNameAsString();
        // 去掉printStackTrace()
        removePrintStackTrace(exceptionParamName, catchClause);
        if (IGNORED.equals(exceptionParamName)) {
            return Boolean.FALSE;
        }
        boolean changed = false;
        ClassOrInterfaceDeclaration classDeclaration = AstUtils.findNodeInParent(catchClause, ClassOrInterfaceDeclaration.class);
        // 获取日志字段，若不存在则增加@Slf4j注解
        String loggerName = getOrCreateLogger(classDeclaration, indexContext);
        // 如果catch语句块内没有任何其它语句，增加日志打印
        changed |= this.addLoggingIfBodyEmpty(classDeclaration, loggerName, exceptionParamName, catchClause.getBody());
        // 检查已有error级别日志的参数
        changed |= this.checkErrorLoggingParams(loggerName, exceptionParamName, catchClause.getBody());
        return changed;
    }

    private void removePrintStackTrace(String exceptionParamName, CatchClause catchClause) {
        // 找到所有e.printStackTrace()调用并移除
        catchClause.findAll(MethodCallExpr.class, methodCallExpr -> {
            // 匹配方法名
            if (!"printStackTrace".equals(methodCallExpr.getNameAsString())) {
                return false;
            }
            // 匹配参数列表（应为空）
            if (methodCallExpr.getArguments().isNonEmpty()) {
                return false;
            }
            // 匹配方法作用域，我们希望去掉的printStackTrace()是针对异常对象的实例调用，因此用NameExpr进行匹配（也可以直接用toString比较）
            return methodCallExpr.getScope()
                    .filter(NameExpr.class::isInstance)
                    .map(NameExpr.class::cast)
                    .map(NodeWithSimpleName::getNameAsString)
                    .map(exceptionParamName::equals)
                    .orElse(Boolean.FALSE);
        }).forEach(Node::removeForced);
    }

    private String getOrCreateLogger(ClassOrInterfaceDeclaration classDeclaration, AstScriptIndexContext indexContext) {
        // 如果类上已经存在lombok的任意日志注解，日志字段变量名都是log
        if (LOMBOK_LOGGER_ANNOTATIONS.stream().anyMatch(classDeclaration::isAnnotationPresent)) {
            return LOMBOK_LOGGER_NAME;
        }
        // 检查当前类中是否存在通过日志工厂创建的日志字段
        String thisFieldLogger = getLoggerFieldName(classDeclaration);
        if (null != thisFieldLogger) {
            return thisFieldLogger;
        }
        // 检查父类字段
        String superFieldLogger = getLoggerBySuperField(classDeclaration, indexContext);
        if (null != superFieldLogger) {
            return superFieldLogger;
        }
        // 增加@Slf4j注解，那么日志字段的变量名自然是log
        classDeclaration.addMarkerAnnotation(Slf4j.class);
        return LOMBOK_LOGGER_NAME;
    }

    private String getLoggerFieldName(ClassOrInterfaceDeclaration classDeclaration) {
        return classDeclaration.getFields()
                .stream()
                .filter(field -> field.getVariables().size() == 1)
                .filter(field -> "Logger".equals(String.valueOf(field.getCommonType())))
                .map(field -> field.getVariables().get(0))
                .map(NodeWithSimpleName::getNameAsString)
                .findFirst()
                .orElse(null);
    }

    private String getLoggerFieldName(Class<?> byteCode) {
        return Stream.of(byteCode.getDeclaredFields())
                .filter(field -> {
                    int modifiers = field.getModifiers();
                    if (Modifier.isPrivate(modifiers)) {
                        return false;
                    }
                    return Logger.class.isAssignableFrom(field.getType());
                })
                .map(Field::getName)
                .findFirst()
                .orElse(null);
    }

    private String getLoggerBySuperField(ClassOrInterfaceDeclaration classDeclaration, AstScriptIndexContext indexContext) {
        return indexContext.getAllParentTypes(classDeclaration).values().stream()
                .map(parentType -> {
                    ClassOrInterfaceDeclaration declaration = parentType.getAstDeclaration();
                    if (null != declaration) {
                        return getLoggerFieldName(declaration);
                    }
                    return getLoggerFieldName(parentType.getByteCode());
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private boolean addLoggingIfBodyEmpty(ClassOrInterfaceDeclaration classDeclaration, String loggerName, String exceptionParamName, BlockStmt body) {
        // 检查是否存在语句（不包括注释），如果已经存在，不进行处理
        if (!body.isEmpty()) {
            return false;
        }
        String className = classDeclaration.getNameAsString();
        String methodName = AstUtils.findNodeInParent(body, MethodDeclaration.class).getNameAsString();
        body.addStatement(new MethodCallExpr(
                new NameExpr(loggerName),
                "error",
                NodeList.nodeList(
                        new StringLiteralExpr("[%s] %s出错".formatted(className, methodName)),
                        new NameExpr(exceptionParamName)
                )));
        return true;
    }

    private boolean checkErrorLoggingParams(String loggerName, String exceptionParamName, BlockStmt body) {
        return body.findAll(MethodCallExpr.class, methodCallExpr -> {
            // 只关心error级别日志
            if (!"error".equals(methodCallExpr.getNameAsString())) {
                return false;
            }
            // 只关心有参数的调用
            if (methodCallExpr.getArguments().isEmpty()) {
                return false;
            }
            // 只关心对日志对象的调用
            return methodCallExpr.getScope()
                    .filter(NameExpr.class::isInstance)
                    .map(NameExpr.class::cast)
                    .map(NodeWithSimpleName::getNameAsString)
                    .map(loggerName::equals)
                    .orElse(Boolean.FALSE);
        }).stream().anyMatch(methodCallExpr -> fixErrorLogFormat(methodCallExpr, exceptionParamName));
    }

    private boolean fixErrorLogFormat(MethodCallExpr methodCallExpr, String exceptionParamName) {
        NodeList<Expression> arguments = methodCallExpr.getArguments();
        Expression firstArgument = arguments.get(0);
        // 第一个参数必定为日志格式，如果它不是字符串字面量，说明可能还存在拼接、从其它地方获取等复杂的逻辑，最好就不要用脚本去批量处理它了……
        if (!(firstArgument instanceof StringLiteralExpr)) {
            return false;
        }
        boolean changed = false;
        // 逐个检查其余参数，看是否存在异常对象或者getStackTrace()调用
        int exceptionInstanceIndex = -1;
        int getStackTraceIndex = -1;
        for (int i = 1; i < arguments.size(); i++) {
            Expression arg = arguments.get(i);
            if (arg instanceof NameExpr nameExpr && (exceptionParamName.equals(nameExpr.getNameAsString()))) {
                exceptionInstanceIndex = i;
            } else if (isGetStackTraceCalling(arg, exceptionParamName)) {
                getStackTraceIndex = i;
            }
        }
        // 如果存在getStackTrace()调用，将其去除
        if (getStackTraceIndex >= 0) {
            arguments.get(getStackTraceIndex).removeForced();
            // 日志格式也需要相应修改
            String logFormat = ((StringLiteralExpr) firstArgument).asString();
            arguments.get(0).replace(new StringLiteralExpr(removeParamPlaceHolder(logFormat, getStackTraceIndex)));
            changed = true;
        }
        // 如果没有打堆栈，把它放到最后
        if (exceptionInstanceIndex < 0) {
            methodCallExpr.addArgument(new NameExpr(exceptionParamName));
            changed = true;
        }
        return changed;
    }

    private boolean isGetStackTraceCalling(Expression expression, String exceptionParamName) {
        if (!(expression instanceof MethodCallExpr methodCallExpr)) {
            return false;
        }
        if (!"getStackTrace".equals(methodCallExpr.getNameAsString())) {
            return false;
        }
        if (methodCallExpr.getArguments().isNonEmpty()) {
            return false;
        }
        return methodCallExpr.getScope()
                .filter(NameExpr.class::isInstance)
                .map(NameExpr.class::cast)
                .map(NodeWithSimpleName::getNameAsString)
                .map(exceptionParamName::equals)
                .orElse(Boolean.FALSE);
    }

    /**
     * 去除第order个{}
     */
    private static String removeParamPlaceHolder(String logFormat, int order) {
        String[] segments = logFormat.split("\\{}");
        StringBuilder newFormat = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            newFormat.append(segments[i]);
            if (i != order - 1) {
                newFormat.append("{}");
            }
        }
        return newFormat.toString().trim();
    }

}
