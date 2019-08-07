package app.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 *
 * Spring bean factory.
 *
 * @author OAK
 *
 * @since 2019/07/23 14:29:00 PM.
 *
 */
@Component
@Scope(value = SCOPE_SINGLETON)
public class SpringBeanFactory implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public <T> T getBean(String name) {
        if (this.beanFactory == null) {
            return null;
        }
        return (T) beanFactory.getBean(name);
    }
}
