package com.example.GreenSelf.service;
import com.example.GreenSelf.Dto.InventoryProductDto;
import com.example.GreenSelf.Dto.NurseryDto;
import com.example.GreenSelf.Dto.ProductDto;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.*;
import com.example.GreenSelf.repo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SellerService {
    private  final AddressRepo addressRepo;
    private final InventoryRepo inventoryRepo;
    private final ProductRepo productRepo;
    private final InventoryProductRepo inventoryProductRepo;
    private final PlantRepo plantRepo;
    private final UserRepo userRepo;
    private final SellerRepo sellerRepo;
    private final NurseryRepo nurseryRepo;
    private final PasswordEncoder passwordEncoder;

    public SellerService(
            AddressRepo addressRepo, InventoryRepo inventoryRepo,
            ProductRepo productRepo,
            InventoryProductRepo inventoryProductRepo,
            PlantRepo plantRepo,
            UserRepo userRepo,
            SellerRepo sellerRepo,
            NurseryRepo nurseryRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.addressRepo = addressRepo;
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
        this.inventoryProductRepo = inventoryProductRepo;
        this.plantRepo = plantRepo;
        this.userRepo = userRepo;
        this.sellerRepo = sellerRepo;
        this.nurseryRepo = nurseryRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createSeller(UserRequestDto userRequestDto) {
        if (userRepo.findUserByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(Role.VENDER);
        User storedUser = userRepo.save(user);

        Seller seller = new Seller();
        seller.setUser(storedUser);
        seller.setApprovedSeller(false);
        sellerRepo.save(seller);
    }

    @Transactional
    public void addNursery(NurseryDto nurseryDto, String username) {
        User user = userRepo.findByUsernameAndRole(username, Role.VENDER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Seller seller = sellerRepo.findByUserAndIsApprovedSeller(user, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not approved"));
        Address address=nurseryDto.getAddress();
        final Address savedAddress = addressRepo.save(address);
        Inventory inventory=new Inventory();
        final Inventory savedInventory = inventoryRepo.save(inventory);
        Nursery nursery = new Nursery();
        nursery.setName(nurseryDto.getName());
        nursery.setAddress(savedAddress);
        nursery.setInventory(savedInventory);
        nursery.setLicenceNumber(nurseryDto.getLicenceNumber());
        nursery.setSeller(seller);
        nurseryRepo.save(nursery);
        savedInventory.setNursery(nursery);
        inventoryRepo.save(savedInventory);
    }
    @Transactional
    public void addProduct(String username, int nurseryId, ProductDto productDto) {
        User user = userRepo.findByUsernameAndRole(username, Role.VENDER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Seller seller = sellerRepo.findByUserAndIsApprovedSeller(user, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not approved"));
        Nursery nursery = nurseryRepo.findByIdAndOwnerAndIsVerified(nurseryId, seller, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nursery not found"));

        Inventory inventory = nursery.getInventory();
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setInventory(inventory);
        inventoryProduct.setCount(productDto.getStock());
        Plant plant = plantRepo.findByPlantId(productDto.getPlantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not available"));
        Product product = new Product();
        product.setPlant(plant);
        product.setName(plant.getCommonName());
        product.setDescription(plant.getDescription());
        product.setProductImage(plant.getImageUrl());
        product.setNursery(nursery);
        product.setPrize(productDto.getPrize());
        Product savedProduct=productRepo.save(product);
        inventoryProduct.setProduct(savedProduct);
        inventoryProductRepo.save(inventoryProduct);
    }
    Page<NurseryDto> nurseryToNuseryDto(Page<Nursery> nurseries){
        return nurseries.map(
                nursery -> {
                    NurseryDto nurseryDto=new NurseryDto();
                    nurseryDto.setAddress(nursery.getAddress());
                    nurseryDto.setName(nursery.getName());
                    nurseryDto.setLicenceNumber(nursery.getLicenceNumber());
                    return nurseryDto;
                }
        );
    }
    public Page<NurseryDto> findNursery(String username, Pageable pageable) {
       User user=userRepo.findUserByUsername(username).orElseThrow(()->new RuntimeException("User does not exsist"));
        Seller seller=sellerRepo.findByUserAndIsApprovedSeller(user,true).orElseThrow(()->new RuntimeException("seller is not approved or not found"));
        Page<Nursery> nurseries=nurseryRepo.findByOwner(seller);
     return nurseryToNuseryDto(nurseries);
    }
    Page<InventoryProductDto> invProToinvProDto(Page<InventoryProduct> inventoryProducts){
      return  inventoryProducts.map(inventoryProduct -> {
               InventoryProductDto inventoryProductDto=new InventoryProductDto();
               inventoryProductDto.setProductId(inventoryProduct.getId());
               inventoryProductDto.setPrize(inventoryProduct.getProduct().getPrize());
               inventoryProductDto.setPlantName(inventoryProduct.getProduct().getName());
               inventoryProductDto.setQuantity(inventoryProduct.getCount());
                 return inventoryProductDto;
                }
        );
    }
    public Page<InventoryProductDto> findInventoryProduct(String username, int nuseryId, Pageable pageable) {
        Nursery nursery=nurseryRepo.findById(nuseryId).orElseThrow(()->new RuntimeException("Nursery does not exist"));
        if(!nursery.getOwner().getUser().getUsername().equals(username)) throw  new ResponseStatusException(HttpStatus.FORBIDDEN,"forbidden request");
        Inventory inventory=nursery.getInventory();
        List<InventoryProduct> inventoryProductsList=inventory.getProductList();
        Page<InventoryProduct>inventoryProducts=  new PageImpl<>(inventoryProductsList,pageable,inventoryProductsList.size());
        return invProToinvProDto(inventoryProducts);
    }
}
