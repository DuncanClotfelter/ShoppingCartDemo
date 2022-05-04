package net.clotfelter.duncan.ShoppingCartDemo;

import net.clotfelter.duncan.ShoppingCartDemo.entities.products.Product;
import net.clotfelter.duncan.ShoppingCartDemo.entities.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ShoppingCartDemoApplication {
	static ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

	public static void main(String[] args) {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		SpringApplication.run(ShoppingCartDemoApplication.class, args);

		var sessionFactory = HibernateAnnotationUtil.getSessionFactory();
		var session = sessionFactory.getCurrentSession();
		System.out.println("Session created");

		var tx = session.beginTransaction();

		session.saveOrUpdate(context.getBean("book"));
		session.saveOrUpdate(context.getBean("apparel"));
		session.saveOrUpdate(context.getBean("product"));
		session.saveOrUpdate(context.getBean("OnlyUser"));

		tx.commit();
	}

}
