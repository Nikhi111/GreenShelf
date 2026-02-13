package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.CartDto;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.*;
import com.example.GreenSelf.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class UserService{
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    AddressRepo addressRepo;
    @Autowired
    InventoryProductRepo inventoryProductRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    CartProductRepo cartProductRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SellerRequestRepo sellerRequestRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    CartRepo cartRepo;
    @Transactional
    public void createUser(UserRequestDto  userRequestDto) {
        Optional<User> data=userRepo.findUserByUsername(userRequestDto.getUsername());
        User user=new User();
        if (data.isPresent()) throw new RuntimeException("User Already exist"+user.getUsername());
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(Role.USER);
        userRepo.save(user);
    }
    @Transactional
    public void addToCart(String username, CartDto cartDto) {
        System.out.println("entered the add to cart");
        User user=userRepo.findUserByUsername(username).orElseThrow(()->new RuntimeException("user not found"));
        Product product=productRepo.findById(cartDto.getProductId()).orElseThrow(()->new RuntimeException("There is no such product"));
         Optional<Cart> cartData=cartRepo.findByUserAndCartStatus(user, CartStatus.ACTIVE);
         if(!checkProductQuantity(product, cartDto.getCount())) throw new RuntimeException("out of stock");
        System.out.println("valid checking done");
         Cart cart;
          if (cartData.isPresent()) {
              cart = cartData.get();
          }
          else {
              cart = new Cart();
              cart.setUser(user);
              cart.setCartStatus(CartStatus.ACTIVE);
          }
        CartProduct cartProduct=new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCount(cartDto.getCount());
        cartProduct.setPrizetotal(product.getPrize()*cartDto.getCount());
        final CartProduct savedCartPoduct = cartProductRepo.save(cartProduct);
        System.out.println("cartproduct saved");
        cart.addCartProduct(savedCartPoduct);
        cart.setTotalPrize(cart.getTotalPrize()+cartProduct.getPrizetotal());
        cartRepo.save(cart);
        System.out.println("end of the add to cart");
    }
     boolean checkProductQuantity(Product product , int count){
        if(count<0) throw  new RuntimeException("Invalid quantiy");
         InventoryProduct inventoryProduct= inventoryProductRepo.findByProduct(product).orElseThrow(()->new RuntimeException("there is no inventory product"));
          if (inventoryProduct.getCount()<count){
              return  false;
          }
          inventoryProduct.setCount(inventoryProduct.getCount()-count);
          return  true;
    }
    @Transactional
    public void createOrder(String username,Address address) {
        User user=userRepo.findUserByUsername(username).orElseThrow(()->new RuntimeException("user not found"));
        Cart cart=cartRepo.findByUserAndCartStatus(user,CartStatus.ACTIVE).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"there is not active cart"));
        List<CartProduct> cartProducts=cart.getCartproductList();
        cart.setCartStatus(CartStatus.EXPIRED);
        cartRepo.save(cart);

        address.setUser(user);
        final Address savedAddress = addressRepo.save(address);
        Order order=new Order();
        order.setUser(user);
        List<OrderProduct> orderProducts=new ArrayList<>();
       for(CartProduct cartProduct: cartProducts){
              OrderProduct orderProduct=new OrderProduct();
               orderProduct.setCount(cartProduct.getCount());
               orderProduct.setProduct(cartProduct.getProduct());
               orderProduct.setPrize(cartProduct.getPrizetotal());
       }

       order.setOrderProducts(orderProducts);
       order.setAddress(savedAddress);
       order.setNursery(cartProducts.get(0).getProduct().getNursery());
       order.setTotalPrice(cart.getTotalPrize());
       order.setOrderStatus(OrderStatus.PENDING);
       orderRepo.save(order);
    }
}