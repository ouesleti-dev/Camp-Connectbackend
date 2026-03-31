package org.example.campconnect;

import org.example.campconnect.Entity.*;
import org.example.campconnect.Entity.Order;
import org.example.campconnect.Repository.*;
import org.example.campconnect.Service.OrderService;
import org.example.campconnect.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderLineRepository orderLineRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private DeliveryRepository deliveryRepository;

    @InjectMocks private OrderService orderService;

    private User mockUser;
    private Product mockProduct;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .idUser(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .role(Role.CAMPER)
                .build();

        mockProduct = Product.builder()
                .idProduct(1L)
                .nameProduct("Tent")
                .priceProduct(50.0)
                .quantityProduct(10)
                .locationProduct("Tunis")
                .build();

        mockOrder = Order.builder()
                .idOrder(1L)
                .orderDate(new Date())
                .deliveryAddress("Tunis")
                .paymentMethod("CASH")
                .totalAmount(100.0)
                .orderStatus("PENDING")
                .user(mockUser)
                .orderLines(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder_Success() {
        // Arrange
        OrderLineRequest lineRequest = new OrderLineRequest(1L, 2);
        OrderRequest request = new OrderRequest(1L, "Tunis", "CASH",
                List.of(lineRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        OrderLine savedLine = OrderLine.builder()
                .idOrderLine(1L)
                .requestedQuantity(2)
                .unitPrice(50.0)
                .totalPrice(100.0)
                .product(mockProduct)
                .build();
        when(orderLineRepository.save(any(OrderLine.class))).thenReturn(savedLine);

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(productRepository).save(any(Product.class)); // stock decreased
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testCreateOrder_UserNotFound() {
        OrderRequest request = new OrderRequest(99L, "Tunis", "CASH", List.of());
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.createOrder(request));
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void testCreateOrder_InsufficientStock() {
        mockProduct.setQuantityProduct(1); // only 1 in stock
        OrderLineRequest lineRequest = new OrderLineRequest(1L, 5); // wants 5
        OrderRequest request = new OrderRequest(1L, "Tunis", "CASH", List.of(lineRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        assertThrows(RuntimeException.class, () -> orderService.createOrder(request));
    }


    @Test
    @DisplayName("Should return all orders")
    void testGetAllOrders() {
        mockOrder.setOrderLines(new ArrayList<>());
        when(orderRepository.findAll()).thenReturn(List.of(mockOrder));

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdOrder());
    }


    @Test
    @DisplayName("Should return orders by user")
    void testGetOrdersByUser() {
        mockOrder.setOrderLines(new ArrayList<>());
        when(orderRepository.findByUserIdUser(1L)).thenReturn(List.of(mockOrder));

        List<OrderResponseDTO> result = orderService.getOrdersByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("Should update order status")
    void testUpdateOrderStatus() {
        mockOrder.setOrderLines(new ArrayList<>());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        OrderResponseDTO result = orderService.updateOrderStatus(1L, "CONFIRMED");

        assertEquals("CONFIRMED", mockOrder.getOrderStatus());
        verify(orderRepository).save(mockOrder);
    }


    @Test
    @DisplayName("Should approve order and create delivery")
    void testApproveOrder() {
        OrderLine line = OrderLine.builder()
                .product(mockProduct).build();
        mockOrder.setOrderLines(List.of(line));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(new Delivery());

        OrderResponseDTO result = orderService.approveOrder(1L);

        assertEquals("APPROVED", mockOrder.getOrderStatus());
        verify(deliveryRepository).save(any(Delivery.class));
    }


    @Test
    @DisplayName("Should delete order")
    void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
    }
}
