package net.clotfelter.duncan.ShoppingCartDemo;

import net.clotfelter.duncan.ShoppingCartDemo.entities.Cart;
import net.clotfelter.duncan.ShoppingCartDemo.entities.products.Product;
import net.clotfelter.duncan.ShoppingCartDemo.entities.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.util.List;


@RestController
public class ShoppingController {
    @GetMapping("/productsbyid")
    public List<Product> getProductsById(@RequestParam int id) {
        try (var session = HibernateAnnotationUtil.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            var criteria = builder.createQuery(Product.class);
            Root<Product> productRoot = criteria.from(Product.class);
            criteria.where(builder.equal(productRoot.get("id"), id));
            return session.createQuery(criteria).getResultList();
        }
    }

    @GetMapping("/productsbyname")
    public List getProductsByName(@RequestParam String name) {
        try (var session = HibernateAnnotationUtil.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Product.class);
            criteria.add(Restrictions.ilike("name", "%"+name+"%"));
            return criteria.list();
        }
    }

    @GetMapping("/productsbytype")
    public List<Object> getProductsByType(@RequestParam String type) {
        try (var session = HibernateAnnotationUtil.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            var criteria = builder.createQuery();
            criteria.select(criteria.from(Class.forName("net.clotfelter.duncan.ShoppingCartDemo.entities.products."+type)));
            return session.createQuery(criteria).getResultList();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @PostMapping("/cart")
    public void createCart() {
        var session = HibernateAnnotationUtil.getSessionFactory().getCurrentSession();
        var tx = session.beginTransaction();
        User u = (User) ShoppingCartDemoApplication.context.getBean("OnlyUser");
        u = session.load(User.class, u.getId());
        if(u.getCart() == null) {
            Cart c = new Cart();
            c.getProducts().add((Product) ShoppingCartDemoApplication.context.getBean("book"));
            c.getProducts().add((Product) ShoppingCartDemoApplication.context.getBean("apparel"));
            u.setCart(c);
            session.saveOrUpdate(c);
            session.saveOrUpdate(u);
            System.out.println("Successfully created new cart: "+c);
        } else {
            System.out.println("Failed to create new cart: already exists!");
        }
        tx.commit();
    }

    @GetMapping("/cart")
    public Cart getCart() {
        var session = HibernateAnnotationUtil.getSessionFactory().getCurrentSession();
        var tx = session.beginTransaction();
        User u = (User) ShoppingCartDemoApplication.context.getBean("OnlyUser");
        u = session.load(User.class, u.getId());
        System.out.println("Successfully read cart: "+u.getCart());
        return u.getCart();
    }

    @PutMapping("/cart")
    public void updateCart(@RequestBody Cart updatedCart) {
        var session = HibernateAnnotationUtil.getSessionFactory().getCurrentSession();
        var tx = session.beginTransaction();
        session.saveOrUpdate(updatedCart);
        tx.commit();
        System.out.println("Successfully updated cart: "+updatedCart);
    }

    @DeleteMapping("/cart")
    public void deleteCart() {
        var session = HibernateAnnotationUtil.getSessionFactory().getCurrentSession();
        var tx = session.beginTransaction();
        User u = (User) ShoppingCartDemoApplication.context.getBean("OnlyUser");
        u = session.load(User.class, u.getId());
        Cart toDelete = u.getCart();
        u.setCart(null);
        session.saveOrUpdate(u);
        session.delete(toDelete);
        tx.commit();
        System.out.println("Successfully deleted cart: "+u.getCart());
    }
}
