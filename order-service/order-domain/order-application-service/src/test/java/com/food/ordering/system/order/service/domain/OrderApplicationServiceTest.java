package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddressDto;
import com.food.ordering.system.order.service.domain.dto.create.OrderItemDto;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import id.rivasyafri.learning.domain.value.objects.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
class OrderApplicationServiceTest {

  @Autowired
  private OrderApplicationService orderApplicationService;
  @Autowired
  private OrderDataMapper orderDataMapper;
  @Autowired
  private OrderRepository mockOrderRepository;
  @Autowired
  private CustomerRepository mockCustomerRepository;
  @Autowired
  private RestaurantRepository mockRestaurantRepository;

  private CreateOrderCommand createOrderCommand;
  private CreateOrderCommand createOrderCommandWrongPrice;
  private CreateOrderCommand createOrderCommandWrongProductPrice;
  private Product theProduct;
  private static final UUID CUSTOMER_ID = UUIDv7.randomUUID();
  private static final UUID RESTAURANT_ID = UUIDv7.randomUUID();
  private static final UUID PRODUCT_ID = UUIDv7.randomUUID();
  private static final UUID ORDER_ID = UUIDv7.randomUUID();
  private static final BigDecimal PRICE = new BigDecimal("200");

  @BeforeAll
  public void init() {
    theProduct = new Product(
        new ProductId(PRODUCT_ID),
        "product-1",
        new Money(new BigDecimal(50))
    );

    createOrderCommand = CreateOrderCommand.builder()
        .customerId(CUSTOMER_ID)
        .restaurantId(RESTAURANT_ID)
        .address(OrderAddressDto.builder()
            .street("street_1")
            .postalCode("1000AB")
            .city("city_1")
            .build())
        .price(PRICE)
        .items(
            Set.of(
                OrderItemDto.builder()
                    .productId(theProduct.getId().getValue())
                    .quantity(1)
                    .price(theProduct.getPrice().amount())
                    .subTotal(theProduct.getPrice().amount())
                    .build(),
                OrderItemDto.builder()
                    .productId(PRODUCT_ID)
                    .quantity(3)
                    .price(theProduct.getPrice().amount())
                    .subTotal(theProduct.getPrice().multiply(3).amount())
                    .build()
            ))
        .build();

    createOrderCommandWrongPrice = CreateOrderCommand.builder()
        .customerId(CUSTOMER_ID)
        .restaurantId(RESTAURANT_ID)
        .address(OrderAddressDto.builder()
            .street("street_1")
            .postalCode("1000AB")
            .city("city_1")
            .build())
        .price(new BigDecimal(250))
        .items(
            Set.of(
                OrderItemDto.builder()
                    .productId(theProduct.getId().getValue())
                    .quantity(1)
                    .price(theProduct.getPrice().amount())
                    .subTotal(theProduct.getPrice().amount())
                    .build(),
                OrderItemDto.builder()
                    .productId(PRODUCT_ID)
                    .quantity(3)
                    .price(theProduct.getPrice().amount())
                    .subTotal(theProduct.getPrice().multiply(3).amount())
                    .build()
            ))
        .build();

    createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
        .customerId(CUSTOMER_ID)
        .restaurantId(RESTAURANT_ID)
        .address(OrderAddressDto.builder()
            .street("street_1")
            .postalCode("1000AB")
            .city("city_1")
            .build())
        .price(PRICE)
        .items(
            Set.of(
                OrderItemDto.builder()
                    .productId(theProduct.getId().getValue())
                    .quantity(1)
                    .price(new BigDecimal(40))
                    .subTotal(new BigDecimal(40))
                    .build(),
                OrderItemDto.builder()
                    .productId(PRODUCT_ID)
                    .quantity(3)
                    .price(theProduct.getPrice().amount())
                    .subTotal(theProduct.getPrice().multiply(3).amount())
                    .build()
            ))
        .build();

    Customer customer = new Customer();
    customer.setId(new CustomerId(CUSTOMER_ID));

    Restaurant restaurantResponse = Restaurant.builder()
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .products(Map.of(theProduct, theProduct))
        .active(true)
        .build();

    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    order.setId(new OrderId(ORDER_ID));

    when(mockCustomerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
    when(mockRestaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    when(mockOrderRepository.save(any(Order.class))).thenReturn(order);
  }

  @Test
  void testCreateOrder() {
    CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
    assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
    assertEquals("Order Created Successfully", createOrderResponse.getMessage());
    assertNotNull(createOrderResponse.getOrderTrackingId());
  }

  @Test
  void testCreateOrderWithWrongTotalPrice() {
    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
        () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
    assertEquals("Total price: 250.00 is not equal to Order items total: 200.00!",
        orderDomainException.getMessage());
  }

  @Test
  void testCreateOrderWithWrongProductPrice() {
    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
        () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
    assertEquals("Order item price: 40.00 is not valid for product " + PRODUCT_ID + "! Valid price: 50.00",
        orderDomainException.getMessage());
  }

  @Test
  void testCreateOrderWithPassiveRestaurant() {
    Restaurant restaurantResponse = Restaurant.builder()
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .products(Map.of(theProduct, theProduct))
        .active(false)
        .build();

    when(mockRestaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
        () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
    assertEquals("Restaurant with id " + restaurantResponse.getId().getValue() + " is currently not active!",
        orderDomainException.getMessage());
  }
}