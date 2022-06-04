package com.mahama.parent.config;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * 自定义命名策略(实现支持解析#{javaConfig.property}获取表名功能 )
 *
 * @author  Java猿人一枚
 * @since 2019-03-26
 */
@Component
public class MySpringPhysicalNamingStrategy extends SpringPhysicalNamingStrategy implements ApplicationContextAware {

    private final StandardEvaluationContext context = new StandardEvaluationContext();

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.context.addPropertyAccessor(new BeanFactoryAccessor());
        this.context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        this.context.setRootObject(applicationContext);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        String nameStr = name.getText();
        if(nameStr.contains(ParserContext.TEMPLATE_EXPRESSION.getExpressionPrefix())){
            //参考SimpleElasticsearchPersistentEntity 实现思想,将tableName参数的值支持表达式获取
            Expression expression = this.parser.parseExpression(nameStr, ParserContext.TEMPLATE_EXPRESSION);
            return Identifier.toIdentifier((String)expression.getValue(this.context, String.class));
        }else {
            //默认方式不变
            return super.toPhysicalTableName(name, jdbcEnvironment);
        }
    }

}