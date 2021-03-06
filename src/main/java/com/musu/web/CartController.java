package com.musu.web;

        import com.musu.model.*;
        import com.musu.service.*;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.PathVariable;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.ModelAttribute;
        import javax.servlet.http.HttpSession;
        import java.util.List;

@Controller
public class CartController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailsService orderDetailsService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    @RequestMapping(value = {"/cart/{product}"})
    public String showCart(Model model, @PathVariable("product")String productname, HttpSession session) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
          username = ((UserDetails)principal).getUsername();
        } else {
          username = principal.toString();
        }
        User user=userService.findByUsername(username);
        ProductsEntity product = productService.findByName(productname);
        List<ShoppingCart> shoppingCart1=cartService.findCartProductByProductId(username,product.getProductId());
        if(shoppingCart1.size()>0){
            int quantity=shoppingCart1.get(0).getQuantity();
     ShoppingCart sCart= shoppingCart1.get(0);
            quantity++;
            sCart.setQuantity(quantity);
            int id =shoppingCart1.get(0).getId();
     cartService.update(quantity,shoppingCart1.get(0).getId());
        }
        else{
            ShoppingCart shoppingCart=new ShoppingCart();
            shoppingCart.setProduct(product);
            shoppingCart.setUser(user);
            shoppingCart.setQuantity(1);
            cartService.save(shoppingCart);
        }

        List<ShoppingCart> shoppingCarts=cartService.findCartByUser(username);
        List<ProductcategoriesEntity> productCategoryEntitiyList = categoryService.findAll();
        model.addAttribute("category",productCategoryEntitiyList);
        model.addAttribute("shoppingCarts",shoppingCarts);
        session.setAttribute("shoppingCart",shoppingCarts);
        return "cart";
    }
    @RequestMapping(value = {"/cart"})
    public String showCart1(Model model,HttpSession session) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        int total=0;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        List<ShoppingCart> shoppingCarts=cartService.findCartByUser(username);
        for(int i=0;i<shoppingCarts.size();i++){
           total+=shoppingCarts.get(i).getQuantity()*shoppingCarts.get(i).getProduct().getProductPrice();
        }
        session.setAttribute("totalAmount",total);
        List<ProductcategoriesEntity> productCategoryEntitiyList = categoryService.findAll();
        model.addAttribute("category",productCategoryEntitiyList);
        model.addAttribute("shoppingCarts",shoppingCarts);
        model.addAttribute("total",total);
        return "cart";
    }
    @RequestMapping(value = {"/cart/remove/{name}"})
    public String DeleteItemFromCart(@PathVariable("name")String productname,Model model,HttpSession session) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        int total=0;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
       List<ShoppingCart> shoppingCart=cartService.shoppingCartByName(username,productname);
        cartService.deleteItemFromCart(shoppingCart.get(0).getId());
        List<ShoppingCart> shoppingCarts=cartService.findCartByUser(username);
        for(int i=0;i<shoppingCarts.size();i++){
            total+=shoppingCarts.get(i).getQuantity()*shoppingCarts.get(i).getProduct().getProductPrice();
        }
        List<ProductcategoriesEntity> productCategoryEntitiyList = categoryService.findAll();
        model.addAttribute("category",productCategoryEntitiyList);
        model.addAttribute("shoppingCarts",shoppingCarts);
        model.addAttribute("total",total);
        session.setAttribute("shoppingCart",shoppingCarts);
        return "cart";
    }
    @RequestMapping(value = {"/checkOut"},method = RequestMethod.GET)
    public String showCheckOut(Model model) {
        model.addAttribute("order", new OrdersEntity());
        return "checkOut";
    }

    @RequestMapping(value = {"/checkOut"},method = RequestMethod.POST)
    public String registerCheckOut(@ModelAttribute("order") OrdersEntity ordersEntity,@ModelAttribute("orderdetails") OrderDetailsEntity orderDetailsEntity) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user=userService.findByUsername(username);
    OrdersEntity ordersEntity1=ordersEntity;
        ordersEntity.setUserId(user);

        List<ShoppingCart> c=cartService.findCartByUser(username);
OrderDetailsEntity orderDetailsEntity1 =orderDetailsEntity ;
   orderService.save(ordersEntity1);

        for (int i=0;i<c.size();i++){
            orderDetailsEntity1.setOrdersEntity(ordersEntity1);
            orderDetailsEntity1.setProductsEntity(c.get(i).getProduct());
            orderDetailsEntity1.setDetailName(c.get(i).getProduct().getProductName());
            orderDetailsEntity1.setDetailQuantity(c.get(i).getQuantity());
            orderDetailsEntity1.setDetailPrice(c.get(i).getProduct().getProductPrice());
            orderDetailsEntity1.setDetailSku(c.get(i).getProduct().getProductSku());
            orderDetailsService.save(orderDetailsEntity1);
        }

        return "redirect:/home";
    }
}
